package phantomjs4java;

import phantomjs4java.binary.PhantomJSInstaller;

public class PhantomJSMain {

	public static void main(String[] args) {
		String dest = null;
		if (args.length > 0) {
			dest = args[0];
		}
		if ("--help".equals(dest)) {
			showHelp();
			return;
		}

		PhantomJSInstaller installer = PhantomJSInstaller.create();
		if (dest == null) {
			installer.toTemporary();
		} else {
			installer.to(dest);
		}
		installer.install();
		String executable = installer.getExecutable();
		System.out.println(executable);
	}

	private static void showHelp() {
		System.out.println("Install to a specific directory: ");
		System.out.println("    PhantomJSMain [directory-destination]");
		System.out.println("Install to a temporary directory: ");
		System.out.println("    PhantomJSMain");
	}

}
