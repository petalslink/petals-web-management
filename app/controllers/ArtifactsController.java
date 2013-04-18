/**
 *
 * Copyright (c) 2013, Linagora
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA 
 *
 */
package controllers;

import java.net.URL;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import models.ArtifactURL;
import models.BaseEvent;
import models.Node;
import models.Property;

import org.ow2.petals.admin.api.artifact.Artifact;
import org.ow2.petals.admin.api.artifact.Component;
import org.ow2.petals.admin.api.artifact.Component.ComponentType;
import org.ow2.petals.admin.api.artifact.ServiceAssembly;
import org.ow2.petals.admin.api.artifact.SharedLibrary;

import play.Logger;
import play.data.validation.Required;
import play.jobs.Job;
import utils.Check;
import utils.PetalsAdmin;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

/**
 * @author chamerling
 */
public class ArtifactsController extends PetalsController {

	public static void index() {
		Collection<Component> components = null;
		Collection<ServiceAssembly> sas = null;
		Collection<SharedLibrary> sls = null;

		try {
			List<Artifact> artifacts = PetalsAdmin.getArtifactAdministration(
					getCurrentNode()).listArtifacts();

			components = Collections2.transform(Sets.newHashSet(Collections2
					.filter(artifacts, new Predicate<Artifact>() {
						public boolean apply(Artifact input) {
							return input != null
									&& (input.getType().equalsIgnoreCase(
											ComponentType.BC.toString()) || input
											.getType()
											.equalsIgnoreCase(
													ComponentType.SE.toString()));
						}
					})), new Function<Artifact, Component>() {
				public Component apply(Artifact input) {
					return (Component) input;
				}
			});

			sas = Collections2.transform(Sets.newHashSet(Collections2.filter(
					artifacts, new Predicate<Artifact>() {
						public boolean apply(Artifact input) {
							return input != null
									&& input.getType().equalsIgnoreCase("SA");
						}
					})), new Function<Artifact, ServiceAssembly>() {
				public ServiceAssembly apply(Artifact input) {
					return (ServiceAssembly) input;
				}
			});

			sls = Collections2.transform(Sets.newHashSet(Collections2.filter(
					artifacts, new Predicate<Artifact>() {
						public boolean apply(Artifact input) {
							return input != null
									&& input.getType().equalsIgnoreCase("SL");
						}
					})), new Function<Artifact, SharedLibrary>() {
				public SharedLibrary apply(Artifact input) {
					return (SharedLibrary) input;
				}
			});

		} catch (Exception e) {
			Logger.error(e, "Error while getting client");
			flash.error("Can not get client : %s", e.getMessage());
		}

		render(components, sas, sls);
	}

	public static void artifact(String name, String type) {
		render();
	}

	public static void deploy() {
		// TODO : Get URLs
		render();
	}

	public static void doDeployAndStartArtifact(
			@Required(message = "URL is required") final String url) {

		validation.required(url);
		validation.isTrue(Check.isURL(url));

		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			deploy();
		}

		final Node node = getCurrentNode();
		new Job() {
			@Override
			public void doJob() throws Exception {
				try {
					PetalsAdmin.getArtifactAdministration(node)
							.deployAndStartArtifact(new URL(url));

					PetalsController.bus().post(
							new BaseEvent(String.format(
									"Artifact deployed and started from %s",
									url), "info"));
				} catch (Exception e) {
					e.printStackTrace();
					PetalsController.bus().post(
							new BaseEvent(String.format(
									"Error while deploying artifact %s : %s",
									url, e.getMessage()), "warning"));
				}
			}
		}.in(2); // delay...

