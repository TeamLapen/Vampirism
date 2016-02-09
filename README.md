Vampirism for Minecraft 1.8.9 - REWRITE
=========

_This is totally WIP code for 1.8.9, check out the 1.7.10 branch for stable code_  
_This is a (partial) rewrite of Vampirism to clean things up_  

## Mod Description ##  

Vampires are fast, strong and blood-thirsty entities, which do not like the sun, but don't fear the night, and the best thing is: You can become one!

This mod adds several rituals which allow you to first become a vampire and then level up as a vampire, with higher levels you will get faster, stronger, better night vision etc, but it brings disadvantages with it, you take sun damage or are hunted by vampire hunters.

As a vampire you don't need to eat all that dry bread or eat these strange fruits called "apples", you prefer some red and tasty blood, which you have to suck from animals or better villagers, but be careful not every animal likes to give blood.

## Links ##  
[Minecraft Forum Thread](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/wip-mods/2364443-vampirism-become-a-vampire)  
[Downloads](http://minecraft.curseforge.com/mc-mods/233029-vampirism-become-a-vampire/files)  
[Help to translate](https://crowdin.com/project/vampirism)

## Team [![Join the chat at https://gitter.im/TeamLapen/Vampirism](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/TeamLapen/Vampirism?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)  
- maxanier _Code_  

#### Inactive ####
- Mistadon _Code/Models_  
- wildbill22 _Code_  
- LRA_10 _Models/Textures_

## Code Structure ##
The source code is currently divided into three parts, which might be split in the future.
#### Vampirism ####
Located in de.teamlapen.vampirism  
Contains the mod source code. Depends on the other two parts.  
#### Vampirism API ####
Located in de.teamlapen.vampirism.api  
Designed to be used by mods that only optionally interact with Vampirism as well as addon mods depending on Vampirism.  
Not final yet. Contact maxanier, if you want to see something in the api or plan on interacting with Vampirism.  
#### VampLib/TeamLapen Lib ####
Located in de.teamlapen.lib 
Independent mod (Contains @Mod).  
Provides Helpers and Registries to automate stuff like EntityUpdates.
Provides abstract classes/default implementations/interfaces to simplify things (located under de.teamlapen.lib.lib).  

## Licence ##
This mod is licenced under [LGPLv3](https://raw.githubusercontent.com/TeamLapen/Vampirism/master/LICENCE)

This mod uses these sounds from freesound:  
DST-VampireMonk.mp3 by Striderjapan -- http://www.freesound.org/people/Striderjapan/sounds/141368/ -- License: CC Attribution  
vampire bites by Bernuy -- http://www.freesound.org/people/Bernuy/sounds/268501/ -- License: CC Attribution  
