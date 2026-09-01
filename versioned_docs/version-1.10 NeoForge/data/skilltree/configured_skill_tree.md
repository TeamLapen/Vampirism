---
title: Configured Skill Tree
sidebar_position: 3
---

When you have a [skill tree](./skilltree) and created a few [skill nodes](./skillnodes) you can create a configured skill tree assigning skill nodes to the skill tree.

## Creating a configured skill tree

```json title="data/<modid>/vampirism/configured_skill_tree/<skill-tree-path>.json"
{
  "skill_tree": <skill-tree>,
  "node": <root-node>,
  "children": <child-nodes>
}
```

| Field        | Type                            | Description                                        |
|--------------|---------------------------------|----------------------------------------------------|
| `skill_tree` | ResourceLocation                | The id of the skill tree that should be configured |
| `node`       | ResourceLocation                | The id of the root node of the skill tree          |
| `children`   | [ChildNodes](#ChildNodes) array | the following nodes after the root node            |


### Example

```json title="data/vampirism/vampirism/configured_skill_tree/hunter/lord.json"
{
  "children": [
    {
      "children": [
        {
          "node": "vampirism:hunter/lord_6"
        }
      ],
      "node": "vampirism:hunter/lord_2"
    },
    {
      "node": "vampirism:hunter/lord_3"
    },
    {
      "node": "vampirism:hunter/lord_4"
    },
    {
      "node": "vampirism:hunter/lord_5"
    }
  ],
  "node": "vampirism:hunter/lord_root",
  "skill_tree": "vampirism:hunter/lord"
}
```


## ChildNodes
```json
{
  "node": "<skill-node>",
  "children": <child-nodes>
}
```

| Field        | Type             | Description                                        |
|--------------|------------------|----------------------------------------------------|
| `node`       | ResourceLocation | The id of the skill node                           |
| `children`   | ChildNodes array | the following nodes after the root node            |