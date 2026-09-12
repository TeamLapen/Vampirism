---
sidebar_position: 3
title: Skill Trees
---

Skills are unlocked by faction players through **skill trees**. Every playable faction has at least one
skill tree (typically one for normal leveling and one for lord leveling), and a tree is made up of
**skill segments** that are wired together into a graph.

Both skill trees and skill segments live in their own dynamic registries and are ordinary datapack
files, so they can be added, overridden or removed by any datapack. In the mod they are data generated.

:::tip JSON Schemas
* Skill tree – [`schemas/skill_tree.schema.json`](https://raw.githubusercontent.com/TeamLapen/Vampirism/refs/heads/dev/schemas/skill_tree.schema.json)
* Skill segment – [`schemas/skill_segment.schema.json`](https://raw.githubusercontent.com/TeamLapen/Vampirism/refs/heads/dev/schemas/skill_segment.schema.json)
:::

## Concepts

* A **skill tree** (`de.teamlapen.faction.api.factions.skills.ISkillTree`) defines the tree that is
  shown as a tab in the skill screen: which faction it belongs to, how it is unlocked, its title and
  icon, and which skill point pool it draws from.
* A **skill segment** (`de.teamlapen.faction.api.factions.skills.ISkillSegment`) is a single node in
  that tree. It holds one skill (or a self-exclusive choice between several skills), the segments it
  descends from (`parents`), the segments that lock it out (`locking_segments`) and an optional layout
  hint (`placement`).
* A segment with **no parents** is a *root*. Roots are placed at the top of the tree, are granted for
  free once the tree is unlocked, and are kept when the player resets their skills. A tree may have
  multiple roots.
* A segment can be unlocked as soon as **any** of its parents is unlocked.

## Location and Id

| Registry                    | Folder                                              |
|-----------------------------|----------------------------------------------------|
| `factionapi:skill_tree`     | `data/<namespace>/factionapi/skill_tree/<path>.json`    |
| `factionapi:skill_segment`  | `data/<namespace>/factionapi/skill_segment/<path>.json` |

The id is derived from the file location like any other datapack registry entry:
`data/vampirism/factionapi/skill_tree/vampire/level.json` produces the id `vampirism:vampire/level`,
and `data/vampirism/factionapi/skill_segment/vampire/blood_vision.json` produces
`vampirism:vampire/blood_vision`.

Use your own `<namespace>` for new content, or `vampirism` / `factionapi` to override built-in entries.

## Skill Tree

```json title="data/<namespace>/factionapi/skill_tree/<name>.json"
{
  "faction": "vampirism:vampire",
  "unlock_predicate": {
    "type_specific": {
      "type": "factionapi:player_faction",
      "faction": "vampirism:vampire"
    }
  },
  "display": { "id": "vampirism:vampire_book" },
  "name": { "translate": "gui.vampirism.skills.level" },
  "background": "vampirism:block/dark_stone_bricks",
  "name_suffix": "factionapi:default",
  "order_after": []
}
```

| Field              | Required | Type                                                                       | Description                                                                                                       |
|--------------------|----------|--------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| `faction`          | yes      | [Resource location](https://minecraft.wiki/w/Resource_location)         | Id of the playable faction this tree belongs to (e.g. `vampirism:vampire`, `vampirism:hunter`).              |
| `unlock_predicate` | yes      | [Entity predicate](https://minecraft.wiki/w/Predicate)                  | Checked against the player to decide whether the tree is unlocked. Re-evaluated when the faction / lord level changes. Use `{}` for "always unlocked". See [Unlock predicate](#unlock-predicate). |
| `display`          | yes      | [Item stack](tasks#item-stack)                                          | Icon shown for the tree tab in the skill screen.                                                             |
| `name`             | yes      | [Component](https://minecraft.wiki/w/Raw_JSON_text_format)              | Title of the tree.                                                                                          |
| `background`       | no       | [Resource location](https://minecraft.wiki/w/Resource_location)         | Background sprite, resolved to `<namespace>:textures/<path>.png`. Defaults to the built-in level background. |
| `name_suffix`      | no       | Skill tree [tag](https://minecraft.wiki/w/Tag) id                       | The skill-point pool the tree spends from (`ISkillTree#skillPointTag`). Tag id **without** `#`. Defaults to `factionapi:default`. |
| `order_after`      | no       | [Skill tree id](#location-and-id)[]                                       | The tree is displayed after all listed trees in the skill screen. Defaults to `[]`.                          |

:::note
Built-in pools are
`factionapi:default`.
:::

### Unlock predicate

`unlock_predicate` is a full vanilla [entity predicate](https://minecraft.wiki/w/Predicate). In
practice you use the `type_specific` field with one of the sub-predicates that Faction API registers:

#### `factionapi:player_faction`

Matches the current player against their own faction state.

```json
{
  "type_specific": {
    "type": "factionapi:player_faction",
    "faction": "vampirism:vampire",
    "level": 3,
    "lord_level": 1
  }
}
```

| Field        | Required | Type                           | Description                                                          |
|--------------|----------|--------------------------------|--------------------------------------------------------------------|
| `faction`    | no       | [Resource location](https://minecraft.wiki/w/Resource_location) | If set, the player must currently be in this faction.               |
| `level`      | no       | int                            | Minimum faction level.                                              |
| `lord_level` | no       | int                            | Minimum lord level.                                                 |

#### `factionapi:faction`

Same fields as above, but `faction` accepts any faction (not only playable ones).

## Skill Segment

```json title="data/<namespace>/factionapi/skill_segment/<name>.json"
{
  "tree": "vampirism:vampire/level",
  "skills": [
    "vampirism:vampire_attack_speed",
    "vampirism:vampire_speed"
  ],
  "parents": [
    "vampirism:vampire/sunscreen"
  ],
  "locking_segments": [],
  "placement": {
    "type": "after",
    "segment": "vampirism:vampire/some_other_segment"
  }
}
```

| Field              | Required | Type                                                            | Description                                                                                                                    |
|--------------------|----------|--------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------|
| `tree`             | yes      | [Skill tree id](#location-and-id)                                | The skill tree this segment is part of.                                                                                      |
| `skills`           | yes      | [Resource location](https://minecraft.wiki/w/Resource_location)[] (non-empty) | Skill ids contained in the segment. They are **self-exclusive** – the player picks exactly one. Use separate segments if multiple choices should be allowed. |
| `parents`          | no       | [Skill segment id](#location-and-id)[]                           | The segments this one descends from. Unlockable as soon as **any** parent is unlocked. Empty ⇒ this segment is a *root*. Defaults to `[]`. |
| `locking_segments` | no       | [Skill segment id](#location-and-id)[]                           | Once a skill in **any** listed segment is unlocked, this segment can no longer be unlocked. The relation is stored one way only, so a mutual exclusion must be declared on **both** segments. Defaults to `[]`. |
| `placement`        | no       | [Placement](#placement)                                       | Layout hint that positions this segment on one side of another segment in the same row. Omit to leave positioning to the layout. |

### Placement

```json
{
  "type": "after",
  "segment": "vampirism:vampire/finisher"
}
```

| Field     | Required | Type                            | Description                                             |
|-----------|----------|---------------------------------|-----------------------------------------------------|
| `type`    | yes      | `before` \| `after`             | `before` = left of, `after` = right of the referenced segment. |
| `segment` | yes      | [Skill segment id](#location-and-id) | The segment to position next to. Must be in the same row. |

## Full example

```json title="data/vampirism/factionapi/skill_tree/vampire/lord.json"
{
  "background": "vampirism:block/dark_stone_bricks",
  "display": { "id": "vampirism:vampire_minion_binding" },
  "faction": "vampirism:vampire",
  "name": { "translate": "gui.vampirism.skills.lord" },
  "order_after": [
    "vampirism:vampire/level"
  ],
  "unlock_predicate": {
    "type_specific": {
      "type": "factionapi:player_faction",
      "faction": "vampirism:vampire",
      "lord_level": 1
    }
  }
}
```

```json title="data/vampirism/factionapi/skill_segment/vampire/level_root.json"
{
  "tree": "vampirism:vampire/level",
  "skills": [
    "vampirism:vampire"
  ]
}
```

```json title="data/vampirism/factionapi/skill_segment/vampire/attack_or_movement_speed.json"
{
  "tree": "vampirism:vampire/level",
  "parents": [
    "vampirism:vampire/sunscreen"
  ],
  "skills": [
    "vampirism:vampire_attack_speed",
    "vampirism:vampire_speed"
  ]
}
```

More generated examples can be found in the mod sources under
[`projects/vampirism/src/generated/resources/data/vampirism/factionapi/skill_tree/`](https://github.com/TeamLapen/Vampirism/tree/dev/projects/vampirism/src/generated/resources/data/vampirism/factionapi/skill_tree)
and
[`.../skill_segment/`](https://github.com/TeamLapen/Vampirism/tree/dev/projects/vampirism/src/generated/resources/data/vampirism/factionapi/skill_segment).
