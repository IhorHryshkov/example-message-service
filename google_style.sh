#!/usr/bin/env sh

version=1.8
mkdir -p .cache
cd .cache
if [ ! -f java-formatter.jar ]; then
	curl -LJo java-formatter.jar "https://github.com/google/google-java-format/releases/download/google-java-format-$version/google-java-format-$version-all-deps.jar"
	chmod 755 java-formatter.jar
fi
cd ..

changed_java_files=$(git diff --cached --name-only --diff-filter=ACMR | grep ".*java$")
echo $changed_java_files
java -jar .cacheq2/java-formatter.jar --replace $changed_java_files