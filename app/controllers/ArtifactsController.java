/**
 * Copyright (c) 2013-2015 Linagora
 *
 * This program/library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This program/library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program/library; If not, see <http://www.gnu.org/licenses/>
 * for the GNU Lesser General Public License version 2.1.
 */
package controllers;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import controllers.actions.PetalsNodeSelected;
import controllers.forms.*;
import models.ArtifactURL;
import models.Node;
import models.Property;
import models.Subscription;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.ow2.petals.admin.api.artifact.Artifact;
import org.ow2.petals.admin.api.artifact.Component;
import org.ow2.petals.admin.api.artifact.Component.ComponentType;
import org.ow2.petals.admin.api.artifact.ServiceAssembly;
import org.ow2.petals.admin.api.artifact.SharedLibrary;
import play.Logger;
import play.Play;
import play.data.Form;
import play.mvc.Http;
import play.mvc.Result;
import utils.ApplicationEvent;
import utils.Constants;
import utils.PetalsAdmin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import views.html.ArtifactsController.*;

/**
 * @author chamerling
 */
@PetalsNodeSelected
public class ArtifactsController extends PetalsController {

    public static Result index() {
        List<Component> components = null;
        List<ServiceAssembly> sas = null;
        List<SharedLibrary> sls = null;

        try {
            List<Artifact> artifacts = PetalsAdmin.getArtifactAdministration(
                    getCurrentNode()).listArtifacts();

            components = Lists.newArrayList(Collections2.transform(Sets.newHashSet(Collections2
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
            }));

            sas = Lists.newArrayList(Collections2.transform(Sets.newHashSet(Collections2.filter(
                    artifacts, new Predicate<Artifact>() {
                public boolean apply(Artifact input) {
                    return input != null
                            && input.getType().equalsIgnoreCase("SA");
                }
            })), new Function<Artifact, ServiceAssembly>() {
                public ServiceAssembly apply(Artifact input) {
                    return (ServiceAssembly) input;
                }
            }));

            sls = Lists.newArrayList(Collections2.transform(Sets.newHashSet(Collections2.filter(
                    artifacts, new Predicate<Artifact>() {
                public boolean apply(Artifact input) {
                    return input != null
                            && input.getType().equalsIgnoreCase("SL");
                }
            })), new Function<Artifact, SharedLibrary>() {
                public SharedLibrary apply(Artifact input) {
                    return (SharedLibrary) input;
                }
            }));

        } catch (Exception e) {
            Logger.error("Error while getting client", e);
            flash("error", String.format("Can not get client : %s", e.getMessage()));
        }

        return ok(index.render(components, sas, sls, play.data.Form.form(DeployArtifact.class)));
    }

    public static Result artifact(String name, String type) {
        // TODO
        flash("error", "TODO");
        return index();
    }

    /**
     * Deploy and start artifact from URL
     *
     * @return
     */
    public static Result doDeployAndStartArtifact() {

        Form<DeployArtifact> form = play.data.Form.form(DeployArtifact.class).bindFromRequest();
        if(form.hasErrors()) {
            flash("error", "Bad parameters");
            return redirect(routes.ArtifactsController.index());
        } else {
            final Node node = getCurrentNode();
            // TODO : Deploy async

            /*
            Akka.system().scheduler().scheduleOnce(
                    Duration.create(10, TimeUnit.SECONDS),
                    new Runnable() {
                        public void run() {
                            ApplicationEvent.info("Deployed artifact %s", "PING");
                        }
                    }, null);
            */

            try {
                PetalsAdmin.getArtifactAdministration(node)
                            .deployAndStartArtifact(new URL(form.get().url));
                ApplicationEvent.info("Artifact deployed and started from %s", form.get().url);
            } catch (Exception e) {
                Logger.error(String.format("Error while deploying artifact %s", form.get().url), e);
                ApplicationEvent.warning("Error while deploying artifact %s : %s", form.get().url, e.getMessage());
            }
            flash("success", String.format("Artifact is being deployed from URL %s...", form.get().url));
        }
        return redirect(routes.ArtifactsController.index());
    }

