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

import models.BaseEvent;
import models.Node;
import play.Logger;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Scope.Session;
import utils.Constants;

import com.google.common.eventbus.EventBus;

/**
 * @author chamerling
 * 
 */
public class PetalsController extends Controller {

	private static EventBus bus;

	@Before(unless = { "bus" })
	private static void currentNode() {
		Logger.info("Getting current node");
		String currentNode = session.get(Constants.SESSION_CURRENT_NODE);
		if (currentNode != null) {
			// TODO : Get from cache if available...

			renderArgs.put(Constants.SESSION_CURRENT_NODE,
					Node.findById(Long.parseLong(currentNode)));
			Logger.info("Got it!");
		} else {
			Logger.info("Can not get node from session");
		}
	}

	public static Node getCurrentNode() {
		Long node = getCurrentNodeId();
		if (node == null) {
			flash.error("Please select a node");
			NodesController.index();
		}
		return Node.findById(getCurrentNodeId());
	}

	protected static Long getCurrentNodeId() {
		return (Session.current().get(Constants.SESSION_CURRENT_NODE) == null) ? null
				: Long.parseLong(Session.current().get(
						Constants.SESSION_CURRENT_NODE));
	}

	protected static void newEvent(BaseEvent event) {
		if (event == null) {
			return;
		}
		bus().post(event);
	}

	public static EventBus bus() {
		if (bus == null) {
			bus = new EventBus();
		}
		return bus;
	}

}
