package doclet.markdown;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;

import doclet.counter.CountInfo;
import doclet.counter.Counter;

/**
 * Provides processing for creating Javadoc documents in Markdown format.
 */
public class MarkdownBuilder {

	/**
	 * Javadoc root document
	 */
	private RootDoc root;

	/**
	 * Markdown output
	 */
	private MarkdownWriter md;

	/**
	 * List for remembering output packages
	 */
	private List<PackageDoc> packages;

	/**
	 * Map for storing step count results
	 */
	private Map<File, CountInfo> counts;

	/**
	 * Display string when no comment is specified
	 */
	private static final String NO_COMMENT = "No description";

	/**
	 * Generate documentation.
	 *
	 * @param rootDoc Javadoc root document
	 * @throws IOException exception
	 */
	public void create(RootDoc rootDoc) throws IOException {

		// Get Javadoc root document
		root = rootDoc;

		// Initialize Markdown output
		md = new MarkdownWriter();

		// Create a cover
		makeCoverPage();

		// Initialize output package list
		packages = new ArrayList<PackageDoc>();

		// Initialize step count results
		counts = new HashMap<File, CountInfo>();

		// Output all classes
		makeClassPages();

		// Output step count results
		makeCountPage();

		// save to file
		md.save(Options.getOption("output-dir", ""),
				Options.getOption("version", ""));
	}

	/**
	 * Create a cover.
	 */
	private void makeCoverPage() {

		// Title information
		String title = Options.getOption("title");
		if (!Options.getOption("subtitle").isEmpty()) {
			if (!title.isEmpty()) {
				title += " ";
			}
			title += Options.getOption("subtitle");
		}
		if (!Options.getOption("version").isEmpty()) {
			if (!title.isEmpty()) {
				title += " ";
			}
			title += Options.getOption("version");
		}

		// Author information
		String company = Options.getOption("company");

		// get date
		Locale locale = new Locale("en", "US", "US");
		Calendar cal = Calendar.getInstance(locale);
		DateFormat jformat = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", locale);
		String stamp = jformat.format(cal.getTime());

		// Output cover information
		md.cover(title, company, stamp);
	}

	/**
	 * Gets the format of the execution method argument as a string.
	 *
	 * @param parameters Argument information
	 * @param type When displaying the class name
	 * @return A string indicating the format of the argument
	 */
	private String getParamSignature(Parameter[] parameters, boolean type) {
		StringBuilder sb = new StringBuilder();
		for (Parameter parameter : parameters) {
			if (0 < sb.length()) {
				sb.append(", ");
			}
			if (type) {
				sb.append(getShortName(parameter.type()));
				sb.append(" ");
			}
			sb.append(parameter.name());
		}
		return sb.toString();
	}

	/**
	 * Gets the comment set in the parameter.
	 *
	 * @param tags Tag information
	 * @param name Parameter name
	 * @return Comment information
	 */
	private String getParamComment(ParamTag[] tags, String name) {
		for (ParamTag tag : tags) {
			if (tag.parameterName().equals(name)) {
				return tag.parameterComment();
			}
		}
		return "";
	}

	/**
	 * Gets the comment set in the exception.
	 *
	 * @param tags Tag information
	 * @param name exception class name
	 * @return Comment information
	 */
	private String getThrowsComment(ThrowsTag[] tags, String name) {
		for (ThrowsTag tag : tags) {
			if (tag.exceptionName().equals(name)) {
				return tag.exceptionComment();
			}
		}
		return "";
	}

