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
	echo "Creating snapshot build"
else
	export RELEASE="true"
fi
#./gradlew setupCIWorkspace
#./gradlew build curse
version=$(<version.txt)
echo "Finished building version: " $version
if [[ $commsg = *"$r"* ]]; then
API_JSON=$(printf '{"tag_name": "v%s","target_commitish": "%s","name": "v%s","body": "Release of version %s","draft": false,"prerelease": false}' $version $(printenv GIT_COMMIT) $version $version)
token=$(printenv TOKEN)
curl --data "$API_JSON" https://api.github.com/repos/${1}/${2}/releases?access_token=${token}
fi
API_JSON=$(printf '{ "body":"[DRONE]%s"}' $(printenv DRONE_BUILD_URL))
echo $API_JSON
curl --data "$API_JSON" https://api.github.com/repos/${1}/${2}/commits/$(printenv GIT_COMMIT)/comments?access_token=${token}