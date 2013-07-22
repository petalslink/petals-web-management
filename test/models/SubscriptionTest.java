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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.test.UnitTest;

/**
 * @author Christophe Hamerling - chamerling@linagora.com
 */
public class SubscriptionTest extends UnitTest {

    @Before
    @After
    public void empty() {
        Subscription.deleteAll();
        Node.deleteAll();
    }

    @Test
    public void saveNode() {
        Node n = new Node();
        n.host = "hhh";
        n.save();
    }

    @Test
    public void checkGet1ForComponent() {
        Subscription s = new Subscription();
        s.status = "";
        s.component = "componentA";
        s.save();
        assertEquals(1, Subscription.subscriptions(s.component).size());
    }

    @Test
    public void checkGet2ForComponent() {
        Subscription s = new Subscription();
        s.component = "componentA";
        s.save();
        Subscription ss = new Subscription();
        ss.component = "componentA";
        ss.save();
        Subscription sss = new Subscription();
        sss.component = "componentB";
        sss.save();
        assertEquals(2, Subscription.subscriptions(s.component).size());
    }

    @Test
    public void checkGetForComponentAndNode() {
        Node n = new Node();
        n.host = "locahost";
        n.port = 7700;

        Subscription s = new Subscription();
        s.component = "componentA";
        s.host = n.host;
        s.port = n.port;
        s.save();

        Subscription ss = new Subscription();
        ss.component = "componentA";
        ss.save();

        assertEquals(1, Subscription.subscriptions(s.component, n).size());
    }

}
