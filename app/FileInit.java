import java.io.File;
import java.util.Date;
import java.util.Iterator;

import models.ArtifactURL;

import org.apache.commons.io.FileUtils;

import play.Logger;
import controllers.ArtifactsController;

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

/**
 * Create required files and folder if not already available
 * 
 * @author chamerling
 * 
 */
//@OnApplicationStart
public class FileInit  {//extends Job {

	public void doJob() throws Exception {
		Logger.info("Creating required folders");
		File readme = new File(ArtifactsController.getArtifactsFolder(),
				"README.md");
		if (!readme.exists()) {
			FileUtils.touch(readme);
			FileUtils.writeStringToFile(readme,
				"This is the local artifacts repository");
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
	};
}
