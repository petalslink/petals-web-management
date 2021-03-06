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
package monitoring;

import models.Alert;
import org.ow2.petals.jmx.api.api.monitoring.Defect;
import play.Logger;

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
        Logger.debug("Got a defect from component " + component);
        Alert a = new Alert();
        a.message = defect.getMessage();
        a.source = defect.getEmitter();
        a.type = defect.getType();
        a.receivedAt = new Date();
        a.sequenceNb = defect.getSequenceNumber();
        a.read = false;
        if(listener != null) {
            try {
                listener.handle(a);
            } catch (MonitoringException e) {
                e.printStackTrace();
            }
        }
    }
}
