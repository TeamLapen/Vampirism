---
sidebar_position: 7
title: Biome Map
---

`vampirism:biome_map` turns a plain `minecraft:map` into a filled map pointing at the nearest
**Vampire Forest** relative to the closest village totem near the loot `origin`. If the item is not a
map, or no target is found, it is returned unchanged.

```json
{
  "function": "vampirism:biome_map",
  "destination": "#vampirism:has_faction/vampire",
  "decoration": "vampirism:crypt",
  "search_radius": 100
}
```

| Field                  | Required | Type                                                            | Default                          | Description                                        |
|------------------------|----------|-----------------------------------------------------------|--------------------------------|--------------------------------------------|
| `destination`          | no       | [Biome tag](https://minecraft.wiki/w/Tag) id                    | `#minecraft:has_swamp_hut`     | Biome tag the map targets.                        |
| `decoration`           | no       | [Resource location](https://minecraft.wiki/w/Resource_location) | `minecraft:woodland_mansion`   | Map decoration type placed on the target.         |
| `zoom`                 | no       | byte                                                          | `2`                            | Map zoom level.                                   |
| `search_radius`        | no       | int                                                          | `50`                           | Chunk search radius.                              |
| `skip_existing_chunks` | no       | bool                                                         | `true`                         | Skip already-generated chunks while searching.    |
| `conditions`           | no       | [Loot condition](../lootconditions/intro)[]                | –                              | Standard conditional-function conditions.         |
