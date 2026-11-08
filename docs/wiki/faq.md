---
sidebar_position: 2
title: FAQ
---

#### I want more skill points
There are two ways of having more skill points by changing config values in the `factionapi-server.toml` (section `faction`):  
1) You can increase the `factionSkillPointsPerLevel` value (the default is 2; the config comment explains how to calculate the value to unlock all skills at max level)  
2) You can set the `factionUnlockAllSkills` value to `true` to be able to unlock all skills on max level

See [Configuration](configuration.md).
#### I can't bite/feed
1) Make sure the keybinding is not blocked by another mod  
2) Check if a red fang icon is displayed when looking at the entity  
3) Make sure to hold down the feed button  
4) On a server with permissions enabled you need the `vampirism.bite.feed` permission, see [Permissions](permissions.md)

#### Fire damage is too high
This can be configured in the `balance` configuration. See [Configuration](configuration.md)

#### I want werewolves etc.
Go write an addon mod for Vampirism (see [Addon Example](https://github.com/TeamLapen/VampirismAddonExample)) or checkout for [Werewolves Mod](https://github.com/TeamLapen/Werewolves)

#### How do I get vampire blood
Killing a Vampire NPC with a Stake in the main hand is the traditional method of getting Vampire Blood (it is a chance). Advanced Vampires (named Vampires) can drop it without a Stake when killed by a player. It can also be found in village hunter trainer chests and is dropped by the Mother.

#### I have a toxicant effect
This happens if you wear hunter armor as a vampire or the other way around

#### How do I become human ("un-vampire"/"un-hunter")
As a vampire look for an Altar of Cleansing (or craft one) and use it. As a hunter use a Sanguinare Injection at an Injection Chair.

#### How do I get vampire books
Vampire books can be found around the world (e.g. in mineshafts or vampire dungeons). They have cryptic names and content.

#### How do I get to Dracula
See [Dracula & Velmorra](dracula/intro.md).

#### You do not have the permission to do this action
Vampirism can use NeoForge's permission system to allow / disallow certain actions. Take a look at [Permissions](./permissions.md#permission-problems) for detailed information.

### I have another question
1) Checkout this wiki  
2) Make sure you have [GuideAPI-VP](https://www.curseforge.com/minecraft/mc-mods/guide-api-village-and-pillage/) 
installed and read the guide book  
3) Checkout our [Discord](https://discord.gg/wuamm4P)
