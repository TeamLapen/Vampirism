Vampirism for Minecraft 1.21 - Latest branch 
============================================
[![](http://cf.way2muchnoise.eu/short_233029_downloads.svg)](https://minecraft.curseforge.com/projects/vampirism-become-a-vampire) [![](https://img.shields.io/modrinth/dt/jVZ0F1wn?label=Modrinth)](https://modrinth.com/mod/vampirism) [![Build Status](https://github.com/TeamLapen/Vampirism/workflows/Java%20CI/badge.svg?branch=1.16)](https://github.com/TeamLapen/Vampirism/actions) [![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0) [![Discord Server](https://img.shields.io/discord/430326060635258881)](https://discord.gg/wuamm4P) [![Crowdin](https://badges.crowdin.net/vampirism/localized.svg)](https://crowdin.com/project/vampirism)

[![forthebadge](https://forthebadge.com/images/badges/built-with-love.svg)](https://vampirism.dev)

## Mod Description

Vampires are fast, strong and bloodthirsty entities, which do not like the sun, but don't fear the night, and the best thing is: You can become one!

This mod allows you to become a vampire with all its benefits and drawbacks.

After being bitten by a vampire or manually injecting some vampire blood you get an effect called "Sanguinare Vampiris" which eventually turns you into a vampire.

For a more detailed description head over to the Minecraft Forum or the Curseforge page.
## Links
[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg)](https://www.curseforge.com/minecraft/mc-mods/vampirism-become-a-vampire/files)
[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/vampirism/versions)
[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/translate/crowdin_vector.svg)](https://translate.vampirism.dev)
[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/documentation/ghpages_vector.svg)](https://wiki.vampirism.dev/docs/wiki/intro)
[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/documentation/website_vector.svg)](https://vampirism.dev)
[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-plural_vector.svg)](https://discord.gg/wuamm4P)

## Issues
https://github.com/TeamLapen/Vampirism/issues
Please use the appropriate template when creating an issue.

The following labeling scheme is used:
- *unconfirmed*: Awaiting triage or bug not reproduced yet
- *discussion*: Looking for feedback
- *enhancement*: Any minor tweak that can be introduced in minor releases
- *feature*: Any change that takes more time to implement and test
- *accepted*: Any feature/enhancement that is planned to be implemented eventually
- *1.12-1.***: Affecting only a specific MC version
- *v1.8-v1.**: Bug affecting or enhancement targeting a specific Vampirism release branch
- *latest*: Bug affecting or enhancement targeting the latest (potentially unreleased) Vampirism branch


## People
- [maxanier](https://maxanier.de)
- [Cheaterpaul](https://paube.de)
- [lunofe](https://github.com/lunofe) _Triage/Support/Community/Official Server/Art_
- [Piklach](https://twitter.com/Piklach) _Community/Official Server_


## Special Thanks to
- PendragonII _Community/Official Server_
- TheRebelT _Models/Textures_
- TinkerHatWill _Textures_
- Alis _Textures_
- dimensionpainter _Textures_
- S_olace _Textures_
- Mistadon _Code/Models_
- wildbill22 _Code_
- LRA_10 _Models/Textures_
- Oreo365 _Models_
- Slippingchip400 _Models_
- Йода _Textures_
- XxKidDowdallxX _Textures_
- F_Spade _Textures_
- Matheo _Lore_
- special_krab _Lore_
- cournualllama2 _Lore_
- Random _Textures/Models_
- Shumnik _Textures/Models_
- BugraaK _Textures/Models_
- MrVityaTrash _Textures_
- FrostedOver _Textures_
- Grid _Models/Textures/Structures_
- T_Corvus _Textures_
- Zeyke _Textures_

## API
Vampirism has an API you can use to add blood values to your mod's creatures or make them convertible and more. For more information and an overview checkout the wiki https://wiki.vampirism.dev/docs/api/intro.


## Setup Gradle build script

<details>
<summary>Use Vampirism in your development environment</summary>
You should be able to include it with the following in your `build.gradle`:
```gradle
repositories {
    //Maven repo for Vampirism
    maven {
        url = "https://maven.maxanier.de/releases"
    }
}
dependencies {
    //compile against the Vampirism API
    compileOnly fg.deobf("de.teamlapen.vampirism:Vampirism:${mc_version}-${vampirism_version}:api")
    //at runtime (in your development environment) use the full Vampirism jar
    runtimeOnly fg.deobf("de.teamlapen.vampirism:Vampirism:${mc_version}-${vampirism_version}")
}
```

#### Choose a version

`${mc_version}` gets replaced by the current Minecraft version. (i.e. `1.20.4`)
`${vampirism_version}` gets replaced by the version of Vampirism you want to use (i.e `1.10.0`)

For a list of available Vampirism version,
see [CurseForge](https://www.curseforge.com/minecraft/mc-mods/vampirism-become-a-vampire) or
the [maven listing](https://maven.maxanier.de/de/teamlapen/vampirism/Vampirism/) .

These properties can be set in a file named `gradle.properties`, placed in the same directory as your `build.gradle`
file. Example `gradle.properties`:

```
mc_version=1.16.5
vampirism_version=1.7.12
```

#### Rerun Gradle setup commands

Please run the commands that you used to set up your development environment again. E.g. `gradlew`
or `gradlew build --refresh-dependencies`
Refresh/Restart your IDE afterwards.

#### Run Vampirism in a deobfuscated environment

Vampirism uses **mixins**. To be able to apply them in a deobfuscated environment using a different set of mappings (
from the one Vampirism uses) you have to enable remapping the refmap:
Add

```
     property 'mixin.env.remapRefMap', 'true'
     property 'mixin.env.refMapRemappingFile', "${projectDir}/build/createSrgToMcp/output.srg"
```

to your run configurations in your `build.gradle` and then regenerate your IDE run configurations (`genIntelliJRuns` or
similar). If that does not work you can also try `property 'mixin.env.disableRefMap', 'true'`
If you still run into issues with the mixins you can also set `mixin.env.ignoreRequired` to `true`. However, not all of
Vampirism will work correctly then.

#### Examples

Checkout this example project: https://github.com/TeamLapen/VampirismAPIExample

If you want to create an addon which access all of Vampirism's classes, not just the API, checkout
this https://github.com/TeamLapen/VampirismAddonExample and consider contacting @Cheaterpaul.

</details>

## Code Structure

The _minecraft_version_ branch serves as the main development branch. There might be older (stable) branches for the
same MC version suffixed with the Vampirism main version.  
It may receive bugfixes until the latest branch is released.  
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

<details>
<summary>old / outdated</summary>


If you would like to compile your own versions or even contribute to Vampirism's development, you need to set up the dev environment like any other mod.

</details>  

## Code Style
The code style used in this project is the IntelliJ default one.

## License
The source code and text in this repository are licenced under [LGPLv3](https://raw.githubusercontent.com/TeamLapen/Vampirism/master/CODE_LICENSE) ***except*** for the following parts:

##### Textures
Any textures included in this mod are licenced under the following terms:
> Any textures (and models) included in the mod may be used, remixed and distributed for anything related to Vampirism (fan art, addon mods, forks, reviews, ...) excluding resource packs.
> 
> If you want to use them in a resource/texture pack, you must credit the Vampirism project or the individual creator where applicable and it must not be used commercially.

##### Sounds
The sounds used in this mod are individual licensed and may only be used outside Vampirism under the respective licensing terms if noted as such.

| Sound                     | Creator               | Link                                                                           | License        |
|---------------------------|-----------------------|--------------------------------------------------------------------------------|----------------|
| DST-VampireMonk.mp3       | Striderjapan          | [freesound](http://www.freesound.org/people/Striderjapan/sounds/141368/)       | CC Attribution |
| vampire bites             | Bernuy                | [freesound](http://www.freesound.org/people/Bernuy/sounds/268501/)             | CC Attribution |
| bow02.ogg                 | Erdie                 | [freesound](https://www.freesound.org/people/Erdie/sounds/65734/)              | CC Attribution |
| the swarm v31m3           | Setuniman             | [freesound](https://www.freesound.org/people/Setuniman/sounds/130695/)         | CC Attribution |
| Boiling Towel             | unfa                  | [freesound](https://www.freesound.org/people/unfa/sounds/174499/)              | CC Attribution |
| Pepper mill grinds pepper | Black_River_Phonogram | [freesound](https://freesound.org/people/Black_River_Phonogram/sounds/424605/) | CC0            |
| Slimey                    | Nebulasnails          | [freesound](https://freesound.org/people/nebulasnails/sounds/495116/)          | CC0            |
| blood_sucker              | Bernuy                | [freesound](https://freesound.org/people/Bernuy/sounds/268499/)                | CC Attribution |
| Organ Ambience, Calm, A   | InspectorJ            | [freesound](https://freesound.org/people/InspectorJ/sounds/411991/)            | CC Attribution |

##### Sit functionality - `sit` package

The code under `de.teamlapen.vampirism.sit` is adapted from bl4ckscor4's Sit mod and licensed under GNU GPLv3 (see
LICENSE.txt in that directory).

##### Radial screen - `lib.*.radialmenu` package

The code under `de.teamlapen.lib.lib.client.gui.screens.radialmenu` is adapted from David Quintana's Radial Menu and
licensed under the terms of the LICENSE.txt file in that directory.