	/**
	 * Outputs information for all classes.
	 */
	private void makeClassPages() {

		// all classes
		for (ClassDoc classDoc : root.classes()) {

			// package
			PackageDoc packageDoc = classDoc.containingPackage();

			// For new packages
			if (!packages.contains(packageDoc)) {

				// Package name
				md.lines.add("#package=" +  packageDoc.name());
				
				// Package description
				md.lines.add("/*");
				print(getText(packageDoc.commentText(), NO_COMMENT), false);
				md.lines.add("*/");

				md.lines.add("package " + packageDoc.name() + ";");

				// Add to output package
				packages.add(packageDoc);
			}

			// Type name
			String classType;
			if (classDoc.isInterface()) {
				classType = "interface";
			} else {
				classType = "class";
			}

			// class
			md.lines.add("#class=" +  classDoc.name());

			// package name
			md.lines.add("package " + classDoc.containingPackage().name() + ";");
			md.lines.add("");
			// md.heading3("Package");
			// md.unorderedList(classDoc.containingPackage().name());
			// md.breakElement();

			// class description
			md.lines.add("/*");
			print(getText(classDoc.commentText(), NO_COMMENT).trim(), false);

			// Source code file information
			File source = classDoc.position().file();
			CountInfo ci = Counter.count(source);
			if (ci != null) {
				md.breakElement();
				md.lines.add("File");
				String qtRepoPattern = "\\/(qt\\w+\\/src\\/.+)";
				String classPath = "";
        		Matcher matcher = Pattern.compile(qtRepoPattern).matcher(source.getPath());
				if (matcher.find())
					classPath = matcher.group(1);
				else
					classPath = source.getName();

				// md.unorderedList(String.format("%s - %,d source code lines and %,d lines in total", classPath, ci.getSteps(), ci.getLines()));
				md.unorderedList(classPath);
				// md.lines.add("Class File: " + classPath);
			}
			counts.put(source, ci);

			// inheritance hierarchy
			List<ClassDoc> classDocs = new ArrayList<ClassDoc>();
			classDocs.add(classDoc);
			ClassDoc d = classDoc.superclass();
			while (d != null && !d.qualifiedName().equals("java.lang.Object")) {
				classDocs.add(d);
				d = d.superclass();
			}
			if (2 <= classDocs.size()) {
				md.breakElement();
				md.lines.add("Inheritance Hierarchy");
				Collections.reverse(classDocs);
				for (int i = 0; i < classDocs.size(); i++) {
					md.orderedList(classDocs.get(i).qualifiedName());
				}
			}

			// interface
			if (0 < classDoc.interfaces().length) {
				md.breakElement();
				md.lines.add("Implemented Interfaces");
				for (int i = 0; i < classDoc.interfaces().length; i++) {
					md.unorderedList(classDoc.interfaces()[i].qualifiedName());
				}
			}

			// version
			Tag[] versionTags = classDoc.tags("version");
			if (0 < versionTags.length) {
				md.lines.add("Version");
				for (int i = 0; i < versionTags.length; i++) {
					md.unorderedList(versionTags[i].text());
				}
				// md.breakElement();
			}

			// Author
			Tag[] authorTags = classDoc.tags("author");
			if (0 < authorTags.length) {
				md.lines.add("Author");
				for (int i = 0; i < authorTags.length; i++) {
					md.unorderedList(authorTags[i].text());
				}
				// md.breakElement();
			}

			md.lines.add("*/");
			
			md.lines.add(classDoc.modifiers() + " " + classType + " " + classDoc.name() + " {");

			// all constants
			if (0 < classDoc.enumConstants().length) {
				// md.heading4("Constant details");
				for (int i = 0; i < classDoc.enumConstants().length; i++) {
					writeFieldDoc(classDoc.enumConstants()[i]);
				}
			}

			// all fields
			if (0 < classDoc.fields().length) {
				// md.heading4("Field details");
				for (int i = 0; i < classDoc.fields().length; i++) {
					writeFieldDoc(classDoc.fields()[i]);
				}
			}

			// all constructors
			for (int i = 0; i < classDoc.constructors().length; i++) {
				writeMemberDoc(classDoc.constructors()[i]);
			}

			// all methods
			for (int i = 0; i < classDoc.methods().length; i++) {
				writeMemberDoc(classDoc.methods()[i]);
			}

			md.lines.add("}");
		}
	}

	/**
	 * Outputs information for all fields.
	 *
	 * @param doc member information
	 */
	private void writeFieldDoc(MemberDoc doc) {

		// Type name
		String fieldType;
		if (doc.isEnumConstant()) {
			fieldType = "enum constant";
		} else if (doc.isEnum()) {
			fieldType = "enumeration type";
		} else {
			fieldType = "field";
		}

		// field information
		md.lines.add("");
		md.lines.add("    /*");
		String comment = getText(doc.commentText(), NO_COMMENT).replaceAll("(?m)^", "    ");
		print(comment);
		md.lines.add("    */");
		md.lines.add("    " + doc.modifiers() + " " + getShortName(((FieldDoc) doc).type()) + " " + doc.name() + ";");
	}

	/**
	 * Outputs information about all executable members.
	 *
	 * @param doc Executable member information
	 */
	private void writeMemberDoc(ExecutableMemberDoc doc) {
		md.lines.add("");
		md.lines.add("    /*");
		String comment = getText(doc.commentText(), NO_COMMENT).replaceAll("(?m)^", "    ");
		print(comment);

		boolean paramBreakAdded = false;
		// parameters
		Parameter[] parameters = doc.parameters();
		if (0 < parameters.length) {
			for (int i = 0; i < parameters.length; i++) {
				String paramText = getText(getParamComment(doc.paramTags(), parameters[i].name()), "");
				if (!paramText.isEmpty()) {
					if (!paramBreakAdded)
						md.breakElement();
					paramBreakAdded = true;
					print("    `" + getShortName(parameters[i].type()) + " " + parameters[i].name() + "`: " 
								+ paramText, true, false);
				}
			}
		}

		// Return value
		if (doc instanceof MethodDoc) {
			MethodDoc method = (MethodDoc) doc;
			if (0 < method.tags("return").length) {
				md.breakElement();
				print("    Returns " + method.tags("return")[0].text(), true, false);
			}
		}

		// exception
		Type[] exceptions = doc.thrownExceptionTypes();
		if (0 < exceptions.length) {
			for (int i = 0; i < exceptions.length; i++) {
				md.breakElement();
				print("    Exception `" + getShortName(exceptions[i]) + "`: " +
						getText(getThrowsComment(doc.throwsTags(), exceptions[i].typeName()), NO_COMMENT), true, false);
			}
		}
		md.lines.add("    */");

		// Method information
		String str = doc.modifiers();
		if (doc instanceof MethodDoc) {
			str += " " + getShortName(((MethodDoc) doc).returnType());
		}
		str += " " + doc.name() + "(" + getParamSignature(doc.parameters(), true) + ");";
		md.lines.add("    " + str);
	}

