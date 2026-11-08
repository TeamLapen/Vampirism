---
title: Skill Tree Nodes
sidebar_position: 2
---
:::danger Outdated
This page describes the pre-FactionApi skill nodes format (`data/<modid>/vampirism/…`), which is no longer loaded.
Skill trees and skill segments are now FactionApi data pack registries – see
[Skill Trees](/factionapi/data/skilltrees).
:::

Skill tree nodes are the nodes in a skill tree


## Creating skill nodes

A skill nodes has the following structure:

```json title="data/<modid>/vampirism/skill_nodes/<skill-node-path>.json"
{
  "skills": <skill-array>,
}
```

| Field    | Type                   | Description                                   |
|----------|------------------------|-----------------------------------------------|
| `skills` | ResourceLocation array | The registry names of the skills in the node. |


### Examples
```json title="data/vampirism/vampirism/skill_nodes/hunter/alchemy1.json"
{
  "skills": [
    "vampirism:basic_alchemy"
  ]
}
```

```json title="data/vampirism/vampirism/skill_nodes/hunter/lord_3.json"
{
  "skills": [
    "vampirism:lord_speed",
    "vampirism:lord_attack_speed"
  ]
}
```