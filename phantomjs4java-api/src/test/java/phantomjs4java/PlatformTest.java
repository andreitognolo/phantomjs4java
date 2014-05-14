package phantomjs4java;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class PlatformTest {

	@Test
	public void testRequirePlatform() {
		assertNotNull(Platform.requirePlatform());
	}

}
