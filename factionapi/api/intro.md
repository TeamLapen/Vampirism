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

## New here?

If you want to **create your own faction**, start with the guides, in this order:

1. [Concepts](./getting-started/concepts) – the mental model, glossary and what is required vs optional.
2. [Your first faction](./getting-started/first-faction) – a complete minimal faction, step by step.
3. [Skills and actions](./getting-started/skills-and-actions) – a skill tree and an ability.
4. [Optional features](./getting-started/optional-features) – lords, tasks, refinements, villages, minions, faction mobs.
5. [Troubleshooting](./getting-started/troubleshooting) – checklist and symptom → cause table.

The remaining pages are the reference.

## Documentation map

| Page                                         | Contents                                                                        |
|----------------------------------------------|------------------------------------------------------------------------------|
| [Getting started](./getting-started/concepts) | Concepts, tutorial for a first faction, skills and actions, optional features, troubleshooting. |
| [Object model](./object-model)               | `IFaction` / `IPlayableFaction`, the player handler, faction extensions.       |
| [Registries](./registries)                   | Every Faction API registry and what it is used for.                           |
| [Registering content](./registering-content) | Adding factions, skills, actions, task types, minions and JSON callbacks.     |
| [Utilities](./utilities)                     | Faction / skill tree tags, faction-specific tags, default targets, target selectors, item data components. |
| [Events](./events)                           | The game- and mod-bus events the API fires, incl. all `FactionVillageEvent`s and `RegisterItemBarsEvent`. |
| [Lords](./lords)                             | `ILordPlayer`, lord titles and the lord builder.                              |
| [Refinements](./refinements)                 | `IRefinement`, `IRefinementSet`, accessory items, rarity and the handler.     |
| [Villages](./villages)                       | Totems, banners, capture entity interfaces and village configuration.        |
| [Minions](./minions)                         | Minion entries, data, stats, appearances, tasks and task categories.         |
| [Misc](./misc)                               | Descriptions, customization, disguise, food, sync API, attachment / data component key lists, interface injection, utilities. |

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
