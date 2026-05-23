# Publish a new version

### Git-based
FactionApi and Vampirism are in the same mono repository but are deployed separately based on the tags.

Tags look like this:

``<project>/<mc-version>-<mod-version>[-(beta|alpha|dev|test).<identifier>]``

**There is no need to modify any gradle properties in the repository anymore**

If you want Vampirism to depend on the new version. You only need to change the `vampirism_factions_version` in the [gradle.properties](/gradle.properties) file.

### Local test version
If you only want to create a local test version you can use `make create-test <project> <test-id>`. It automatically sets the correct versions

## What is deployed?

| Mod            | artifactPath                         | Release                                                                            | Snapshots                                                                           | maven classifier                                   | Curseforge                                                                            | Modrinth                                        |
|----------------|--------------------------------------|------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------|----------------------------------------------------|---------------------------------------------------------------------------------------|-------------------------------------------------|
| FactionApi     | de.teamlapen.faction.FactionApi      | [Maven](https://maven.maxanier.de/#/releases/de/teamlapen/faction/FactionApi)      | [Maven](https://maven.maxanier.de/#/snapshots/de/teamlapen/faction/FactionApi)      | jar, source, accesstransformer, interfaceinjection | [Curseforge](https://legacy.curseforge.com/minecraft/mc-mods/factionapi)              | [Modrinth](https://modrinth.com/mod/factionapi) |
| FactionApi API | de.teamlapen.faction.FactionApi-api  | [Maven](https://maven.maxanier.de/#/releases/de/teamlapen/faction/FactionApi-api)  | [Maven](https://maven.maxanier.de/#/snapshots/de/teamlapen/faction/FactionApi-api)  | jar, source,                                       |                                                                                       |                                                 |
| Vampirism      | de.teamlapen.vampirism.Vampirism     | [Maven](https://maven.maxanier.de/#/releases/de/teamlapen/vampirism/Vampirism)     | [Maven](https://maven.maxanier.de/#/snapshots/de/teamlapen/vampirism/Vampirism)     | jar, source, accesstransformer, interfaceinjection | [Curseforge](https://www.curseforge.com/minecraft/mc-mods/vampirism-become-a-vampire) | [Modrinth](https://modrinth.com/mod/vampirism)  |
| Vampirism API  | de.teamlapen.vampirism.Vampirism-api | [Maven](https://maven.maxanier.de/#/releases/de/teamlapen/vampirism/Vampirism-api) | [Maven](https://maven.maxanier.de/#/snapshots/de/teamlapen/vampirism/Vampirism-api) | jar, source                                        |                                                                                       |                                                 |


## Deployment differences

| classifier | tag                                                               | maven repository | publishes mod | requires release | has changelog | purpose              |
|------------|-------------------------------------------------------------------|------------------|---------------|------------------|---------------|----------------------|
| dev        | ``<project>/<mc-version>-<mod-version>-dev.<dev-identifier>``     | snapshots        | false         | false            | false         | for dev environments |
| test       | ``<project>/<mc-version>-<mod-version>-test.<test-identifier>``   | snapshots        | false         | false            | false         | for testing          |
| alpha      | ``<project>/<mc-version>-<mod-version>-alpha.<alpha-identifier>`` | snapshots        | true          | false            | false         | alpha versions       |
| beta       | ``<project>/<mc-version>-<mod-version>-beta.<beta-identifier>``   | snapshots        | true          | true             | true          | rc version           |
| release    | ``<project>/<mc-version>-<mod-version>``                          | releases         | true          | true             | true          | new releases         |


## Deploy a version

### Test versions

_applies to classifiers: `dev`, `test`, `alpha`_

Tag the commit and push it to GitHub.

`dev` and `test` versions are only published to the maven repository, while `alpha` is deployed to curseforge and modrinth as well.

### Release versions

_applies to classifiers: `beta`, `release`_

1. (Optional) Create a tag on the commit to release.
2. Create a release on GitHub.
   - Select the already existing tag if available
   - Create a new tag
3. Add release notes
4. Publish the release as prerelease or release

The mod is published to the maven repository and curseforge and modrinth.

## GitHub requirements

| environment       | secrets                                                                                                          | env variables                 |
|-------------------|------------------------------------------------------------------------------------------------------------------|-------------------------------|
| faction/dev       | `MAVEN_URL`,`MAVEN_USERNAME`,`MAVEN_PASSWORD`                                                                    |                               |
| faction/release   | `MAVEN_URL_RELEASES`,`MAVEN_URL_SNAPSHOTS`,`MAVEN_USERNAME`,`MAVEN_PASSWORD`,`CURSEFORGE_TOKEN`,`MODRINTH_TOKEN` | `CURSEFORGE_ID`,`MODRINTH_ID` |
| vampirism/dev     | `MAVEN_URL`,`MAVEN_USERNAME`,`MAVEN_PASSWORD`                                                                    |                               |
| vampirism/release | `MAVEN_URL_RELEASES`,`MAVEN_URL_SNAPSHOTS`,`MAVEN_USERNAME`,`MAVEN_PASSWORD`,`CURSEFORGE_TOKEN`,`MODRINTH_TOKEN` | `CURSEFORGE_ID`,`MODRINTH_ID` |
