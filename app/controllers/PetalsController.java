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

import models.Node;
import play.mvc.Controller;
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
