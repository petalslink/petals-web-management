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
package utils;

import com.google.common.collect.Sets;
import models.Node;
import models.Property;
import org.ow2.petals.admin.api.ArtifactAdministration;
import org.ow2.petals.admin.api.ContainerAdministration;
import org.ow2.petals.admin.api.PetalsAdministrationFactory;
import org.ow2.petals.admin.api.RegistryAdministration;
import org.ow2.petals.jmx.api.api.SystemMonitoringServiceClient;

import java.util.Set;

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

    /**
     * Return system info if possible
     *
     * @param node
     * @return
     * @throws Exception
     */
    public static Set<Property> systemInfo(Node node) throws Exception {
        SystemMonitoringServiceClient client = JMXClientManager.get(node).getSystemMonitoringServiceClient();
        if (client == null) {
            return Sets.newHashSet();
        }
        Set<Property> info = Sets.newHashSet();
        info.add(new Property("Available processors", "" + client.getAvailableProcessors()));
        info.add(new Property("Committed Virtual Memory Size", "" + client.getCommittedVirtualMemorySize()));
        info.add(new Property("Daemon Thread Count", "" + client.getDaemonThreadCount()));
        info.add(new Property("Free Physical Memory Size", "" + client.getFreePhysicalMemorySize()));
        //info.add(new Property("Heap Memory Usage", "" + client.getHeapMemoryUsage()));
        info.add(new Property("Loaded Class Count", "" + client.getLoadedClassCount()));
        info.add(new Property("Peak Thread Count", "" + client.getPeakThreadCount()));
        info.add(new Property("Pending Objects", "" + client.getPendingObjects()));
        info.add(new Property("Process CPU Time", "" + client.getProcessCpuTime()));
        info.add(new Property("Thread Count", "" + client.getThreadCount()));
        info.add(new Property("Total Loaded Class Count", "" + client.getTotalLoadedClassCount()));
        info.add(new Property("Total Physical Memory Size", "" + client.getTotalPhysicalMemorySize()));
        info.add(new Property("Total Started ThreadCount", "" + client.getTotalStartedThreadCount()));
        info.add(new Property("Unloaded Class Count", "" + client.getUnloadedClassCount()));
        info.add(new Property("Uptime", "" + client.getUptime()));
        return info;
    }
}
