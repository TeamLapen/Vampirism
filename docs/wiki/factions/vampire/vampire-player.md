---
sidebar_position: 1
title: Vampire Player
---

## Traits
* Players can have different Vampire levels
* Speed, damage, jump height and health are increased depending on the level
* The player's food bar is replaced by a blood bar and the blood is consumed over time
* Enough blood or blood shortage is treated similar to the food level effects
  * Vampires cannot starve, but they get severe de-buffs if they have no blood
* Players can suck blood from an animal and add it to their own blood bar 
* Mobs that were bitten and gave blood are converted to a vampire version or die
* Blood can be stored in [Blood Bottles](../../content/items#blood-bottle) and [Blood Containers](../../content/blocks#blood-container) and be consumed later 
* Eye and fang overlays are applied to the player texture
* Receive damage from the sun

## Appearance
As a vampire, your eyes and mouth will look different. This is achieved by overlaying a texture over your skin.
It can be customized in several ways:
- You can edit your appearance in the Vampire Appearance Gui, which is accessible over the Vampirism Menu. In that GUI you can select and preview different eye and fang types
- You can use the command `/vampirism eye 3` and `/vampirism fang 1` (try different numbers)
- You can completely disable this for all players (client side), by changing `vampirism-client.toml->client->render->vampireEyes` to `false` (see [here](../../configuration))

You can use the command `/eye <id>` to adjust the overlay to fit to your skin.  


## Blood
Your blood bar works very similarly to the food bar except that you have to drink blood and not eat food to fill it.  
If you have glass bottles or blood bottles in your hot bar, when you suck blood from a creature,
any blood that does not fit into the bar will be filled in those.
You can drink that later.
[Advanced blood management tricks video](https://youtu.be/N2v0kDbD0fc)
Do not worry if you have meat lying around, they will not go to waste.
You can grind them into blood using a [Blood Grinder](../../content/blocks#blood-grinder).

## Sun Damage
There are three types of impact of the sun: Nausea, weakness and damage.  
As of level two, you will become weak in the sun,
as of level three you will get a nausea effect and if you are level four or higher, you will receive actual damage.  
Luckily, you can sleep in coffins to avoid the daylight and unlock a skill which enables you to see better in the dark.

## Leveling
More information [here](./leveling)
## Skills
For all skills, please take a look [here](./skills)

## Vision

Vampires have the ability to see in different ways. Once visions are unlocked they can be switched by pressing "N" (default keybinding).
Currently, there are the following visions available:
- [Night Vision](./skills#night-vision): Have night vision
- [Blood Vision](./skills#blood-vision): Sense entities while otherwise being blind.

## Stop being a Vampire
If you should choose to be no longer a vampire,
you can search for an [Altar of Cleansing](../../content/blocks#altar-of-cleansing) in a temple or craft one.
There you can choose to leave your vampire life behind.
Be aware that you will lose all your progress.
If you want to become a Vampire again, you will need to start at level 1.

After interacting with the altar, you will die. **In Hardcore mode you will not die.**