---
sidebar_position: 9
title: Wings
---

Vampire players can unlock special wing **textures**. Which player gets which texture is defined in a single
JSON file, `wings.json`.

:::tip JSON Schema
[`schemas/wings.schema.json`](https://raw.githubusercontent.com/TeamLapen/Vampirism/refs/heads/dev/schemas/wings.schema.json)
:::

:::warning Not a data pack
Unlike the other pages in this section, `wings.json` is **not** loaded from data packs. `WingsManager` reads
it with `VampirismMod.class.getResourceAsStream("/wings.json")` – the file at the root of the mod jar
([`projects/vampirism/src/main/resources/wings.json`](https://github.com/TeamLapen/Vampirism/blob/dev/projects/vampirism/src/main/resources/wings.json)) –
once during common setup. A data pack or resource pack cannot change it; it documents what ships with the mod
(and is what a fork would edit).
:::

## Format

The file is an array of texture blocks:

```json title="wings.json"
[
  {
    "texture": "dev",
    "players": [
      { "name": "maxanier", "id": "a5405ebf-9ff2-43bf-a260-075d2a2160cc" }
    ]
  }
]
```

| Field     | Required | Type   | Description |
|-----------|----------|--------|-------------|
| `texture` | yes      | enum   | `default`, `dev` or `castle_contest` – the lowercase names of `IWingsEntity.Texture`. An unknown value makes the entry fail to parse. |
| `players` | yes      | array  | Players that get this texture (may be empty). |

Player entry:

| Field  | Required | Type   | Description |
|--------|----------|--------|-------------|
| `id`   | yes      | string | The player's Minecraft account UUID (`8-4-4-4-12` lower-case hex). The only field used to match a player. |
| `name` | yes      | string | Human readable label only; never used for matching. |

A UUID may appear in several blocks – the player then gets each of those textures.

## How it is used

* Every player can use the `default` texture (`textures/entity/wings/wings.png`); the textures from `wings.json`
  are added to that (`WingsManager#getAvailableWings`). The textures are
  `wings.png`, `wings_dev.png` and `wings_castle_contest.png` in `assets/vampirism/textures/entity/wings/`.
* In a **development environment** (not `FMLEnvironment.isProduction()`) every texture is available to everyone.
* The available textures are offered in the vampire appearance screen (`VampirePlayerAppearanceScreen`) and
  checked server-side in the player's `Customization`.
* If the file is missing or cannot be parsed, a warning is logged. Entries that did parse are still used when
  only part of the file failed.
