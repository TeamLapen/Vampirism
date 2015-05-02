#!/bin/bash
#Arguments: 1:Githubowner 2:Github repository name. Same as in url
#Environment variable token:Github api token, pass:add password
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
#Get lasttag
lasttag=$(git describe --abbrev=0 --tags)
echo "Last tag: " $lasttag
#Get mainversion:
IFS=. read major minor build <<<"${lasttag##*v}"
echo "Old Version: "$major"."$minor"."$build
if [[ -z "$build" ]]; then
	build=0
fi
#Check if release
r="#release"
if [[ $commsg != *"$r"* ]]; then
	export MODVERSION=$major"."$minor"."$build"."$(printenv DRONE_BUILD_NUMBER)
	echo "Creating snapshot build: "$(printenv MODVERSION)
else
	recommend=1
	#Extract new version
	v="VERSION:"
	if [[ $commsg == *"$v"* ]]; then
		echo "Found new Mainversion"
		echo "${commsg##*VERSION:}"
		IFS=. read major minor build <<<"${commsg##*VERSION:}"
		echo "New Version:"$major"."$minor"."$build
		export MODVERSION=$major"."$minor"."$build
	else
		build=$(($build+1))
		echo "New Version:"$major"."$minor"."$build
		export MODVERSION=$major"."$minor"."$build
	fi

	#Create release
	fversion=$(printenv MODVERSION)
	echo "Creating release for v"$fversion
	API_JSON=$(printf '{"tag_name": "v%s","target_commitish": "%s","name": "v%s","body": "Release of version %s","draft": false,"prerelease": false}' $fversion $(printenv DRONE_BRANCH) $fversion $fversion)
	token=$(printenv TOKEN)
	curl --data "$API_JSON" https://api.github.com/repos/${1}/${2}/releases?access_token=${token}
fi
./gradlew setupCIWorkspace
./gradlew build
for f in build/libs/*.jar; do
if [[ $f == *"dev"* ]]
then
dev=$f;
else
file=$f;
fi
done
curl -F "pass=$PASS" -F "file=@$file" -F "dev=@$dev" -F "recommend=$recommend" http://teamlapen.de/projects/vampirism/files/upload.php