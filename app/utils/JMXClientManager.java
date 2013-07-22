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
import org.ow2.petals.admin.jmx.JMXClientConnection;
import org.ow2.petals.jmx.api.api.JMXClient;

/**
 * @author Christophe Hamerling - chamerling@linagora.com
 */
public class JMXClientManager {

    private JMXClientManager() {
    }

    /**
     * Instanciate a new client.
     * Put them in cache is not allowed since connections may be closed by the JMX server or client without any easy way to detect.
     *
     * @param node
     * @return
     * @throws Exception
     */
    public static JMXClient get(Node node) throws Exception {
        return JMXClientConnection.createJMXClient(node.host, node.port, node.login, node.password);
    }
}
