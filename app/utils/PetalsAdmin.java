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
package utils;

import models.Node;

import org.ow2.petals.admin.api.ArtifactAdministration;
import org.ow2.petals.admin.api.ContainerAdministration;
import org.ow2.petals.admin.api.PetalsAdministrationFactory;
import org.ow2.petals.admin.api.RegistryAdministration;

/**
 * @author chamerling
 * 
 */
public class PetalsAdmin {

	/**
	 * 
	 */
	private PetalsAdmin() {
	}

	public static ContainerAdministration getContainerAdministration(Node node)
			throws Exception {
		try {
			// ... looks like we need to getContainerAdmin before to switch
			// connection to the right node...
			PetalsAdministrationFactory.newInstance().newContainerAdministration()
					.disconnect();
		} catch (Exception e) {
			// ignore if not already connected...
		}
		ContainerAdministration containerAdmin = PetalsAdministrationFactory
				.newInstance().newContainerAdministration();
		containerAdmin.connect(node.host, (int) node.port, node.login,
				node.password);
		return containerAdmin;
	}

	public static ArtifactAdministration getArtifactAdministration(Node node)
			throws Exception {
		getContainerAdministration(node);
		return PetalsAdministrationFactory.newInstance()
				.newArtifactAdministration();
	}

	/**
	 * @return
	 * 
	 */
	public static RegistryAdministration getRegistryAdministration(Node node)
			throws Exception {
		getContainerAdministration(node);
		return PetalsAdministrationFactory.newInstance()
				.newRegistryAdministration();
	}

}
