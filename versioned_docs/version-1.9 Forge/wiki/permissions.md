---
sidebar_position: 6
title: Permissions
---

__Permission are only relevant if you are hosting a server with a permission mod/plugin__

Vampirism utilizes Forge's permissions system for a few actions. Thereby it should be compatible with Forge based permission mods as well as SpongeForge's permission system.

## Allow Everything

If you do not want to bother with Vampirism's permissions, you can simply give the `vampirism.*` permission to all players.

## Available permissions
- `vampirism.check` Should be given to all players. Used to check if the permission system works
- `vampirism.bite.attack` Attack a mob by biting
- `vampirism.bite.attack.player` Attack a player by biting
- `vampirism.bite.feed` Feed from an animal
- `vampirism.bite.feed.player` Feed from a player
- `vampirism.infect.player` Infect a player with sanguinare vampirism by biting
- `vampirism.action.*` Allow a player to use the specific action (e.b. `vampirism.action.vampirism.bat`)

## Permission Problems
### Hybrid Server - Actions etc. can not be used on a server when not being op

"*You do not have the permission to do this action*" - This message is displayed when a player tries to use an action (e.g. transform into a bat) but does not have the permission to do so.

There is a common problem for some hybrid server (Bukkit + Forge) that the Hybrid server implementation requires the user to have a permission even if there is no permission plugin installed.
There is nothing we can do about this, because this is solely an issue in the implementations of the hybrid server.

As a workaround you need to set up a permission plugin yourself and give all players the `vampirism.*` permission for everything to work.

[Luckperms](https://luckperms.net) is a popular choice.