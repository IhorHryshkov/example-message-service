#!/usr/bin/env sh
# Script install pre-commit with google-java-format
# Copyright (C) 2020 Ihor Hryshkov (i.m.igor.grishkov.olegovich@gmail.com)
# Permission to copy and modify is granted under the MIT license
# Version 1.0.0
# Since 2020-08-23T04:55

if [ ! -f .git/hooks/pre-commit ]; then
	cat > .git/hooks/pre-commit <<EOF
#!/usr/bin/env sh
if [ ! -d ".cache" ]; then
	version=1.7
	mkdir .cache
	cd .cache
	if [ ! -f java-formatter.jar ]; then
		curl -LJo java-formatter.jar "https://github.com/google/google-java-format/releases/download/google-java-format-\$version/google-java-format-\$version-all-deps.jar"
		chmod 755 java-formatter.jar
	fi
	cd ..
fi
changed_java_files=\$(git diff --cached --name-only --diff-filter=ACMR | grep ".*java\$")
echo $changed_java_files
java -jar .cache/java-formatter.jar --replace \$changed_java_files
EOF
fi
sh .git/hooks/pre-commit
