Vampirism for Minecraft 1.10
============================

## Mod Description 

Vampires are fast, strong and blood-thirsty entities, which do not like the sun, but don't fear the night, and the best thing is: You can become one!

This mod adds several rituals which allow you to first become a vampire and then level up as a vampire, with higher levels you will get faster, stronger, better night vision etc, but it brings disadvantages with it, you take sun damage or are hunted by vampire hunters.

As a vampire you don't need to eat all that dry bread or eat these strange fruits called "apples", you prefer some red and tasty blood, which you have to suck from animals or better villagers, but be careful not every animal likes to give blood.

## Links 
[Minecraft Forum Thread](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/wip-mods/2364443-vampirism-become-a-vampire)  
[Downloads](http://minecraft.curseforge.com/mc-mods/233029-vampirism-become-a-vampire/files)  
[Help to translate](https://crowdin.com/project/vampirism)

## Team [![Join the chat at https://gitter.im/TeamLapen/Vampirism](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/TeamLapen/Vampirism?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)  
- [maxanier](http://maxanier.de) _Code/Models_  

#### Inactive 
- Mistadon _Code/Models_  
- wildbill22 _Code_  
- LRA_10 _Models/Textures_

## API
Vampirism has an API you can use to add blood values to your mod's creatures or make them convertible and more.
You should be able to include it with the following in your build.gradle:
```gradle
repositories {
    //Maven repo for Vampirism
    maven {
        url = "https://maxanier.de/maven2"
    }
}
dependencies {
    deobfProvided 'de.teamlapen.vampirism:Vampirism:1.10.2-1.0.0:api'//Adjust version
}
```
Checkout this example project: https://github.com/TeamLapen/VampirismAPIExample

If you want to create an addon, checkout this https://github.com/TeamLapen/VampirismAddonExample and consider contacting maxanier.

## Code Structure 
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
The following instructions will setup a multi module setup for IntelliJ (Free community edition or Non-Free Ultimate edition). If you already have a setup or want to use another IDE, jump [here](#setting-up-vampirism-in-another-environment).

#### Preperations
1. Make sure you have the Java **JDK** (minimum Java 7) as well as the IntelliJ IDE installed.
2. If you want to contribute to the development (via pull requests), fork Vampirism on Github.
3. (Optionally) Install Git, so you can clone the repository and push changes.
4. Create a folder for all Minecraft related development files.
5. Create one folders "Run" inside.
7. Clone (`git clone https://github.com/TeamLapen/Vampirism`) or [download](https://github.com/TeamLapen/Vampirism/archive/master.zip) Vampirism to a new "Vampirism" folder next to the "Run" one.

You should have a build.gradle along other files in the "Vampirism" folder now

#### Import
1. Open a console windows inside the "Vampirism" folder (on windows use shift-right click and select "Open in console")
2. Run `gradlew.bat setupDecompWorkspace ideaModule` on Windows or `./gradlew setupDecompWorkspace ideaModule` on Linux
4. Open IntelliJ and create an **empty** project in the top folder you've created
5. Open "Project Structure", modules and import the `Vampirism.iml` in the "Vampirism" folder. Make sure that src/main/java and src/lib/java are marked as source folders and /src/main/resources and src/lib/resources are marked as resource folders. 
6. Make sure you choose Java 7 as language level.

You should have Vampirism's code in the project now and no errors should be displayed
#### Run configurations
1. Click `Run->Edit Configurations` and create a new one.
2. Set it up like [this](http://picload.org/image/wpoaicg/run_config.png) use the second folder "Run" you've created as working directory. It will store your world and configs etc.
3. If you want run a server use GradleStartServer instead of GradleStart

That's it.

#### Setting up Vampirism in another environment
If you would like to setup Vampirism in another way or another IDE, you should pay regard to the following points.
1. Make sure `src/main/java` and `src/lib/java` are marked as source folders and `src/main/resources` and `src/lib/resources` are marked as resource folders.
2. Vampirism might have a few dependencies (e.g. Waila), which are specified in the gradle files and should be automatically downloaded and added when you run `ideaModule` or `eclipse`.
3. Vampirism requires at least Java 7


## Licence 
This mod is licenced under [LGPLv3](https://raw.githubusercontent.com/TeamLapen/Vampirism/master/LICENCE)

This mod uses these sounds from freesound:  
DST-VampireMonk.mp3 by Striderjapan -- http://www.freesound.org/people/Striderjapan/sounds/141368/ -- License: CC Attribution  
vampire bites by Bernuy -- http://www.freesound.org/people/Bernuy/sounds/268501/ -- License: CC Attribution  
bow02.ogg by Erdie https://www.freesound.org/people/Erdie/sounds/65734/ -- Licence: CC Attribution
the swarm v31m3 by Setuniman https://www.freesound.org/people/Setuniman/sounds/130695/ -- Licence: CC Attribution
Boiling Towel by unfa https://www.freesound.org/people/unfa/sounds/174499/ -- Licence: CC Attribution
