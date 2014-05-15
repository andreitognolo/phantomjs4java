package phantomjs4java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class PhantomJSExecTest {

	@Test
	public void testExec() {
		PhantomJS phantomjs = BasicPhantomJS.instance();
		PhantomJSExec exec = PhantomJSExec.create().eval("console.info('aaa: ' + JSON.stringify(phantom.args)); phantom.exit();").args("a", "b");
		assertEquals("aaa: [\"a\",\"b\"]", phantomjs.exec(exec).trim());

		File file = Util.writeTempFile("console.info('ccc: ' + JSON.stringify(phantom.args)); phantom.exit();", "utf-8");
		file.deleteOnExit();
		exec = PhantomJSExec.create().source(file.getPath()).args("a", "b");
		assertEquals("ccc: [\"a\",\"b\"]", phantomjs.exec(exec).trim());
		assertTrue(file.exists());
		file.delete();

	}
}
