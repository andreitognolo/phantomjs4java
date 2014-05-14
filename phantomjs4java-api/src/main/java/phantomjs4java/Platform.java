package phantomjs4java;

public class Platform {

	public static String requirePlatform() {
		String platform = System.getProperty("os.name").toLowerCase();
		String arch = System.getProperty("os.arch").toLowerCase();
		if (platform.contains("win")) {
			return "windows";
		} else if (platform.contains("mac")) {
			return "macosx";
		} else if (platform.contains("nux")) {
			return "linux-" + (arch.contains("64") ? "x86_64" : "i686");
		} else {
			throw new RuntimeException("unknown platform: " + platform + " " + arch);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(requirePlatform());
	}

}
