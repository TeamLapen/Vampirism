Vampirism for Minecraft 1.12 - Latest branch [![](http://cf.way2muchnoise.eu/short_233029_downloads.svg)](https://minecraft.curseforge.com/projects/vampirism-become-a-vampire) [![Build Status](https://travis-ci.org/TeamLapen/Vampirism.svg?branch=1.12)](https://travis-ci.org/TeamLapen/Vampirism) 
============================================
[![forthebadge](https://forthebadge.com/images/badges/built-with-love.svg)](https://maxanier.de) 
## Mod Description

Vampires are fast, strong and blood-thirsty entities, which do not like the sun, but don't fear the night, and the best thing is: You can become one!

This mod allows you to become a vampire with all it's benefits and drawbacks.

After being bitten by a vampire or manually injecting some vampire blood you get an effect called "Sanguinare Vampiris" which eventually turns you into a vampire.

For a more detailed description head over to the Minecraft forums or the Curseforge page.
## Links
[Curseforge Project](https://minecraft.curseforge.com/projects/vampirism-become-a-vampire)
[Minecraft Forum Thread](http://www.minecraftforum.net/forums/mapping-and-modding-java-edition/minecraft-mods/2756555-vampirism-become-a-vampire)  
[Downloads](https://minecraft.curseforge.com/projects/vampirism/files)  
[Help to translate](https://crowdin.com/project/vampirism)

## Team
- [maxanier](http://maxanier.de) _Everything_  

#### Inactive
- Mistadon _Code/Models_  
- wildbill22 _Code_  
- LRA_10 _Models/Textures_

## API
Vampirism has an API you can use to add blood values to your mod's creatures or make them convertible and more.
#### Setup Gradle build script
You should be able to include it with the following in your `build.gradle`:
```gradle
repositories {
    //Maven repo for Vampirism
    maven {
        url = "https://maxanier.de/maven2"
    }
}
dependencies {
    //compile against the Vampirism API
    deobfCompile "de.teamlapen.vampirism:Vampirism:${mc_version}-${vampirism_version}:api"
    //at runtime (in your development environment) use the full Vampirism jar
    runtime "de.teamlapen.vampirism:Vampirism:${mc_version}-${vampirism_version}"
}
```

#### Choose a version
`${mc_version}` gets replaced by the current Minecraft version. (i.e. `1.10.2`)
`${vampirism_version}` gets replaced by the version of Vampirism you want to use (i.e `1.0.3`)

For a list of available Vampirism version, see [CurseForge](https://minecraft.curseforge.com/projects/vampirism/files) or the [maven listing](https://maxanier.de/maven2/de/teamlapen/vampirism/Vampirism/) .

These properties can be set in a file named `gradle.properties`, placed in the same directory as your `build.gradle` file.
Example `gradle.properties`:
```
mc_version=1.10.2
vampirism_version=1.0.3
```

#### Rerun Gradle setup commands
Please run the commands that you used to setup your development environment again.
E.g. `gradlew setupDecompWorkspace eclipse --refresh-dependencies` or `gradlew setupDecompWorkspace ideaModule --refresh-dependencies`
Refresh/Restart your IDE afterwards.

#### Examples
Checkout this example project: https://github.com/TeamLapen/VampirismAPIExample

If you want to create an addon which access all of Vampirism's classes, not just the API, checkout this https://github.com/TeamLapen/VampirismAddonExample and consider contacting maxanier.

## Code Structure
The _master branch_ serves as the main development branch. Besides that there is a branch with the latest stable release code, it receives bugfixes which are usually cherry-pick merged in to the master branch.
There also is a 1.9 branch which might be up-to-date with the latest release branch (for 1.10).
The source code is currently divided into three parts, which might be split in the future.
#### Vampirism
Located in de.teamlapen.vampirism  
Contains the mod source code. Depends on the other two parts.  
#### Vampirism API
Located in de.teamlapen.vampirism.api  
Designed to be used by mods that only optionally interact with Vampirism as well as addon mods depending on Vampirism.  
#### VampLib/TeamLapen Lib
Located in de.teamlapen.lib
Independent mod (Contains @Mod).  
Provides Helpers and Registries to automate stuff like EntityUpdates.
Provides abstract classes/default implementations/interfaces to simplify things (located under de.teamlapen.lib.lib).  

## Setting up the development environment
If you would like to compile your own versions or even contribute to Vampirism's development you need to setup a dev environment.
The following example instructions will setup IntelliJ (Free community edition or Non-Free Ultimate edition). If you already have a setup or want to use another IDE, jump [here](#setting-up-vampirism-in-another-environment).

#### IntelliJ
1. Make sure you have the Java **JDK** (minimum Java 8) as well as the IntelliJ IDE installed.
2. If you want to contribute to the development (via pull requests), fork Vampirism on Github.
3. (Optionally) Install Git, so you can clone the repository and push changes.
4. Clone (`git clone https://github.com/TeamLapen/Vampirism`) or [download](https://github.com/TeamLapen/Vampirism/archive/master.zip) Vampirism to a new "Vampirism" folder.
5. In IntelliJ use `New...` -> `New from Version Control` -> Fill out repo, directory and name
6. After cloning is done IntelliJ offers you to import a unlinked Gradle Project. Click this.
7. Select `Create directories for empty content roots` and __deselect__ `Create seperate module per source set` [Image](https://picload.org/image/ripradpa/importprojectfromgradle_001.png)  
8. Select the gradle task `setupDecompWorkspace` in the IntelliJ Gradle window and add the following arguments `-Xmx4g -Xms4g`. Run it.  
9. Refresh the gradle project  
10. Run `genIntellijRuns`
11. Make sure `Settings -> Build, Execution, Deployment -> Compiler -> 'Add runtime assertions for not-null-annotated methods and parameters' is disabled` (Unfortunately required, requires rebuild if the project has been built before)
12. You might have to modify the projets compiler output path  


That's it.

#### Setting up Vampirism in another environment
If you would like to setup Vampirism in another way or another IDE, you should pay regard to the following points.  
1. Make sure `src/main/java`, `src/api/java´ and `src/lib/java` are marked as source folders and `src/main/resources` and `src/lib/resources` are marked as resource folders.  
2. Vampirism might have a few dependencies (e.g. Waila), which are specified in the gradle files and should be automatically downloaded and added when you run `ideaModule` or `eclipse`.  
3. Vampirism requires at least Java 8 


## Licence
This mod is licenced under [LGPLv3](https://raw.githubusercontent.com/TeamLapen/Vampirism/master/LICENCE)

This mod uses these sounds from freesound:  
DST-VampireMonk.mp3 by Striderjapan -- http://www.freesound.org/people/Striderjapan/sounds/141368/ -- License: CC Attribution  
vampire bites by Bernuy -- http://www.freesound.org/people/Bernuy/sounds/268501/ -- License: CC Attribution  
bow02.ogg by Erdie https://www.freesound.org/people/Erdie/sounds/65734/ -- Licence: CC Attribution  
the swarm v31m3 by Setuniman https://www.freesound.org/people/Setuniman/sounds/130695/ -- Licence: CC Attribution  
Boiling Towel by unfa https://www.freesound.org/people/unfa/sounds/174499/ -- Licence: CC Attribution  
