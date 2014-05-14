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

	public static void execAndWait(String[] cmds, StringBuilder out, StringBuilder err) {
		Process p = null;
		Reader stdout = null;
		Reader stderr = null;
		try {
			p = Runtime.getRuntime().exec(cmds);
			int code = p.waitFor();
			if (code != 0) {
				throw new RuntimeException("exec error: " + code);
			}
			stdout = new InputStreamReader(p.getInputStream(), "utf-8");
			stderr = new InputStreamReader(p.getInputStream(), "utf-8");
			Util.copyAll(stdout, out);
			Util.copyAll(stderr, err);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			Util.close(stdout);
			Util.close(stderr);
			Util.close(p);
		}
	}
}
