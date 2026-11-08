---
title: Minions
sidebar_position: 14
---

Create, recall, respawn or purge the minions of a player. If no target is given, the command is executed on you.

## Syntax

```
/factions minion create <faction> [<name> [<skin> [<use_minion_skin> [<use_lord_skin>]]]]
/factions minion recall [<target>]
/factions minion respawn [<target>]
/factions minion purge [<target>]
```

### Create

Creates a new minion of the faction at your position. You have to be a lord of that faction with a free minion slot. The appearance arguments are optional, but have to be given in order.

| Parameter       | Type      | Description                                      |
|:----------------|:----------|:-------------------------------------------------|
| faction         | `faction` | The faction of the minion, e.g. `vampirism:vampire` or `vampirism:hunter` |
| name            | `string`  | The name of the minion                           |
| skin            | `integer` | The skin type of the minion (minimum 0)          |
| use_minion_skin | `boolean` | Use the minion specific skin                     |
| use_lord_skin   | `boolean` | Use the skin of the lord player                  |

### Recall

Calls all minions of the target to the target player.

### Respawn

Respawns all unclaimed minions of the target at the target player.

### Purge

:::danger

Do not use unless you know what you are doing.

:::

Purges the minion data of the target player. Requires a world reload.

| Parameter | Type     | Description                      |
|:----------|:---------|:---------------------------------|
| target    | `player` | A single player target selector  |

The target has to be able to have minions (lord level high enough).

## Permissions

:::info

Requires permission level 2 (gamemaster/cheats)

:::

See also [Modify Minion Inventory](./modify_minion_inventory.md).
