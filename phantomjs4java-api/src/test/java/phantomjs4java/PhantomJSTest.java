package phantomjs4java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

public class PhantomJSTest {

	@Test
	public void testExecutable() {
		PhantomJS phantomjs = BasicPhantomJS.instance();
		assertEquals("aaa", phantomjs.eval("console.info('aaa'); phantom.exit();").trim());
		assertEquals("bbb", phantomjs.eval("console.info('bbb'); phantom.exit();").trim());
		File file = Util.writeTempFile("console.info('ccc'); phantom.exit();", "utf-8");
		file.deleteOnExit();
		assertEquals("ccc", phantomjs.source(file.getPath()).trim());
		assertTrue(file.exists());
		file.delete();
	}

	@Test
	public void testExecutableTimeout() {
		PhantomJS phantomjs = BasicPhantomJS.instance();
		long timeout = 500l;
		assertEquals("aaa", phantomjs.eval("console.info('aaa'); phantom.exit();", timeout).trim());
		File file = Util.writeTempFile("console.info('ccc'); phantom.exit();", "utf-8");
		file.deleteOnExit();
		assertEquals("ccc", phantomjs.source(file.getPath(), timeout).trim());
		assertTrue(file.exists());

		Util.write(file, "console.info('ccc');");

		try {
			phantomjs.eval("console.info('aaa');", timeout);
			fail("never reached");
		} catch (RuntimeException e) {
			// ok
		}

		try {
			phantomjs.source(file.getPath(), timeout);
			fail("never reached");
		} catch (RuntimeException e) {
			// ok
		}

		file.delete();
	}

}
