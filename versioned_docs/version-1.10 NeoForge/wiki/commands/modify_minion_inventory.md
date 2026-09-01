---
title: Modify Minion Inventory
sidebar_position: 12
---

View the inventory of a minion and add or remove items from the inventories


## Syntax

```
/vampirism modifyMinionInventory <minionid> list
/vampirism modifyMinionInventory <minionid> add <item> [<amount>]
/vampirism modifyMinionInventory <minionid> remove <item> [<amount>]
```


## Arguments

| Parameter | Type       | Description                     |
|:----------|:-----------|:--------------------------------|
| minionid  | `MinionId` | the target minion               |
| item      | `Item`     | the item to be added or removed |
| amount    | `integer`  | the item amount (Default 1)     |

## Permissions

:::info

Following commands requires the player to be admin

:::