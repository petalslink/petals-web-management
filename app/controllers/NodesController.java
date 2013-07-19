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

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSortedSet;
import models.Message;
import models.Node;
import models.Property;
import org.ow2.petals.admin.api.ContainerAdministration;
import org.ow2.petals.jmx.api.api.JMXClient;
import play.Logger;
import play.data.validation.Required;
import play.mvc.Scope.Session;
import utils.*;
import utils.Check;

import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author chamerling
 */
public class NodesController extends PetalsController {

	public static void index() {
		List<Node> nodes = Node.all().fetch();
		render(nodes);
	}

	public static void node(Long id) {
		Node node = Node.findById(id);
		String info = null;
		Set<Property> properties = null;
		Set<org.ow2.petals.admin.api.artifact.Logger> loggers = null;
        Set<Property> systemInfo = null;

		try {
			ContainerAdministration admin = PetalsAdmin
					.getContainerAdministration(node);
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
            }).addAll(PetalsAdmin.systemInfo(node)).build();

			// TODO
			// Not available in release 4.1
			// Domain topology = admin.getTopology("foo");
		} catch (Exception e) {
			e.printStackTrace();
			flash.error("Error while connecting to node : %s", e.getMessage());
		}
		render(node, info, properties, loggers, systemInfo);
	}

	public static void connect(long id) {
		Node node = Node.findById(id);
		if (node == null) {
			flash.error("Can not find node %s", id);
			index();
		}

		// try to connect for real...
		try {
			PetalsAdmin.getContainerAdministration(node);
			Logger.info("Connected to node %s", id);
			Session.current().put(Constants.SESSION_CURRENT_NODE, "" + id);
		} catch (Exception e) {
			flash.error("Can not connect to node %s : %s", id, e.getMessage());
			Logger.error(e, "Error while trying to connect to node %s", id);
			index();
		}
        ApplicationEvent.info("Connected to node %d : %s:%s", id, node.host, node.port);
		flash.success("Connected to node %s : %s:%s", id, node.host, node.port);
		index();
	}

	/**
	 * Shutdown a container
	 * 
	 * @param id
	 */
	public static final void shutdown(Long id) {
		Node node = Node.findById(id);
		if (node == null) {
			flash.error("Can not find node %s", id);
			index();
		}

		try {
			PetalsAdmin.getContainerAdministration(node).shutdownContainer();
		} catch (Exception e) {
			flash.error("Error while trying to shutdown node %s : %s", id,
					e.getMessage());
		}
		index();
	}

	/**
	 * Stop a container
	 * 
	 * @param id
	 */
	public static final void stop(Long id) {
		Node node = Node.findById(id);
		if (node == null) {
			flash.error("Can not find node %s", id);
			index();
		}

		try {
			PetalsAdmin.getContainerAdministration(node).stopContainer();
		} catch (Exception e) {
			flash.error("Error while trying to stop node %s : %s", id,
					e.getMessage());
		}
		index();
	}

	/**
	 * Disconnect from current node
	 * 
	 */
	public static void disconnect() {
		String node = session.get(Constants.SESSION_CURRENT_NODE);
		Session.current().remove(Constants.SESSION_CURRENT_NODE);
		flash.success("Disconnected from node %s", node);
        ApplicationEvent.info("Disconnected from node %s", node);
		index();
	}

	/**
	 * Synchronize with the current node topology: Add nodes if they are not
	 * already registered in the local database.
	 * 
	 */
	public static void syncTopology() {
		flash.error("SyncTopology not implemented");
		index();
	}

	public static void doCreateNode(
			@Required(message = "Host is required") String host,
			@Required(message = "Port is required") String port, String login,
			String password) {

		validation.required(host);
		validation.required(port);
		validation.isTrue(port != null && Check.isInteger(port)
				&& Integer.parseInt(port) > 1);

		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			index();
		}

		Node node = new Node();
		node.host = host;
		node.port = Integer.parseInt(port);
		node.login = login;
		node.password = password;

		node.save();
		ApplicationEvent.info("Node %s:%s has been created", host, port);
		flash.success("Node %s:%s has been registered", host, port);
		index();
	}

    public static void jsonSystemInfo() {
        Node node = getCurrentNode();

        try {
            renderJSON(PetalsAdmin.systemInfo(node));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            Message m = new Message();
            m.type = "error";
            m.content = e.getMessage();
            renderJSON(m);
        }
    }

    /**
     * Check that the current node is reachable
     */
    public static void jsonCheckNode() {
        if (getCurrentNode() == null) {
            renderJSON("null");
        }

        Node node = getCurrentNode();
        try {
            JMXClient client = JMXClientManager.get(node);
            client.getPetalsAdminServiceClient().ping();
        } catch (Exception e) {
            renderJSON("down");
        }
        renderJSON("up");
    }

}