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

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

/**
 * Manage remote artifacts URLs
 * 
 * @author chamerling
 * 
 */
@Entity
public class ArtifactURL extends Model {

    @Id
    public Long id;

	public String name;

	public String version;

	public String url;

	public Date date;

    public static Finder<String,ArtifactURL> find = new Finder<String,ArtifactURL>(
            String.class, ArtifactURL.class
    );

	/**
	 * Local artifact (stored in the artifacts folder)
	 */
	public boolean local;

	public static List<ArtifactURL> byName() {
		return find.orderBy("name").findList();
	}

	public static List<ArtifactURL> locals() {
		return find.where().ieq("local", "true").findList();
	}

	public static boolean localExists(String url) {
		return find.where().ieq("local", "true").ieq("url", url).findRowCount() > 0;
	}

    public static ArtifactURL findById(Long id) {
        return find.byId("" + id);
    }
}
