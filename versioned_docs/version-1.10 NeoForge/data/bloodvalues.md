---
sidebar_position: 3
title: Blood Values
---

Blood values can be configured for items, entities and fluids.

Starting with NeoForge Vampirism uses the new data maps to assign blood values to items, fluids and entities. Take a look at the [official wiki](https://docs.neoforged.net/docs/datamaps/structure) for more information about data maps.

## Items
Item blood values are used by the [Grinder](../wiki/content/blocks#grinder) to determine the amount of impure blood of an item when grinding it.
A configured value of `20` means that the grinder will produce 20mb impure blood when grinding the item.

### Schema
```json title="data/vampirism/data_maps/item/item_blood_value.json"
{
  "replace": false,
  "values": {
    "<item-id>": {
      "blood": <blood-amount>
    },
    "<item-id>": {
      "blood": <blood-amount>
    }
  }
}
```

| Field     | Type             | Description                                                                                                   |
|-----------|------------------|---------------------------------------------------------------------------------------------------------------|
| `replace` | bool             | If the values should replace the existing values. If `false` the values will be added to the existing values. |
| `item-id` | ResourceLocation | The id of the item tha should be convertible to impure blood                                                  |
| `blood`   | int              | The amount of blood the item will produce when grinding it. e.g. `20` for 20mb impure blood.                  |

## Entities
Entity blood values are relevant for biting creatures as Vampire. It determines the amount of blood the creature has or if it has no blood at all.
A configured value of `10` means that the creature can fill 10 half-blood / 5 blood in the blood bar.
A value of `0` means that the creature cannot be bitten.

### Schema
```json title="data/vampirism/data_maps/item/entity_blood_value.json"
{
  "replace": false,
  "values": {
    "<entity-id>": {
      "blood": 0  # no blood
    },
    "<entity-id>": {
      "blood": <blood-amount>
    }
  }
}
```

| Field             | Type             | Description                                                                                                   |
|-------------------|------------------|---------------------------------------------------------------------------------------------------------------|
| `replace`         | bool             | If the values should replace the existing values. If `false` the values will be added to the existing values. |
| `entity-id`       | ResourceLocation | The id of the entity that should have blood or no blood                                                       |
| `blood`           | int              | The amount of blood the entity will produce when bitten. e.g. `10` for 10 half-blood / 5 blood.               |

## Fluids
Fluid blood values are conversion rates from other fluids to blood. It is used by the [Blood Sieve](../wiki/content/blocks#blood-sieve) to transform [Impure Blood](../wiki/content/fluids#impure-blood) into [Blood](../wiki/content/fluids#blood).
But this also supports third party fluids if configured. A configured value of `0.75` means that 1 bucket of the fluid is converted to 0.75 buckets of blood.

### Schema

```json title="data/vampirism/data_maps/item/fluid_blood_conversion.json"
{
  "replace": false,
  "values": {
    "<fluid-id>": {
      "conversionRate": <conversion-rate>
    },
    "<fluid-id>": {
      "conversionRate": <conversion-rate>
    }
  }
}
```

| Field            | Type             | Description                                                                                                   |
|------------------|------------------|---------------------------------------------------------------------------------------------------------------|
| `replace`        | bool             | If the values should replace the existing values. If `false` the values will be added to the existing values. |
| `fluid-id`       | ResourceLocation | The id of the fluid to convert to blood                                                                       |
| `conversionRate` | float            | The conversion rate from the fluid to impure blood. e.g. `0.75` for 1 bucket to 0.75 buckets of blood.        |