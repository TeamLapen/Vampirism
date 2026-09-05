---
sidebar_position: 1
title: Overview
---

Vampirism adds several custom crafting stations, each with its own recipe type. Recipes are normal
Minecraft recipe files:

```
data/<namespace>/recipe/<name>.json
```

| Station                                          | `type`                                                                       | Page |
|-------------------------------------------------|----------------------------------------------------------------------------|------|
| Weapon Table                                     | `vampirism:shaped_crafting_weapontable`, `vampirism:shapeless_crafting_weapontable` | [Weapon Table](./weapontable) |
| Alchemical Cauldron                              | `vampirism:alchemical_cauldron`                                              | [Alchemical Cauldron](./alchemicalcauldron) |
| Alchemy Table                                    | `vampirism:alchemical_table`                                                | [Alchemy Table](./alchemytable) |
| Blood Infuser                                    | `vampirism:infuser`                                                         | [Infuser](./infuser) |

Default recipes:
[`data/vampirism/recipe/`](https://github.com/TeamLapen/Vampirism/tree/dev/projects/vampirism/src/generated/resources/data/vampirism/recipe).

## Shared building blocks

### `result`

Every result is a vanilla **item stack template**: either a bare item id, or

```json
{ "id": "vampirism:human_heart", "count": 5, "components": { /* … */ } }
```

`count` is `1`–`99` (default `1`); `components` is an optional data component patch.

### `skill` and `level`

The Weapon Table, Alchemical Cauldron and Alchemy Table are Hunter stations. Recipes can gate
themselves behind hunter progression:

| Field   | Type                                                          | Default | Meaning                                          |
|---------|-------------------------------------------------------------|---------|----------------------------------------------|
| `skill` | [Skill](/factionapi/api/registering-content) id[]           | `[]`    | All listed skills must be unlocked.            |
| `level` | int                                                         | `1`     | Minimum hunter faction level.                  |

### `show_notification`

All of these recipe types carry the vanilla `CommonInfo` (`show_notification`, default `true`).
The **field name differs per station**:

| Station             | Where it goes                                  |
|---------------------|----------------------------------------------|
| Weapon Table        | `show_notification` at the top level          |
| Alchemical Cauldron | inside `"info": { … }`                         |
| Alchemy Table       | inside `"common_info": { … }`                  |
| Infuser             | inside `"commoninfo": { … }`                   |

## CraftTweaker

The Weapon Table and Alchemical Cauldron recipe types can also be manipulated with CraftTweaker when
[Vampirism Integrations](https://www.curseforge.com/minecraft/mc-mods/vampirism-integrations) is
installed.
