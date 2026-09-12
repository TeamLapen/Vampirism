---
sidebar_position: 1
title: Overview
---

Vampirism ships an API for addon mods and cross-mod compatibility. It lives in the
`de.teamlapen.vampirism.api` package (the `Vampirism-api` Gradle module).

:::info Faction, skill, level and task APIs moved to FactionApi
Vampirism is built on top of **[FactionApi](/factionapi/api/intro)**. Everything about factions,
faction players, levels, lord levels, skills, actions, tasks, refinements and minions is FactionApi's
API now – `IVampirePlayer` / `IHunterPlayer` simply extend FactionApi's `IFactionPlayer` and
`ISkillPlayer`. This section only documents what is *Vampirism-specific* (blood, biting, sun damage,
oils, garlic, fog, …).
:::

## Documentation map

| Page                                           | Contents                                                                 |
|------------------------------------------------|----------------------------------------------------------------------|
| [Depending on Vampirism](./setup)              | Gradle repository, artifacts, mixins / access transformers.           |
| [Registries](./registries)                     | Vampirism's own registries (oils, converters, visions, vampire books). |
| [Players & world](./players-and-world)         | Player capabilities, world handlers and the API service registries.    |
| [Items](./items)                               | Oils, blood-charged items, brewing, item data components.             |
| [Events](./events)                             | The game-bus events the API fires.                                    |
| [IMC messages](./imc)                          | Lightweight compatibility messages you can send to Vampirism.         |
| [Reference](./reference)                       | `VReference`, attachments, tags, data maps, enums.                    |

## Entry points

```java
// Global services
IVampirismServices services = VampirismApi.services();

// Player capabilities (attached to every player)
IVampirePlayer vampire = VampirismApi.vampirePlayer(player);
IHunterPlayer  hunter  = VampirismApi.hunterPlayer(player);

// World / creature capabilities
IExtendedCreatureVampirism ext = VampirismApi.extendedCreatureVampirism(creature);
IGarlicChunkHandler garlic = VampirismApi.garlicHandler(level);
IFogHandler fog = VampirismApi.fogHandler(level);
```

`VampirismApi.services()` returns an [`IVampirismServices`](./players-and-world#services) with the
sun-damage, entity, blood-conversion and extended-brewing registries.

## Examples & help

* [VampirismIntegrations](https://github.com/TeamLapen/VampirismIntegrations) – a real mixed example.
* [Discord](https://discord.gg/wuamm4P) · [GitHub](https://github.com/TeamLapen/Vampirism)
