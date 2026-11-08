---
title: Blood Bar
sidebar_position: 11
---

Fills, empties or sets the blood bar of vampire players. Players that are not vampires are ignored. If no players are given, the command is executed on you.

## Syntax

```
/vampirism bloodBar fill [<players>]
/vampirism bloodBar empty [<players>]
/vampirism bloodBar set <amount> [<players>]
```

## Arguments

| Parameter | Type              | Description                  |
|:----------|:------------------|:-----------------------------|
| amount    | `integer`         | The blood amount (minimum 0) |
| players   | `player_selector` | A player target selector. If not given, the command is executed on you |

## Permissions

:::info

Requires permission level 2 (gamemaster/cheats)

:::
