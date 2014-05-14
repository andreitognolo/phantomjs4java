package phantomjs4java;

import java.io.File;

import phantomjs4java.binary.PhantomJSInstaller;

public class PhantomJS {

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

		StringBuilder out = new StringBuilder();
		StringBuilder err = new StringBuilder();
		ExecUtil.execAndWait(new String[] { installer.getExecutable(), scriptFile.getPath() }, out, err);
		return out.toString();
	}

}