    /**
     * Stop and undeploy component
     *
     * @param name
     * @param type
     * @return
     */
    public static Result doStopAndUndeployArtifact(final String name,
                                                   final String type) {

        if (name == null || type == null) {
            flash("error", "Bad parameters");
            return redirect(routes.ArtifactsController.index());
        } else {
            final Node node = getCurrentNode();
            // TODO : Async
            try {
                PetalsAdmin.getArtifactAdministration(node)
                        .stopAndUndeployArtifact(type, name, null);
                ApplicationEvent.info("Artifact '%s' stopped and undeployed", name);
            } catch (Exception e) {
                Logger.error(String.format("Error while undeploying artifact %s"), e);
                ApplicationEvent.warning("Error while undeploying artifact '%s' : %s",
                        name, e.getMessage());
            }
            flash("success", String.format("Stopping and undeploying artifact %s",
                    name));
        }
        return redirect(routes.ArtifactsController.index());
    }

    public static Result component(String name, String tyype) {
        try {
            Artifact artifact = PetalsAdmin.getArtifactAdministration(
                    getCurrentNode()).getArtifactInfo(tyype, name, null);

            if (artifact != null) {
                final Component c = (Component) artifact;

                Set<Property> properties = ImmutableSortedSet
                        .orderedBy(new Comparator<Property>() {
                            public int compare(Property r1, Property r2) {
                                return r1.name.compareToIgnoreCase(r2.name);
                            }
                        })
                        .addAll(Collections2.transform(c
                                .getParameters().stringPropertyNames(),
                                new Function<String, Property>() {
                                    public Property apply(String key) {
                                        return new Property(key, c
                                                .getParameters().getProperty(
                                                        key));
                                    }
                                })).build();

                List<Subscription> subscriptions = Subscription.subscriptions(name, tyype, getCurrentNode());
                return ok(component.render(c, properties, subscriptions));
            } else {
                flash("error", "No such component");
                return index();
            }
        } catch (Exception e) {
            flash("error", String.format("Error while getting component information : %s",
                    e.getMessage()));
            return index();
        }
    }

    public static Result sa(String name) {
        try {
            Artifact artifact = PetalsAdmin.getArtifactAdministration(
                    getCurrentNode()).getArtifactInfo("SA", name, null);

            if (artifact != null) {
                final ServiceAssembly serviceAssembly = (ServiceAssembly) artifact;
                return ok(sa.render(serviceAssembly));
            } else {
                flash("error", "No such service assembly");
                return index();
            }
        } catch (Exception e) {
            flash("error", String.format(
                    "Error while getting service assembly information : %s",
                    e.getMessage()));
            return index();
        }
    }

    public static Result sl(String name) {
        try {
            Artifact artifact = PetalsAdmin.getArtifactAdministration(
                    getCurrentNode()).getArtifactInfo("SL", name, null);

            if (artifact != null) {
                final SharedLibrary sharedLibrary = (SharedLibrary) artifact;
                return ok(sl.render(sharedLibrary));
            } else {
                flash("error", "No such shared library");
                return index();
            }
        } catch (Exception e) {
            flash("error", String.format("Error while getting shared library information : %s",
                    e.getMessage()));
            return index();
        }
    }

    public static Result repository() {
        List<ArtifactURL> artifacts = Lists.newArrayList(Collections2.transform(
                ArtifactURL.byName(), new Function<ArtifactURL, ArtifactURL>() {
            public ArtifactURL apply(ArtifactURL input) {
                input.url = getArtifactURL(input);
                return input;
            }
        }));
        return ok(repository.render(artifacts, Form.form(DeployArtifactToRepository.class)));
    }

