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
package monitoring;

import controllers.actors.WebSocket;
import models.Alert;
import play.Logger;
import utils.ApplicationEvent;

/**
 * Local alert receiver. Used to centralize all the stuff to do when an alert is received.
 *
 * @author Christophe Hamerling - chamerling@linagora.com
 */
public class DefaultAlertListener implements AlertListener {

    @Override
    public void handle(Alert alert) {
        Logger.debug("Handle alert");
        if (alert == null) {
            return;
        }

        try {
            alert.save();
        } catch (Exception e) {
        }
        ApplicationEvent.info("New alert has been received : %s", alert.message);
        WebSocket.alert(alert);
    }
}
