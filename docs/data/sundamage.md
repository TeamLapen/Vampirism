---
sidebar_position: 5
title: Disable Sun Damage
---

Biomes, Dimension and Levels can be configured to not apply sun damage to vampires. They can be configured in the API in the config options and via data packs.

## No sun damage file

```json title="data/<modid>/vampirism/no_sun_damage.json"
{
  "biomes": [
    "#vampirism:has_faction/vampire"
  ],
  "dimensions": [
    "minecraft:the_nether",
    "minecraft:the_end"
  ],
  "levels": {
    "damage": [
      "minecraft:overworld"
    ],
    "no_damage": [
      "vampirism:vampire_level"
    ]
  }
}
```

- `biomes`: A list of biome is or biome tags that will not apply sun damage to vampires.
- `dimensions`: A list of dimension ids or dimension tags that will not apply sun damage to vampires.
- `levels/no_damage`: A list of level ids that will not apply sun damage to vampires.
- `levels/damage`: A list of level ids that will apply sun damage to vampires regardless of dimension settings.