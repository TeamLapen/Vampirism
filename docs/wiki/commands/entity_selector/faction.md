---
title: Faction Selector
---

The faction selector selects all entities of a given faction or not of a given faction.

## Syntax

```
@e["vampirism:faction"="<faction>"]
@e["vampirism:faction"=!"<faction>"]
```

## Arguments

| Parameter | Type      | Description              |
|:----------|:----------|:-------------------------|
| faction   | `faction` | The target faction id    |

## Examples

```
/kill @e["vampirism:faction"="vampirism:vampire"]
```
Kills all vampire players and vampire mobs.

```
/kill @e["vampirism:faction"=!"vampirism:vampire"]
```
Kills all non vampire players and non vampire mobs.  