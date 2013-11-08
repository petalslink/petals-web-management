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

import com.google.common.collect.Lists;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * @author chamerling
 * 
 */
@Entity
public class BaseEvent extends Model {

    @Id
    public Long id;

	public Date date;

    @Size(max=1024)
	public String message;

	public String type;

    public String getType() {
        return type;
    }

    /**
     * Emit the event to the client?
     */
    public boolean emit = true;

	public BaseEvent(String message, String type) {
		this.message = message;
		this.type = type;
		this.date = new Date();
	}

	public static List<BaseEvent> pasts() {
		return find.all();
	}

	public static BaseEvent event(String type, String pattern, Object... params) {
		return new BaseEvent(String.format(pattern, params), type);
	}

    public static Finder<String,BaseEvent> find = new Finder<String,BaseEvent>(
            String.class, BaseEvent.class
    );

    public static void deleteAll() {
        // TODO
    }

    /**
     * Post registration callback. Notifies the user about a new event
     */
    @PostPersist
    public void notifyUser() {
        //ApplicationEvent.live(this.message);
    }
}
