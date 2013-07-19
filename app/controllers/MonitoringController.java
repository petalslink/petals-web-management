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

import models.Alert;
import models.Node;
import models.Subscription;
import monitoring.MonitoringException;
import monitoring.MonitoringManager;
import play.jobs.Job;
import utils.ApplicationEvent;

import java.util.Date;
import java.util.List;

/**
 * Monitoring Controller
 *
 * @author Christophe Hamerling - chamerling@linagora.com
 */
public class MonitoringController extends PetalsController {

    /**
     * Default monitoring page
     */
    public static void index() {
        List<Alert> alerts = Alert.unread();
        render(alerts);
    }

    /**
     *
     */
    public static void subscriptions() {
        List<Subscription> subscriptions = Subscription.find("order by date desc").fetch();
        render(subscriptions);
    }

    /**
     * Subscribe to 'real time' notifications
     *
     * @param component
     * @param container
     */
    public static void subscribe(String component, String container) {
        subscriptions();
    }

    /**
     * Unsubscribe
     *
     * @param id
     */
    public static void unsubscribe(Long id) {

        final Subscription s = Subscription.findById(id);
        if (s == null) {
            flash.error("Can not find subscription");
            subscriptions();
        }

        // let's do it async...
        new Job() {
            @Override
            public void doJob() throws Exception {
                Thread.sleep(3000);
                try {
                    MonitoringManager.unsubscribe(s);
                    ApplicationEvent.live("Unsubcribed from %s", s.component);
                } catch (MonitoringException e) {
                    ApplicationEvent.warning("Error while unsubscribing from %s@%s", s.component, s.node.host);
                }
            }
        }.now();

        flash.success("Unsubscribing from %s@%s...", s.component, s.node.host);
        subscriptions();
    }

    /**
     * Force to delete subscription without any interaction with the remote runtimes
     *
     * @param id
     */
    public static void delete(Long id) {
        Subscription.delete("byId", id);

        final Subscription s = Subscription.findById(id);
        if (s == null) {
            flash.error("Can not find subscription");
            subscriptions();
        }

        // let's do it async...
        new Job() {
            @Override
            public void doJob() throws Exception {
                Thread.sleep(3000);
                try {
                    MonitoringManager.unsubscribe(s);
                    ApplicationEvent.live("Unsubcribed from %s", s.component);
                } catch (MonitoringException e) {
                    ApplicationEvent.warning("Error while unsubscribing from %s@%s", s.component, s.node.host);
                }
            }
        }.now();

        flash.success("Unsubscribing from %s@%s...", s.component, s.node.host);
        subscriptions();
    }

    /**
     * Fake to add alert to the list...
     */
    public static void addFakeAlert() {
        Alert a = new Alert();
        a.message = "Problem on the SOAP component";
        a.read = false;
        a.receivedAt = new Date();
        a.source = "petals/1/petals-bc-soap";

        a = a.save();
        ApplicationEvent.info("New alert has been raised : %s", a.message);
        AlertWebSocket.alertStream.publish(a);

        renderJSON(a);
    }

    /**
     * Fake to add subscription to the list...
     */
    public static void addFakeSubscription() {
        Subscription s = new Subscription();
        Node node = new Node();
        node.host = "localhost";
        node.port = 88976;

        s.date = new Date();
        s.component = "petals-bc-soap";
        s.node = node;
        s.status = "Active";
        s = s.save();
        ApplicationEvent.info("New subscription to %s@%s", s.component, s.node.host);

        renderJSON(s);
    }

    /**
     * Get the unread alerts as JSON
     */
    public static void jsonUnreadAlerts() {
        renderJSON(Alert.unreadCount());
    }

    /**
     * GET
     */
    public static void markAllAsRead() {
        Alert.readAll();
        index();
    }

    /**
     * GET
     */
    public static void clearAll() {
        Alert.deleteAll();
        index();
    }
}
