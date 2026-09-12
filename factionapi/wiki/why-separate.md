---
sidebar_position: 2
title: Why a separate mod?
---

The faction system used to live inside Vampirism. It was split out into its own mod so that the
framework and the content that uses it can evolve independently.

## Reasons for the split

### Reuse without the rest of Vampirism

Vampirism is a large mod: blood mechanics, a custom biome, dozens of entities, structures, potions and
world generation. A mod that only wants "joinable factions with skill trees and quests" should not
have to depend on – or ship alongside – all of that. FactionApi is the faction machinery with none of
the vampire/hunter content.

### A smaller, more stable contract

Addon authors compile against `de.teamlapen.faction.api`, a focused surface of interfaces, registry
keys and events. Keeping it in a dedicated module makes it obvious what is API and what is
implementation detail, and lets the API carry its own semantic version
(`factionapi` is at `1.x` while Vampirism is at `2.x`).

### Dogfooding

Vampirism consumes FactionApi exactly the way a third-party mod would. Anything Vampirism needs gets
added to the API first, so the API stays capable and the "Vampirism is just another consumer" claim
stays true.

### Interoperability

Factions, faction tags, the `NEUTRAL` faction and the targeting predicates all live in one shared
registry set. Multiple faction-based mods loaded together see each other's factions and can target or
exempt them, instead of each mod inventing its own incompatible system.

### Independent release cadence

Framework fixes can ship without a full Vampirism release, and vice versa.

## Module layout

| Module           | Artifact         | Package(s)                                   | Role                                                                 |
|------------------|------------------|---------------------------------------------|--------------------------------------------------------------------|
| `faction-api`    | `FactionApi-api` | `de.teamlapen.faction.api`                   | Compile-time API: interfaces, registry keys, events, builders.       |
| `faction`        | `FactionApi`     | `de.teamlapen.faction.common` / `.client` / … | The runtime mod (`modId = factionapi`) – registries, logic, GUIs, ATs, interface injection. |

Both are published together under the same version.
