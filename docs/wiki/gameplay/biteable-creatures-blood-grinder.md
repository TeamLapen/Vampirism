---
sidebar_position: 6
title: Bite-able Creatures & Blood Grinder
---

In Vampirism you can bite several creatures to get blood.  
Sometimes they even become vampires and act different.
The blood grinder can be used to extract blood out of some items.

## Dynamically calculated entity values
Starting with Vampirism 1.4 values for unknown animals (`instanceof AnimalEntity`) are dynamically calculated during runtime.  
Thereby creatures from most other mods should be biteable.  
The values are calculated based on the entities (collision box) size. However, there is a minimum size requirement for making a creature biteable. Also the values are capped at a certain values and set lower slightly than the hardcoded values to avoid exploits.  

Value are indirectly synced between server and client and also stored with the entity itself (so changed values only affect newly spawned creatures).

Dynamically calculated values are saved to and loaded from (`world/vampirism/dynamic-blood-values.txt`).

## Adjusting the values / Adding new biteable creatures / Preventing dynamic calculation
### 1.14
Starting with 1.14 blood values are specified within data packs.
There are tree types of blood values to add/modify:
1. Entity blood values (blood values for biting)
2. Item blood values (blood values for the grinder)
3. Fluid blood values (fluid conversion to blood)

(**adding** fluid values only effects if another mod supports fluid extraction into the Blood Sieve)

The pattern of the config file inside the datapack is "data/**_modid_**/vampirism_blood_values/**_type_**/**_modid_**.txt".
Replace **_modid_** with the desired ModId and **_type_** with either "entities", "items" or "fluids".

If the modid of the filename is not loaded as Mod the values will get ignored even if the items/entities exists, so make sure you use "minecraft"/"vampirism" if you want to override the default values.

Take a look at the [default values](https://github.com/TeamLapen/Vampirism/tree/1.14/src/main/resources/data/vampirism/vampirism_blood_values)

[Here](https://github.com/TeamLapen/VampirismDatapackExamples) you can find an example datapack
### 1.12
#### Users and mod pack creator
_If you want to adjust the amount of blood a creature gives, follow these instructions_
1. Go to your `.minecraft/config/vampirism` directory  
2. Create a `vampirism_blood_values.txt` file  (or `vampirism_blood_values_grinder.txt` for the grinder)
3. Set a blood value by adding a line `entity_id=value`, e.g `minecraft:cow=5` (or 'item_id=value' e.g. 'minecraft:beef=4' for the grinder)

To find out an entity's id, spawn one and use the "/vampirism-test entity" command near to it. One or more names should pop up, choose the one that sounds right.    

You also can change the blood value of all creatures (including modded) at once by changing `minecraft:multiplier=10` to something else (must be an integer). All blood values are multiplied by this divided by 10. This does not affect dynamically calculated ones. 

These values will overwrite any default values or dynamically calculated values.
The resulting file should look similar to [here](https://github.com/TeamLapen/Vampirism/blob/aec60d2086c88093f7cf854e0327e98295994201/src/main/resources/blood_values/default_blood_values.txt).  
If you have added values for a larger count of modded mobs, please send them to us so we can add them as defaults.  

To prevent a creature from having a dynamic value assigned set it's blood value to 0 here.

Blood grinder values can be modified as well, just create the file "vampirism_blood_values_grinder.txt" and insert an item id instead of an entity id like above.
#### Mod authors
If you want to add blood values for your creatures, you can either use the [API](https://github.com/TeamLapen/Vampirism/wiki/API) or (starting with Vampirism 1.4) send a [Inter-Mod-Communication-Message](https://github.com/TeamLapen/Vampirism/wiki/Inter-Mod-Communication-Messages#blood-values) via Forge:

## Adding a new convertible creature
_If you want a certain creature to be convertible to a vampire version, follow these instructions._
You first need to create a overlay image. It has to have the size of entity's texture and has to be see-though. Anything in this image will be rendered over the original entity texture.  
Example (Cow): [Image](https://raw.githubusercontent.com/TeamLapen/Vampirism/aec60d2086c88093f7cf854e0327e98295994201/src/main/resources/assets/vampirism/textures/entity/vanilla/cow_overlay.png)  

- First get the texture of the creature (either it is available e.g. on Github, or you have to open the mod file (as zip, e.g. using 7zip, or by changing the file ending to .zip) and navigate to 'resources/assets...').
- Then the file with an image tool that supports layers (e.g. GIMP or Photoshop). Create a new transparent layer and draw the 'converted changes' (e.g. red eyes) into this layer.
- Afterwards make the base layer invisible and only export your own drawings. Send the image to me (vampirism@maxanier.de)
- If you want to test how it looks in-game beforehand, you can save the complete image (with base layer) and put it into a resource pack (at the same path as you found it in the mod's file).

Then contact the mod authors of Vampirism (e.g. send the image to `vampirism@maxanier.de`).
If you are a mod author consider making your modded creatures convertible using the [API](https://github.com/TeamLapen/Vampirism/wiki/API).


