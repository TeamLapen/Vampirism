---
sidebar_position: 1
title: Factions
---

The faction condition will check the `this_entity` loot parameter. It will check if the entity is a player and if the player is a member of the specified faction.

### Restrict to faction

This will match if the player is in the given faction.
If min or max level are supplied they will be matched against the players level
```json
{
  "condition": "vampirism:faction",
  "predicate": {
    "type": "FACTION",
    "faction": "<faction-id>",
    "min_level": int,
    "max_level": int
  }
}
```
- `faction-id`: is the id of the faction
- `min_level`: is the minimum level of the faction. *Optional*
- `max_level`: is the maximum level of the faction. *Optional*

### No faction

This will only match if the player is not in a faction

```json
{
  "condition": "vampirism:faction",
  "predicate": {
    "type": "NO_FACTION"
  }
}
```

### Any faction

This will match if the player is in any faction
If min or max level are supplied they will be matched against the players level

```json
{
  "condition": "vampirism:faction",
  "predicate": {
    "type": "ANY_FACTION",
    "min_level": int,
    "max_level": int
  }
}
```

- `min_level`: is the minimum level of the faction. *Optional*
- `max_level`: is the maximum level of the faction. *Optional*