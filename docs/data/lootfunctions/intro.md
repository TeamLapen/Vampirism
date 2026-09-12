---
sidebar_position: 1
title: Overview
---

Vampirism registers custom **loot functions** (`Registries.LOOT_FUNCTION_TYPE`). They can be used
anywhere vanilla loot functions are accepted (loot table `functions`, `given_item_modifiers`, …).

Every function object has a `"function"` key naming the type, and – since they all extend
`LootItemConditionalFunction` – an optional `"conditions"` array of
[loot conditions](../lootconditions/intro).

:::tip JSON Schema
[`schemas/vampirism_loot_functions.schema.json`](https://raw.githubusercontent.com/TeamLapen/Vampirism/refs/heads/dev/schemas/vampirism_loot_functions.schema.json)
:::

| `function`                       | Effect                                                            | Page |
|----------------------------------|-----------------------------------------------------------------|------|
| `vampirism:set_vampire_book`     | Fill a vampire book with random content from a tag                | [Set Vampire Book](./set_vampire_book) |
| `vampirism:set_item_blood_charge`| Charge an `IBloodChargeable` item                                 | [Charge Blood](./set_item_blood_charge) |
| `vampirism:add_refinement_set`   | Apply a random refinement set to an accessory                     | [Add Refinement Set](./add_refinement_set) |
| `vampirism:set_oil`              | Apply an oil to the item                                          | [Set Oil](./set_oil) |
| `vampirism:set_blood`            | Set the blood amount of a blood bottle                            | [Set Blood](./set_blood) |
| `vampirism:biome_map`            | Turn a map into a vampire-forest locator map                      | [Biome Map](./biome_map) |

Vampirism also ships one global loot modifier – see [Smelting](./smelting).
