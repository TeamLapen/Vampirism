#!/bin/bash
echo ""
echo ""
echo "Version Managment: "
git fetch -t
#Get commit message
commsg=$(git show -s --format=%s $(printenv GIT_COMMIT))
echo "Commit message: " $commsg


./gradlew setupCIWorkspace
./gradlew build
version=$(<version.txt)
echo "Finished building version: " $version
token=$(printenv TOKEN)
API_JSON=$(printf '{ "body":"[DRONE]%s"}' $(printenv DRONE_BUILD_URL))
curl -u maxanier:${token} --data "$API_JSON" https://api.github.com/repos/${1}/${2}/commits/$(printenv GIT_COMMIT)/comments