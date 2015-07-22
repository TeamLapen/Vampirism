#!/bin/bash
echo ""
echo ""
echo "VersionManagment:"
#Get commit message
commsg=$(git show -s --format=%s $(printenv GIT_COMMIT))
echo "Commit message: " $commsg

#Check if release
rt="#release"
vt="#build"
if [[ $commsg != *"$rt"* ]] && [[ $commsg != *"$vt"* ]] ; then
	echo "Commit does not include #release or #build"
	exit 0
fi
#Check if release
r="#release"
if [[ $commsg != *"$r"* ]]; then
	echo "Creating snapshot build: "$(printenv MODVERSION)
else
	export RELEASE=true
fi
./gradlew setupCIWorkspace
./gradlew build curse