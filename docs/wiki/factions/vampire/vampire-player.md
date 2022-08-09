---
sidebar_position: 1
title: Vampire Player
---

## Traits
* Player can have different Vampire levels
* Speed, damage, jump height and health are increased depending on the level
* The player's foodbar is replaced by a blood bar and the blood is consumed over time
* Enough blood or blood shortage is treated similar to the food level effects
* Players with level > 0 can suck blood from a mob, depending on the mob, the player gets blood added or deals damage to the other mob
* Mobs that were bitten and gave blood are converted to a vampire version or die
* Blood can be stored in blood bottles and blood containers and be consumed later 
* The skin is modified slightly if the player is a vampire 

## Appearance
As a vampire, your eyes and mouth will look different. This is achieved by overlaying a texture over your skin.
It can be customized in several ways:
- Starting with Vampirism 1.7 there is a GUI accessible from the skills menu (bottom left). In that GUI you can select and preview different eye and fang types
- You can use the command `/vampirism eye 3` and `/vampirism fang 1` (try different numbers)
- You can completely disable this for all players (client side), by changing `vampirism-client.toml->client->render->vampireEyes` to `false` (see [here](https://github.com/TeamLapen/Vampirism/wiki/Configuration))

You can use the command `/eye <id>` to adjust the overlay to fit to your skin.  


## Blood
Your blood bar works very similar to the food bar except that you have to drink blood and not eat food to fill it.  
If you have glass bottles or blood bottles in your hotbar when you suck blood from a creature, any blood that does not fit into the bar will be filled in those. You can drink that later.
[Advanced blood management tricks video](https://youtu.be/N2v0kDbD0fc)

## Sun Damage
There are three types of impact of the sun: Nausea, weakness and damage.  
As of level two you will become weak in the sun, as of level three you will get a nausea effect and if your are level four or higher, you will receive actual damage.  
Luckily you can sleep in coffins to avoid the daylight and unlock a skill which enables you to see better in the dark.

## Leveling
More information [here](https://github.com/TeamLapen/Vampirism/wiki/Leveling-as-a-Vampire-Player)
## Skills
Vampire Player can unlock skills. More information [here](https://github.com/TeamLapen/Vampirism/wiki/Skills).
### Selection of skills
#### Bat Transformation
At night you can convert to a bat and fly like in creative mod, but you cannot place or break blocks or use items. Another big disadvantage of the bat is its low health, so avoid hostile creatures. You can toggle it as often as you want, but at sunrise you will be automatically converted back.
#### Summon Bats
Can only be used when in bat mode and has a long cool down. It is designed to be used in multi-player, on activation a lot of bats spawn around you which makes it hard for other players to spot you. On top of that the summoned bats give any non vampire player they hit a short blindness effect. After some time the bats will die.
### A lot more
Just take a look ingame.