	/**
	 * Outputs Javadoc content in Markdown format.
	 *
	 * @param str Javadoc content
	 */
	private void print(String str, boolean indent, boolean isMainComment) {

		// Process each paragraph
		String[] paragraphs = str.split("\\s*<(p|P)>\\s*");
		for (int i = 0; i < paragraphs.length; i++) {

			// Combining line breaks
			paragraphs[i] = paragraphs[i].replaceAll("\\s*[\\r\\n]+\\s*", " ");

			paragraphs[i] = md.markdown(paragraphs[i]);
			
			paragraphs[i] = paragraphs[i].replaceAll(" 1. ", "\n1. ");
			paragraphs[i] = paragraphs[i].replaceAll("Note: ", "\nNote: ");

			// Insert line break after each line
			if (indent) {
				if (isMainComment)
					paragraphs[i] = paragraphs[i].replaceAll("(.{75,86}(?=\\s) )", "$1\n    ");
				else
					paragraphs[i] = paragraphs[i].replaceAll("(.{75,86}(?=\\s) )", "$1\n        ");
			} else {
				paragraphs[i] = paragraphs[i].replaceAll("(.{75,86}(?=\\s) )", "$1\n");
			}

			paragraphs[i] = paragraphs[i].replaceAll(" \n", "\n");

			// Process every line break
			String[] lines = paragraphs[i].split("\n");
			Pattern regex = Pattern.compile("</ul>\\s*$");
			for (int j = 0; j < lines.length; j++) {
				if (!regex.matcher(lines[j]).find())
					md.line(lines[j]);
			}

			// Ending a Markdown element
			if (i < paragraphs.length - 1)
				md.breakElement();
		}
	}

	private void print(String str, boolean indent) {
		print(str, indent, true);
	}

	private void print(String str) {
		print(str, true, true);
	}

	/**
	 * Returns the default string if the specified string is empty.
	 *
	 * @param str string
	 * @param def default string
	 * @return selected string
	 */
	private String getText(String str, String def) {
		if (str == null || str.isEmpty()) {
			return def;
		}
		return str.trim().replace("\n ", "\n").replaceAll("(\\n)+$", "");
	}

	/**
	 * Removes the package name from the class name.
	 *
	 * @param type class
	 * @return abbreviated class name
	 */
	private String getShortName(Type type) {
		String name = type.toString();
		name = name.replaceAll("[a-zA-Z0-9\\-\\_]+\\.", "");
		return name;
	}

	/**
	 * Outputs the step count result page.
	 */
	private void makeCountPage() {

		// Initialize total value
		int count = 0;
		long sumSize = 0;
		int sumSteps = 0;
		int sumBranks = 0;
		int sumLines = 0;

		// package name
		md.lines.add("#statistics-section");
		md.heading1("Source Code Statistics");

		// table header
		md.columns("File", "Bytes", "Source code lines", "Blank lines", "Number of lines");
		md.columns(":-----", "-----:", "-----:", "-----:", "-----:");

		// If the target file exists
		if (0 < counts.size()) {

			// File list generation (file name order)
			List<File> files = new ArrayList<File>(counts.keySet());
			Collections.sort(files, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});

			// File list output
			for (File file : files) {

				// File information
				long size = file.length();
				int steps = counts.get(file).getSteps();
				int branks = counts.get(file).getBranks();
				int lines = counts.get(file).getLines();

				// Adding the total value
				count++;
				sumSize += size;
				sumSteps += steps;
				sumBranks += branks;
				sumLines += lines;

				// File information
				md.columns(file.getName(), // file
						String.format("%,d", size), // size
						String.format("%,d", steps), // step
						String.format("%,d", branks), // blank line
						String.format("%,d", lines) // Number of lines
				);
			}
		}

		// total line
		md.columns(String.format("Total %,d files", count), // number of files
				String.format("%,d", sumSize), // size
				String.format("%,d", sumSteps), // step
				String.format("%,d", sumBranks), // blank line
				String.format("%,d", sumLines) // Number of lines
		);
		md.breakElement();
	}
}