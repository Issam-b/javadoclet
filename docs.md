% Markdown Doclet Docs
% 
% 13 June 2024 19:44:14

# doclet.markdown package

This is a package that stores MarkdownDoclet classes.  

## public Options class

A class that stores runtime options.  

### Package

* doclet.markdown

### File

* Options.java - 27 source code lines and 61 lines in total

#### Details of the field

##### public static String[][] options field

Array of Javadoc options  

#### public Options () constructor

\<There is no description\>  

#### public static String getOption (name) method

Get option string.  

If the corresponding option is not specified, an empty string is returned.  

##### String name parameters

option name  

##### Return value

option value  

#### public static String getOption (name, defaultValue) method

Get option string.  

If the corresponding option is not specified, the default value will be returned.  

##### String name parameters

option name  

##### String defaultValue parameters

value to use if option is not specified  

##### Return value

option value  

#### public static boolean isSupportedOption (option) method

Determine whether the option name is supported.  

##### String option parameters

option name  

##### Return value

Returns true if the option name is supported.  

## public MarkdownDoclet class

A doclet that creates Javadoc documents in Markdown format.  

### Package

* doclet.markdown

### File

* MarkdownDoclet.java - 26 source code lines and 54 lines in total

### All inherited class hierarchies

1. com.sun.javadoc.Doclet
1. doclet.markdown.MarkdownDoclet

#### public MarkdownDoclet () constructor

\<There is no description\>  

#### public static boolean start (rootDoc) method

Executes Javadoc generation processing.  

When executed, Javadoc information is generated as a Markdown file.  
If a file with the same name already exists, it will be overwritten.  

##### RootDoc rootDoc parameters

Javadoc root document  

##### Return value

Returns the execution result as a boolean value.  

#### public static int optionLength (option) method

Returns the number of optional arguments.  

##### String option parameters

option name  

##### Return value

Number of parameters including the corresponding argument itself  

#### public static LanguageVersion languageVersion () method

Specify the corresponding Java version.  

##### Return value

Supported Java version  

## public MarkdownBuilder class

Provides processing for creating Javadoc documents in Markdown format.  

### Package

* doclet.markdown

### File

* MarkdownBuilder.java - 302 source code lines and 511 lines in total

#### public MarkdownBuilder () constructor

\<There is no description\>  

#### public void create (rootDoc) method

Generate documentation.  

##### RootDoc rootDoc parameters

Javadoc root document  

##### Exception

IOException
:   exception

## public MarkdownWriter class

Outputs data in Markdown format.  

### Package

* doclet.markdown

### File

* MarkdownWriter.java - 105 source code lines and 209 lines in total

#### public MarkdownWriter () constructor

\<There is no description\>  

#### public void cover (title, author, date) method

Outputs the cover information.  

##### String title parameters

Title  

##### String author parameters

Author  

##### String date parameters

date  

#### public void heading1 (str) method

Outputs level 1 headings.  

##### String str parameters

heading string  

#### public void heading2 (str) method

Outputs level 2 headings.  

##### String str parameters

heading string  

#### public void heading3 (str) method

Outputs level 3 headings.  

##### String str parameters

heading string  

#### public void heading4 (str) method

Outputs level 4 headings.  

##### String str parameters

heading string  

#### public void heading5 (str) method

Outputs level 5 headings.  

##### String str parameters

heading string  

#### public void unorderedList (str) method

Outputs an unordered list.  

##### String str parameters

list contents  

#### public void orderedList (str) method

Print a numbered list.  

##### String str parameters

list contents  

#### public void definition (item, term) method

Outputs the definition list.  

##### String item parameters

definition name  

##### String term parameters

contents of definition  

#### public void line (str) method

Outputs line-by-line information.  

##### String str parameters

line-by-line information  

#### public void columns (cols) method

Print table rows.  

##### String[] cols parameters

column information  

#### public void breakElement () method

Ending a Markdown element  

#### public void save (file) method

Save the Markdown file.  

##### String file parameters

file name  

##### Exception

IOException
:   exception

# Source Code Statistics

|File|Bytes|Source code lines|Blank lines|Number of lines|
|:-----|-----:|-----:|-----:|-----:|
|MarkdownBuilder.java|13,167|302|74|511|
|MarkdownDoclet.java|1,354|26|5|54|
|MarkdownWriter.java|4,908|105|21|209|
|Options.java|1,336|27|5|61|
|Total 4 files|20,765|460|105|835|

