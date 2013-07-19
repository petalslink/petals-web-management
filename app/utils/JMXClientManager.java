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

import com.google.common.cache.*;
import models.Node;
import org.ow2.petals.admin.jmx.JMXClientConnection;
import org.ow2.petals.jmx.api.api.JMXClient;
import play.Logger;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Christophe Hamerling - chamerling@linagora.com
 */
public class JMXClientManager {

    private static LoadingCache<Node, JMXClient> clients = CacheBuilder.newBuilder()
            .maximumSize(1000).expireAfterAccess(60, TimeUnit.SECONDS).removalListener(new RemovalListener<Node, JMXClient>() {
                @Override
                public void onRemoval(RemovalNotification<Node, JMXClient> objectObjectRemovalNotification) {
                    Logger.info("JMXClient removed from cache : %s", objectObjectRemovalNotification.getKey().toString());
                }
            })
            .build(
                    new CacheLoader<Node, JMXClient>() {
                        public JMXClient load(Node node) throws Exception {
                            return JMXClientConnection.createJMXClient(node.host, node.port, node.login, node.password);
                        }
                    });

    private JMXClientManager() {
    }

    /**
     * Get a JMXClient from the client cache. Up to guava to instanciate it if needed.
     *
     * @param node
     * @return
     * @throws Exception
     */
    public static JMXClient get(Node node) throws Exception {
        try {
            return clients.get(node);
        } catch (ExecutionException e) {
            Logger.warn(e.getMessage());
            throw new Exception("Can not et JMX client", e);
        }
    }
}
