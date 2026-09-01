---
sidebar_position: 2
title: Tasks
---

Tasks are small objectives that are handed out to faction players by a Faction Representative. A task
consists of a set of **requirements** the player has to fulfil, a **reward** that is granted on completion
and an optional list of **unlockers** that decide whether the task is offered to the player at all.

In the mod itself tasks are data generated, but they are ordinary datapack files and can be added,
overridden or removed by any datapack.

:::tip JSON Schema
A JSON schema for validation / editor auto-completion is available at
[`schemas/task.schema.json`](https://raw.githubusercontent.com/TeamLapen/Vampirism/refs/heads/dev/schemas/task.schema.json).
:::

## Location

Task files are loaded from the dynamic registry `factionapi:tasks`, so every file has to be placed in

```
data/<namespace>/factionapi/tasks/<path>.json
```

Use your own `<namespace>` for new tasks. Use `vampirism` (or `factionapi`) to override or remove one of
the built-in tasks.

## Id

The id of a task is derived from the file location, exactly like advancements or loot tables:
`data/mypack/factionapi/tasks/hunter/hunter_lord2.json` produces the id `mypack:hunter/hunter_lord2`.

The id is used for translations and for referencing a task as the parent of another task
(see [Parent Unlocker](#parent-unlocker)).

## Structure

```json title="data/<namespace>/factionapi/tasks/<name>.json"
{
  "title": { "translate": "task.mypack.my_task" },
  "description": { "translate": "task.mypack.my_task.desc" },
  "requirements": [],
  "reward": {},
  "unlocker": []
}
```

| Field          | Required | Type                                                                     | Description                                                                       |
|----------------|----------|-------------------------------------------------------------------------|---------------------------------------------------------------------------------|
| `title`        | yes      | [Component](https://minecraft.wiki/w/Raw_JSON_text_format)             | Display name of the task.                                                        |
| `description`  | no       | [Component](https://minecraft.wiki/w/Raw_JSON_text_format)             | Longer description shown in the task screen tooltip.                             |
| `requirements` | yes      | [Requirements](#requirements)                                          | Wrapper object holding the list of requirements to complete the task.           |
| `reward`       | yes      | [Reward](#rewards)                                                     | The single reward granted when the task is completed.                           |
| `unlocker`     | no       | [Unlocker](#unlockers)[]                                              | Conditions that must all be met for the task to be offered. Defaults to `[]`.   |

> Note: the field is `reward` (singular). It holds exactly one reward object, not a list.

## Requirements

```json
{
  "requirements": [
    {
      "type": "factionapi:entity",
      "...": "..."
    },
    {
      "type": "factionapi:item",
      "...": "..."
    }
  ]
}
```

Each entry is a typed object dispatched on its `type` field.

Most requirement types accept an optional `id` field. It is the unique key that is used to track the
player's progress for that requirement. If it is omitted it defaults to the `entityType` / `item` /
`stat` id of the requirement. **If a task contains two requirements that would resolve to the same
default id (e.g. two `factionapi:item` requirements for `minecraft:gold_ingot`) you have to set a
distinct `id` on at least one of them.**

The `description` field of a requirement is a
[Component](https://minecraft.wiki/w/Raw_JSON_text_format) and is shown next to the progress bar in the
task screen.

### Entity Requirement

Completed once the player has killed `amount` entities of the exact given entity type. Progress is read
from the vanilla `minecraft:killed` statistic.

```json
{
  "type": "factionapi:entity",
  "entityType": "minecraft:skeleton",
  "amount": 20,
  "description": { "translate": "entity.minecraft.skeleton" },
  "id": "minecraft:skeleton"
}
```

| Field         | Required | Type                                                                   | Description                                                      |
|---------------|----------|-----------------------------------------------------------------------|--------------------------------------------------------------|
| `entityType`  | yes      | [Resource location](https://minecraft.wiki/w/Resource_location)       | Entity type that has to be killed.                            |
| `amount`      | yes      | int (>= 0)                                                            | Number of kills required.                                     |
| `description` | yes      | [Component](https://minecraft.wiki/w/Raw_JSON_text_format)           | Text shown in the task screen.                                |
| `id`          | no       | [Resource location](https://minecraft.wiki/w/Resource_location)       | Progress key, defaults to `entityType`.                       |

### Entity Type Requirement

Like the [Entity Requirement](#entity-requirement) but matches against an **entity type tag** instead of
a single entity type. Killing any entity contained in the tag counts towards the requirement.

```json
{
  "type": "factionapi:entity_type",
  "entityType": "vampirism:hunter",
  "amount": 10,
  "description": { "translate": "task_tag.vampirism.hunter" },
  "id": "vampirism:hunter"
}
```

| Field         | Required | Type                                                                   | Description                                                                    |
|---------------|----------|-----------------------------------------------------------------------|----------------------------------------------------------------------------|
| `entityType`  | yes      | [Entity type tag](https://minecraft.wiki/w/Tag) id                    | Tag id **without** the leading `#` (e.g. `vampirism:hunter`).               |
| `amount`      | yes      | int (>= 0)                                                            | Number of kills required.                                                  |
| `description` | yes      | [Component](https://minecraft.wiki/w/Raw_JSON_text_format)           | Text shown in the task screen.                                             |
| `id`          | no       | [Resource location](https://minecraft.wiki/w/Resource_location)       | Progress key, defaults to the tag id.                                     |

### Item Requirement

Completed while the player has the described item stack (matching item **and** components) in their
inventory. The required `count` is taken from the `item` stack. The items are removed from the inventory
when the task is handed in.

```json
{
  "type": "factionapi:item",
  "item": {
    "id": "minecraft:gold_ingot",
    "count": 32
  },
  "description": { "translate": "item.minecraft.gold_ingot" },
  "id": "minecraft:gold_ingot"
}
```

| Field         | Required | Type                                              | Description                                                       |
|---------------|----------|--------------------------------------------------|---------------------------------------------------------------|
| `item`        | yes      | [Item stack](#item-stack)                        | Item, amount and components that have to be present.             |
| `description` | yes      | [Component](https://minecraft.wiki/w/Raw_JSON_text_format) | Text shown in the task screen.                        |
| `id`          | no       | [Resource location](https://minecraft.wiki/w/Resource_location) | Progress key, defaults to the item id.               |

### Stat Requirement

Completed once a given custom statistic has increased by `amount` since the task was accepted.

```json
{
  "type": "factionapi:stat",
  "stat": "factionapi:capture_village",
  "amount": 2,
  "description": { "translate": "stat.factionapi.capture_village" },
  "id": "factionapi:capture_village"
}
```

| Field         | Required | Type                                                                   | Description                                                                   |
|---------------|----------|-----------------------------------------------------------------------|--------------------------------------------------------------------------|
| `stat`        | yes      | [Resource location](https://minecraft.wiki/w/Resource_location)       | Id of a registered custom stat (`minecraft:custom_stat` registry).       |
| `amount`      | yes      | int (>= 0)                                                            | Required increase of the stat.                                          |
| `description` | yes      | [Component](https://minecraft.wiki/w/Raw_JSON_text_format)           | Text shown in the task screen.                                         |
| `id`          | no       | [Resource location](https://minecraft.wiki/w/Resource_location)       | Progress key, defaults to `stat`.                                      |

Faction API ships the stats `factionapi:capture_village` and `factionapi:win_village_capture`. Add-on
mods can register more.

### Boolean Requirement

Completed while a registered predicate returns `true` for the player. The predicate has to be registered
through the API (`factionapi:faction_player_boolean_supplier` registry); it cannot be defined purely in
a datapack.

```json
{
  "type": "factionapi:boolean",
  "function": "mymod:has_special_status",
  "description": { "translate": "task.mymod.special_status" }
}
```

| Field         | Required | Type                                                                   | Description                                                       |
|---------------|----------|-----------------------------------------------------------------------|-------------------------------------------------------------|
| `function`    | yes      | [Resource location](https://minecraft.wiki/w/Resource_location)       | Id of the registered boolean supplier.                       |
| `description` | yes      | [Component](https://minecraft.wiki/w/Raw_JSON_text_format)           | Text shown in the task screen.                              |

This requirement has no `id` field; the progress key is the `function` id.

## Rewards

`reward` is a single typed object dispatched on its `type` field.

### Item Reward

Gives the player a fixed item stack (dropped on the ground if the inventory is full).

```json
{
  "type": "factionapi:item",
  "item": {
    "id": "vampirism:human_heart",
    "count": 5
  }
}
```

| Field  | Required | Type                       | Description                     |
|--------|----------|---------------------------|-------------------------------|
| `item` | yes      | [Item stack](#item-stack) | The stack that is rewarded.    |

### Refinement Reward

A special item reward that hands out an accessory ("refinement") item. Any parameter that is left out is
rolled randomly from the available refinements for the faction.

```json
{
  "type": "factionapi:refinement",
  "faction": "#vampirism:is_vampire",
  "rarity": "rare"
}
```

| Field     | Required | Type                                              | Description                                                                                 |
|-----------|----------|--------------------------------------------------|-----------------------------------------------------------------------------------------|
| `faction` | yes      | Faction [holder set](https://minecraft.wiki/w/Tag) | One faction id, a list of faction ids, or a faction tag (`#namespace:tag`). Pool of factions the accessory can belong to. |
| `item`    | no       | [Item stack](#item-stack)                        | Force a specific accessory item instead of rolling one.                                    |
| `rarity`  | no       | enum                                             | Minimum refinement rarity: `common`, `uncommon`, `rare`, `epic`, `legendary`.             |

### Lord Level Reward

Raises the player's lord level to `targetLevel`. Only applies if the player is currently exactly one
level below `targetLevel`.

```json
{
  "type": "factionapi:lord_level",
  "targetLevel": 1,
  "description": { "translate": "task.vampirism.hunter_lord1.reward" }
}
```

| Field         | Required | Type                                                        | Description                       |
|---------------|----------|-----------------------------------------------------------|-------------------------------|
| `targetLevel` | yes      | int                                                       | Lord level to grant.             |
| `description` | yes      | [Component](https://minecraft.wiki/w/Raw_JSON_text_format) | Text shown as the reward.       |

### Consumer Reward

Runs a registered action on the player. The action has to be registered through the API
(`factionapi:faction_player_consumer` registry).

```json
{
  "type": "factionapi:consumer",
  "consumer": "mymod:grant_something",
  "description": { "translate": "task.mymod.reward" }
}
```

| Field         | Required | Type                                                        | Description                                 |
|---------------|----------|-----------------------------------------------------------|-----------------------------------------|
| `consumer`    | yes      | [Resource location](https://minecraft.wiki/w/Resource_location) | Id of the registered player consumer.  |
| `description` | yes      | [Component](https://minecraft.wiki/w/Raw_JSON_text_format) | Text shown as the reward.               |

### Map Reward

Gives the player a filled map pointing to the nearest structure of a structure tag.

```json
{
  "type": "factionapi:map",
  "destination": "minecraft:eye_of_ender_located",
  "displayName": "filled_map.mymod.stronghold",
  "decorationType": "minecraft:target_x"
}
```

| Field            | Required | Type                                                            | Description                                                          |
|------------------|----------|--------------------------------------------------------------|----------------------------------------------------------------|
| `destination`    | yes      | [Structure tag](https://minecraft.wiki/w/Tag) id             | Tag id **without** `#`; the map targets the nearest structure of this tag. |
| `displayName`    | yes      | string                                                       | Translation key used as the map's item name.                        |
| `decorationType` | yes      | [Resource location](https://minecraft.wiki/w/Resource_location) | Id of a `minecraft:map_decoration_type` used as the marker.      |

## Unlockers

Every entry in the optional `unlocker` array is a typed object dispatched on its `type` field. A task is
only offered when **all** unlockers report unlocked. An empty list (or a missing field) means the task
is always available.

### Level Unlocker

```json
{
  "type": "factionapi:level",
  "reqLevel": 5,
  "maxLevel": 10
}
```

| Field      | Required | Type | Description                                                      |
|------------|----------|------|------------------------------------------------------------|
| `reqLevel` | yes      | int  | Minimum faction level required.                             |
| `maxLevel` | no       | int  | Maximum faction level that still allows the task. `-1` (default) disables the upper bound. |

### Lord Level Unlocker

```json
{
  "type": "factionapi:lord_level",
  "reqLordLevel": 1,
  "exact": false
}
```

| Field          | Required | Type | Description                                                                 |
|----------------|----------|------|-----------------------------------------------------------------------|
| `reqLordLevel` | yes      | int  | Lord level to compare against.                                         |
| `exact`        | yes      | bool | `true`: player's lord level must equal `reqLordLevel`. `false`: it must be at least `reqLordLevel`. |

### Parent Unlocker

```json
{
  "type": "factionapi:parent",
  "parent": "vampirism:hunter/hunter_lord1"
}
```

| Field    | Required | Type                                                            | Description                                             |
|----------|----------|--------------------------------------------------------------|---------------------------------------------------|
| `parent` | yes      | [Task id](#id)                                               | Id of another task that has to be completed first. |

## Item stack

`item` fields use the vanilla item-stack-template format:

```json
{
  "id": "minecraft:potion",
  "count": 1,
  "components": {
    "minecraft:potion_contents": { "potion": "vampirism:vampire_fire_resistance" }
  }
}
```

| Field        | Required | Type                                                            | Description                                       |
|--------------|----------|--------------------------------------------------------------|---------------------------------------------|
| `id`         | yes      | [Resource location](https://minecraft.wiki/w/Resource_location) | Item id.                                    |
| `count`      | no       | int (1-99)                                                   | Stack size, defaults to `1`.                |
| `components` | no       | [Data component](https://minecraft.wiki/w/Data_component_format) map | Item components patch.              |

As a shorthand a bare item id string may be used instead of the object (`"item": "minecraft:diamond"`).

## Full example

```json title="data/vampirism/factionapi/tasks/hunter/hunter_lord1.json"
{
  "title": { "translate": "task.vampirism.hunter_lord1" },
  "requirements": {
    "requirements": [
      {
        "type": "factionapi:item",
        "description": { "translate": "item.minecraft.gold_ingot" },
        "id": "minecraft:gold_ingot",
        "item": { "id": "minecraft:gold_ingot", "count": 32 }
      },
      {
        "type": "factionapi:entity_type",
        "amount": 50,
        "description": { "translate": "task_tag.vampirism.vampire" },
        "entityType": "vampirism:vampire",
        "id": "vampirism:vampire"
      },
      {
        "type": "factionapi:stat",
        "amount": 3,
        "description": { "translate": "stat.factionapi.win_village_capture" },
        "id": "factionapi:win_village_capture",
        "stat": "factionapi:win_village_capture"
      }
    ]
  },
  "reward": {
    "type": "factionapi:lord_level",
    "description": { "translate": "task.vampirism.hunter_lord1.reward" },
    "targetLevel": 1
  },
  "unlocker": [
    {
      "type": "factionapi:level",
      "reqLevel": 14
    }
  ]
}
```

More generated examples can be found in the mod sources under
[`projects/vampirism/src/generated/resources/data/vampirism/factionapi/tasks/`](https://github.com/TeamLapen/Vampirism/tree/dev/projects/vampirism/src/generated/resources/data/vampirism/factionapi/tasks).
