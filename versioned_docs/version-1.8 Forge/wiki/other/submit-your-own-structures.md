---
sidebar_position: 6
title: Submit your own Structures
---

## Structures
Vampirism (will) generates several structures in the overworld (or rather will generate at the time of writing this).  
You can submit your own structures to Vampirism if you want.

### Before we start
- Make sure there is actually a demand for new structures (check [this issue](https://github.com/TeamLapen/Vampirism/issues/236)), if there already are a lot of structures present it is unlikely new ones will be accepted unless their are exceptionally good
- Remember also that it is not guaranteed that a submitted structure is actually accepted even if there is a demand. It is still at the mod author's own discretion to decide if the structure style and quality fits to the mod
- Yes this manual is long :D But I want to minimize the my time effort, ensure a quality level and prevent wasted time (by you and me)

### General restrictions
- Try to stay smaller than 16x16 blocks. Height should also be reasonable. Smaller structures are more likely to be accepted.
- Don't use any valuable blocks (metal or diamond blocks, netherward, ...)

### General procedure
- Recommend: Use a flat world (e.g. with redstone ready preset) to have a clean building ground
- Build structure
- Capture it using Vanilla's [structure blocks](https://minecraft.gamepedia.com/Structure_Block) ("/give \<username\> minecraft:structure_block")
- 
```
1. Place a block in corner mode with the structure name set in one of the top corners
2. Place a structure block in save mode with the structure name set in the diagonal opposite bottom corner 
3. Use "detect blocks" button to see if everything is captured (also make sure only the minimal required area is covered)
4. Click the "SAVE" button
```
- Create at one or two screenshots showcasing your structures
- Locate the structure file in your world/save directory in the "structures" subfolder. It should be a .nbt file.
- Upload the file and the screenshots [here](https://maxanier.de/projects/vampirism/submit_structure.php).
- Comment on [this issue](https://github.com/TeamLapen/Vampirism/issues/236) (or PM maxanier on Twitter or mail to vampirism@maxgb.de) mentioning the id you received when uploading, also include one screenshot again if possible.
- Done

### Special blocks
#### Chests and entities
If you want to include loot chests in your building, don't use chests, but place structure blocks in "data" mode and give them a unique id (e.g. "chest1", "chest2").
If you want like to see a special creature in the building (e.g. a hunter as home owner), place a structure block in "data" mode and give it a unique id (e.g. "entity1")
Don't place to many chests or entities.

##### Placeholder blocks
To allow Vampirism to integrate the structure better in the surroundings there are a few placeholder blocks.
It is very recommended to use them.
- Bedrock: Will be replaced by the terrains filler block (for Plains this is dirt, for Extreme Hills it is stone, ...)
- Mycelium: Will be replaced by the terrains surface block (for Plains this is grass, for Desert this is sand, ...)
- Sponge: Unlike any other block (**including** the Air blocks of the structure) this will not replace a terrain block. Use this for corners/areas which can stay as they are (generated terrain)

To make this more clear, take a look at these images:
###### Template
![Template](https://picload.org/image/drdppigr/2017-11-06_20.45.14.png)
###### Generated 1
![Generated 1](https://picload.org/image/drdppcwi/2017-11-06_20.42.33.png)
###### Generated 2
![Generated 2](https://picload.org/image/drdppcww/2017-11-06_20.44.45.png)
###### Generated 3
![Generated 3](https://picload.org/image/drdppcwl/2017-11-06_20.41.16.png)

