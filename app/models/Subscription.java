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
package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import java.util.Date;
import java.util.List;

/**
 * A monitoring/alarm subscription
 *
 * @author Christophe Hamerling - chamerling@linagora.com
 */
@Entity
public class Subscription extends Model {

    /**
     * Target subscription
     */
    public String component;

    /**
     * Remote node properties.
     * Do not want to link Node since we may want to have 'unlinked' registrations...
     */
    public String host;
    public int port;
    public String login;
    public String password;

    /**
     * Subscription date
     */
    public Date date;

    /**
     * Keep the subscription in the database but update its status when needed...
     */
    public String status = "";

    /**
     *
     */
    public Date unsubscribedAt;

    /**
     * Get all the subscription for a component
     *
     * @param component
     * @return
     */
    public static List<Subscription> subscriptions(String component) {
        return Subscription.find("component like ?", component).fetch();
    }

    /**
     * Get all subscriptions for a component and a node
     *
     * @param component
     * @return
     */
    public static List<Subscription> subscriptions(String component, Node node) {
        return Subscription.find("component like ? and host like ? and port like ?", component, node.host, node.port).fetch();
    }

    /**
     * Subscriptions for a given node
     *
     * @param node
     * @return
     */
    public static List<Subscription> subscriptions(Node node) {
        return Subscription.find("host like ? and port like ?", node.host, node.port).fetch();
    }

}
