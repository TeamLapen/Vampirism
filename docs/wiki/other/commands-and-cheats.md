---
sidebar_position: 6
title: Commands and Cheats
---

It is obviously not recommend to use these commands during regular gameplay, since they might destroy your experience. Therefore most of them can only be used as an OP or in creative-mode.
But for creating videos or solving problems caused by a bug or testing the alpha versions the following commands might be helpful.   
**There might be more commands, but they are explained on the wiki pages where they are needed**

_You can find the command implementation here https://github.com/TeamLapen/Vampirism/tree/master/src/main/java/de/teamlapen/vampirism/command_



## `/vampirism <subcommand>`

### Public commands

>#### `glowingEye <true/false>`
>Choose if your vampire eyes should be rendered glowing
>
>#### `fang <type>`
>Change your vampire fang type. Type has to be a number >=0.
>
>#### `eye <type>`
>Change your vampire eye type. Type has to be a number >=0.
>
>#### `title-gender <true/false>`
>Change the gender of your lord title. True means female.
>
>#### `bind-action <slot> <action>`
>There are two keybindings that can be used to quickly activate actions.  
>You can bind a specific action to one of these keys with this command.  
>E.g. use `/vampirism bind-action 1 vampirism:bat` to bind the bat action to the first key.
>Use autocomplete to see all available action ids.
>
>#### `currentDimension`
>Prints the dimension id of the current dimension. Useful if custom sundamage dimensions should be specified in the config
>
>#### `changelog`/`checkForUpdate`
>If a new version is available prints changelog and download link

### Cheat commands

>#### `level <faction> <level> [<player>]`
>Use `/vampirism level vampirism:vampire 10` to become a vampire of level 10, or `/vampirism level vampirism:hunter 4` to become a hunter level 4. To become a human just use any faction and level 0.  
>You can optionally specify a player for this should be done.
>#### `levelup [<player>]`
>Increase the (faction) level of the player by one. Only works if the player is already part of a faction.
>
>#### `bloodBar <empty/fill> [players] or bloodbar set <amount> [players]`
Manipulates the bloodbar of given players or yourself.

### Admin only commands

>#### `skill <skill> [force]`/`skill disableall`
>Unlock skills by command instead of the skill gui. If force is set skillpoint cap is ignored.
>
>#### `config`
>With this command you can change some config values without editing the file. Rerun the command to undo a blacklist. Available commands are the following:
>> ##### `bloodvalues entity`
>> Entities can be blacklisted by looking at them while execute the command or searched for in the suggestions. Which means that vampire can no longer get blood from them. (Works for newly spawned entities)
>>
>> ##### `sundamage blacklist`
>> Dimensions and Biomes can be blacklisted for vampire sundamage.
>>
>> ##### `sundamage enforce`
>> Dimensions can be enforced to deliver sundamage to vampires.

### Removed in 1.16
>#### `checkForVampireBiome`
>##### replaced by `/locateBiome`
>Checks if there is a vampire biome somewhere in the world. Be careful with this since it will pause the server's game loop until it is finished.
>Usage: `/vampirism checkForVampireBiome`

## `/vampirism-test <subcommand>`

### usefull Commands
(most are admin-only)
>#### `setSwordCharged <amount> [players]`
>Charges held vampire sword to the given amount.
>
>#### `setSwordTrained <amount> [players]`
>Trains held vampire sword to the given amount.
>
>#### `village`
>>##### `capture <faction>`
>>Captures the nearby village for the given faction.
>>##### `abort`
>>Aborts a running capture process.
>
>#### `minion`
>>##### `spawnNew <faction> [<name> [<type> [<use_lord_skin>]]]`
>>spawns a new minion of the faction with optional attributes. This requires the player to be a lord of the same faction with remaining minion space.
>>
>>##### `recall`
>>Calls all minion to the player.
>>##### `respawnAll`
>>Respawns all minions
>>##### `purge`
>>**Do not use**. Purges Minion handler and requires a world reload.
>
>#### `task`
>>##### `clear [players]`
>>Purges all players active tasks/completed tasks/visible tasks.
>>##### `refreshTaskList [players]`
>>Refreshes all non accepted tasks.
>>##### `resetTaskList [players]`
>>Like `refreshTaskList` but also removes non unique tasks
>>##### `resetLordTasks [players]`
>>Resets active lord tasks.
>
>#### `entity`
>Prints ids of nearby creature entities that can be used to define bloodvalues.
>
>#### `biome`
>Prints current biome.
>
>#### `resetActions [players]`
>Resets action duration and cooldown for the executing player or given players.
>
>#### `garlicCheck [print]`
>Shows garlic strength at current location. If print is true send all world data about garlic.
>
>#### `giveAccessories`
>>##### `slot <set>`
>>Creates an accessory for the accessory slot with the given refinement set
>>##### `random [amount]`
>>Creates random accessories
>>##### `help`
>>Prints help message
>
>#### `banner <faction>`
>Creates faction banner

<details><summary>other test commands</summary>

>#### `info-entities`
>Prints entity counts of EntityClassifications (Creatures/Monsters/Hunter/Vampire).
>
>#### `marker [name]`
>Prints a marker into the logfile.
>
>#### `info-entity`/`printEntityNBT`
>Writes nearby entities to nbt and print the saved data.
>
>#### `makeVillagerAgressive`
>Arms nearby villager.
>
>#### `tent [advanced]`
>Turns a tent into a (advanced) spawner
>
>#### `vampireBook`
>Adds a random vampire book to the players inventory.
>
>#### `debugGen`
>Enables/Disables vampirism's debug gen mode
>
>####  `runTests`
>Runs tests
>
>#### `spawnTestAnimal`
>Spawn a cow with low health.
>
>#### `heal`
>Heals the player.
>
>#### `forcePlayerSync`
>Forces a client sync from the server.
>
>#### `summonDummy <hunter/vampire>`
>Summons dummy hunter/vampire without ai, but a damage tracker on right click.


</details>

---

# Entity selectors (as of version 1.1)
Vampirism adds a few custom entity selectors, which can be used together with the vanilla ones in commands ("@e[...]").  
**Note: Starting with 1.14 is necessary to use quotes `"` around anything containing `:`**  
Example: `/kill @e["vampirism:faction"="vampirism:vampire"]`

### Faction - 'vampirism:faction'
Select all entities that belong to a given faction. Can be inverted.  
_Example: `/kill @e["vampirism:faction"="vampirism:vampire"]`_  
Kills all vampire players and vampire mobs.
_Example 2: `/kill @e["vampirism:faction"=!"vampirism:vampire"]`_  
Kills all non vampire players and non vampire mobs.  

### Level - 'vampirism:level'
Selects all players that are on the given level  
_Example: `/kill @a["vampirism:level"=5,"vampirism:faction"="vampirism:vampire"]`_  
Kills all vampire players on level 5.   
For a range of levels use `5..8`  
_Example: `/kill @a["vampirism:level"=5..8,"vampirism:faction"="vampirism:vampire"]`_    

### MinLevel/MaxLevel - 'vampirism:minLevel'/'vampirism:maxLevel'
**Only 1.12**
Selects all players that are at least on the given level/at max on the given level.  
_Example: `/kill @e["vampirism:minLevel"=7]`_  
Kills all players that are level 7 or higher (vampire and hunter)
