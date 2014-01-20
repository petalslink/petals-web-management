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
package controllers.events;

import com.google.common.eventbus.Subscribe;
import models.BaseEvent;
import play.Logger;

/**
 * Listen to user activities and store them...
 * 
 * @author chamerling
 * 
 */
public class StoreEventListener {

    /**
     * Store the base event when published in the event bus
     *
     * @param event
     */
	@Subscribe
	public void store(BaseEvent event) {
		if (event == null) {
			return;
		}
		Logger.debug(String.format("New event to store %s", event.message));
		event.save();
	}
}
