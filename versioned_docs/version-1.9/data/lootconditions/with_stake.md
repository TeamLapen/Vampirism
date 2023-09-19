---
sidebar_position: 3
title: Holding Stake
---

This condition will check if the involved entity is holding a stake.

### 

This will match if the specified entity parameter has a stake in their main hand

```json
{
  "condition": "vampirism:with_stake",
  "predicate": {
    "entity": "<entity target>"
  }
}
```
- `entity target`: The type of entity target. Can be `this`, `killer`, `direct_killer` or `killer_player` from the vanilla types.