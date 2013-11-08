/**
 * Copyright (c) 2013 Linagora
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

import models.Message;
import org.codehaus.jackson.JsonNode;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;

/**
 * @author Christophe Hamerling - chamerling@linagora.com
 */
public class Websocket extends Controller {

    public static WebSocket<JsonNode> messageSocket() {
        return new WebSocket<JsonNode>() {

            // Called when the Websocket Handshake is done.
            @Override
            public void onReady(final WebSocket.In<JsonNode> in,
                                final WebSocket.Out<JsonNode> out) {
                try {
                    controllers.actors.WebSocket.register(java.util.UUID.randomUUID().toString(),
                            in, out);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }

    public static Result ping() {
        Message m = new Message();
        m.type = "message";
        m.title = "hey";
        m.content = "Test WS";
        controllers.actors.WebSocket.message(m);
        return ok();
    }
}
