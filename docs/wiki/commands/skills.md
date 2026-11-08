---
title: Skills
sidebar_position: 13
---

Enable, disable or toggle skills by command instead of the skill gui. If `force` is set, the skill requirements (e.g. skill points, parent skills) are ignored.

`enable all` unlocks every skill of the player's faction. `disable all` locks all skills and resets the refinements. If no player is given, the command is executed on you.

## Syntax

```
/factions skills enable <skill> [<player>] [force]
/factions skills enable <skill> force
/factions skills enable all [<players>]
/factions skills disable <skill> [<player>]
/factions skills disable all [<players>]
/factions skills toggle <skill> [<player>] [force]
/factions skills toggle <skill> force
```

## Arguments

| Parameter | Type              | Description                                  |
|:----------|:------------------|:---------------------------------------------|
| skill     | `Skill`           | The skill id                                 |
| player    | `player_selector` | A single player target selector              |
| players   | `player_selector` | A player target selector (for `all`)         |
| force     | literal           | Ignore the skill requirements                |

## Permissions

:::info

Requires permission level 3 (admin)

:::
