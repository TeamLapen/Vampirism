---
sidebar_position: 4
title: Add Refinement Set
---

`vampirism:add_refinement_set` applies a random refinement set to an accessory item (any
`IRefinementItem` – amulet, ring, obi belt). Non-accessory items pass through unchanged.

```json
{ "function": "vampirism:add_refinement_set", "faction": "vampirism:vampire" }
```

| Field        | Required | Type                                                            | Description                                                    |
|--------------|----------|-----------------------------------------------------------|--------------------------------------------------------|
| `faction`    | yes      | [Resource location](https://minecraft.wiki/w/Resource_location) | Faction id whose refinement sets are eligible.               |
| `conditions` | no       | [Loot condition](../lootconditions/intro)[]                | Standard conditional-function conditions.                    |
