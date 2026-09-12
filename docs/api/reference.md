---
sidebar_position: 8
title: Reference
---

Static helper classes in `de.teamlapen.vampirism.api`.

## `VampirismApi`

Central access point – `services()`, `vampirePlayer(player)`, `hunterPlayer(player)`,
`extendedCreatureVampirism(mob)`, `garlicHandler(level)`, `fogHandler(level)`.

## `VReference`

Static constants: `MODID`, `VAMPIRE_FACTION_ID` / `HUNTER_FACTION_ID`, `FOOD_TO_FLUID_BLOOD` (100),
the `BLOOD` fluid supplier, and effect ids (`PERMANENT_INVISIBLE_MOB_EFFECT`,
`VAMPIRE_NIGHT_VISION_EFFECT`).

## `VampirismFactions`

`VAMPIRE` and `HUNTER` – `DeferredFaction` holders for the two playable factions
(ids `vampirism:vampire`, `vampirism:hunter`).

## `VampirismAttachments`

`AttachmentType` holders and `Keys`: `EXTENDED_CREATURE`, `VAMPIRE_PLAYER`, `HUNTER_PLAYER`,
`GARLIC_HANDLER`, `FOG_HANDLER` (plus keys for `DRACULA_PLAYER`, `MARSHALL_PLAYER`,
`INFECTION_STATUS`, `NEAREST_VILLAGE`, minion data, …).

## `VampirismDataComponents` / `VampirismDataMaps` / `VampirismTags`

Component types + keys, data-map types + keys (see [Registries](./registries)), and faction tag keys
(`VampirismTags.Factions.IS_VAMPIRE` / `IS_HUNTER`).

## Enums

| Type          | Values                                                              |
|---------------|-----------------------------------------------------------------|
| `EnumStrength`| Garlic strength: `NONE` (0), `WEAK` (1), `MEDIUM` (2), `STRONG` (3); `getStrength()`, `isStrongerThan(...)`, `getFromStrength(int)`. |
| `IItemWithTier.Tier` | `NORMAL`, `ENHANCED`, `ULTIMATE`.                            |
| `VEnums`      | `EnumProxy` extensions: `HUNTER_CATEGORY` / `VAMPIRE_CATEGORY` (`MobCategory`), `PEDESTAL` (`ItemDisplayContext`). |

## Utilities

* `VIdentifier` – `mod(path)` etc. for `vampirism:`-namespaced ids.
* `RegUtil` – registry lookup helpers.
* `APIUtil`, `ThreadSafeAPI` – internal wiring.
