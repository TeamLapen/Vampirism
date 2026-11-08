---
title: Set Level
sidebar_position: 4
---

Sets the faction and faction level of players. The faction is part of the command (e.g. `vampirism:vampire`, use autocomplete). If no level is given, level 1 is used. If the level exceeds the highest reachable level of the faction, it is capped. If no players are given, the command is executed on you.

## Syntax

```
/factions faction set <faction>
/factions faction set <faction> level [<level> [<players>]]
```

## Arguments

| Parameter | Type              | Description                                  |
|:----------|:------------------|:---------------------------------------------|
| faction   | `faction`         | A playable faction id, e.g. `vampirism:vampire` |
| level     | `integer`         | The target level (minimum 1)                 |
| players   | `player_selector` | A player target selector. If not given, the command is executed on you |

In dev builds there is also the shorter [`/level`](./dev_commands.md) command.

## Permissions

:::info

Requires permission level 2 (gamemaster/cheats)

:::
