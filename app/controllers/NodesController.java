/**
 * Copyright (c) 2013 Linagora
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
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSortedSet;
import controllers.actions.PetalsNodeSelected;
import controllers.forms.NodeCreate;
import models.Message;
import models.Node;
import models.Property;
import org.ow2.petals.admin.api.ContainerAdministration;
import org.ow2.petals.jmx.api.api.JMXClient;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import utils.*;

import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import views.html.NodesController.*;


/**
 * @author chamerling
 */
public class NodesController extends PetalsController {

    @PetalsNodeSelected(exclude=true)
	public static Result index() {
		List<Node> nodes = Node.find.all();
		return ok(index.render(nodes, play.data.Form.form(NodeCreate.class)));
	}

    @PetalsNodeSelected
    public static Result node(Long id) {
		Node mynode = Node.findById(id);
		String info = null;
		Set<Property> properties = null;
		Set<org.ow2.petals.admin.api.artifact.Logger> loggers = null;
        Set<Property> systemInfo = null;

		try {
			ContainerAdministration admin = PetalsAdmin
					.getContainerAdministration(mynode);
			info = admin.getSystemInfo();

			final Properties props = admin.getServerProperties();
			properties = ImmutableSortedSet
					.orderedBy(new Comparator<Property>() {
						public int compare(Property r1, Property r2) {
							return r1.name.compareToIgnoreCase(r2.name);
						}
					})
					.addAll(Collections2.transform(props.stringPropertyNames(),
							new Function<String, Property>() {
								public Property apply(String key) {
									return new Property(key, props
											.getProperty(key));
								}
							})).build();

			loggers = ImmutableSortedSet
					.orderedBy(
							new Comparator<org.ow2.petals.admin.api.artifact.Logger>() {
								public int compare(
										org.ow2.petals.admin.api.artifact.Logger r1,
										org.ow2.petals.admin.api.artifact.Logger r2) {
									return r1.getName().compareToIgnoreCase(
											r2.getName());
								}
							}).addAll(admin.getLoggers()).build();

            systemInfo = ImmutableSortedSet.orderedBy(new Comparator<Property>() {
                public int compare(Property r1, Property r2) {
                    return r1.name.compareToIgnoreCase(r2.name);
                }
            }).addAll(PetalsAdmin.systemInfo(mynode)).build();

			// TODO
			// Not available in release 4.1
			// Domain topology = admin.getTopology("foo");
		} catch (Exception e) {
			e.printStackTrace();
			flash("error", String.format("Error while connecting to node : %s", e.getMessage()));
            return ok();
		}
		return ok(node.render(mynode, info, properties, loggers, systemInfo));
	}

	/**
	 * Shutdown a container
	 * 
	 * @param id
	 */
    @PetalsNodeSelected
    public static final Result shutdown(Long id) {
		Node node = Node.findById(id);
		if (node == null) {
			flash("error", "Can not find node " + id);
			return index();
		}

		try {
			PetalsAdmin.getContainerAdministration(node).shutdownContainer();
		} catch (Exception e) {
			flash("error", String.format("Error while trying to shutdown node %s : %s", id,
                    e.getMessage()));
		}
		return index();
	}

	/**
	 * Stop a container
	 * 
	 * @param id
	 */
    @PetalsNodeSelected
    public static final Result stop(Long id) {
		Node node = Node.findById(id);
		if (node == null) {
			flash("error", String.format("Can not find node %s", id));
			return index();
		}

		try {
			PetalsAdmin.getContainerAdministration(node).stopContainer();
		} catch (Exception e) {
			flash("error", String.format("Error while trying to stop node %s : %s", id,
                    e.getMessage()));
		}
		return index();
	}

	/**
	 * Synchronize with the current node topology: Add nodes if they are not
	 * already registered in the local database.
	 * 
	 */
    @PetalsNodeSelected
    public static Result syncTopology() {
		flash("error", "SyncTopology not implemented");
		return index();
	}

    @PetalsNodeSelected(exclude = true)
    public static Result doCreateNode() {
        Form<NodeCreate> createForm = play.data.Form.form(NodeCreate.class).bindFromRequest();
        if(createForm.hasErrors()) {
            flash("error", "Bad parameters");
            return index();
        } else {
            Node node = new Node();
            node.host = createForm.get().host;
            node.port = createForm.get().port;
            node.login = createForm.get().login;
            node.password = createForm.get().password;
            node.save();
            ApplicationEvent.info("Node %s:%s has been created", node.host, node.port);
            flash("success", String.format("Node %s:%s has been registered", node.host, node.port));
            return redirect(routes.NodesController.index());
        }
	}

    @PetalsNodeSelected
    public static Result jsonSystemInfo() {
        Node node = getCurrentNode();

        try {
            return ok(play.libs.Json.toJson(PetalsAdmin.systemInfo(node)));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            Message m = new Message();
            m.type = "error";
            m.content = e.getMessage();
            return ok(play.libs.Json.toJson(m));
        }
    }

    /**
     * Check that the current node is reachable
     */
    @PetalsNodeSelected
    public static Result jsonCheckNode() {
        if (getCurrentNode() == null) {
            return ok(Json.toJson("Null"));
        }

        Node node = getCurrentNode();
        try {
            JMXClient client = JMXClientManager.get(node);
            client.getPetalsAdminServiceClient().ping();
        } catch (Exception e) {
            return ok(Json.toJson("Down"));
        }
        return ok("Up");
    }

}