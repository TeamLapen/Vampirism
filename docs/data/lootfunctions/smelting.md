---
sidebar_position: 8
title: Smelting (Loot Modifier)
---

`vampirism:smelting` is a **global loot modifier** (`IGlobalLootModifier`), not a loot function. It
auto-smelts generated loot when the killing tool has the *Smelting Oil* applied.

It runs when:

* the `this_entity` parameter is a `LivingEntity`, **and**
* the `tool` parameter has the `vampirism:smelt` oil applied.

Each dropped item is replaced by its `minecraft:smelting` recipe result (if any), and the oil's
remaining duration is reduced by one use.

## File

Global loot modifiers are configured in `data/<namespace>/loot_modifiers/` and listed in
`data/<namespace>/loot_modifiers/global_loot_modifiers.json` (vanilla NeoForge mechanism). Vampirism's
own file:

```json title="data/vampirism/loot_modifiers/smelting.json"
{
  "type": "vampirism:smelting",
  "conditions": [
    { "condition": "vampirism:with_oil_item", "oil": "vampirism:smelt" },
    { "condition": "vampirism:faction", "type": "faction", "faction": "vampirism:hunter" }
  ]
}
```

| Field        | Required | Type                                       | Description                                            |
|--------------|----------|------------------------------------------|--------------------------------------------------|
| `type`       | yes      | `vampirism:smelting`                      | The modifier serializer.                               |
| `conditions` | yes      | [Loot condition](../lootconditions/intro)[] | Vanilla GLM conditions (may be empty).             |

The modifier has no other fields.
