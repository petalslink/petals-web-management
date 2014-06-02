/**
 * Copyright (c) 2013-2014 Linagora
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

import java.util.List;

import org.ow2.petals.admin.api.RegistryAdministration;
import org.ow2.petals.admin.registry.Endpoint;
import org.ow2.petals.admin.registry.RegistryView;

import play.mvc.Result;
import utils.PetalsAdmin;
import views.html.EndpointsController.index;
import controllers.actions.PetalsNodeSelected;


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
            RegistryView view = registry.getRegistryContent(".*", ".*", ".*", ".*");
            List<Endpoint> endpoints = view.getAllEndpoints();
            return ok(index.render(endpoints));
        } catch (Exception e) {
            flash("error", String.format("Can not get registry client : %s", e.getMessage()));
            return ok(index.render(null));
        }
    }

    public static Result endpoint(String name) {
        flash("error", "Not implemented");
        return redirect(routes.EndpointsController.index());
    }
}