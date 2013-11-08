/**
 * Copyright (c) 2013 Linagora
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
package controllers.actions;

import play.Logger;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import utils.Constants;

/**
 * @author Christophe Hamerling - chamerling@linagora.com
 */
public class NodeAction extends Action<PetalsNodeSelected> {

    @Override
    public Result call(Http.Context context) throws Throwable {

        // Do not check not selection stuff
        if (configuration.exclude()) {
            Logger.debug("Exclude connect checking");
            return delegate.call(context);
        }

        if (context.session().get(Constants.SESSION_CURRENT_NODE) == null) {
            Logger.debug("Not connected, redirect to /nodes");
            context.flash().put("error", "You are not connected to any Petals node, please select one");
            return redirect("/nodes");
        } else {
            Logger.debug("Connected to node " + context.session().get(Constants.SESSION_CURRENT_NODE));
            return delegate.call(context);
        }
    }
}
