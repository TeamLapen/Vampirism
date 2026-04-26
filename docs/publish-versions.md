# Publish a new version

FactionApi and Vampirism are in the same mono repository but are deployed separately based on the tags.

Tags look like this:

``<project>/<mc-version>-<mod-version>[-(beta|alpha|dev|test).<identifier>]``

**There is no need to modify any gradle properties in the repository anymore**

It is only required to change `factions_version` in the [gradle.properties](/gradle.properties) file if vampirism uses features of the new faction version.

## What is deployed?

| Mod        | artifactPath                     | Release                                                                        | Snapshots                                                                       | maven classifier | Curseforge                                                                            | Modrinth                                        |
|------------|----------------------------------|--------------------------------------------------------------------------------|---------------------------------------------------------------------------------|------------------|---------------------------------------------------------------------------------------|-------------------------------------------------|
| FactionApi | de.teamlapen.faction.FactionApi  | [Maven](https://maven.maxanier.de/#/releases/de/teamlapen/vampirism/Vampirism) | [Maven](https://maven.maxanier.de/#/snapshots/de/teamlapen/vampirism/Vampirism) | jar, source, api | [Curseforge](https://legacy.curseforge.com/minecraft/mc-mods/factionapi)              | [Modrinth](https://modrinth.com/mod/factionapi) |
| Vampirism  | de.teamlapen.vampirism.Vampirism | [Maven](https://maven.maxanier.de/#/releases/de/teamlapen/faction/FactionApi)  | [Maven](https://maven.maxanier.de/#/snapshots/de/teamlapen/faction/FactionApi)  | jar, source, api | [Curseforge](https://www.curseforge.com/minecraft/mc-mods/vampirism-become-a-vampire) | [Modrinth](https://modrinth.com/mod/vampirism)  |


## Deployment differences

| classifier | tag                                                               | maven repository | publishes mod | requires release | has changelog |
|------------|-------------------------------------------------------------------|------------------|---------------|------------------|---------------|
| dev        | ``<project>/<mc-version>-<mod-version>-dev.<dev-identifier>``     | private          | false         | false            | false         |
| test       | ``<project>/<mc-version>-<mod-version>-test.<test-identifier>``   | private          | false         | false            | false         |
| alpha      | ``<project>/<mc-version>-<mod-version>-alpha.<alpha-identifier>`` | snapshots        | true          | false            | false         |
| beta       | ``<project>/<mc-version>-<mod-version>-beta.<beta-identifier>``   | snapshots        | true          | true             | true          |
| release    | ``<project>/<mc-version>-<mod-version>``                          | releases         | true          | true             | true          |


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

| environment       | secrets                                                                           | env variables                 |
|-------------------|-----------------------------------------------------------------------------------|-------------------------------|
| faction/dev       | `MAVEN_URL`,`MAVEN_USERNAME`,`MAVEN_PASSWORD`                                     |                               |
| faction/staging   | `MAVEN_URL`,`MAVEN_USERNAME`,`MAVEN_PASSWORD`,`CURSEFORGE_TOKEN`,`MODRINTH_TOKEN` | `CURSEFORGE_ID`,`MODRINTH_ID` |
| faction/release   | `MAVEN_URL`,`MAVEN_USERNAME`,`MAVEN_PASSWORD`,`CURSEFORGE_TOKEN`,`MODRINTH_TOKEN` | `CURSEFORGE_ID`,`MODRINTH_ID` |
| vampirism/dev     | `MAVEN_URL`,`MAVEN_USERNAME`,`MAVEN_PASSWORD`                                     |                               |
| vampirism/staging | `MAVEN_URL`,`MAVEN_USERNAME`,`MAVEN_PASSWORD`,`CURSEFORGE_TOKEN`,`MODRINTH_TOKEN` | `CURSEFORGE_ID`,`MODRINTH_ID` |
| vampirism/release | `MAVEN_URL`,`MAVEN_USERNAME`,`MAVEN_PASSWORD`,`CURSEFORGE_TOKEN`,`MODRINTH_TOKEN` | `CURSEFORGE_ID`,`MODRINTH_ID` |

shared secrets can be stored as repository secrets. (`MAVEN_USERNAME`, `MAVEN_PASSWORD`, `CURSEFORGE_TOKEN`, `MODRINTH_TOKEN`)