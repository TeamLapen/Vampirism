---
title: Level Selector
---

The level selector selects all players with a given faction level (in whatever faction they currently are).

## Syntax

```
@a[vampirism_level=<level>]
@a[vampirism_level=<level_range>]
```

## Arguments

| Parameter   | Type            | Description            |
|:------------|:----------------|:-----------------------|
| level       | `integer`       | the target level       |
| level_range | `integer_range` | the target level range |

## Examples

```
/kill @a[vampirism_level=5]
```
Kills all players on faction level 5.

```
/kill @a[vampirism_level=5..8]
```
For a range of levels use `5..8`
