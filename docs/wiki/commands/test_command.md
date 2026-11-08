---
title: Test Commands
sidebar_position: 19
---

:::caution

Test commands are meant for testing and debugging. Most of them require operator permissions.

:::

Additionally, to the commands listed in the sidebar, there are some more commands available. They are mostly used for testing, but can also help to fix bugs.

The general syntax is:

```
/vampirism test <command> [<args>]
```

| Command                                                                 | Description | Permission level |
|:------------------------------------------------------------------------|:------------|:-----------------|
| `heal`                                                                  | Heals you completely | 2 |
| `tent [advanced]`                                                       | Turns the tent you look at into an (advanced) spawner. `advanced` is a literal | 2 |
| `garlicCheck [<print>]`                                                 | Shows the garlic strength at your chunk. `print` (`boolean`) prints more data | 2 |
| `banner <faction>`                                                      | Gives you the village banner of the faction | 2 |
| `enchantment <item> supported`, `enchantment <item> primary`            | Lists the supported/primary enchantments for the item | 3 |
| `entity`                                                                | Prints the ids of nearby creatures (to define blood values) | 3 |
| `info-entities`                                                         | Prints the entity counts per mob category (Creature / Monster / Hunter / Vampire) | 3 |
| `printEntityNBT`                                                        | Debug: serializes nearby entities | 3 |
| `marker [<args>]`                                                       | Prints a marker (with optional text) to the log | 3 |
| `spawnTestAnimal`                                                       | Spawns a cow with low health | 3 |
| `summonDummy vampire`, `summonDummy hunter`                             | Summons a training dummy of the type | 3 |
| `tasks clear [<players>]`                                               | Clears the tasks of the players | 3 |
| `tasks refreshTaskList [<players>]`                                     | Updates the task lists of the players | 3 |
| `tasks resetTaskList [<players>]`                                       | Resets the task lists of the players | 3 |
| `tasks resetLordTasks [<players>]`                                      | Resets the lord tasks of the players | 3 |
| `velmorra ...`                                                          | See below | none |

## Velmorra

Debug commands for [Velmorra and the Dracula fight](../dracula/velmorra.md). The `fight` subcommands only work inside the Velmorra dimension. These commands have no permission requirement registered in the mod, so restrict them yourself (e.g. with a permission mod) on public servers.

```
/vampirism test velmorra create
/vampirism test velmorra remove
/vampirism test velmorra marker dracula
/vampirism test velmorra portal create
/vampirism test velmorra portal activate
/vampirism test velmorra portal deactivate
/vampirism test velmorra fight start
/vampirism test velmorra fight stage <passive|ranged|raged>
/vampirism test velmorra fight kill
/vampirism test velmorra fight collapse <remainingTicks>
```

| Command | Description |
|:--------|:------------|
| `create` / `remove` | Creates the Velmorra dimension / closes all portals and marks the dimension for removal |
| `marker dracula` | Places the Dracula spawn marker at your position |
| `portal create` | Builds a portal frame in front of you (look horizontally) |
| `portal activate` / `deactivate` | Activates/deactivates the portal at your position |
| `fight start` | Starts the Dracula fight |
| `fight stage` | Sets the fight stage of the spawned Dracula |
| `fight kill` | Kills Dracula |
| `fight collapse <remainingTicks>` | Sets the ticks until Velmorra collapses (`integer`, minimum 0) |
