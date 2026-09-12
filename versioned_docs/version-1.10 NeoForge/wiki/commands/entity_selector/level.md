---
title: Level Selector
---

The faction selector selects all players of a given level, assuming they have the faction.

## Syntax

```
@a["vampirism:level"="<level>"]
@a["vampirism:level"="<level_range>"]
```

## Arguments

| Parameter   | Type            | Description            |
|:------------|:----------------|:-----------------------|
| level       | `integer`       | the target level       |
| level_range | `integer_range` | the target level range |

## Examples

```
/kill @a["vampirism:level"=5,"vampirism:faction"="vampirism:vampire"]
```
Kills all vampire players on level 5.

```
/kill @a["vampirism:level"=5..8,"vampirism:faction"="vampirism:vampire"]
```
For a range of levels use `5..8` 