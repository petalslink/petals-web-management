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