    /**
     * Deploy from repository ie get the URL from the repository.
     *
     * @param id
     */
    public static Result repositoryDeploy(Long id) {
        final ArtifactURL artifact = ArtifactURL.findById(id);
        if (artifact == null) {
            flash("error", "Artifact not found");
            return redirect(routes.ArtifactsController.repository());
        } else {
            // get it here since we are in the session context...
            // TODO : Async
            final Node node = getCurrentNode();
            try {
                PetalsAdmin.getArtifactAdministration(node)
                        .deployAndStartArtifact(
                                new URL(getArtifactURL(artifact)));

                ApplicationEvent.info("Artifact %s deployed and started", artifact.name);
            } catch (Exception e) {
                Logger.error(String.format("Error while deploying artifact %s", artifact.name), e);
                ApplicationEvent.warning("Error while deploying artifact %s : %s", artifact.name, e.getMessage());
            }
            flash("info", String.format("Artifact is being deployed from URL %s...",
                    getArtifactURL(artifact)));
            return redirect(routes.ArtifactsController.index());
        }
    }

    public static Result doRepositoryCreate() {
        Form<RepositoryCreate> form = play.data.Form.form(RepositoryCreate.class).bindFromRequest();
        if(form.hasErrors()) {
            flash("error", "Bad parameters");
            return redirect(routes.ArtifactsController.repository());
        } else {
            ArtifactURL a = new ArtifactURL();
            a.date = new Date();
            a.name = form.get().name;
            a.url = form.get().url;
            a.version = form.get().version;
            a.save();
            ApplicationEvent.info("Artifact %s has been created", a.name);
            flash("success", String.format("Artifact %s has been registered", a.name));
            return redirect(routes.ArtifactsController.repository());
        }
    }

    /**
     * Upload a new artifact from file and add it to the artifacts list
     */
    public static Result doUpload() {

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart f = body.getFile("artifact");
        if (f != null) {
            String fileName = f.getFilename();
            System.out.println(fileName);
            File file = f.getFile();

            // TODO : get a new name if already exists...
            File out = new File(getArtifactsFolder(), fileName);
            try {
                FileInputStream is = new FileInputStream(file);
                IOUtils.copy(is, new FileOutputStream(out));
            } catch (IOException e) {
                e.printStackTrace();
                ApplicationEvent.warning("Can not copy file to local folder %s", e.getMessage());
                flash("error", String.format("Can not copy file to local folder : %s",
                        e.getMessage()));
                return repository();
            }

            String version = null;

            // when copied, store the artifact in the database
            ApplicationEvent.info("Artifact %s uploaded into repository", fileName);
            ArtifactURL artifact = new ArtifactURL();
            artifact.date = new Date();
            artifact.name = fileName;
            artifact.version = version == null ? "" : version;
            artifact.local = true;
            // storing just the local name for later processing and URL handling...
            artifact.url = fileName;
            artifact.save();

            flash("success", "Artifact deployed");
            return redirect(routes.ArtifactsController.repository());
        } else {
            flash("error", "Missing file");
            return redirect(routes.ArtifactsController.repository());
        }
    }

    /**
     * Delete local files and their entries in the DB
     */
    public static Result deleteLocalArtifacts() {
        List<ArtifactURL> locals = ArtifactURL.locals();
        for (ArtifactURL artifactURL : locals) {
            artifactURL.delete();
        }

        deleteLocalArtifactFiles();

        flash("success", "Local artifacts deleted");
        return repository();
    }

    /**
     * Protected so that it does not fire a render event...
     */
    protected static Result deleteLocalArtifactFiles() {
        Iterator<File> iter = FileUtils.iterateFiles(getArtifactsFolder(),
                new String[] { "zip" }, false);
        while (iter.hasNext()) {
            System.out.println("TODO : Delete " + iter.next());
            //FileUtils.deleteQuietly(iter.next());
        }
        return repository();
    }

    public static File getArtifactsFolder() {
        return new File(Play.application().getFile("public"), Constants.ARTIFACTS_FOLDER);
    }

    public static String getArtifactURL(ArtifactURL artifact) {
        if (artifact.local) {
            // TODO : Get it from Play
            return "http://" + request().host() + "/assets/" + Constants.ARTIFACTS_FOLDER + "/" + artifact.name;
        }
        return artifact.url;
    }
}