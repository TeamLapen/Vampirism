---
sidebar_position: 2
title: FAQ
---

#### I want more skill points
There are two ways of having more skill points by changing config values:  
1) You can increase the `skillPointsPerLevel` value in the `vampirism-balance.toml` (e.g. to 1.5 to have enough skill point on lvl 14 to unlock all skills)  
2) You can set the `unlockAllSkills` value in the `vampirism-server.toml` to `true` to be able to unlock all skills on max level

If you are more interested in a general powerup for vampires you can install the [Vampirism Godly](https://www.curseforge.com/minecraft/mc-mods/godly-vampirism) Mod. It automatically increases the `skillPointsPerLevel` value
#### I can't bite/feed
1) Make sure the keybinding is not blocked by another mod  
2) Check if a red fang icon is displayed when looking at the entity  
3) Make sure to hold down the feed button  

#### Fire damage is too high
This can be configured in the `balance` configuration. See [Configuration](configuration.md)

#### I want werewolves etc.
Go write an addon mod for Vampirism (see [Addon Example](https://github.com/TeamLapen/VampirismAddonExample)) or checkout for [Werewolves Mod](https://github.com/TeamLapen/Werewolves)

#### How do I get vampire blood
Killing a Vampire NPC with a Stake is the traditional method of getting Vampire Blood. Only the final hit has to be with a Stake so the first few hits may be done with a sword. Advanced Vampires (named Vampires) have a passive chance of dropping Vampire Blood without the need of a Stake. Hunter Expert villagers may offer Vampire Blood as a trade. 

#### I have a toxicant effect
This happens if you wear hunter armor as a vampire or the other way around

#### How do I become human ("un-vampire"/"un-hunter")
As a vampire look for an [Altar of Cleansing](content/blocks.mdx#altar-of-cleansing) (or craft one), as a hunter use a [Sanguinare Injection](content/items#sanguinare-injection) at a [Med Chair](content/blocks.mdx#injection-chair).

#### How do I get vampire books
Vampire books can be found around the world (e.g. in mineshafts or vampire dungeons). They have cryptic names and content.

#### Only player with the OP role can do certain actions (biting/using action/infecting)
Then you need to give the players the appropriate permissions in your permission plugin. See [Permissions](./permissions.md)
Should you not use any permission plugin this is most likely a bug in the server software, and you need to add a permission plugin yourself.

### I have another question
1) Checkout this wiki  
2) Make sure you have [Guide-API-VP](https://www.curseforge.com/minecraft/mc-mods/guide-api-village-and-pillage/) (1.14+) 
installed and read the guide book  
3) Checkout our [Discord](https://discord.gg/wuamm4P)