---
sidebar_position: 3
title: Data Packs
---

Starting with 1.14 Vampirism uses datapacks for different content. This allows users and content creators to further customized their Vampirism experience or even built new challenges.  
Have a look at the mod data directory [here](https://github.com/TeamLapen/Vampirism/tree/1.14/src/main/resources/data) or at the incomplete example repositoy [here](https://github.com/TeamLapen/VampirismDatapackExamples)

## How it works
Checkout the Minecraft Wiki to see how [datapacks](https://minecraft.gamepedia.com/Data_pack) work.  
[Installing them](https://minecraft.gamepedia.com/Tutorials/Installing_a_data_pack)  
[Creating them](https://minecraft.gamepedia.com/Tutorials/Creating_a_data_pack)  

Also make sure you are familiar with the namespace concept. If you want to modify/override Vampirism's own definition you have to use "vampirism" for everything else use your own.

## Things that are customizable
### Vanilla things
You can modifiy Vampirism's advancements, loottables and the crafting table recipes.

### Weapon Table recipes
These are based on the standard recipes and located in the same folder. Use `vampirism:shaped_crafting_weapontable` or `vampirism:shapeless_crafting_weapontable` as type.
In addition to the vanilla arguments you can specify `level` (Integer), `lava` (Integer) and `skill` (Array of Strings).

### Alchemical Cauldron
These are based on the standard recipes and located in the same folder. Use `vampirism:alchemical_cauldron` as type.
Have a look on the included recipes on how these are structured.

### Skilltree
The skill tree is generated based on individual nodes. You can either replace a node (by putting a file in the same namespace/path), removed (by putting a file in the same namespace/path and put `{"removed":true}` inside) or extend (put a file in your own namespace and specify an existing node as `parent`. It is also possible to request a merge (by specifying `merge` instead of `parent` which is useful for other mods.  
The faction a skill node belongs to is determined by the root skill the node is branched off.

