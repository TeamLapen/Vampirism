---
sidebar_position: 5
title: Troubleshooting
---

## Procedure
1. Make sure you are using the latest Vampirism version for your MC version
2. Search open and closed issues here for similar bugs
3. Check known issues below
4. Make sure the issue is directly related to Vampirism or does only occur with Vampirism installed
5. Open an issue here and fill in the template completely. If the game crashes provide the crash report. See below

## Known issues
### Game crashes on player death
Some mod is likely messing with Forge capabilities/player entities.
#### Mohist (1.16)
Certain version of Mohist `1.16#700-740` are bugged.

### No vampires are spawning
#### General
This is a tricky issue as mob spawning is quite complex and many things can mess it up.
It is likely that either a) some other mod purposely modifies the spawning algorithm/list or b) some other mod accidentally spawns too many other entities.
Have a look on your mod list and try to spot suspicious mods. Then try to narrow it down by removing mods and testing vampire spawn in a freshly generated flat world


## Crash report - If your game crashes or does not start, we do NEED a crash-report
_Without a crash-report file, we can do absolutely nothing to help you._  
_So please follow the following steps (If you have questions about the steps themselves, feel free to ask):_

1. Find the Minecraft crash report file. If you use the standard Minecraft launcher, the folder can be found [here](https://minecraft.gamepedia.com/.minecraft). The interesting file is inside the _crash-reports_, choose the latest file. If you use another launcher, it might have a function to easily upload or locate log files.
2. Create an issue here, describe the problem and include the log either within a spoiler or as a [Paste](http://paste.ee) of it. Alternatively you can post it on our Discord.

There is a helpful guide [here](https://hypixel.net/threads/guide-how-to-post-a-crash-report.577718/)


Besides that there are two things which can make stuff easier for you or us:  
1. [MultiMc](http://multimc.org/) is a very useful launcher/mod manager, which allows you to upload your log files with one click.  
2. [OpenEye](http://openeye.openmods.info/download) is a useful mod, which sends crashes together with some useful information to the mod authors, so please consider installing this