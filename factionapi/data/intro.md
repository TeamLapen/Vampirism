---
sidebar_position: 1
title: Data Packs
---

The Faction API drives most of its faction content through **dynamic registries**, which are just
data pack files. This lets pack makers, mod packs and addon mods add, override or remove faction
content without writing any code.

Vampirism itself is a consumer of the Faction API: all of the content below is
[data generated](https://github.com/TeamLapen/Vampirism/tree/dev/projects/vampirism/src/generated/resources/data/vampirism/factionapi)
in the mod and shipped as an internal data pack. Those generated files are the best reference when
building your own.

## How data packs work

Check the Minecraft Wiki for the basics:
[Data packs](https://minecraft.wiki/w/Data_pack) ·
[Installing them](https://minecraft.wiki/w/Tutorial:Installing_a_data_pack) ·
[Creating them](https://minecraft.wiki/w/Tutorial:Creating_a_data_pack)

Make sure you are comfortable with the **namespace** concept:

* Use **your own** namespace for new content.
* Use `vampirism` (or `factionapi`) to override or replace one of the built-in entries.

Every entry's id is derived from its file location, e.g.
`data/mypack/factionapi/skill_tree/vampire/level.json` has the id `mypack:vampire/level`.

## Registries

| Content                                      | Registry                   | Folder                                       |
|----------------------------------------------|----------------------------|----------------------------------------------|
| [Tasks](./tasks)                             | `factionapi:tasks`         | `data/<namespace>/factionapi/tasks/`         |
| [Skill Trees](./skilltrees)                  | `factionapi:skill_tree`    | `data/<namespace>/factionapi/skill_tree/`    |
| [Skill Segments](./skilltrees#skill-segment) | `factionapi:skill_segment` | `data/<namespace>/factionapi/skill_segment/` |

* **Tasks** – objectives handed out to faction players by a Faction Representative, with requirements,
  a reward and optional unlock conditions.
* **Skill Trees** – the tabs shown in the faction skill screen: which faction they belong to, how they
  are unlocked, their icon and title.
* **Skill Segments** – the individual nodes that make up a skill tree and the graph that connects them.

JSON schemas for editor validation are linked at the top of each page.

## Other customization

The Faction API also reuses a number of vanilla data pack systems (tags, loot,
advancements, the `minecraft:custom_stat` registry used by task stat requirements, …).
