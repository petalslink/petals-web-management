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

/**
 * @author chamerling
 *
 */
@Entity
public class Node extends Model {

    @Id
    public Long id;

	public String host;

	public int port;

	public String login;

	public String password;

    public static Finder<String,Node> find = new Finder<String,Node>(
            String.class, Node.class
    );

    public static Node findById(Long id) {
        return find.byId("" + id);
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Node node = (Node) o;

        if (port != node.port) return false;
        if (host != null ? !host.equals(node.host) : node.host != null) return false;
        if (id != null ? !id.equals(node.id) : node.id != null) return false;
        if (login != null ? !login.equals(node.login) : node.login != null) return false;
        if (password != null ? !password.equals(node.password) : node.password != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + port;
        result = 31 * result + (login != null ? login.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
