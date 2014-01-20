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
package monitoring;

import models.Node;
import models.Subscription;
import utils.ApplicationEvent;
import utils.JMXClientManager;

import java.util.Date;

/**
 * @author Christophe Hamerling - chamerling@linagora.com
 */
public class MonitoringManager {

    private MonitoringManager() {
    }

    /**
     * Subscribe to a component notification running on a node
     *
     * @param node
     * @param component
     * @return
     */
    public static Subscription subscribe(final Node node, final String component, final String componentType, final AlertListener listener) throws MonitoringException {
        Subscription result = null;

        if (node == null || component == null) {
            throw new MonitoringException("Can not subscribe to null references");
        }

        try {
            JMXClientManager.get(node).subscribeToComponentMonitoringService(component, new monitoring.DefectListener(component, listener), null);
        } catch (Exception e) {
            ApplicationEvent.warning("Can not subscribe to component %s@%s:%s", component, node.host, "" + node.port);
            throw new MonitoringException("Problem while subscribing", e);
        }

        ApplicationEvent.info("Subscribed to component %s", component);

        result = new Subscription();
        result.date = new Date();
        result.component = component;
        result.componentType = componentType;
        result.host = node.host;
        result.port = node.port;
        result.login = node.login;
        result.password = node.password;
        result.status = "Active";
        result.save();
        return result;
    }

    /**
     * @param subscription
     */
    public static void unsubscribe(Subscription subscription) throws MonitoringException {

        if (subscription == null || subscription.component == null) {
            throw new MonitoringException("Can not unsubscribe from null subscription");
        }

        Node node = new Node();
        node.host = subscription.host;
        node.port = subscription.port;
        node.login = subscription.login;
        node.password = subscription.password;

        try {
            JMXClientManager.get(node).unsubscribeToComponentMonitoringService(subscription.component);
        } catch (Exception e) {
            throw new MonitoringException(e);
        }
    }
}
