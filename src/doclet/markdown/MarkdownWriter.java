package doclet.markdown;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Outputs data in Markdown format.
 */
public class MarkdownWriter {

	/**
	 * Markdown output line information
	 */
	List<String> lines = new ArrayList<String>();

	/**
	 * Outputs the cover information.
	 *
	 * @param title Title
	 * @param author Author
	 * @param date date
	 */
	public void cover(String title, String author, String date) {
		lines.add(0, "% " + markdown(date));
		lines.add(0, "% " + markdown(author));
		lines.add(0, "% " + markdown(title));
		breakElement();
	}

	/**
	 * Outputs level 1 headings.
	 *
	 * @param str heading string
	 */
	public void heading1(String str) {
		lines.add("# " + markdown(str));
		breakElement();
	}

	/**
	 * Outputs level 2 headings.
	 *
	 * @param str heading string
	 */
	public void heading2(String str) {
		lines.add("## " + markdown(str));
		breakElement();
	}

	/**
	 * Outputs level 3 headings.
	 *
	 * @param str heading string
	 */
	public void heading3(String str) {
		lines.add("### " + markdown(str));
		breakElement();
	}

	/**
	 * Outputs level 4 headings.
	 *
	 * @param str heading string
	 */
	public void heading4(String str) {
		lines.add("#### " + markdown(str));
		breakElement();
	}

	/**
	 * Outputs level 5 headings.
	 *
	 * @param str heading string
	 */
	public void heading5(String str) {
		lines.add("##### " + markdown(str));
		breakElement();
	}

	/**
	 * Outputs an unordered list.
	 *
	 * @param str list contents
	 */
	public void unorderedList(String str) {
		lines.add("* " + markdown(str));
	}

	/**
	 * Print a numbered list.
	 *
	 * @param str list contents
	 */
	public void orderedList(String str) {
		lines.add("1. " + markdown(str));
	}

	/**
	 * Outputs the definition list.
	 *
	 * @param item definition name
	 * @param term contents of definition
	 */
	public void definition(String item, String term) {
		String defString;
		if (!term.isEmpty())
			defString = markdown(term).trim();
		else
			defString = "(undef)";

		lines.add(markdown(item) + ": " + defString);

		// breakElement();
	}

	/**
	 * Outputs line-by-line information.
	 *
	 * @param str line-by-line information
	 */
	public void line(String str) {
		lines.add(markdown(str));
	}

	/**
	 * Print table rows.
	 *
	 * @param cols column information
	 */
	public void columns(String... cols) {
		StringBuilder sb = new StringBuilder();
		sb.append('|');
		for (int i = 0; i < cols.length; i++) {
			sb.append(markdown(cols[i]));
			sb.append('|');
		}
		lines.add(sb.toString());
	}

	/**
	 * Ending a Markdown element
	 */
	public void breakElement() {
		lines.add("");
	}

	/**
	 * Converts Javadoc information to a Markdown format string.
	 *
	 * @param str Javadoc format string
	 * @return Markdown format string
	 */
	public String markdown(String str) {
		// Markdown conversion of Javadoc inline tags
		str = Pattern.compile("<code>(.*?)</code>").matcher(str).replaceAll("`$1`");
		str = Pattern.compile("<i>(.*?)</i>").matcher(str).replaceAll("_$1_");
		str = Pattern.compile("<em>(.*?)</em>").matcher(str).replaceAll("_$1_");
		// str = Pattern.compile("<b>(.*?)</b>").matcher(str).replaceAll("__$1__");
		// str = Pattern.compile("<strong>(.*?)</strong>").matcher(str).replaceAll("__$1__");
		str = Pattern.compile("<b>(.*?)</b>").matcher(str).replaceAll("$1");
		str = Pattern.compile("<strong>(.*?)</strong>").matcher(str).replaceAll("$1");
		str = Pattern.compile("<a href=\"(https?://.+?)\">(.+?)</a>").matcher(str).replaceAll("[$2]($1)");
		str = Pattern.compile("\\{@link +([^,{}]+?) +([^,{}]+?)\\}").matcher(str).replaceAll("[$2]($1)");
		str = Pattern.compile("\\{@link +(.+?)\\}").matcher(str).replaceAll("[$1]($1)");
		str = Pattern.compile("\\{@code +(.+?)\\}").matcher(str).replaceAll("`$1`");
		str = Pattern.compile("</p>\\s+").matcher(str).replaceAll("\n");
		str = Pattern.compile("<li>\\s*").matcher(str).replaceAll("1. ");
		str = Pattern.compile("<\\/?ul>\\s*").matcher(str).replaceAll("");

		// escape
		str = str.replaceAll("\\\\", "\\\\\\\\");
		str = Pattern.compile("<(.*?)>").matcher(str).replaceAll("\\<$1\\>");

		// Restore entity references
		str = str.replaceAll("&lt;", "<");
		str = str.replaceAll("&gt;", ">");
		str = str.replaceAll("&quot;", "\"");
		str = str.replaceAll("&apos;", "'");
		str = str.replaceAll("&nbsp;", " ");
		str = str.replaceAll("&amp;", "&");

		// Returning results
		return str;
	}

	/**
	 * Save the Markdown file.
	 *
	 * @param file file name
	 * @throws IOException exception
	 */
	public void save(String outputDirRoot, String version) throws IOException {
		Writer writer = null;
		String packageName = "";
		String outputDir = "";
		boolean hasApiChanges = false;
		boolean writeStats = false;

		String apiDir = outputDirRoot + "/java-api-review-" + version;
		new File(apiDir).mkdir();

		try {
			for (String line : lines) {
				if (line.startsWith("#class=")) {
					String className = line.split("#class=")[1];
					String outputFilename = outputDir + "/" + className + ".java";

					if (writer != null)
						writer.close();

					writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilename), "UTF-8"));
					hasApiChanges = true;
					continue;
				} else if (line.startsWith("#package=")) { 
					packageName = line.split("#package=")[1];
					
					outputDir = apiDir + "/" + packageName;
					new File(outputDir).mkdir();

					String outputFilename = outputDir + "/package.java";

					if (writer != null)
						writer.close();

					writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilename), "UTF-8"));
					hasApiChanges = true;
					continue;
				} else if (line.startsWith("#statistics-section") && hasApiChanges) { 
					outputDir = apiDir;
					String outputFilename = outputDir + "/code-statistics.md";

					if (writer != null)
						writer.close();

					if (writeStats)
						writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilename), "UTF-8"));
					else
						writer = null;
					
					continue;
				} else if (writer != null) {
					writer.write(line);
					writer.write(System.lineSeparator());
				}
			}
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) { }
			}
		}
	}
}