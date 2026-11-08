---
title: Set Lord Level
sidebar_position: 5
---

Sets the player to the maximum level of the faction and sets the given lord level. If no lord level is given, lord level 1 is used. If no players are given, the command is executed on you.

## Syntax

```
/factions faction set <faction> lord [<level> [<players>]]
```

## Arguments

| Parameter | Type              | Description                                  |
|:----------|:------------------|:---------------------------------------------|
| faction   | `faction`         | A playable faction id, e.g. `vampirism:vampire` |
| level     | `integer`         | The target lord level (minimum 1)            |
| players   | `player_selector` | A player target selector. If not given, the command is executed on you |

In dev builds there is also the shorter [`/lord`](./dev_commands.md) command.

## Permissions

:::info

Requires permission level 2 (gamemaster/cheats)

:::
