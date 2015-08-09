#!/bin/bash
echo ""
echo ""
echo "VersionManagment:"
git fetch -t
git show-ref --tags
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
	echo "Creating snapshot build"
else
	export RELEASE="true"
fi
./gradlew setupCIWorkspace
./gradlew build
version=$(<version.txt)
echo "Finished building version: " $version
token=$(printenv TOKEN)
if [[ $commsg = *"$r"* ]]; then
API_JSON=$(printf '{"tag_name": "v%s","target_commitish": "%s","name": "v%s","body": "Release of version %s","draft": false,"prerelease": false}' $version $(printenv GIT_COMMIT) $version $version)
curl -u maxanier:${token} --data "$API_JSON" https://api.github.com/repos/${1}/${2}/releases
fi
API_JSON=$(printf '{ "body":"[DRONE]%s"}' $(printenv DRONE_BUILD_URL))
curl -u maxanier:${token} --data "$API_JSON" https://api.github.com/repos/${1}/${2}/commits/$(printenv GIT_COMMIT)/comments