		flash.success("Artifact is being deployed from URL %s...", url);
		index();
	}

	public static void doStopAndUndeployArtifact(final String name,
			final String type) {

		final Node node = getCurrentNode();
		new Job() {
			@Override
			public void doJob() throws Exception {
				try {
					PetalsAdmin.getArtifactAdministration(node)
							.stopAndUndeployArtifact(type, name);

					PetalsController
							.bus()
							.post(new BaseEvent(
									String.format(
											"Artifact %s stopped and undeployed",
											name), "info"));
				} catch (Exception e) {
					e.printStackTrace();
					PetalsController.bus().post(
							new BaseEvent(String.format(
									"Error while undeploying artifact %s : %s",
									name, e.getMessage()), "warning"));
				}
			}
		}.in(2); // delay...

		flash.success("Artifact %s stopped and undeployed in the background",
				name);
		index();
	}

	public static void component(String name, String type) {
		try {
			Artifact artifact = PetalsAdmin.getArtifactAdministration(
					getCurrentNode()).getArtifactInfo(type, name);

			if (artifact != null) {
				final Component component = (Component) artifact;

				Set<Property> properties = ImmutableSortedSet
						.orderedBy(new Comparator<Property>() {
							public int compare(Property r1, Property r2) {
								return r1.name.compareToIgnoreCase(r2.name);
							}
						})
						.addAll(Collections2.transform(component
								.getParameters().stringPropertyNames(),
								new Function<String, Property>() {
									public Property apply(String key) {
										return new Property(key, component
												.getParameters().getProperty(
														key));
									}
								})).build();

				render(component, properties);
			} else {
				flash.error("No such component");
				index();
			}
		} catch (Exception e) {
			flash.error("Error while getting component information : %s",
					e.getMessage());
			index();
		}
	}

	public static void sa(String name) {
		try {
			Artifact artifact = PetalsAdmin.getArtifactAdministration(
					getCurrentNode()).getArtifactInfo("SA", name);

			if (artifact != null) {
				final ServiceAssembly sa = (ServiceAssembly) artifact;
				render(sa);
			} else {
				flash.error("No such service assembly");
				index();
			}
		} catch (Exception e) {
			flash.error(
					"Error while getting service assembly information : %s",
					e.getMessage());
			index();
		}
	}

	public static void sl(String name) {
		try {
			Artifact artifact = PetalsAdmin.getArtifactAdministration(
					getCurrentNode()).getArtifactInfo("SL", name);

			if (artifact != null) {
				final SharedLibrary sl = (SharedLibrary) artifact;
				render(sl);
			} else {
				flash.error("No such shared library");
				index();
			}
		} catch (Exception e) {
			flash.error("Error while getting shared library information : %s",
					e.getMessage());
			index();
		}
	}

	public static void repository() {
		List<ArtifactURL> artifacts = ArtifactURL.byName();
		render(artifacts);
	}

	public static void repositoryDeploy(Long id) {
		final ArtifactURL artifact = ArtifactURL.findById(id);
		if (artifact == null) {
			flash.error("Artifact not found");
			repository();
		}

		// get it here since we are in the session context...
		final Node node = getCurrentNode();
		new Job() {
			@Override
			public void doJob() throws Exception {
				try {
					PetalsAdmin.getArtifactAdministration(node)
							.deployAndStartArtifact(new URL(artifact.url));

					PetalsController.bus().post(
							new BaseEvent(String.format(
									"Artifact %s deployed and started",
									artifact.name), "info"));
				} catch (Exception e) {
					e.printStackTrace();
					PetalsController.bus().post(
							new BaseEvent(String.format(
									"Error while deploying artifact %s : %s",
									artifact.name, e.getMessage()), "warning"));
				}
			}
		}.in(2); // delay...

		flash.success("Artifact is being deployed from URL %s...", artifact.url);
		index();
	}

	public static void doRepositoryCreate(
			@Required(message = "Name is required") String name,
			@Required(message = "URL is required") String url, String version) {
		validation.required(name);
		validation.required(url);

		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			repository();
		}

		ArtifactURL a = new ArtifactURL();
		a.date = new Date();
		a.name = name;
		a.url = url;
		a.save();

		newEvent(new BaseEvent(String.format("Artifact %s has been created",
				name), "info"));
		flash.success("Artifact %s has been registered", name);
		repository();
	}
}