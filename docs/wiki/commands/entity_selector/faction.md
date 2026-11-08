---
title: Faction Selector
---

The faction selector selects all faction entities (e.g. vampire and hunter mobs) of a given faction or not of a given faction. The faction id has to be put in quotes.

## Syntax

```
@e[vampirism_faction="<faction>"]
@e[vampirism_faction=!"<faction>"]
```

## Arguments

| Parameter | Type      | Description           |
|:----------|:----------|:----------------------|
| faction   | `faction` | The target faction id |

## Examples

```
/kill @e[vampirism_faction="vampirism:vampire"]
```
Kills all vampire mobs.

```
/kill @e[vampirism_faction=!"vampirism:vampire"]
```
Kills all entities that are not vampire mobs.
