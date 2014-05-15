package phantomjs4java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

		phantomjs.getInstaller().killAll();
	}

}
