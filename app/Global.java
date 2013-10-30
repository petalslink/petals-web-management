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

import com.avaje.ebean.Ebean;
import controllers.ArtifactsController;
import controllers.events.AlertEventListener;
import controllers.events.PushToWebSocketEventListener;
import controllers.events.StoreEventListener;
import models.ArtifactURL;
import models.Node;
import org.apache.commons.io.FileUtils;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Yaml;
import utils.ApplicationEvent;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Global some init data to avoid some useless test for the application long
 * life time
 * 
 * 
 */
public class Global extends GlobalSettings {

    @Override
    public void onStart(Application application) {
        System.out.println("Starting the Petals Web Console...");
        ApplicationEvent.register(new StoreEventListener(), new PushToWebSocketEventListener(), new AlertEventListener());

		if (Node.find.findRowCount() == 0) {
			Logger.info("Loading initial data");
            Map<String, List<Object>> all = (Map<String, List<Object>>)Yaml.load("initial-data.yml");
            System.out.println(all);
            Ebean.save(all.get("nodes"));
		}

        // registering files from artifacts folder...

        Logger.info("Creating required folders");
        File readme = new File(ArtifactsController.getArtifactsFolder(),
                "README.md");
        if (!readme.exists()) {
            try {
                FileUtils.touch(readme);
                FileUtils.writeStringToFile(readme,
                        "This is the local artifacts repository");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // getting all files from the folder and adding them to the artifacts
        // repository if not already here...
        Iterator<File> iter = FileUtils.iterateFiles(
                ArtifactsController.getArtifactsFolder(),
                new String[] { "zip" }, false);

        while (iter.hasNext()) {
            File file = iter.next();

            // find the artifact with the file name as URL
            if (!ArtifactURL.localExists(file.getName())) {
                ArtifactURL a = new ArtifactURL();
                a.date = new Date();
                a.local = true;
                a.name = file.getName();
                a.url = file.getName();
                a.save();
            } else {
                Logger.info("Aleady registered " + file.getName());
            }
        }
		ApplicationEvent.info("Application started");
	}
}
