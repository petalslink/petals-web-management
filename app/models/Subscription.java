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
package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

/**
 * A monitoring/alarm subscription
 *
 * @author Christophe Hamerling - chamerling@linagora.com
 */
@Entity
public class Subscription extends Model {

    @Id
    public Long id;

    /**
     * Target subscription
     */
    public String component;

    /**
     * Component type may be useful...
     */
    public String componentType;

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

    public static Finder<String,Subscription> find = new Finder<String,Subscription>(
            String.class, Subscription.class
    );

    /**
     * Get all the subscription for a component
     *
     * @param component
     * @return
     */
    public static List<Subscription> subscriptions(String component, String componentType) {
        return find.where().ieq("component", component).ieq("componentType", componentType).findList();
    }

    /**
     * Get all subscriptions for a component and a node
     *
     * @param component
     * @return
     */
    public static List<Subscription> subscriptions(String component, String componentType, Node node) {
        return find.where().ieq("component", component).ieq("componentType", componentType).ieq("host", node.host).ieq("port", "" + node.port).findList();
    }

    /**
     * Subscriptions for a given node
     *
     * @param node
     * @return
     */
    public static List<Subscription> subscriptions(Node node) {
        return find.where().ieq("host", node.host).ieq("port", "" + node.port).findList();
    }

    public static Subscription findById(Long id) {
        return find.byId(id.toString());
    }

    public static void delete(Long id) {
        find.byId(id.toString()).delete();
    }
}
