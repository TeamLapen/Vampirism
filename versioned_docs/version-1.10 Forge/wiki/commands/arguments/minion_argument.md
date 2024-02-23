---
title: Minion Argument
---

The minion argument is used to target a specific minion. 

## Syntax
### Short
A short variant which does not specify the minion name, which might make it difficult to use in some cases.
```
<playername>:<minionid>
```
### Long

A long variant which specifies the minion's name as well as the id. It is important to specify `"` quotation marks around the argument.
```
"<playername>:<minionid> | <minionname>"
```

## Arguments

| Parameter  | Type      | Description              |
|:-----------|:----------|:-------------------------|
| playername | `string`  | The name of the player   |
| minionid   | `integer` | The number of the minion |
| minionname | `string`  | The name of the minion   |