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
package controllers.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import models.Alert;
import models.Message;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import play.Logger;
import play.libs.Akka;
import play.libs.F;
import play.libs.Json;

import java.util.HashMap;
import java.util.Map;

/**
 * Akka Actor used to receive and publish messages to websockets
 *
 * @author Christophe Hamerling - chamerling@linagora.com
 */
public class WebSocket extends UntypedActor {

    /**
     * Creates a new actor in order to be able to publish messages from controllers.
     */
    static ActorRef actor = Akka.system().actorOf(new Props(WebSocket.class));

    /**
     * The registred clients.
     */
    Map<String, play.mvc.WebSocket.Out<JsonNode>> registrered = new HashMap<String, play.mvc.WebSocket.Out<JsonNode>>();

    /**
     *
     * @param id
     * @param in
     * @param out
     * @throws Exception
     */
    public static void register(final String id,
                                final play.mvc.WebSocket.In<JsonNode> in, final play.mvc.WebSocket.Out<JsonNode> out)
            throws Exception {

        actor.tell(new RegistrationMessage(id, out));

        // For each event received on the socket,
        in.onMessage(new F.Callback<JsonNode>() {
            @Override
            public void invoke(JsonNode event) {
                // nothing to do
            }
        });

        // When the socket is closed.
        in.onClose(new F.Callback0() {
            @Override
            public void invoke() {
                actor.tell(new UnregistrationMessage(id));
            }
        });
    }

    public static void message(Message message) {
        actor.tell(message);
    }

    public static void alert(Alert alert) {
        actor.tell(alert);
    }

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof RegistrationMessage) {

            // Received a Join message
            RegistrationMessage registration = (RegistrationMessage) message;
            Logger.info("Registering websocket client" + registration.id + "...");
            registrered.put(registration.id, registration.channel);

        } else if (message instanceof UnregistrationMessage) {

            UnregistrationMessage quit = (UnregistrationMessage) message;
            Logger.info("Unregistering websocket client" + quit.id + "...");
            registrered.remove(quit.id);

        } else if (message instanceof Alert) {

            Alert a = (Alert) message;
            ObjectNode event = Json.newObject();
            event.put("id", a.id);
            event.put("type", "alert");
            event.put("message", a.message);

            Logger.info("Publishing alert to websocket...");
            for (play.mvc.WebSocket.Out<JsonNode> channel : registrered.values()) {
                channel.write(event);
            }

        } else if (message instanceof Message) {

            Message m = (Message) message;
            ObjectNode event = Json.newObject();
            event.put("content", m.content);
            event.put("title", m.title);
            event.put("type", "message");

            Logger.info("Publishing message to websocket...");
            for (play.mvc.WebSocket.Out<JsonNode> channel : registrered.values()) {
                channel.write(event);
            }

        } else {
            unhandled(message);
        }
    }

    public static class RegistrationMessage {
        public String id;
        public play.mvc.WebSocket.Out<JsonNode> channel;

        public RegistrationMessage(String id, play.mvc.WebSocket.Out<JsonNode> channel) {
            super();
            this.id = id;
            this.channel = channel;
        }
    }

    public static class UnregistrationMessage {
        public String id;

        public UnregistrationMessage(String id) {
            super();
            this.id = id;
        }
    }
}
