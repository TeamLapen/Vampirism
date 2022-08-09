---
sidebar_position: 5
title: Commands
---

The `/vampirism` command is the parent command for everything (except test commands).

## Appearance commands

:::info

These commands can be used by everyone

:::

### Eye appearance
`/vampirism eye <type>`

- `<type>`: the eye texture id 

Change your vampire eye type

### Fang appearance
`/vampirism fang <type>`

- `<type>`: the fang texture id

Change your vampire fang type

### Glowing Eyes
`/vampirism glowingEye <on>`

- `<on>`: `true` or `false`
  Choose if your vampire eyes should be rendered glowing

### Title Gender
`/vampirism title-gender <female>`

- `<female>` `true` or `false`

Change the gender of your lord title. True means female.

## Utility commands

### Bind Action
`/vampirism bind-action <slot> <action>`

- `<slot>` action shortcut button
- `<action>` action to bind to the shortcut

There are some keybindings that can be used to quickly activate actions.
You can bind a specific action to one of these keys with this command.  
E.g. use `/vampirism bind-action 1 vampirism:bat` to bind the bat action to the first key.
Use autocomplete to see all available action ids.

## Usefully commands

### Get Current Dimension
`/vampirism currentDimension`

Prints the dimension id of the current dimension. Useful if custom sundamage dimensions should be specified in the config

## Cheat commands

:::info

These commands can only be used with cheat permissions

:::

### Set Level
`/vampirism level <faction> <level> [<player>]`

- `<faction>`: the faction id
- `<level>`: the new faction level
- `<player>`: the player to set the level for (optional)

This command can be used to set the faction level of a player. The player is optional, if not specified the command uses the executing player.


### Levelup
`/vampirism levelup [<player>]`

- `<player>`: the player which level should be increased (optional)

Increase the (faction) level of the player by one. Only works if the player is already part of a faction.  The player is optional, if not specified the command uses the executing player.

### Set Lord Level
`/vampirism lord-level <level> [<player>]`

- `<level>`: the new lord level
- `<player>`: the player to set the level for (optional)

Only works for player who are max level and their faction allows lord player.  
This command can be used to set the lord level of a player. The player is optional, if not specified the command uses the executing player.

### Blood Bar
`/vampirism bloodBar <empty/fill> [players]`  
`/vampirism bloodBar set <amount> [players]`

- `<empty/fill>`: `empty` or `fill`
- `<amount>`: the amount of blood to set
- `<players>`: the players to set the blood for (optional)

Manipulates the blood bar of given players or yourself if `<players>` is not given.

:::info

Following commands requires the player to be admin

:::
### Skill

`/vampirism skill <skill> [force]`  
`/vampirism skill disableall`

- `<skill>`: the skill id
- `<force>`: `true` or `false`

Unlock skills by command instead of the skill gui. If force is set skill point cap is ignored.

### Config
`/vampirism config`
With this command you can change some config values without editing the file. Rerun the command to undo a blacklist. Available commands are the following: 
#### Entity Bloodvalues
`/vampirism config bloodvalues entity [<entityid>]`

- `<entityid>`: the entity id to blacklist (optional)

Entities can be blacklisted by looking at them while execute the command or searched for in the suggestions. Which means that vampire can no longer get blood from them. (Works for newly spawned entities)

#### Sundamage
##### Blacklist
`/vampirism config sundamage blacklist biome [<biome>]`  
`/vampirism config sundamage blacklist dimension [<dimension>]`  

- `<biome>`: the biome id to blacklist (optional). If not given the current biome is used.
- `<dimension>`: the dimension id to blacklist (optional). If not given the current dimension is used.

Dimensions and Biomes can be blacklisted for vampire sundamage.
##### Enforcing
`/vampirism config sundamage enforce dimension [<dimension>]`

- `<dimension>`: the dimension id to enforce (optional). If not given the current dimension is used.
Dimensions can be enforced to deliver sundamage to vampires.

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

# Entity selectors
Vampirism adds a few custom entity selectors, which can be used together with the vanilla ones in commands ("@e[...]").  
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