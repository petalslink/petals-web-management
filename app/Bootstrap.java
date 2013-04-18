/*
 * Copyright 2013 Linagora
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import models.BaseEvent;
import models.Node;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

import com.google.common.eventbus.EventBus;

import controllers.PetalsController;
import controllers.events.PushToWebSocketEventListener;
import controllers.events.StoreEventListener;

/**
 * Bootstrap some init data to avoid some useless test for the application long
 * life time
 * 
 * 
 */
@OnApplicationStart
public class Bootstrap extends Job {

	@Override
	public void doJob() throws Exception {

		// loading event listeners
		EventBus eventBus = PetalsController.bus();
		eventBus.register(new StoreEventListener());
		eventBus.register(new PushToWebSocketEventListener());

		if (Node.count() == 0) {
			Logger.info("Loading initial data");
			Fixtures.loadModels("initial-data.yml");
		}
		eventBus.post(new BaseEvent("Application started", "info"));
	}
}
