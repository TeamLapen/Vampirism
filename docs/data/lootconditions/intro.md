---
sidebar_position: 1
title: Overview
---

Vampirism registers custom **loot conditions** (`Registries.LOOT_CONDITION_TYPE`). They can be used
anywhere vanilla loot conditions are accepted: in loot table pools/entries, in predicates, and in the
`conditions` list of a global loot modifier.

Every condition object has a `"condition"` key naming the type.

:::tip JSON Schema
[`schemas/vampirism_loot_conditions.schema.json`](https://raw.githubusercontent.com/TeamLapen/Vampirism/refs/heads/dev/schemas/vampirism_loot_conditions.schema.json)
– validates a single Vampirism loot condition object.
:::

| `condition`                   | Checks                                            | Page |
|-------------------------------|-------------------------------------------------|------|
| `vampirism:faction`           | faction membership / level of `this_entity`       | [Faction](./faction) |
| `vampirism:with_oil_item`     | the `tool` has a specific applied oil             | [Has Applied Oil](./has_oil) |
| `vampirism:with_stake`        | a targeted entity holds a stake                   | [Holding Stake](./with_stake) |
| `vampirism:adjustable_level`  | a targeted `IAdjustableLevel` entity's exact level | [Adjustable Level](./adjustable_level) |
| `vampirism:is_tent_spawner`   | the `block_entity` is an active tent spawner      | [Tent Spawner](./is_tent_spawner) |

## Entity targets

Conditions that take a `target` use the vanilla `LootContext.EntityTarget` values:

`this`, `attacker`, `direct_attacker`, `attacking_player`, `target_entity`, `interacting_entity`.

The corresponding loot parameter must be provided by the context (e.g. `this` needs `this_entity`).
