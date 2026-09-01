---
title: Test Commands
sidebar_position: 13
---

:::caution

Test commands are cheat only

:::

Additionally, to the commands listed in the sidebar, there are some more commands available. They are mostly used for testing, but work the same way to fix bugs

The general syntax is:

```
/vampirism-test` <command> [<args>]
```

### Charge Sword

Charges the held vampire sword to the given amount.

#### Syntax

```
/vampirism-test setSwordCharged <amount> [<players>]
```

#### Arguments

| Parameter | Type              | Description                                                                 |
|:----------|:------------------|:----------------------------------------------------------------------------|
| amount    | `float`           | The amount of charges the sword should have (0.0-1.0)                       |
| players   | `player_selector` | A player target selector. If not given, the command will be executed on you |

### Train Sword

Train the held vampire sword to the given amount.

#### Syntax

```
/vampirism-test setSwordTrained <amount> [<players>]
```

#### Arguments

| Parameter | Type              | Description                                                                 |
|:----------|:------------------|:----------------------------------------------------------------------------|
| amount    | `float`           | The amount of training the sword should have (0.0-1.0)                      |
| players   | `player_selector` | A player target selector. If not given, the command will be executed on you |

### Village
#### Capture
Capture the nearby village for the given faction
##### Syntax
```
/vampirism-test village capture <faction>
```

#### Arguments

| Parameter | Type              | Description                                                                 |
|:----------|:------------------|:----------------------------------------------------------------------------|
| faction   | `Faction`         | The faction to capture the village for                                      |

#### Abort ongoing capture
Aborts a running capture process in the nearby village.

##### Syntax
```
/vampirism-test village abort
```

### Minions
#### Spawn new minion
Spawn a new minion of the faction with optional attributes. This requires the player to be a lord of the same faction with remaining minion space.
##### Syntax
```
/vampirism-test minion spawnNew <faction> [<name>] [<type>] [<use_lord_skin>]
```

##### Arguments

| Parameter     | Type         | Description                                                   |
|:--------------|:-------------|:--------------------------------------------------------------|
| faction       | `Faction`    | The faction of the minion.                                    |
| name          | `string`     | The name of the minion (optional). Custom name for the minion |
| type          | `MinionType` | The type of the minion (optional). Skin type of the minion.   |
| use_lord_skin | `boolean`    | Lets the minion uses the lord player's skin.                  |

#### Recall Minion
Call all minions to the player.

##### Syntax
```
/vampirism-test minion recall [<player>]
```

##### Arguments

| Parameter | Type              | Description                                                                 |
|:----------|:------------------|:----------------------------------------------------------------------------|
| players   | `player_selector` | A player target selector. If not given, the command will be executed on you |

#### Respawn Minions
Respawns all minions to the player
##### Syntax
```
/vampirism-test minion respawnAll [<player>]
```

##### Arguments

| Parameter | Type              | Description                                                                 |
|:----------|:------------------|:----------------------------------------------------------------------------|
| players   | `player_selector` | A player target selector. If not given, the command will be executed on you |


#### Purge Minions
**Do not use**
Purges the minion handler of the player and requires a world reload.
##### Syntax
```
/vampirism-test minion purge [<player>]
```

##### Arguments

| Parameter | Type              | Description                                                                 |
|:----------|:------------------|:----------------------------------------------------------------------------|
| players   | `player_selector` | A player target selector. If not given, the command will be executed on you |


### Nearby Entities
Prints ids of nearby creature entities that can be used to define bloodvalues.
#### Syntax
```
/vampirism-test entity
```

### Current Biome
Prints current biome.
#### Syntax
```
/vampirism-test biome
```

### Reset Actions
Resets action duration and cooldown for the executing player or given players.

#### Syntax
```
/vampirism-test resetActions [<players>]
```

#### Arguments

| Parameter | Type              | Description                                                                 |
|:----------|:------------------|:----------------------------------------------------------------------------|
| players   | `player_selector` | A player target selector. If not given, the command will be executed on you |


### Garlic Check
Shows garlic strength at the current location

#### Syntax
```
/vampirism-test garlicCheck [<print>]
```

#### Arguments

| Parameter | Type              | Description                                  |
|:----------|:------------------|:---------------------------------------------|
| print     | `boolean`         | If set the garlic check will print more data |


### Faction Banner
Creates faction banner item for the given faction.

#### Syntax
```
/vampirism-test factionBanner <faction>
```

#### Arguments

| Parameter | Type      | Description                          |
|:----------|:----------|:-------------------------------------|
| faction   | `Faction` | The faction to create the banner for |



### Entity Classification
Prints entity counts of EntityClassifications (Creatures / Monsters / Hunters / Vampires).

#### Syntax
```
/vampirism-test info-entities
```


### Marker
Print a marker into the logfile.
#### Syntax
```
/vampirism-test marker [<name>]
```

#### Arguments

| Parameter | Type     | Description                                                                  |
|:----------|:---------|:-----------------------------------------------------------------------------|
| name      | `string` | The name of the marker (optional). If not given the marker is named "Marker" |

### Debug Entity Data
Write nearby entities to nbt and print the saved data.
#### Syntax
```
/vampirism-test printEntityNBT
```


### Aggressive Villager
Arms nearby villager.
#### Syntax
```
/vampirism-test makeVillagerAgressive
```

### Spawning Tent
Turns a tent into an (advanced) spawner
#### Syntax
```
/vampirism-test tent [<advanced>]
```

#### Arguments

| Parameter | Type      | Description                                |
|:----------|:----------|:-------------------------------------------|
| advanced  | `boolean` | If set the tent can spawn advanced hunter. |


### Vampire Book
Add a random vampire book to the player inventory.

#### Syntax
```
/vampirism-test vampireBook
```

### Run Tests

Runs tests
#### Syntax
```
/vampirism-test runTests
```

### Spawn Test Animal
Spawns a cow with low health.
#### Syntax
```
/vampirism-test spawnTestAnimal
```

### Healing
Heals the player.
#### Syntax
```
/vampirism-test heal
```



### Force Sync for Player
Force a client sync from the server.
#### Syntax
```
/vampirism-test forcePlayerSync
```

### Summon Dummy Entity
Summons dummy hunter/vampire without AI, but a damage tracker on right-click.
#### Syntax
```
/vampirism-test summonDummy <type>
```

#### Arguments

| Parameter | Type     | Description                                                                                 |
|:----------|:---------|:--------------------------------------------------------------------------------------------|
| type      | `string` | The type of the dummy entity. The parameter may be filled with either `hunter` or `vampire` |
