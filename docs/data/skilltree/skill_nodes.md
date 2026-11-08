---
title: Skill Segments
sidebar_position: 2
---

The nodes of a skill tree are **skill segments**, a [FactionApi data pack registry](/factionapi/data/skilltrees#skill-segment) (`factionapi:skill_segment`). A segment holds one skill, or several skills the player has to choose between, and the segments it follows.

```
data/<namespace>/factionapi/skill_segment/<path>.json
```

Schema: [`skill_segment.schema.json`](https://raw.githubusercontent.com/TeamLapen/Vampirism/refs/heads/dev/schemas/skill_segment.schema.json).

| Field | Description |
|-------|-------------|
| `tree` | The [skill tree](./skill_tree) the segment belongs to, e.g. `vampirism:vampire/level`. |
| `skills` | The skill ids in the segment. More than one means the player can only take one of them. |
| `parents` | Segments of which at least one has to be unlocked first. Without parents the segment is a root. |

See the [FactionApi page](/factionapi/data/skilltrees#skill-segment) for the remaining fields (`locking_segments`, `placement`).

## Built-in segments

Vampirism's segments are generated into `vampirism/factionapi/skill_segment/vampire/` and `.../hunter/`, one file per skill or choice. Segments that offer a choice are named `a_or_b`, for example `vampirism:vampire/fast_recovery_or_swift_resurrection` (Fast Recovery or Swift Resurrection) or `vampirism:hunter/double_it_or_dual_wielding`.

## Example

```json title="data/vampirism/factionapi/skill_segment/vampire/fast_recovery_or_swift_resurrection.json"
{
  "parents": [ "vampirism:vampire/disguise" ],
  "skills": [ "vampirism:fast_recovery", "vampirism:swift_resurrection" ],
  "tree": "vampirism:vampire/level"
}
```
