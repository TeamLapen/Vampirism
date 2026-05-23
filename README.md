Vampirism for Minecraft 26.1 – Dev branch 
============================================
[![](http://cf.way2muchnoise.eu/short_233029_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/vampirism-become-a-vampire) [![](https://img.shields.io/modrinth/dt/jVZ0F1wn?label=Modrinth)](https://modrinth.com/mod/vampirism) [![Gradle Build](https://github.com/TeamLapen/Vampirism/actions/workflows/build.yml/badge.svg)](https://github.com/TeamLapen/Vampirism/actions/workflows/build.yml) [![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0) [![Discord Server](https://img.shields.io/discord/430326060635258881)](https://discord.gg/wuamm4P) [![Crowdin](https://badges.crowdin.net/vampirism/localized.svg)](https://crowdin.com/project/vampirism)

[![forthebadge](https://forthebadge.com/images/badges/built-with-love.svg)](https://vampirism.dev)

## Mod Description

Vampires are fast, strong, and bloodthirsty entities, which do not like the sun, but don't fear the night, and the best thing is: You can become one!

This mod allows you to become a vampire with all its benefits and drawbacks.

After being bitten by a vampire or manually injecting some vampire blood you get an effect called "Sanguinare Vampiris" which eventually turns you into a vampire.

For a more detailed description head over to the Minecraft Forum or the Curseforge page.
## Links
[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg)](https://www.curseforge.com/minecraft/mc-mods/vampirism-become-a-vampire/files)
[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/vampirism/versions)
[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/translate/crowdin_vector.svg)](https://translate.vampirism.dev)
[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/documentation/gitbook_vector.svg)](https://wiki.vampirism.dev/docs/wiki/intro)
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
- [Mike](https://github.com/supermike1999) _Balancing/Ideas_


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
- Grid _Code/Textures/Models/Structures/Lore_
- T_Corvus _Textures_
- Zeyke _Textures_
- zozozrob _Textures/Models_

## Datapacks
Vampirism uses datapacks to configure blood values and other settings.

[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/documentation/ghpages_vector.svg)](https://wiki.vampirism.dev/docs/data/intro)

## API
Vampirism has an API you can use to add blood values to your mod's creatures or make them convertible and more. For more information and an overview checkout the wiki.

[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/documentation/ghpages_vector.svg)](https://wiki.vampirism.dev/docs/api/intro)

## Integrated Mods in Gradle

If you want to use FactionApi or Vampirism in your own mod, configure your Gradle setup as follows:

[![](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/documentation/ghpages_vector.svg)](./docs/depend-on.md)

## Code Structure

### Branches

| Branch                                             | Description                                |
|----------------------------------------------------|--------------------------------------------|
| dev                                                | The main development branch.               |
| version/<mc-version>/[<sub-version>/]<mod-version> | The main branch for a specific MC version. |
| feature/*                                          | A feature branches.                        |
| pages/*                                            | The wiki pages deployed to github pages.   |

### Projects

| Project         | Description                                                                                                                                                                                                                                               |
|-----------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `faction-api`   | Pure API module defining the faction system: interfaces for faction registration, player faction handlers, events, registries, and sync abstractions. Depend on this when you only need to read/interact with faction state.                              |
| `faction`       | Implementation of `faction-api`. Contains the full faction logic.                                                                                                                                                                                         |
| `vampirism-api` | Pure API module for the Vampirism mod, extending `faction-api`. Exposes interfaces for vampire and hunter players, creature conversion, world events, difficulty, data maps, and all Vampirism registries. This is the primary dependency for addon mods. |
| `vampirism`     | Full implementation of the Vampirism mod. Implements all vampire/hunter mechanics, world generation, entity conversions, mod integrations (JEI, GuideAPI, TerraBlender, …), and NeoForge service wiring. Depends on `vampirism-api` and `faction`;        |

## Code Style
The code style used in this project is the IntelliJ default one.

## License
The content in this repository is licensed as follows:  
*If not explicitly stated, the content is licensed under the [LGPLv3](licenses/CODE_LICENSE).*

##### Code
All source code is licensed under [LGPLv3](licenses/CODE_LICENSE).

##### Narrative Content
All narrative content (in-game books, letters, dialogues, lore entries, and descriptive texts) is licensed under the [Vampirism Narrative Content License (VNCL)](licenses/NARRATIVE_LICENSE).

##### Textures and Models
All textures and models included in the mod are licensed under the [Vampirism Asset License](licenses/ASSET_LICENSE)

##### Sounds
The sounds used in this mod are individually licensed and may only be used outside Vampirism under the respective licensing terms noted below.

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
| two drops.WAV             | Millavsb              | [freesound](https://freesound.org/people/Millavsb/sounds/197900/)              | CC0            |

##### Sit functionality - `sit` package

The code under `de.teamlapen.vampirism.misc.sit` is adapted from bl4ckscor4's Sit mod and licensed under GNU GPLv3 (see
LICENSE.txt in that directory).

##### Radial screen - `radialmenu` package

The code under `de.teamlapen.faction.client.gui.radialmenu` is adapted from David Quintana's Radial Menu and
licensed under the terms of the LICENSE.txt file in that directory.

