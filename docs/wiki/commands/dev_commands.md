---
title: Dev Commands
sidebar_position: 8
---

:::caution

These commands (and the short root aliases `/v` for `/vampirism` and `/f` for `/factions`) are only registered in development environments, not in released builds.

:::

Shortcuts that act on the executing player.

## Syntax

```
/level <faction> [<level>]
/lord [<faction> [<level>]]
/l vampire
/l hunter
```

- `/level` sets the faction and level (without level: the maximum level).
- `/lord` sets the maximum level and the lord level (without level: maximum lord level; without faction: your current faction).
- `/l vampire` / `/l hunter` set level 14 and lord level 5 of that faction.

## Arguments

| Parameter | Type      | Description                          |
|:----------|:----------|:-------------------------------------|
| faction   | `faction` | A playable faction id                |
| level     | `integer` | The target (lord) level, minimum 0   |

## Permissions

:::info

`/level` and `/lord` require permission level 2 (gamemaster/cheats), `/l` requires permission level 3 (admin)

:::
