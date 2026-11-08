---
title: Modify Minion Inventory
sidebar_position: 15
---

View the inventory of a minion and add or remove items from the inventory. Removed items are given to you. Changes are written to the faction log if faction logging is enabled.

## Syntax

```
/factions minion inventory <minion> list
/factions minion inventory <minion> add <item> [<count>]
/factions minion inventory <minion> remove <item> [<count>]
```

## Arguments

| Parameter | Type                                              | Description                         |
|:----------|:--------------------------------------------------|:------------------------------------|
| minion    | [`Minion`](./arguments/minion_argument.md)        | The target minion                   |
| item      | `Item`                                            | The item to be added or removed     |
| count     | `integer`                                         | The item count (Default 1, minimum 1) |

## Permissions

:::info

Requires permission level 3 (admin)

:::
