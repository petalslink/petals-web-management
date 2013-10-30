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

import models.Node;
import play.Logger;
import play.mvc.Result;
import utils.ApplicationEvent;
import utils.Constants;
import utils.PetalsAdmin;

/**
 * @author Christophe Hamerling - chamerling@linagora.com
 */
public class ConnectController extends PetalsController {

    public static Result connect(long id) {

        // this one should not redirect to /nodes...

        System.out.println("Connect to node " + id);
        Node node = Node.findById(id);
        if (node == null) {
            flash("error", "Can not find node %s" + id);
            return redirect("/nodes");
        }

        // try to connect for real...
        try {
            PetalsAdmin.getContainerAdministration(node);
            Logger.info("Connected to node " + id);
            session(Constants.SESSION_CURRENT_NODE, "" + id);
        } catch (Exception e) {
            flash("error", String.format("Can not connect to node %s : %s", id, e.getMessage()));
            Logger.error("Error while trying to connect to node" + id, e);
            return redirect("/nodes");
        }
        ApplicationEvent.info("Connected to node %d : %s:%s", id, node.host, node.port);
        flash("success", String.format("Connected to node %s : %s:%s", id, node.host, node.port));
        return redirect("/nodes");
    }

    /**
     * Disconnect from current node
     *
     */
    public static Result disconnect() {
        String node = session(Constants.SESSION_CURRENT_NODE);
        session().remove(Constants.SESSION_CURRENT_NODE);
        flash("success", String.format("Disconnected from node %s", node));
        ApplicationEvent.info("Disconnected from node %s", node);
        return redirect("/nodes");
    }
}
