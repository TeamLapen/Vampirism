#!/bin/bash
#Arguments: 1:Githubowner 2:Github repository name. Same as in url 3:Changelog register url ("no" if no upload)
#Environment variable token:Github api token, pass:changelog add password
echo ""
echo ""
echo "VersionManagment: "

#Get commit message
commsg=$(git show -s --format=%s $(printenv GIT_COMMIT))
echo "Commit message: " $commsg

#Get lasttag
lasttag=$(git describe --abbrev=0 --tags)
echo "Last tag: " $lasttag

#Get mainversion:
IFS=. read major minor build <<<"${lasttag##*v}"
echo "MainVersion: "$major"."$minor
echo "Shell: "$SHELL
export VERSION=$major"."$minor

#Check if release
r="#release"
if [[ $commsg != *"$r"* ]]; then
	echo "Commit does not include #release"
    exit
fi

#Extract new version
v="VERSION:"
if [[ $commsg == *"$v"* ]]; then
	echo "Found new Mainversion"
	echo "${commsg##*VERSION:}"
	IFS=. read major minor <<<"${commsg##*VERSION:}"
	echo "New Mainversion:"$major"."$minor
	export VERSION=$major"."$minor
fi

#Generate Changelog
origin=https://github.com/${1}/${2}
echo "Origin url: " $origin
changelog=$(git log ${lasttag}..  --pretty=format:'<li> <a href="'${origin}'/commit/%H">view commit </a> &bull; %s</li> ' --reverse | grep "#changelog")
changelogfile=changelog.html
echo $changelog > $changelogfile

chpass=$(printenv PASS)
if [ $3 ]
then
	echo "Uploading changelog"
	curl --data "major=${major}&minor=${minor}&change=${changelog}&pass=${chpass}&build=$(printenv DRONE_BUILD_NUMBER)" ${3}
fi

#Create release
fversion=$(printenv VERSION)"."$(printenv DRONE_BUILD_NUMBER)
echo "Creating release for v"$fversion

API_JSON=$(printf '{"tag_name": "v%s","target_commitish": "master","name": "v%s","body": "Release of version %s","draft": false,"prerelease": false}' $fversion $fversion $fversion)
token=$(printenv TOKEN)
curl --data "$API_JSON" https://api.github.com/repos/${1}/${2}/releases?access_token=${token}