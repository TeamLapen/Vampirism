---
sidebar_position: 4
title: Adjustable Level Entity
---

This condition will check involved entities for their level. This works only for Entities which implements the `IAdjustableLevel` interface (Basic Vampire and Hunters for example).

### 

This will match if the specified entity parameter has the exact level

```json
{
  "condition": "vampirism:adjustable_level",
  "predicate": {
    "level": "<level>",
    "entity": "<entity target>"
  }
}
```
- `level` The exact level of the entity
- `entity` The type of entity target. Can be `this`, `killer`, `direct_killer` or `killer_player` from the vanilla types.