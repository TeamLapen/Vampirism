---
sidebar_position: 2
title: Has Applied Oil
---

This condition will check if the `tool` loot parameter is an item that has an applied oil.

### Restrict to oil

This will match if the item has this oil applied.

```json
{
  "condition": "vampirism:with_oil_item",
  "predicate": {
    "oil": "<oil-id>"
  }
}
```
- `oil-id`: is the id of the oil