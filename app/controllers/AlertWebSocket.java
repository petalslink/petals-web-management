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

import com.google.gson.Gson;
import models.Alert;
import play.libs.F;
import play.mvc.WebSocketController;

/**
 * Handle all alerts between client and server
 *
 * @author Christophe Hamerling - chamerling@linagora.com
 */
public class AlertWebSocket extends WebSocketController {

    public static F.EventStream<Alert> alertStream = new F.EventStream<Alert>();

    /**
     * Server side publish mechanism.
     *
     * @param alert
     */
    protected static void publish(Alert alert) {
        if (alert != null) {
            alertStream.publish(alert);
        }
        return;
    }

    /**
     * Client side Web socket channel.
     */
    public static void newAlert() {
        while (inbound.isOpen()) {
            Alert alert = await(alertStream.nextEvent());
            if (alert != null) {
                String json = new Gson().toJson(alert);
                try {
                    outbound.send(json);
                } catch (java.lang.IllegalStateException e) {
                    // NOP
                }
            }
        }
    }
}
