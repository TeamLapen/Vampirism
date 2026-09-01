---
title: Config
sidebar_position: 10
---

With this command you can change some config values without editing the file. Rerun the command to undo a blacklist. Available commands are the following:

## Base Syntax

```
/vampirism config
```

### Entity Bloodvalues

Entities can be blacklisted by looking at them while executing the command or searched for in the suggestions. Which means that vampires can no longer get blood from them. (Works for newly spawned entities)

#### Syntax

```
/vampirism config bloodvalues entity [<entityid>]
```

#### Arguments

| Parameter | Type               | Description                                                 |
|:----------|:-------------------|:------------------------------------------------------------|
| entityid  | `ResourceLocation` | Instead of the current target, the specified entity is used | 

### Sundamage

#### Blacklist
Dimensions and Biomes can be blacklisted for vampire sundamage.

##### Syntax

```
/vampirism config sundamage blacklist biome [<biome>]
/vampirism config sundamage blacklist dimension [<dimension>]
```


##### Arguments

| Parameter | Type               | Description                                                       |
|:----------|:-------------------|:------------------------------------------------------------------|
| biome     | `ResourceLocation` | Instead of the current biome, the specified biome is used         |
| dimension | `ResourceLocation` | Instead of the current dimension, the specified dimension is used |

### Enforcing
Dimensions can be enforced to deliver sundamage to vampires.

#### Syntax

```
/vampirism config sundamage enforce dimension [<dimension>]
```

#### Arguments

| Parameter | Type               | Description                                                       |
|:----------|:-------------------|:------------------------------------------------------------------|
| dimension | `ResourceLocation` | Instead of the current dimension, the specified dimension is used |


## Permissions

:::info

Following commands requires the player to be admin

:::