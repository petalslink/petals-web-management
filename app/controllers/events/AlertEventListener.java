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
import controllers.actors.WebSocket;
import models.Alert;

/**
 * React on alert events.
 *
 * @author Christophe Hamerling - chamerling@linagora.com
 */
public class AlertEventListener {

    @Subscribe
    public void alert(Alert alert) {
        if (alert != null) {
            WebSocket.alert(alert);
        }
    }
}
