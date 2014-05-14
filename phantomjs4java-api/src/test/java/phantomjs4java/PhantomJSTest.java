package phantomjs4java;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PhantomJSTest {

	@Test
	public void testExecutable() {
		PhantomJS phantomjs = BasicPhantomJS.instance();
		String str = phantomjs.eval("console.info('abc'); phantom.exit();");
		assertEquals("abc", str.trim());
	}

}
