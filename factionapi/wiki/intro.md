---
sidebar_position: 1
title: What is FactionApi?
---

**FactionApi** (mod id `factionapi`) is a standalone NeoForge library mod that provides a complete
faction framework for Minecraft: joinable factions, per-faction player levels and *lord* levels,
skill trees and activatable actions, a task/quest system, accessory "refinements", minions and
faction-controlled villages.

It is developed and maintained by the Vampirism team (maxanier, cheaterpaul, gridexpert) as part of
the [Vampirism monorepo](https://github.com/TeamLapen/Vampirism) (`projects/faction-api` and
`projects/faction`), and is licensed under **LGPL-3.0**.

## What it gives you

| System              | Summary                                                                                            |
|---------------------|--------------------------------------------------------------------------------------------------|
| Factions            | A shared registry of factions plus a built-in `NEUTRAL` faction. Any entity resolves to a faction. |
| Player progression  | Each playable faction has its own level track and an optional *lord* level track with titles.       |
| Skills & actions    | Data-driven skill trees, skill segments and cooldown/duration based player actions.                 |
| Tasks               | A quest board system with data-pack-defined requirements, rewards and unlock conditions.            |
| Refinements         | Rollable accessory items that grant attribute modifiers, grouped into rarity-tiered sets.           |
| Minions             | Per-faction minion entities with assignable tasks and the `/factions minion` commands.                          |
| Villages            | Faction capture mechanics, totems, guards, banners and bad-omen raids.                              |
| Cross-mod glue      | Faction tags, faction-aware targeting predicates and item data components (slayer, restriction).    |

## Relationship to Vampirism

Vampirism's vampire/hunter gameplay is built **entirely on top of FactionApi** – the two factions,
their skill trees, tasks and lord titles are just FactionApi content shipped by Vampirism. Because of
that, Vampirism declares `factionapi` as a **required dependency**: installing Vampirism means you
must also install FactionApi (see [Installation](./installation)).

Another mod can use the same framework to add its own factions without pulling in any of Vampirism's
content.

## Where to start

| You are …                                  | Start here |
|--------------------------------------------|------------|
| A player or modpack author                 | [Installation](./installation), then [Commands, permissions & config](./commands-and-config) |
| A server admin                             | [Commands, permissions & config](./commands-and-config) |
| A mod developer who wants a **new faction**| [Concepts](../api/getting-started/concepts) → [Your first faction](../api/getting-started/first-faction) |
| A mod developer who only **reads** factions (e.g. checks a player's faction) | [Object model](../api/object-model) and [Overview](../api/intro#entry-points) |
| A data pack maker                          | [Data packs](../data/intro) |

## Where to get it

* **Players / modpacks:** download from Modrinth (project `factionapi`). See
  [Installation](./installation).
* **Mod developers:** add it as a Gradle dependency, see [Depending on FactionApi](./gradle-setup). To create your
  own faction follow [Your first faction](../api/getting-started/first-faction); the
  [API documentation](../api/intro) and [data pack documentation](../data/intro) are the reference.

* **Server admins:** commands, permissions and config options are listed in
  [Commands, permissions & config](./commands-and-config).

## Links

* [Source & issues](https://github.com/TeamLapen/Vampirism)
* [Discord](https://discord.gg/wuamm4P)
