package phantomjs4java;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {

	private static final Logger LOG = LoggerFactory.getLogger(Util.class);

	public static void close(AutoCloseable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (Exception e) {
				LOG.info("Error closing", e);
			}
		}
	}

	public static void close(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (Exception e) {
				LOG.info("Error closing", e);
			}
		}
	}

	public static String encodeURI(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<String> newList(Collection<String> c) {
		if (c == null) {
			return new ArrayList<String>();
		}
		return new ArrayList<String>(c);
	}

	public static String joinCollection(String delimiter, Iterable<?> args) {
		StringBuilder ret = new StringBuilder();
		Iterator<?> it = args.iterator();
		while (it.hasNext()) {
			Object arg = it.next();
			if (arg == null) {
				arg = "";
			}
			ret.append(arg);
			if (it.hasNext()) {
				ret.append(delimiter);
			}
		}
		return ret.toString();
	}

	public static String join(String delimiter, Object... args) {
		return joinCollection(delimiter, Arrays.asList(args));
	}

	@SuppressWarnings("unchecked")
	public static <T extends Collection<String>> T extract(T ret, String regex, String str) {
		if (ret == null) {
			ret = (T) new ArrayList<String>();
		}
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			ret.add(matcher.group());
		}
		return ret;
	}

	public static void exists(Collection<?> coll) {
		if (coll == null) {
			throw new RuntimeException("it is required");
		}
		for (Object object : coll) {
			if (object == null) {
				throw new RuntimeException("it is required");
			}
			if (object instanceof String && ((String) object).trim().length() == 0) {
				throw new RuntimeException("it is required");
			}
		}
	}

	public static String str(Object words) {
		if (words == null) {
			return null;
		}
		String ret = words.toString().trim();
		return ret.length() == 0 ? null : ret;
	}

	public static String stringfy(Object words) {
		String str = str(words);
		return str == null ? "" : str;
	}

	public static <T> T require(T obj, String msg) {
		if (obj == null) {
			throw new RuntimeException("Value is required: " + msg);
		}

		return obj;
	}

	public static Properties classpathProperties(String path) {
		URL url = classpath(path);
		return properties(url);
	}

	public static Properties properties(URL url) {
		if (url == null) {
			return null;
		}
		Reader in = null;
		try {
			in = new InputStreamReader(new BufferedInputStream(url.openStream()), "utf-8");
			Properties ret = new Properties();
			ret.load(in);
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			close(in);
		}
	}

	public static URL classpath(String path) {
		return Util.class.getResource(path);
	}

	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] readAll(InputStream in) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			copyAll(in, out);
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void copyAll(InputStream in, OutputStream out) {
		try {
			IOUtils.copy(in, out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String classpathString(String path) {
		return classpathString(path, "utf-8");
	}

	public static String classpathString(String path, String enc) {
		URL url = classpath(path);
		if (url == null) {
			return null;
		}
		Reader in = null;
		try {
			in = new InputStreamReader(new BufferedInputStream(url.openStream()), enc);
			String ret = readAll(in);
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			close(in);
		}
	}

	public static String readAll(Reader in) {
		CharArrayWriter writer = new CharArrayWriter();
		copyAll(in, writer);
		return writer.toString();
	}

	public static void copyAll(Reader in, Writer out) {
		try {
			IOUtils.copy(in, out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String read(Console console, String label, String def) {
		String ret = console.readLine(label, def);
		if (ret == null) {
			ret = "";
		}
		ret = ret.trim();
		if (ret.length() == 0) {
			return def;
		}
		return ret;
	}

	public static String read(URL url, String charset) {
		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(url.openStream(), charset);
			return readAll(inputStreamReader);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (inputStreamReader != null) {
				close(inputStreamReader);
			}
		}
	}

	public static Properties argsToProperties(String[] args) {
		if (args == null || args.length % 2 != 0) {
			throw new RuntimeException("requires pairs of values");
		}
		Properties ret = new Properties();
		for (int i = 0; i < args.length; i += 2) {
			String name = str(args[i]);
			String value = str(args[i + 1]);
			if (name == null) {
				throw new RuntimeException("name is required");
			}
			ret.put(name, value);
		}
		return ret;
	}

	public static String[] trim(Object[] array) {
		String[] ret = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			ret[i] = stringfy(array[i]);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public static <T> int compare(Comparable<T> a, Comparable<T> b) {
		if (a == null || b == null) {
			return a == b ? 0 : a == null ? -1 : 1;
		}
		return a.compareTo((T) b);
	}

	public static byte[] toBytes(String content, String enc) {
		try {
			return content.getBytes(enc);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getStack(Throwable e) {
		StringWriter str = new StringWriter();
		PrintWriter writer = new PrintWriter(str);
		e.printStackTrace(writer);
		writer.close();
		return str.toString();
	}

	public static String toString(byte[] buffer, String enc) {
		try {
			String string = new String(buffer, enc);
			return Util.str(string);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isEqualIterable(Iterable<?> a, Iterable<?> b) {
		if (a == b) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		Iterator<?> ait = a.iterator();
		Iterator<?> bit = b.iterator();
		while (ait.hasNext()) {
			if (!bit.hasNext()) {
				return false;
			}
			Object ao = ait.next();
			Object bo = bit.next();
			if (!isEqual(ao, bo)) {
				return false;
			}
		}
		return !bit.hasNext();
	}

	private static boolean isEqual(Object ao, Object bo) {
		if (ao == bo) {
			return true;
		}
		if (ao == null || bo == null) {
			return false;
		}
		return ao.equals(bo);
	}

	public static String dateToString(Date date) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		sdf.setLenient(false);
		return sdf.format(date);
	}

	@SuppressWarnings("unchecked")
	public static <T> T serialCopy(T obj) {
		byte[] bytes = serial(obj);
		return (T) deserial(bytes);
	}

	public static Object deserial(byte[] bytes) {
		try {
			ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
			ObjectInputStream in = new ObjectInputStream(bin);
			Object ret = in.readObject();
			in.close();
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] serial(Object obj) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(bout));
			out.writeObject(obj);
			out.close();
			return bout.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Throwable rootCause(Throwable ex) {
		while (ex != null) {
			Throwable cause = ex.getCause();
			if (cause == null) {
				return ex;
			}
			ex = cause;
		}
		return null;
	}

	public static Long sum(Iterable<Long> nums) {
		Long ret = 0l;
		for (Long num : nums) {
			if (num != null) {
				ret += num;
			}
		}
		return ret;
	}

	public static Long max(Long... values) {
		if (values == null || values.length == 0) {
			return null;
		}
		Long ret = values[0];
		for (int i = 1; i < values.length; i++) {
			if (ret == null || (values[i] != null && ret < values[i])) {
				ret = values[i];
			}
		}
		return ret;
	}

	public static String concat(Object... values) {
		StringBuilder sb = new StringBuilder();
		for (Object v : values) {
			sb.append(v);
		}
		return sb.toString();
	}

	public static boolean in(Object obj, Object... others) {
		for (Object other : others) {
			if (obj == null && other == null) {
				return true;
			}
			if (obj != null && obj.equals(other)) {
				return true;
			}
		}
		return false;
	}

	public static List<String> names(Enum<?>... values) {
		if (values == null) {
			return null;
		}
		List<String> ret = new ArrayList<String>(values.length);
		for (Enum<?> value : values) {
			ret.add(value == null ? null : value.name());
		}
		return ret;
	}

	public static void write(File file, String data) {
		Writer out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
			out.write(data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			close(out);
		}
	}

	public static String stringfyStackTrace(Exception e) {
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}

	public static String generateText(String text, int times) {
		if (text == null) {
			return null;
		}
		StringBuilder ret = new StringBuilder(text.length() * times);
		for (int i = 0; i < times; i++) {
			ret.append(text);
		}
		return ret.toString();
	}

	public static void copyAll(URL url, File file) {
		InputStream in = null;
		FileOutputStream out = null;
		try {
			in = url.openStream();
			out = new FileOutputStream(file);
			copyAll(in, out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			close(in);
			close(out);
		}
	}

	public static void close(Process p) {
		if(p != null) {
			p.destroy();
		}
	}

}
