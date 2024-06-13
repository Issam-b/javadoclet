package doclet.markdown;

import com.sun.javadoc.Doclet;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;

/**
 * A doclet that creates Javadoc documents in Markdown format.
 */
public class MarkdownDoclet extends Doclet {

	/**
	 * Executes Javadoc generation processing.
	 * <p>
	 * When executed, Javadoc information is generated as a Markdown file. 
	 * If a file with the same name already exists, it will be overwritten.
	 *
	 * @param rootDoc Javadoc root document
	 * @return Returns the execution result as a boolean value.
	 */
	public static boolean start(RootDoc rootDoc) {
		MarkdownBuilder creator = new MarkdownBuilder();
		try {
			Options.options = rootDoc.options();
			creator.create(rootDoc);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Returns the number of optional arguments.
	 *
	 * @param option option name
	 * @return Number of parameters including the corresponding argument itself
	 */
	public static int optionLength(String option) {
		if (Options.isSupportedOption(option)) {
			return 2;
		}
		return 0;
	}

	/**
	 * Specify the corresponding Java version.
	 *
	 * @return Supported Java version
	 */
	public static LanguageVersion languageVersion() {
		return LanguageVersion.JAVA_1_5;
	}
}