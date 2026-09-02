---
sidebar_position: 1
title: Data Packs
---

Vampirism exposes a lot of its content through data packs, so pack makers and mod-pack authors can
tweak, extend or completely rework parts of the mod without any code.

Reference material:

* mod data – [`src/main/resources/data/vampirism`](https://github.com/TeamLapen/Vampirism/tree/dev/projects/vampirism/src/main/resources/data/vampirism)
* generated data – [`src/generated/resources/data`](https://github.com/TeamLapen/Vampirism/tree/dev/projects/vampirism/src/generated/resources/data)

## How data packs work

See the Minecraft Wiki: [Data packs](https://minecraft.wiki/w/Data_pack) ·
[Installing](https://minecraft.wiki/w/Tutorial:Installing_a_data_pack) ·
[Creating](https://minecraft.wiki/w/Tutorial:Creating_a_data_pack).

Mind the **namespace**: use `vampirism` to override one of Vampirism's own definitions, and your own
namespace for anything new.

## JSON schemas

Every custom format on the following pages has a JSON schema under
[`schemas/`](https://github.com/TeamLapen/Vampirism/tree/dev/schemas) in the repository (linked at the
top of each page) for editor auto-completion and validation.

## What you can customize

| Area                                | Notes                                                                 |
|-------------------------------------|-------------------------------------------------------------------|
| [Blood Values](./bloodvalues)       | Item / entity / fluid blood via data maps.                          |
| [Convertibles](./convertibles)      | Which creatures can be turned vampiric, and how.                    |
| [Sun Damage](./sundamage)           | Biomes / dimensions exempt from vampire sun damage.                 |
| [Recipes](./recipes/intro)          | Weapon Table, Alchemical Cauldron, Alchemy Table, Infuser.          |
| [Loot Conditions](./lootconditions/intro) | `vampirism:*` loot conditions.                                |
| [Loot Functions](./lootfunctions/intro)   | `vampirism:*` loot functions + the smelting loot modifier.    |
| [Tasks](./tasks)                    | Provided by **FactionApi** – see [its docs](/factionapi/data/tasks).      |
| Skill trees                         | Provided by **FactionApi** – see [its docs](/factionapi/data/skilltrees). |

Plus everything vanilla data packs already let you change (loot tables, advancements, tags, worldgen,
recipes, …), and Vampirism's tags for mob spawns, village content and more.
