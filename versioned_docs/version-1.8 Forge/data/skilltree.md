---
sidebar_position: 2
title: Skill Tree
---

The Skill tree for each faction if build using skill nodes that can be defined.

#### Location
All skill nodes must be placed in the `data/<modid>/vampirismskillnodes/` folder.

#### Notes
- The initial skill node is not loaded, but is created automatically with id of the faction. For Vampires that is `vampirism:vampire` and for Hunter `vampirism:hunter`.

- The skill node's id is defined by the filename in the `vampirismskillnodes` folder. E.g. the node in the file `data/vampirism/vampirismsskillnodes/hunter/alchemy1.json` will have the id `vampirism:hunter/alchemy1`

## Basic Schema
```json title="skillnodeid.json"
{
  "parent": "",
  "skills": []
}
```
- `parent`: The id of the parent node of the skill.
- `skills`: String array of the registry names of the skills (a maximum of two skills are allowed).

## Remove Schema

Since data packs get merged all together this will and data pack entries can not be removed, this will ensure that the existing skill node with the same id will be ignored.

```json title="skillnodeid.json"
{
  "remove": true
}
```


## Merge Schema

You can also merge skills into existing nodes by specifying the id of the target node.

```json title="skillnodeid.json"
{
  "merge": "",
  "skills": []
}
```
- `merge`: The id of the skill node in which the skills will be merged.
- `skills`: String array of the registry names of the skills (a maximum of two skills are allowed).


## Locking Schema
```json title="skillnodeid.json"
{
  "parent": "",
  "skills": [],
  "lockingNodes": []
}
```
- `parent`: The id of the parent node of the skill.
- `skills`: String array of the registry names of the skills (a maximum of two skills are allowed).
- `lockingNodes`: String array of skill nodes ids that can not be enabled together with this skill node (must be set for all involved skill nodes).