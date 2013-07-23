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
package monitoring;

import models.Alert;
import org.ow2.petals.jmx.api.api.monitoring.Defect;
import play.Logger;
import play.jobs.Job;

import java.util.Date;

/**
 * A defect listener which uses Play! Job. This is mandatory to be in the same thread and avoid initialization and context issues.
 *
 * @author Christophe Hamerling - chamerling@linagora.com
 */
public class DefectListener implements org.ow2.petals.jmx.api.api.monitoring.DefectListener {

    private final String component;

    private final AlertListener listener;

    /**
     *
     * @param component
     * @param listener
     */
    public DefectListener(String component, AlertListener listener) {
        this.component = component;
        this.listener = listener;
    }

    @Override
    public void onDefect(final Defect defect) {
        Logger.debug("Got a defect from component %s!", component);

        // Need to create a play JOB to run in the right thread
        // ie JMX listeners runs in their own threads
        Job job = new Job() {
            @Override
            public void doJob() throws Exception {
                Alert a = new Alert();
                a.message = defect.getMessage();
                a.source = defect.getEmitter();
                a.type = defect.getType();
                a.receivedAt = new Date();
                a.sequenceNb = defect.getSequenceNumber();
                a.read = false;
                // TODO : Handle user data... defect.getUserData()
                if (listener != null) {
                    listener.handle(a);
                }
            }
        };
        job.now();
    }
}
