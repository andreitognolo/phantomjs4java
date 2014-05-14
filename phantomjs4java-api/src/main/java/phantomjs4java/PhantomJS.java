package phantomjs4java;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phantomjs4java.binary.PhantomJSInstaller;

public class PhantomJS {

	private static final Logger LOG = LoggerFactory.getLogger(PhantomJS.class);

	private PhantomJSInstaller installer;

	public String getExecutable() {
		return installer.getExecutable();
	}

	public void configure(PhantomJSInstaller installer) {
		if (this.installer != null) {
			throw new RuntimeException("already configurated: " + installer);
		}
		if (!installer.check()) {
			installer.install();
		}
		if (!installer.check()) {
			throw new RuntimeException("PhantomJSInstaller check fail: " + installer);
		}
		this.installer = installer;
	}

	public String eval(String script) {
		File scriptFile = Util.writeTempFile(script, "utf-8");
		scriptFile.deleteOnExit();
		try {
			StringBuilder out = new StringBuilder();
			StringBuilder err = new StringBuilder();
			ExecUtil.execAndWait(new String[] { installer.getExecutable(), scriptFile.getPath() }, out, err);
			return out.toString();
		} finally {
			try {
				scriptFile.delete();
			} catch (Exception e) {
				LOG.error("error deleting script: " + scriptFile);
			}
		}
	}

}
