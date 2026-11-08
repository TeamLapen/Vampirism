---
sidebar_position: 6
title: Bite-able Creatures & Blood Grinder
---

In Vampirism, you can bite several creatures to get blood.  
Sometimes they even become vampires and act different.
The [Blood Grinder](#blood-grinder) can be used to extract blood out of some items, the [Blood Sieve](#blood-sieve) converts blood of other mods.

## Dynamically calculated entity values
Values for unknown animals are dynamically calculated during runtime (can be disabled with the server option `autoCalculateEntityBlood`).  
Thereby creatures from most other mods should be biteable.  
The values are calculated based on the entities (collision box) size. However, there is a minimum size requirement for making a creature biteable. Also, the values are capped at a certain values and set lower slightly than the hardcoded values to avoid exploits.  

Value are indirectly synced between server and client and also stored with the entity itself (so changed values only affect newly spawned creatures).

## Adjusting the values / Adding new biteable creatures / Preventing dynamic calculation

Defining such values are done by [data packs](../../data/bloodvalues.md) (item blood for the Blood Grinder, entity blood, fluid blood conversion for the Blood Sieve)

## Blood Grinder
The Blood Grinder extracts blood from meat and other blood-rich items (the amount is defined by the [item blood values](../../data/bloodvalues.md)).
- The items are inserted from the top.
- It needs **Fabric Filters** which are inserted from the side (by hand or e.g. hopper) and wear out while grinding.
- The blood is poured down into a container or extracted from below (e.g. by pipes). It can also be collected manually with buckets or bottles.
- It can be turned off with a redstone signal, similar to hoppers.

## Blood Sieve
The Blood Sieve converts blood from other mods into the blood of this mod ([fluid blood conversion](../../data/bloodvalues.md)). The fluid is inserted from the top, either via containers or pipes, and filtered. It also uses up Fabric Filters inserted from the side. The result is poured down into a container or extracted from below.

## Adding a new convertible creature
_If you want a certain creature to be convertible to a vampire version, add it to the `entity_converter` [data map](../../data/convertibles.md)._  
You also need an overlay image. It has to have the size of entity's texture and has to be see-though. Anything in this image will be rendered over the original entity texture (see [Overlay texture](../../data/convertibles.md#overlay-texture) for the location).  
Example (Cow): [Image](https://raw.githubusercontent.com/TeamLapen/Vampirism/aec60d2086c88093f7cf854e0327e98295994201/src/main/resources/assets/vampirism/textures/entity/vanilla/cow_overlay.png)  

- First get the texture of the creature (either it is available e.g. on Github, or you have to open the mod file (as zip, e.g. using 7zip, or by changing the file ending to .zip) and navigate to 'resources/assets...').
- Then the file with an image tool that supports layers (e.g. GIMP or Photoshop). Create a new transparent layer and draw the 'converted changes' (e.g. red eyes) into this layer.
- Afterwards make the base layer invisible and only export your own drawings. Send the image to me (vampirism@maxanier.de)
- If you want to test how it looks in-game beforehand, you can save the complete image (with base layer) and put it into a resource pack (at the same path as you found it in the mod's file).

Then contact the mod authors of Vampirism (e.g. send the image to `vampirism@maxanier.de`).
If you are a mod author consider making your modded creatures convertible using the [API](../../api/intro.md).


