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
import models.Message;
import play.Logger;
import play.libs.F.EventStream;
import play.mvc.WebSocketController;

/**
 * @author chamerling
 *
 */
public class WebSocket extends WebSocketController {
	
	public static EventStream<Message> liveStream = new EventStream<Message>();

	public static void newMessage() {
		while (inbound.isOpen()) {
			Message message = await(liveStream.nextEvent());
			if (message != null) {
				String json = new Gson().toJson(message);
                try {
				    outbound.send(json);
                } catch (IllegalStateException e) {
                    Logger.debug("Websocket error", e);
                }
			}
		}
	}
}
