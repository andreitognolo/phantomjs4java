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

	public String source(String file, Long timeout) {
		return exec(PhantomJSExec.create().source(file).timeout(timeout));
	}

	public String source(String file) {
		return source(file, null);
	}

	public PhantomJSInstaller getInstaller() {
		return installer;
	}

	public String eval(String script, Long timeout) {
		return exec(PhantomJSExec.create().eval(script).timeout(timeout));
	}

	public String eval(String script) {
		return eval(script, null);
	}

	@Override
	public String toString() {
		return "[PhantomJS " + installer + "]";
	}

	public String exec(PhantomJSExec exec) {
		File scriptFile = null;
		try {
			if (exec.getSource() == null) {
				scriptFile = Util.writeTempFile(exec.getEval(), "utf-8");
				scriptFile.deleteOnExit();
				exec.source(scriptFile.getPath());
			}
			String[] cmds = new String[2 + exec.getArgs().length];
			cmds[0] = installer.getExecutable();
			cmds[1] = exec.getSource();
			System.arraycopy(exec.getArgs(), 0, cmds, 2, exec.getArgs().length);
			StringBuilder out = new StringBuilder();
			StringBuilder err = new StringBuilder();
			ExecUtil.execAndWaitSuccess(cmds, out, err, exec.getTimeout());
			return out.toString();
		} finally {
			if (scriptFile != null) {
				try {
					scriptFile.delete();
				} catch (Exception e) {
					LOG.error("error deleting script: " + scriptFile);
				}
			}
		}
	}

}
