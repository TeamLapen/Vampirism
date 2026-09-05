---
sidebar_position: 2
title: Blood Values
---

Vampirism assigns blood-related values to **items**, **entities** and **fluids** through three
[NeoForge data maps](https://docs.neoforged.net/docs/datamaps/structure). Each is an ordinary data
pack file, so any pack can add, override or remove entries.

All three data maps are **synced to the client**.

:::tip JSON Schemas
* Item blood – [`schemas/item_blood.schema.json`](https://raw.githubusercontent.com/TeamLapen/Vampirism/refs/heads/dev/schemas/item_blood.schema.json)
* Entity blood – [`schemas/entity_blood.schema.json`](https://raw.githubusercontent.com/TeamLapen/Vampirism/refs/heads/dev/schemas/entity_blood.schema.json)
* Fluid blood conversion – [`schemas/fluid_blood_conversion.schema.json`](https://raw.githubusercontent.com/TeamLapen/Vampirism/refs/heads/dev/schemas/fluid_blood_conversion.schema.json)
:::

## Data map file format

Every data map file uses the standard NeoForge wrapper:

```json
{
  "replace": false,
  "values": {
    "<id-or-#tag>": { /* entry */ }
  },
  "remove": [ "<id-or-#tag>" ]
}
```

| Field     | Type    | Description                                                                                  |
|-----------|---------|------------------------------------------------------------------------------------------|
| `replace` | bool    | `true` clears all previously loaded entries first. Defaults to `false` (merge).            |
| `values`  | object  | Maps a registry id (or a `#tag`) to an entry. Later data packs override earlier ones.     |
| `remove`  | array   | Ids (or `#tags`) to drop from the merged map. Optional.                                    |

Entries also accept a `neoforge:conditions` array to load them only when another mod/item is present.

## Item blood

```
data/<namespace>/data_maps/item/item_blood.json
```

Data map `vampirism:item_blood`. Sets how much **impure blood** (in mB) the
Blood Grinder produces from one of the item. `0` marks the item as
not grindable.

If an item has no entry, Vampirism falls back to `nutrition × 10` for uncooked items in the
`#minecraft:meat` tag, and `0` otherwise.

```json title="data/vampirism/data_maps/item/item_blood.json"
{
  "values": {
    "minecraft:beef": { "blood": 200 },
    "minecraft:mutton": 100,
    "minecraft:cooked_beef": { "blood": 0 }
  }
}
```

| Field   | Type            | Description                                                     |
|---------|-----------------|-------------------------------------------------------------|
| `blood` | int (>= 0)      | mB of impure blood produced per item. `0` = not grindable.   |

The entry may be written as a bare integer (`"minecraft:mutton": 100`) instead of the object form.

## Entity blood

```
data/<namespace>/data_maps/entity_type/entity_blood.json
```

Data map `vampirism:entity_blood`. Sets how much blood a creature holds when a vampire bites it, and
gates whether the creature can be [converted](./convertibles) at all. `0` marks the entity as **not
biteable** (and not convertible).

If an entity has no entry and the `autoCalculateEntityBlood` server config is enabled, Vampirism
derives a value from the creature's hitbox size (capped at `15`, and forced to `0` for creatures with
more than 50 max health). A data map entry always wins over the auto-calculation.

```json title="data/vampirism/data_maps/entity_type/entity_blood.json"
{
  "values": {
    "minecraft:cow": { "blood": 10 },
    "minecraft:cat": { "blood": 3 },
    "minecraft:chicken": { "blood": 0 }
  }
}
```

| Field   | Type            | Description                                                                 |
|---------|-----------------|-----------------------------------------------------------------------|
| `blood` | int (>= 0)      | Blood units the creature holds. `0` = not biteable / not convertible.  |

The entry must be the object form (`{ "blood": N }`).

## Fluid blood conversion

```
data/<namespace>/data_maps/fluid/fluid_blood_conversion.json
```

Data map `vampirism:fluid_blood_conversion`. Sets the conversion ratio a
Blood Sieve applies when turning a fluid into
Blood: `output_blood = conversionRate × input_amount`. `0` means the
fluid cannot be converted.

Vampirism ships this map empty; `vampirism:impure_blood` conversion is handled in code. Use it to make
third-party fluids convertible.

```json title="data/vampirism/data_maps/fluid/fluid_blood_conversion.json"
{
  "values": {
    "examplemod:diluted_blood": { "conversionRate": 0.75 }
  }
}
```

| Field            | Type   | Description                                                             |
|------------------|--------|-------------------------------------------------------------------|
| `conversionRate` | float  | Fraction of the input fluid amount returned as blood. `0` = no conversion. |

The entry may be written as a bare number (`"examplemod:diluted_blood": 0.75`) instead of the object form.
