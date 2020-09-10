#!/usr/bin/env sh
# Script install pre-commit with google-java-format
# Copyright (C) 2020 Ihor Hryshkov (i.m.igor.grishkov.olegovich@gmail.com)
# Permission to copy and modify is granted under the MIT license
# Version 1.0.0
# Since 2020-08-23T04:55

if [ ! -f .git/hooks/pre-commit ]; then
	cat >.git/hooks/pre-commit <<EOF
#!/usr/bin/env sh
if [ ! -d ".cache" ]; then
	version=1.9
	mkdir .cache
	cd .cache
	if [ ! -f java-formatter.jar ]; then
		curl -LJo java-formatter.jar "https://github.com/google/google-java-format/releases/download/google-java-format-\$version/google-java-format-\$version-all-deps.jar"
		chmod +x java-formatter.jar
	fi
	cd ..
fi
changed_java_files=\$(git diff --cached --name-only --diff-filter=ACMR | grep ".*java\$" || true)
if [ -n "\$changed_java_files" ]
then
    echo "Reformatting Java files: \$changed_java_files"
    if ! java -jar .cache/java-formatter.jar --replace --set-exit-if-changed \$changed_java_files
    then
        echo "Some files were changed, aborting commit!" >&2
        exit 1
    fi
else
    echo "No Java files changes found."
fi
EOF
fi
chmod +x .git/hooks/pre-commit
sh .git/hooks/pre-commit
