---
sidebar_position: 3
title: Blood Values
---

Blood values can be configured for items, entities and fluids.

## Types
In the following context the `file-id` is an identified for your file that is relevant if you want to  override values. This works similar to the tag system. Creating the file `vampirism.json` and setting `replace` to `true` will remove all values that vampirism sets in `vamprism.json` and adds your values.
### Items
Item blood values are used by the [Grinder](../wiki/content/blocks#grinder) to determine the amount of impure blood of an item when grinding it.
A configured value of `20` means that the grinder will produce 20mb impure blood when grinding the item.

File location : `data/<your-modid>/vampirism/bloodvalues/items/<file-id>.json`

### Entities
Entity blood values are relevant for biting creatures as Vampire. It determines the amount of blood the creature has or if it has no blood at all.
A configured value of `10` means that the creature can fill 10 half-blood / 5 blood in the blood bar.
A value of `0` means that the creature cannot be bitten.

File location : `data/<your-modid>/vampirism/bloodvalues/entities/<file-id>.json`

### Fluids
Fluid blood values are conversion rates from other fluids to blood. It is used by the [Blood Sieve](../wiki/content/blocks#blood-sieve) to transform [Impure Blood](../wiki/content/fluids#impure-blood) into [Blood](../wiki/content/fluids#blood).
But this also support third party fluids if configured. A configured value of `0.75` means that 1 bucket of the fluid is converted to 0.75 buckets of blood.

File location : `data/<your-modid>/vampirism/bloodvalues/fluids/<file-id>.json`

## Schema
All types share the same schema:

##### Minecraft 1.16
```json title="<fileid>.json"
{
  "replace": false,
  "values": {
    "<item_id>": <blood_value>,
    "<item_id>": <blood_value>,
    "<item_id>": <blood_value>,
    "<item_id>": <blood_value>
  }
}
```
- `<item-id>`: The registry id of the type.
- `<blood_value>`: The floating point blood value.

##### Minecraft 1.19
```json title="<fileid>.json"
{
  "replace": false,
  "values": [
    {
      "id": "<item_id>",
      "value": <blood_value>
    },
    {
      "id": "<item_id>",
      "value": <blood_value>
    },
    {
      "id": "<item_id>",
      "value": <blood_value>
    }
  ]
}
```
- `<item-id>`: The registry id of the type.
- `<blood_value>`: The floating point blood value.