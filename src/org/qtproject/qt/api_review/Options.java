package org.qtproject.qt.api_review;

/**
 * A class that stores runtime options.
 */
public class Options {

	/**
	 * Array of Javadoc options
	 */
	public static String[][] options;

	/**
	 * Get option string.
	 * <p>
	 * If the corresponding option is not specified, an empty string is returned.
	 *
	 * @param name option name
	 * @return option value
	 */
	public static String getOption(String name) {
		return getOption(name, "");
	}

	/**
	 * Get option string.
	 * <p>
	 * If the corresponding option is not specified, the default value will be returned.
	 *
	 * @param name option name
	 * @param defaultValue value to use if option is not specified
	 * @return option value
	 */
	public static String getOption(String name, String defaultValue) {
		for (int i = 0; i < options.length; i++) {
			String[] opt = options[i];
			if (opt[0].equals("-" + name)) {
				return opt[1];
			}
		}
		return defaultValue;
	}

	/**
	 * Determine whether the option name is supported.
	 *
	 * @param option option name
	 * @return Returns true if the option name is supported.
	 */
	public static boolean isSupportedOption(String option) {
		switch (option) {
		case "-output-dir":
		case "-title":
		case "-subtitle":
		case "-version":
		case "-company":
			return true;
		}
		return false;
	}
}
