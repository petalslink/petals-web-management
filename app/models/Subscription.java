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
     * TODO : JPA; For now we keep the models unlinked since we do not want to remove subscriptions when removing nodes...
     */
    public Node node;

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

}
