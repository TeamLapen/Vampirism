---
sidebar_position: 5
title: Set Oil
---

`vampirism:set_oil` applies an oil to the item.

```json
{ "function": "vampirism:set_oil", "oil": "vampirism:vampire_blood" }
```

| Field        | Required | Type                                                            | Description                                                                 |
|--------------|----------|-----------------------------------------------------------|---------------------------------------------------------------------|
| `oil`        | no       | [Resource location](https://minecraft.wiki/w/Resource_location) | Oil id (`vampirism:oils` registry). If omitted, a random oil **not** in `vampirism:non_treasure` is chosen. |
| `conditions` | no       | [Loot condition](../lootconditions/intro)[]                | Standard conditional-function conditions.                                  |
