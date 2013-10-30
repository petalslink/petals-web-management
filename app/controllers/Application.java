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

import java.util.List;

import controllers.actions.PetalsNodeSelected;
import models.BaseEvent;
import play.mvc.Result;
import views.html.Application.*;


/**
 * @author chamerling
 */
@PetalsNodeSelected
public class Application extends PetalsController {
	
	public static Result index() {
		List<BaseEvent> events = BaseEvent.pasts();
		return ok(index.render(events));
	}

    /**
     * Clear all the events from the index page
     */
    public static Result clearEvents() {
        BaseEvent.deleteAll();
        return index();
    }

}