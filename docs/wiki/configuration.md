---
sidebar_position: 3
title: Configuration
---

## Configuration files
In Minecraft Forge configuration is divided in general (`client` and `common`) and world specific (`server`) config types.  
Following that Vampirism's configuration is split across several files:
- *vampirism-client.toml* (located in `.minecraft/config/`) for client specific options
- *vampirism-common.toml* (located in `.minecraft/config/`) for common options
- *vampirism-server.toml* (located in `<world-dir>/serverconfig/`) for gameplay options per world
- *vampirism-balance.toml* (located in `<world-dir>/serverconfig/`) for fine-tuning of the balancing per world

If you want to change the world specific options before creating a world, either to change world-generation or to change it for all created worlds, you can use the `defaultconfigs` folder.
It is located in your `.minecraft` or server directory. Copy a server or balance config from an existing world to this folder and change the values as desired. It will be used for new worlds.

Additionally, Here is a video description of Vampirism's config system [Description](https://youtu.be/JyfdM_sv2WQ)

Even more detailed customization can be done via [datapacks](../data/intro.md).

### Noticeable configuration options
#### Mob type compatibility/IMob
'entityIMob' in *server*  
Change how Vampirism's mobs are seen by other mods. See https://github.com/TeamLapen/Vampirism/wiki/IMob-issue
#### Sundamage dimension configuration
The way how sundamage is handled in the vanilla overworld dimensions as well as additional modded ones can be configured.  
`sundamageUnknownDimension` in *server*  
Specify whether players should be able to receive sundamage in other (unspecified) dimensions.   
`sundamageDimensionsPositiveOverride` in *server*  
Add dimension ids to this list to make sure sundamage can be dealt in them.  
`sundamageDimensionsNegativeOverride` in *server*  
Add dimension ids to this list to make sure no sundamage can be dealt in them.  
  
To find out the id of the current dimension use `/vampirism currentDimensions`.
#### Sundamage biome configuration
By default, sundamage is dealt in all biomes except for the Vampire Forest. Additional biomes can be added to  
`sundamageDisabledBiomes` in *server*

To find out the id of the current biome use the F3 debug overlay.

#### GUI overlay render location
The position of the level indicator as well as the skill button can be changed in the client specific configuration.

## Datapacks
As Mojang pushes more and more towards datapack based content/configuration, Vampirism has to adapt to that. Therefore, some things are only configurable via datapacks. This is a bit more complicated, but at the same time more powerful. This affects e.g. recipes, blood values and parts of world generation

To see things that can be configured via datapacks, have a look at the following two folders: [First](https://github.com/TeamLapen/Vampirism/tree/1.18/src/main/resources/data/vampirism) and [Second](https://github.com/TeamLapen/Vampirism/tree/1.18/src/generated/resources/data/vampirism). Make sure to select the correct version branch.

For datapack creation in general checkout the [Minecraft Wiki](https://minecraft.fandom.com/wiki/Tutorials/Creating_a_data_pack).

There also is an example datapack [here](https://github.com/TeamLapen/VampirismDatapackExamples).


## Permissions
Using appropriate server mods/plugins certain actions can also be controlled with permissions.  
See [Permissions](https://github.com/TeamLapen/Vampirism/wiki/Permissions)