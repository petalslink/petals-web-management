package models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class ArtifactsURLTest extends UnitTest {

	@Before
	@After
	public void empty() {
		ArtifactURL.deleteAll();
	}

	@Test
	public void checkLocals() {
		ArtifactURL url = new ArtifactURL();
		url.local = true;
		url.name = "foo";
		url.save();
		url = new ArtifactURL();
		url.local = false;
		url.name = "bar";
		url.save();
		assertEquals(1, ArtifactURL.locals().size());
    }
}
