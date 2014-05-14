package phantomjs4java;

import phantomjs4java.binary.PhantomJSInstaller;

public class BasicPhantomJS {

	private static final Object MUTEX = new Object();

	private static PhantomJS instance = null;

	public static PhantomJS instance() {
		if (instance == null) {
			synchronized (MUTEX) {
				if (instance == null) {
					PhantomJS ret = new PhantomJS();
					ret.configure(PhantomJSInstaller.create().toTemporary());
					instance = ret;
				}
			}
		}
		return instance;
	}

}
