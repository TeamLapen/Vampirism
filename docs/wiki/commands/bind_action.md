---
title: Bind Action
sidebar_position: 9
---

There are some keybindings that can be used to quickly activate actions.
You can bind a specific action to one of these keys with this command.  
E.g. use `/factions actions bind ACTION_1 vampirism:bat` to bind the bat action to the first key.
Use autocomplete to see all available action ids.

## Syntax

```
/factions actions bind <key> <action>
```

## Arguments

| Parameter | Type     | Description                                   |
|:----------|:---------|:----------------------------------------------|
| key       | `enum`   | The shortcut key: `ACTION_1` to `ACTION_9`    |
| action    | `Action` | The action that should be bound               |

## Permissions

:::info

Available to everyone

:::
