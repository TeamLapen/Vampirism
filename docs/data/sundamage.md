---
sidebar_position: 4
title: Sun Damage
---

Vampires take damage in sunlight. You can exempt whole **biomes**, **dimension types** or
**dimensions (levels)** from that with a `no_sun_damage.json` file. The resolved settings are
**synced to the client** (used e.g. for the screen tint).

:::tip JSON Schema
[`schemas/no_sun_damage.schema.json`](https://raw.githubusercontent.com/TeamLapen/Vampirism/refs/heads/dev/schemas/no_sun_damage.schema.json)
:::

## Location

```
data/<namespace>/vampirism/no_sun_damage.json
```

This is read by a custom reload listener (not a data pack registry): every file whose path ends in
`vampirism/…/no_sun_damage.json` is loaded and **all packs stack** – later packs add to earlier ones
unless they set `replace`.

The same exemption sets are also fed by the server config
(`noSundamageBiomes`, `noSundamageDimensions`, `enforceSundamageDimensions`) and by the addon API
(`ISundamageRegistry`). Everything is merged together.

## Fields

```json title="data/vampirism/vampirism/no_sun_damage.json"
{
  "replace": false,
  "biomes": [
    "#vampirism:has_faction/vampire",
    "minecraft:deep_dark"
  ],
  "dimensions": [
    "minecraft:the_nether",
    "minecraft:the_end"
  ],
  "levels": {
    "no_damage": [ "mymod:shadow_realm" ],
    "damage": [ "minecraft:overworld" ]
  }
}
```

| Field              | Type                                                                     | Description                                                                                                  |
|--------------------|-----------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------|
| `replace`          | bool (default `false`)                                                  | If `true`, clears the biome / dimension / `no_damage` entries collected so far before applying this file. `damage` entries are not cleared. |
| `biomes`           | ([Resource location](https://minecraft.wiki/w/Resource_location) \| `#biome_tag`)[] | Biomes with no vampire sun damage. Defaults to `[]`.                                              |
| `dimensions`       | ([Resource location](https://minecraft.wiki/w/Resource_location) \| `#dimension_type_tag`)[] | **Dimension type** ids (e.g. `minecraft:the_nether`) with no sun damage. Defaults to `[]`. |
| `levels`           | object                                                                 | Per-dimension overrides (see below). Optional.                                                             |
| `levels.no_damage` | [Resource location](https://minecraft.wiki/w/Resource_location)[]       | Dimension (level) ids with no sun damage. Ids only, no tags. Defaults to `[]`.                             |
| `levels.damage`    | [Resource location](https://minecraft.wiki/w/Resource_location)[]       | Dimension (level) ids that **always** have sun damage, overriding everything else. Defaults to `[]`.        |

## Resolution order

For the vampire's current position, sun damage is checked in this order:

1. If the current **dimension** is in `levels.damage` → sun damage applies (highest priority).
2. Else if the current **dimension type** is exempt (`dimensions`) → no sun damage.
3. Else if the current **dimension** is in `levels.no_damage` → no sun damage.
4. Else if the current **biome** is exempt (`biomes`) → no sun damage.
5. Otherwise → sun damage applies.

For a dimension that appears in neither `no_damage` nor `damage`, and whose type is not exempt, the
`sundamageInUnknownDimensions` server config decides.
