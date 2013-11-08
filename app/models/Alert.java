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
package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

/**
 * @author Christophe Hamerling - chamerling@linagora.com
 */
@Entity
public class Alert extends Model {

    @Id
    public Long id;

    /**
     * Alert has been read?
     */
    public boolean read = false;

    /**
     * Date the app received the alert
     */
    public Date receivedAt;

    /**
     * Who delivered the alert
     */
    public String source;

    /**
     * Attached message
     */
    public String message;

    /**
     * An alert may have a sequence number. This is useful to order alerts...
     */
    public long sequenceNb;

    /**
     * Type of alert
     */
    public String type;

    public static Finder<String,Alert> find = new Finder<String,Alert>(
            String.class, Alert.class
    );

    /**
     * Get the unread alerts ordered by date
     */
    public static List<Alert> unread() {
        return find.where().ieq("read", "false").orderBy("receivedAt desc").findList();
    }

    public static void readAll() {
        //JPA.em().createQuery("Update Alert set read = true").executeUpdate();
        // TODO
        return;
    }

    /**
     * The the number of unread alerts
     *
     * @return
     */
    public static long unreadCount() {
        return find.where().ieq("read", "false").findRowCount();
    }

    public static void deleteAll() {
        //TODO
    }

    @Override
    public String toString() {
        return "Alert{" +
                "read=" + read +
                ", receivedAt=" + receivedAt +
                ", source='" + source + '\'' +
                ", message='" + message + '\'' +
                ", sequenceNb=" + sequenceNb +
                ", type='" + type + '\'' +
                '}';
    }
}
