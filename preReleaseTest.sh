#!/bin/bash

dir=$(mktemp -d)
echo "Working in $dir"
mkdir "$dir/.m2"

(
  set -o pipefail #This makes sure the exit status of the next line is determined by the right most command with exit status !=0 (instead of the right most). The parentheses are here to limit this change to this command
  ./gradlew -Dmaven.repo.local="$dir/.m2" build publishToMavenLocal -Ptestid=prerelease | tee "$dir/vampirism.build.log"
)
vampirism_success=$?


if [ $vampirism_success -eq 0 ];
then
	echo -e "'\033[0;32m'Success"
else
	echo -e "'\033[0;31m'Failed to build Vampirism"
	exit 1
fi


version=$(sed -n -E 's/Version\s(.*)/\1/p' "$dir/vampirism.build.log")
mc_version=$(sed -n -E 's/minecraft_version=([\d.]*)/\1/p' gradle.properties)
forge_version=$(sed -n -E 's/forge_version=([\d.]*)/\1/p' gradle.properties)
branch_name=$(git rev-parse --abbrev-ref HEAD)
echo "Found version $mc_version $version on $branch_name"

mkdir "$dir/vampirism"
cp "build/libs/Vampirism-$mc_version-$version.jar" "$dir/vampirism/"

#Werewolves
git clone https://github.com/TeamLapen/Werewolves.git "$dir/werewolves"
cd "$dir/werewolves" || exit
git checkout "origin/$branch_name"
echo -e "\nrepositories{ maven{ url='$dir/.m2' }}" >> build.gradle
./gradlew build -Pvampirism_version="$version"
werewolves_success=$?

#Integrations
#git clone https://github.com/TeamLapen/VampirismIntegrations.git "$dir/integrations"
#cd "$dir/integrations" || exit
#git checkout "origin/$branch_name"
#echo -e "\nrepositories{ maven{ url='$dir/.m2' }}" >> build.gradle
#./gradlew build -Pvampirism_version="$version" -Pvampirism_mcversion="$mc_version"
integrations_success=0 #$?

if [ $werewolves_success -eq 0 ] && [ $integrations_success -eq 0 ];
then
	echo -e "'\033[0;32m'Success"
else
	echo -e "'\033[0;31m'Failed to build addons"
	exit 1
fi


sudo docker run -v "$dir/vampirism:/data/mods" -e TYPE=FORGE -e VERSION="$mc_version" -e FORGE_VERSION="$forge_version" -e EULA=true --name vampirism_test_runner --rm -it docker.io/itzg/minecraft-server

rm -rf $dir
