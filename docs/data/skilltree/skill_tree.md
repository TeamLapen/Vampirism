---
title: Skill Tree
sidebar_position: 1
---

Skill trees are the tabs of the skill screen. They are a [FactionApi data pack registry](/factionapi/data/skilltrees#skill-tree) (`factionapi:skill_tree`), so Vampirism's trees are ordinary data pack files:

```
data/<namespace>/factionapi/skill_tree/<path>.json
```

Schema: [`skill_tree.schema.json`](https://raw.githubusercontent.com/TeamLapen/Vampirism/refs/heads/dev/schemas/skill_tree.schema.json). The format is documented in the FactionApi docs.

## Built-in skill trees

| Id | Faction | Unlocked for | Skill points |
|----|---------|--------------|--------------|
| `vampirism:vampire/level` | Vampire | every vampire | normal skill points |
| `vampirism:vampire/lord` | Vampire | lord level 1+ | normal skill points |
| `vampirism:vampire/dracula` | Vampire | players with the Dracula rank (`vampirism:dracula`) | `vampirism:dracula` pool (Dracula marks) |
| `vampirism:hunter/level` | Hunter | every hunter | normal skill points |
| `vampirism:hunter/lord` | Hunter | lord level 1+ | normal skill points |
| `vampirism:hunter/marshall` | Hunter | players with the Marshall rank (`vampirism:marshall`) | `vampirism:marshall` pool (research points) |

The Dracula and Marshall trees use Vampirism's own unlock predicates (`vampirism:dracula`, `vampirism:marshall`) and skill point pools. Trees are ordered with `order_after`: level, then lord, then the rank tree.

## Example

```json title="data/vampirism/factionapi/skill_tree/vampire/dracula.json"
{
  "background": "vampirism:block/dark_stone_bricks",
  "display": { "id": "vampirism:vampire_clothing_hat" },
  "faction": "vampirism:vampire",
  "name": { "translate": "gui.vampirism.skills.dracula" },
  "order_after": [ "vampirism:vampire/lord" ],
  "skill_point_tag": "vampirism:dracula",
  "unlock_predicate": {
    "type_specific": { "type": "vampirism:dracula" }
  }
}
```

To add skills to a tree, add [skill segments](./skill_nodes) that reference it.
