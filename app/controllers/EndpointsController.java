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

import org.ow2.petals.admin.api.RegistryAdministration;
import org.ow2.petals.admin.registry.Endpoint;
import org.ow2.petals.admin.registry.RegistryView;
import play.jobs.Job;
import utils.ApplicationEvent;
import utils.PetalsAdmin;

import java.util.List;

/**
 * @author chamerling
 * 
 */
public class EndpointsController extends PetalsController {

	public static void index() {
		try {
			RegistryAdministration registry = PetalsAdmin
					.getRegistryAdministration(getCurrentNode());
			RegistryView view = registry.getRegistryContent(".*", ".*", ".*");
			List<Endpoint> endpoints = view.getAllEndpoints();
			render(endpoints);
		} catch (Exception e) {
			flash.error("Can not get registry client : %s", e.getMessage());
			render();
		}
	}

	public static void synchronizeAll() {
		try {
			final RegistryAdministration registry = PetalsAdmin
					.getRegistryAdministration(getCurrentNode());
			new Job() {
				@Override
				public void doJob() throws Exception {

					try {
						registry.synchronizeRegistryOnAllNodes();
						ApplicationEvent.info("All registries have been synchronized");
                    } catch (Exception e) {
                        ApplicationEvent.warning("All Registries sync error : %s", e.getMessage());
					}
				}
			}.now();
		} catch (Exception e) {
			e.printStackTrace();
			flash.error("Unable to call synchronize '%s'", e.getMessage());
		}
		flash.success("Registry sync has been launched in the background...");
		index();
	}

	public static void synchronize() {
		try {
			final RegistryAdministration registry = PetalsAdmin
					.getRegistryAdministration(getCurrentNode());
			new Job() {
				@Override
				public void doJob() throws Exception {
					try {
						registry.synchronizeRegistry();
                        ApplicationEvent.info("Registry Synchronized");
					} catch (Exception e) {
                        ApplicationEvent.warning("Registry sync error : %s", e.getMessage());
					}
				}
			}.now();
		} catch (Exception e) {
			e.printStackTrace();
			flash.error("Unable to call synchronize '%s'", e.getMessage());
		}

		flash.success("Registry sync has been launched in the background...");
		index();
	}

	public static void endpoint(String name) {
		flash.error("Not implemented");
		index();
	}
}
