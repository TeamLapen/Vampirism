---
sidebar_position: 3
title: Configuration
---

## Configuration files
In NeoForge configuration is divided in general (`client` and `common`) and world specific (`server`) config types.  
Vampirism consists of two mods (Vampirism and the Faction API), each with its own files:

| File | Location | Content |
|:-----|:---------|:--------|
| *vampirism-client.toml* | `.minecraft/config/` | Client specific options (rendering, overlays, colors) |
| *vampirism-common.toml* | `.minecraft/config/` | Common options (world generation, recipes) |
| *vampirism-server.toml* | `<world-dir>/serverconfig/` | Gameplay options per world |
| *vampirism-balance.toml* | `<world-dir>/serverconfig/` | Fine-tuning of the balancing per world |
| *factionapi-client.toml* | `.minecraft/config/` | Client specific options of the faction system (HUD positions, faction menu button) |
| *factionapi-server.toml* | `<world-dir>/serverconfig/` | Options of the faction system per world |

If you want to change the world specific options before creating a world, either to change world-generation or to change it for all created worlds, you can use the `defaultconfigs` folder.
It is located in your `.minecraft` or server directory. Copy a server or balance config from an existing world to this folder and change the values as desired. It will be used for new worlds.

Additionally, Here is a video description of Vampirism's config system [Description](https://youtu.be/JyfdM_sv2WQ)

Even more detailed customization can be done via [datapacks](../data/intro.md).

:::note

Many options were renamed in 2.0. Settings shared by all factions (skill points per level, PvP between factions, minions, villages) now live in *factionapi-server.toml*.

:::

### Noticeable configuration options
#### Mob type compatibility/IMob
`entitiesIMob` in *factionapi-server.toml* (section `admin`)  
Change how Vampirism's mobs are seen by other mods. Values: `ALWAYS_IMOB`, `NEVER_IMOB`, `SMART`. See [IMob-Issue](other/imob-issue.md) for more information.

#### Skill points
In *factionapi-server.toml* (section `faction`):  
`factionSkillPointsPerLevel` and `factionSkillPointsPerLordLevel` Skill points per (lord) level (default 2). Other values are unbalanced.  
`factionUnlockAllSkills` Allow unlocking all skills upon reaching max level.

#### PVP and infection
`factionPvpOnlyBetweenFactions` in *factionapi-server.toml* (section `faction`): PVP is only allowed between members of different factions. Requires PVP to be enabled in the `server.properties`.  
`playerCanInfectPlayers`, `fangInfection` and `mobBiteInfection` in *vampirism-server.toml* control the infection with sanguinare vampirism.

#### Sundamage configuration
Exempt biomes and dimensions in *vampirism-server.toml* (section `sundamage`):  
`sundamageInUnknownDimensions` Whether vampires receive sundamage in dimensions that are not configured otherwise.  
`noSundamageDimensions` Dimension ids in which no sundamage is dealt.  
`noSundamageBiomes` Biome ids in which no sundamage is dealt.  

Sundamage can also be configured with the `no_sun_damage.json` datapack file, see [Sun Damage](../data/sundamage.md). All sources are merged.  
The ids of the current biome/dimension can be found in the F3 debug overlay, or toggled directly with [`/vampirism config sundamage blacklist`](commands/config.md).

#### Sunscreen Beacon
`sunscreenBeaconRadius` and `sunscreenBeaconMineable` in *vampirism-server.toml* (section `entities`).

#### Balance
Values like fire vulnerability (`fireVulnerabilityMod` in section `vampirePlayer`) or action durations and cooldowns are in *vampirism-balance.toml*.

#### GUI overlay render location
The position of the faction level indicator and the faction menu button can be changed in *factionapi-client.toml* (`factionLevelOverlayXPos`, `factionLevelOverlayYPos`, `factionMenuButtonXPos`, `factionMenuButtonYPos`). Overlays can be toggled in both client configs.

## Datapacks
As Mojang pushes more and more towards datapack based content/configuration, Vampirism has to adapt to that. Therefore, some things are only configurable via datapacks. This is a bit more complicated, but at the same time more powerful. This affects e.g. recipes, blood values and parts of world generation

To see things that can be configured via datapacks, take a look at [Data Packs](../data/intro.md).

For datapack creation in general checkout the [Minecraft Wiki](https://minecraft.wiki/w/Tutorials/Creating_a_data_pack).

There also is an example datapack [here](https://github.com/TeamLapen/VampirismDatapackExamples).


## Permissions
Using an appropriate server mod certain actions can also be controlled with permissions.  
See [Permissions](permissions.md)
