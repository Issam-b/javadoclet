# MarkdownDoclet
Doclet which creates API changes for review as *.java files.

## How to use with javadoc

```bash
javadoc -sourcepath src \
    -doclet org.qtproject.qt.api_review.ApiReviewDoclet \
    -docletpath ~/Downloads/api-review-doclet-1.0.jar \
    -title "Java API Docs" \
    -output-dir output-dir \
    -version "1.0" \
    -company "Author Name" \
    com.package.name
```

## How to use as Ant task

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<project default="javadoc">
  <target name="javadoc">
    <javadoc access="private" additionalparam="-encoding utf-8" packagenames="org.qtproject.qt.api_review" sourcepath="src">
      <doclet name="org.qtproject.qt.api_review.ApiReviewDoclet" path="api-review-doclet-1.0.jar">
        <param name="-file" value="document.md" />
        <param name="-title" value="SUBJECT" />
        <param name="-subtitle" value="SUBTITLE" />
        <param name="-version" value="VER 1.0" />
        <param name="-company" value="XXX PROJECT" />
      </doclet>
    </javadoc>
  </target>
</project>
```

## Copyright and License
All the source code avaiable in this repository is licensed under the **[GPL, Version 3.0](http://www.gnu.org/licenses)**
