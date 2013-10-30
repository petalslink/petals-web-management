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
import play.mvc.Controller;
import play.mvc.Http;
import utils.Constants;

/**
 * @author chamerling
 * 
 */
public class PetalsController extends Controller {

    /**
     * Get the current node from the session or redirect to the node selection page
     * @return
     */
	public static Node getCurrentNode() {
		Long node = getCurrentNodeId();
		if (node == null) {
			flash("error", "Not connected, please select a node");
            // TODO : Redirect to the /nodes page
			//redirect("/nodes");
		}
		return Node.findById(getCurrentNodeId());
	}

	protected static Long getCurrentNodeId() {
		return (session(Constants.SESSION_CURRENT_NODE) == null) ? null
				: Long.parseLong(session(Constants.SESSION_CURRENT_NODE));
	}
}
