package phantomjs4java;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phantomjs4java.binary.PhantomJSInstaller;

public class PhantomJS {

	private static final Logger LOG = LoggerFactory.getLogger(PhantomJS.class);

	private static final Long TIMEOUT = 5000l;

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

	public String source(String file, Long timeout) {
		if (timeout == null) {
			timeout = TIMEOUT;
		}
		File script = new File(file);
		StringBuilder out = new StringBuilder();
		StringBuilder err = new StringBuilder();
		ExecUtil.execAndWaitSuccess(new String[] { installer.getExecutable(), script.getPath() }, out, err, timeout);
		return out.toString();
	}

	public String source(String file) {
		return source(file, null);
	}

	public PhantomJSInstaller getInstaller() {
		return installer;
	}

	public String eval(String script, Long timeout) {
		if (timeout == null) {
			timeout = TIMEOUT;
		}
		File scriptFile = Util.writeTempFile(script, "utf-8");
		scriptFile.deleteOnExit();
		try {
			return source(scriptFile.getPath(), timeout);
		} finally {
			try {
				scriptFile.delete();
			} catch (Exception e) {
				LOG.error("error deleting script: " + script);
			}
		}
	}

	public String eval(String script) {
		return eval(script, null);
	}

	@Override
	public String toString() {
		return "[PhantomJS " + installer + "]";
	}

}
