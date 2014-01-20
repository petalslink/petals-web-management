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
package controllers;

import controllers.actions.PetalsNodeSelected;
import models.Alert;
import models.Node;
import models.Subscription;
import monitoring.DefaultAlertListener;
import monitoring.MonitoringException;
import monitoring.MonitoringManager;
import org.codehaus.jackson.node.ObjectNode;
import play.Logger;
import play.mvc.Result;
import play.data.validation.Constraints;
import play.libs.Json;
import play.mvc.BodyParser;
import utils.ApplicationEvent;

import java.util.Date;
import java.util.List;

import views.html.MonitoringController.*;


/**
 * Monitoring Controller
 *
 * @author Christophe Hamerling - chamerling@linagora.com
 */
@PetalsNodeSelected
public class MonitoringController extends PetalsController {

    /**
     * Default monitoring page
     */
    public static Result index() {
        List<Alert> alerts = Alert.unread();
        return ok(index.render(alerts));
    }

    /**
     * Get the current node subscriptions
     */
    public static Result subscriptions() {
        List<Subscription> s = Subscription.subscriptions(getCurrentNode());
        return ok(subscriptions.render(s));
    }

    /**
     * Subscribe to 'real time' notifications. User needs to be connected to the node to subscribe.
     *
     * @param component
     */
    public static Result subscribe(String component, String tyype) {
        Node node = getCurrentNode();

        // TODO : Validate that component is not null
        if (component == null) {
            flash("error", "Component name can not be null");
            return redirect(routes.ArtifactsController.component(component, tyype));
        }

        try {
            Subscription subscription = MonitoringManager.subscribe(node, component, tyype, new DefaultAlertListener());
            flash("success", String.format("Subscribed to component %s:%s", component, tyype));
        } catch (Exception e) {
            final String message = e.getMessage();
            Logger.error(String.format("Error while subscribing to component %s : %s", component, message), e);
            flash("error", String.format("Error while subscribing to component %s : %s", component, message));
            return subscriptions();
        }

        return subscriptions();
    }

    /**
     * Unsubscribe
     *
     * @param id
     */
    public static Result unsubscribe(Long id) {

        final Subscription s = Subscription.findById(id);
        if (s == null) {
            flash("error", "Can not find subscription");
            return subscriptions();
        }
        s.delete();

        // TODO : Async
        try {
            MonitoringManager.unsubscribe(s);
            ApplicationEvent.live("Unsubcribed from %s", s.component);
        } catch (MonitoringException e) {
            ApplicationEvent.warning("Error while unsubscribing from %s@%s:%s", s.component, s.host, "" + s.port);
        }
        flash("success", String.format("Unsubscribing from %s@%s:%s...", s.component, s.host, "" + s.port));
        return subscriptions();
    }

    /**
     * Force to delete subscription without any interaction with the remote runtimes
     *
     * @param id
     */
    public static Result delete(Long id) {
        Subscription.delete(id);
        flash("success", "Subscription removed");
        return subscriptions();
    }

    /**
     * Fake to add alert to the list...
     */
    public static Result addFakeAlert() {
        Alert a = new Alert();
        a.message = "Problem on the SOAP component";
        a.read = false;
        a.receivedAt = new Date();
        a.source = "petals/1/petals-bc-soap";

        a.save();
        ApplicationEvent.info("New alert has been raised : %s", a.message);

        //
        // TODO
        System.out.println("TODO : Push to WS alert");
        //AlertWebSocket.alertStream.publish(a);
        return ok(Json.toJson(a));
    }

    /**
     * Fake to add subscription to the list...
     */
    public static Result addFakeSubscription() {
        Subscription s = new Subscription();

        s.date = new Date();
        s.component = "petals-bc-soap";
        s.host = "localhost";
        s.port = 7700;
        s.status = "Active";
        s.save();
        ApplicationEvent.info("New subscription to %s@%s:%s", s.component, s.host, "" + s.port);

        return ok(Json.toJson(s));
    }

    /**
     * Get the unread alerts as JSON
     */
    @BodyParser.Of(BodyParser.Json.class)
    public static Result jsonUnreadAlerts() {
        ObjectNode result = Json.newObject();
        result.put("nb", Alert.unreadCount());
        return ok(result);
    }

    /**
     * GET
     */
    public static Result markAllAsRead() {
        Alert.readAll();
        return index();
    }

    /**
     * GET
     */
    public static Result clearAll() {
        Alert.deleteAll();
        return index();
    }
}
