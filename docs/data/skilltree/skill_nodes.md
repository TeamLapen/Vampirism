---
title: Skill Tree Nodes
sidebar_position: 2
---

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