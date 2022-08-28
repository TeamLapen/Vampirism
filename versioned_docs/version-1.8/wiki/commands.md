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

### Changelog
`/vampirism changelog`

Print changes in newer versions and links.

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
`/vampirism bloodBar <status> [<players>]`  
`/vampirism bloodBar set <amount> [<players>]`

- `<status>`: `empty` or `fill`
- `<amount>`: the amount of blood to set
- `<players>`: the players to set the blood for (optional)

Manipulates the blood bar of given players or yourself if `<players>` is not given.

:::info

Following commands requires the player to be admin

:::
### Skill

`/vampirism skill <skill> [<force>]`  
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

## Test Commands

the test commands are prefixed with `/vampirism-test`

:::caution

Test commands are cheat only

:::

### Charge Sword
`/vampirism-test setSwordCharged <amount> [<players>]`

- `<amount>`: the charged amount for the sword
- `<players>`: the players to set the charged amount for (optional). Default is the executing player.

Charges the held vampire sword to the given amount.

### Train Sword
`/vampirism-test setSwordTrained <amount> [<players>]`

- `<amount>`: the trained amount for the sword
- `<players>`: the players to set the trained amount for (optional). Default is the executing player.

Trains the held vampire sword to the given amount.

### Village
#### Capture
`/vampirism-test village capture <faction>`

- `<faction>`: the faction id of the faction that should control the village

Captures the nearby village for the given faction.

#### Abort ongoing capture
`/vampirism-test village abort`

Aborts a running capture process in the nearby village.

### Minions
#### Spawn new minion
`/vampirism-test minion spawnNew <faction> [<name> [<type> [<use_lord_skin>]]]`

*This commands not necessarily supports all factions*

- `<faction>`: the faction of the minion.
- `<name>`: the name of the minion (optional). Custom name for the minion
- `<type>`: the type of the minion (optional). Skin type of the minion.
- `<use_lord_skin>`: `true` or `false` (optional). Lets the minion uses the lord player's skin.

spawns a new minion of the faction with optional attributes. This requires the player to be a lord of the same faction with remaining minion space.

#### Recall Minions
`/vampirism-test minion recall`

Calls all minion to the player.

#### Recall Minions
`/vampirism-test minion recall`

Calls all minion to the player.

#### Respawn Minions
`/vampirism-test minion respawnAll`

Respawns all minions

#### Purge Minions
`/vampirism-test minion purge`

**Do not use**

Purges Minion handler and requires a world reload.

### Task Handling
#### Clear Tasks
`/vampirism-test task clear [<players>]`

- `<players>`: the players to clear the tasks for (optional). Default is the executing player.

Purges all players active tasks/completed tasks/visible tasks.

#### Refresh Tasks
`/vampirism-test task refreshTaskList [<players>]`

- `<players>`: the players to refresh the tasks for (optional). Default is the executing player.

Refreshes all non-accepted tasks.
#### Reset Task List
`/vampirism-test task resetTaskList [<players>]`

- `<players>`: the players to reset the tasks for (optional). Default is the executing player.

Like `refreshTaskList` but also removes non unique tasks

#### Reset Lord Tasks
`/vampirism-test task resetLordTasks [<players>]`

- `<players>`: the players to reset the tasks for (optional). Default is the executing player.

Resets active lord tasks.

### Nearby Entities
`/vampirism-test entity`

Prints ids of nearby creature entities that can be used to define bloodvalues.

### Current Biome
`/vampirism-test biome`

Prints current biome.

### Reset Actions
`/vampirism-test resetActions [<players>]`

- `<players>`: the players to reset the actions for (optional). Default is the executing player.

Resets action duration and cooldown for the executing player or given players.

### Garlic Check
`/vampirism-test garlicCheck [<print>]`

- `<print>`: `true` or `false` (optional). If set the garlic check will use print more data.

Shows garlic strength at current location..

### Give Accessories
#### For Slot
`/vampirism-test giveAccessories <slot> <set>`

- `<slot>`: the slot to give the accessories to (1-3)
- `<set>`: the refinement set that should be applied to the accessory

Creates an accessory for the accessory slot with the given refinement set
#### Random Refinements
`/vampirism-test giveAccessories random [<amount>]`

- `<amount>`: the amount of random accessories to give (optional). Default is 1.

Creates random accessories

#### Help
`/vampirism-test giveAccessories help`

Prints help message

### Faction Banner
`/vampirism-test factionBanner <faction>`

- `<faction>`: the faction id of the faction

Creates faction banner item for the given faction.

### Entity Classification
`/vampirism-test info-entities`

Prints entity counts of EntityClassifications (Creatures/Monsters/Hunter/Vampire).

### Marker
`/vampirism-test marker [<name>]`

- `<name>`: the name of the marker (optional). If not given the marker is named "Marker"

Prints a marker into the logfile.

### Debug Entity Data
`/vampirism-test printEntityNBT`
Writes nearby entities to nbt and print the saved data.

### Aggressive Villager
`/vampirism-test makeVillagerAgressive`

Arms nearby villager.

### Spawning Tent
`/vampirism-test tent [<advanced>]` 

- `<advanced>`: `true` or `false` (optional). If set the tent can spawn advanced hunter.

Turns a tent into a (advanced) spawner

### Vampire Book
`/vampirism-test vampireBook`

Adds a random vampire book to the players inventory.

### Debug Generation
`/vampirism-test debugGen`

Enables/Disables vampirism's debug gen mode

### Tun Tests
`/vampirism-test runTests`  

Runs tests

### Spawn Test Animal
`/vampirism-test spawnTestAnimal`

Spawn a cow with low health.

### Healing
`/vampirism-test heal`

Heals the player.

### Force Sync for Player
`/vampirism-test forcePlayerSync`

Forces a client sync from the server.

### Summon Dummy Entity
`/vampirism-test summonDummy <type>`

- `<type>`: `hunter` or `vampire` the type of the dummy entity (optional).

Summons dummy hunter/vampire without AI, but a damage tracker on right click.

## Entity selectors
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