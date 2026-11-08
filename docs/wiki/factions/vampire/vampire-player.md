---
sidebar_position: 1
title: Vampire Player
---

## Traits
* Players can have different Vampire levels
* Max health increases with the level. Attack damage, attack speed and movement speed are no longer level bonuses, they are [skills](./skills) (Art of the Cut, Art of the Draw, Fleet of Foot)
* The player's food bar is replaced by a blood bar and the blood is consumed over time
* Enough blood or blood shortage is treated similar to the food level effects
  * Vampires cannot starve, but they get severe de-buffs if they have no blood
* Players can suck blood from an animal and add it to their own blood bar 
* Mobs that were bitten and gave blood are converted to a vampire version or die
* Blood can be stored in Blood Bottles and Blood Containers and be consumed later 
* Eye and fang overlays are applied to the player texture
* Receive damage from the sun, and are blinded when looking at it

## Appearance
As a vampire, your eyes and mouth will look different. This is achieved by overlaying a texture over your skin.
You can edit your appearance in the Appearance Screen (including a face editor) or with commands, see [Customization](./customization).
You can completely disable this for all players (client side), by changing `vampirism-client.toml->client->render->vampireEyes` to `false` (see [here](../../configuration)).

## Blood
Your blood bar works very similarly to the food bar except that you have to drink blood and not eat food to fill it.  
If you have glass bottles or blood bottles in your inventory, when you suck blood from a creature,
any blood that does not fit into the bar will be filled in those.
[Advanced blood management tricks video](https://youtu.be/N2v0kDbD0fc) (made for an older version)

**Blood Bottles** are drunk in one go. Blood left over that does not fit into your blood bar is not given back.  
Do not worry if you have meat lying around, it will not go to waste.
You can grind it into blood using a Blood Grinder.

## Sun
There are two separate effects of the sun, both only apply in direct sunlight during the day in dimensions where the sun harms vampires.

### Sun Damage
Depending on your level, direct sun affects you:
- from level 2: weakness
- from level 3: nausea
- from level 4: actual damage (starts after about 5 seconds in the sun)

The levels are configurable on the server (`balance` config). Sun damage does not apply
- in rain,
- while holding an Umbrella in the main hand,
- while in [Mist](./skills#mist) form or under the effect of [Aura of Darkness](./skills#aura-of-darkness),
- in creative or spectator mode,
- in biomes or dimensions that are marked as having no sun damage.

The *Sunscreen* effect (from the Sunscreen potion, the Sunscreen Beacon or the [Sun Protection](./skills#sun-protection) action) protects you from it. The Sunscreen *item* no longer exists.
[Tough Skin](./skills#tough-skin) reduces the damage.
Luckily, you can sleep in coffins to avoid the daylight and unlock a skill which enables you to see better in the dark.
Burning vampires look as if on fire and spawn ash particles. If the sun kills you, you turn to ash.

### Sun Blindness
Looking directly at the sun washes out your screen. The intensity depends on your level: it grows with every level up to level 4 and is then at its maximum.
It can be configured or switched off on the client, and the overlay can be hidden when shaders are active. It does not apply in Mist form.

## Downed but not out (DBNO)
Vampires of level 4 or higher (configurable) are not killed by lethal damage but go down instead, unless the damage source is one that kills vampires outright. You also cannot go down while you are neonatal.
After a wait (base 60 seconds, configurable, shortened by [Swift Resurrection](./skills#swift-resurrection)) you can resurrect.
You can also give up. After a resurrection you suffer from the neonatal effect for a while ([Fast Recovery](./skills#fast-recovery) shortens it).
Vampires below that level die normally.
With the mod PlayerRevive installed, the time spent downed there counts towards the wait.

## Leveling
More information [here](./leveling)
## Skills
For all skills, please take a look [here](./skills)

## Actions
Actions you unlocked can be bound to keys or used from the action radial menu. Among others:
- [Bat Form](./skills#bat-form): turn into a bat
- [Mist](./skills#mist) (Dracula only): turn into mist
- [Hissing](./skills#hissing): disorient nearby enemies
- [Dark Stalker](./skills#dark-stalker): move silently
- [Aura of Darkness](./skills#aura-of-darkness) (Dracula only): block the sun for nearby vampires

## Vision

Vampires have the ability to see in different ways. Once visions are unlocked they can be switched by pressing "N" (default keybinding).
Currently, there are the following visions available:
- [Night Vision](./skills#night-vision): Have night vision
- [Blood Vision](./skills#blood-vision): Sense entities while otherwise being blind.

## Stop being a Vampire
If you should choose to be no longer a vampire,
you can search for an Altar of Cleansing in a temple or craft one.
There you can choose to leave your vampire life behind.
Be aware that you will lose all your progress.
If you want to become a Vampire again, you will need to start at level 1.

After interacting with the altar, you will die. **In Hardcore mode you will not die.**
