---
sidebar_position: 6
title: Permissions
---

__Permissions are only relevant if you are hosting a server with a permission mod__

Vampirism uses NeoForge's permission API for a few actions. Therefore, it works with every mod that provides a NeoForge permission handler.

Permission checks are **disabled by default**. To enable them set `usePermissions` to `true` in the `admin` section of the `factionapi-server.toml` (see [Configuration](configuration.md)). While it is disabled, everything is allowed. If it is enabled but no permission mod is installed, all nodes default to allowed as well.

## Allow Everything

If you do not want to bother with Vampirism's permissions, you can simply give the `vampirism.*` and `factionapi.*` permissions to all players (depending on your permission mod's wildcard support).

## Available permissions

All permissions are `true` by default.

### Vampirism
- `vampirism.check` Should be given to all players. Used to check if the permission system works. If it is missing, players receive a message that the permission plugin is not set up properly.
- `vampirism.bite.feed` Feed from an animal
- `vampirism.bite.feed.player` Feed from a player
- `vampirism.infect.player` Infect a player with sanguinare vampirism by biting

### Factions
- `factionapi.check` Should be given to all players
- `factionapi.action` Use an action. The action is passed as the permission context `action` (the action id, e.g. `vampirism:bat`), so it can be allowed/denied per action

## Permission Problems
### Actions etc. can not be used on a server

"*You do not have the permission to do this action*" - This message is displayed when a player tries to use an action (e.g. transform into a bat) but does not have the permission `factionapi.action` for it.

Either set `usePermissions` to `false` or set up a permission mod and give your players the permissions above.
