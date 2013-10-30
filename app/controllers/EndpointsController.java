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

import controllers.actions.PetalsNodeSelected;
import org.ow2.petals.admin.api.RegistryAdministration;
import org.ow2.petals.admin.registry.Endpoint;
import org.ow2.petals.admin.registry.RegistryView;
import play.Logger;
import play.mvc.Result;
import utils.ApplicationEvent;
import utils.PetalsAdmin;

import java.util.List;

import views.html.EndpointsController.*;


/**
 * @author chamerling
 * 
 */
@PetalsNodeSelected
public class EndpointsController extends PetalsController {

    public static Result index() {
        try {
            RegistryAdministration registry = PetalsAdmin
                    .getRegistryAdministration(getCurrentNode());
            RegistryView view = registry.getRegistryContent(".*", ".*", ".*");
            List<Endpoint> endpoints = view.getAllEndpoints();
            return ok(index.render(endpoints));
        } catch (Exception e) {
            flash("error", String.format("Can not get registry client : %s", e.getMessage()));
            return ok(index.render(null));
        }
    }

    public static Result synchronizeAll() {
        try {
            final RegistryAdministration registry = PetalsAdmin
                    .getRegistryAdministration(getCurrentNode());
            // TODO : Async
            try {
                registry.synchronizeRegistryOnAllNodes();
                ApplicationEvent.info("All registries have been synchronized");
            } catch (Exception e) {
                Logger.error("All Registries sync error", e);
                ApplicationEvent.warning("All Registries sync error : %s", e.getMessage());
            }
        } catch (Exception e) {
            Logger.error("All Registries sync error", e);
            flash("error", String.format("Unable to call synchronize '%s'", e.getMessage()));
        }
        flash("info", "Registry sync has been launched in the background...");
        return redirect(routes.EndpointsController.index());
    }

    public static Result synchronize() {
        try {
            final RegistryAdministration registry = PetalsAdmin
                    .getRegistryAdministration(getCurrentNode());
            // TODO : Async
            try {
                registry.synchronizeRegistry();
                ApplicationEvent.info("Registry Synchronized");
            } catch (Exception e) {
                ApplicationEvent.warning("Registry sync error : %s", e.getMessage());
                }
        } catch (Exception e) {
            Logger.error("Syn error", e);
            flash("error", String.format("Unable to call synchronize '%s'", e.getMessage()));
        }
        flash("info", "Registry sync has been launched in the background...");
        return index();
    }

    public static Result endpoint(String name) {
        flash("error", "Not implemented");
        return redirect(routes.EndpointsController.index());
    }
}