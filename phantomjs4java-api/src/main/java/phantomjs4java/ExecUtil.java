package phantomjs4java;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public class ExecUtil {

	public static String getOutputAsString(String... args) {
		Process p = null;
		InputStream in = null;
		try {
			p = Runtime.getRuntime().exec(args);
			int code = p.waitFor();
			if (code != 0) {
				throw new RuntimeException("exec error: " + code);
			}
			in = p.getInputStream();
			String ret = Util.readAll(new InputStreamReader(in, Charset.defaultCharset()));
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			Util.close(in);
			Util.close(p);
		}
	}

	public static void execAndWaitSuccess(String[] cmds, StringBuilder out, StringBuilder err, Long timeout) {
		int code = execAndWait(cmds, out, err, timeout);
		if (code != 0) {
			throw new RuntimeException("exec error: " + code);
		}
	}

	public static int execAndWait(String[] cmds, StringBuilder out, StringBuilder err, Long timeout) {
		Process p = null;
		Reader stdout = null;
		Reader stderr = null;
		try {
			p = Runtime.getRuntime().exec(cmds);
			Integer code = null;
			long before = System.currentTimeMillis();
			long max = before + timeout;
			while (code == null) {
				code = code(p);
				long now = System.currentTimeMillis();
				if (now > max) {
					throw new RuntimeException("timeout, max: " + timeout + ", but was: " + (now - before));
				}
				if (code == null) {
					Util.sleep(10l);
				}
			}
			stdout = new InputStreamReader(p.getInputStream(), "utf-8");
			stderr = new InputStreamReader(p.getInputStream(), "utf-8");
			Util.copyAll(stdout, out);
			Util.copyAll(stderr, err);
			return code.intValue();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			Util.close(stdout);
			Util.close(stderr);
			Util.close(p);
		}
	}

	private static Integer code(Process p) {
		try {
			return p.exitValue();
		} catch (IllegalThreadStateException e) {
			return null;
		}
	}
}
