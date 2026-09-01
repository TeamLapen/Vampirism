---
sidebar_position: 1
title: Overview
---

The **Faction API** is the code layer that powers Vampirism's faction system (factions, levels, lord
levels, skills, actions, tasks, refinements, minions and faction villages). It is split into two
Gradle modules:

| Module        | Package                        | Contents                                                    |
|---------------|-------------------------------|-----------------------------------------------------------|
| `FactionApi-api` | `de.teamlapen.faction.api`   | Interfaces, registry keys, events, builders, data components – everything an addon compiles against. |
| `FactionApi`     | `de.teamlapen.faction.common` / `.client` / … | The implementation. Needed at runtime, and needed at compile time if you register your own faction, skill or action (the base classes live here). |

Vampirism itself is just a consumer of this API, so anything Vampirism does, your mod can do too.

:::info
API surface is marked with `@ApiStatus` annotations. Types/methods tagged `@ApiStatus.Internal` are
not part of the contract and may change without notice.
:::

## Documentation map

| Page                                         | Contents                                                                        |
|----------------------------------------------|------------------------------------------------------------------------------|
| [Object model](./object-model)               | `IFaction` / `IPlayableFaction`, the player handler, faction extensions.       |
| [Registries](./registries)                   | Every Faction API registry and what it is used for.                           |
| [Registering content](./registering-content) | Adding factions, skills, actions, task types, minions and JSON callbacks.     |
| [Utilities](./utilities)                     | Faction-specific tags, target selectors, item data components.               |
| [Events](./events)                           | The game- and mod-bus events the API fires.                                   |

## Entry points

Everything is reached from two static hubs:

```java
// Global services (server + client)
IFactionServices services = FactionsApi.services();
services.factionHelper();      // IFactionHelper  – resolve an entity's faction
services.factionPredicates();  // IFactionPredicates – build target selectors
services.factionTags();        // IFactionSpecificTags – per-faction tags

// Per-player state (attached to every player)
IFactionPlayerHandler handler = FactionsApi.factionPlayerHandler(player);
```

Many sub-interfaces also expose their own `get(...)` shortcut:

| Call                                   | Returns                                             |
|----------------------------------------|--------------------------------------------------|
| `IFactionHelper.get()`                 | the faction helper                                 |
| `IFactionSpecificTags.get()`           | the faction tag service                            |
| `ISkillPlayer.get(player)`             | `Optional<ISkillPlayer>` for the active faction    |
| `ISkillHandler.get(player)`            | `Optional<ISkillHandler>` – unlocked/enabled skills |
| `IActionHandler.get(player)`           | `Optional<IActionHandler>` – action cooldowns/toggles |
| `IRefinementHandler.get(player)`       | `Optional<IRefinementHandler>` – equipped accessories |
| `IFactionPredicate.builder()`          | a new target-selector builder                      |

## Need help?

The API is still evolving. If something is missing or unclear, open an issue or ask on
[Discord](https://discord.gg/wuamm4P) / [GitHub](https://github.com/TeamLapen/Vampirism).
