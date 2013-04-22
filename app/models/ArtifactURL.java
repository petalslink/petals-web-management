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

import play.db.jpa.Model;

/**
 * Manage remote artifacts URLs
 * 
 * @author chamerling
 * 
 */
@Entity
public class ArtifactURL extends Model {

	public String name;

	public String version;

	public String url;

	public Date date;

	/**
	 * Local artifact (stored in the artifacts folder)
	 */
	public boolean local;

	public static List<ArtifactURL> byName() {
		return ArtifactURL.find("order by name desc").fetch();
	}

	public static List<ArtifactURL> locals() {
		return ArtifactURL.find("local=true").fetch();
	}

	public static boolean localExists(String url) {
		return ArtifactURL.count("local=true and url like ?", url) > 0;
	}

}
