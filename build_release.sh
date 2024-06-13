#!/bin/bash

VERSION="1.0"

build_markdown_doclet_jar() {
    find src -name "*.java" -exec javac -d build -source 8 -target 8 {} +
    jar cfm markdowndoclet-$VERSION.jar src/MANIFEST.MF -C build doclet
    rm -R build
}

build_markdown_doclet_jar
