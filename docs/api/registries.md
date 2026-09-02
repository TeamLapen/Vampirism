---
sidebar_position: 3
title: Registries
---

Registry keys are in `de.teamlapen.vampirism.api.VampirismRegistries.Keys`; access a registry at
runtime via `VampirismRegistries.<NAME>.get()`.

:::info Most gameplay registries are FactionApi's
Skills, actions, factions, faction players, tasks, refinements and minions are registered through
**[FactionApi](/factionapi/api/registries)** (`factionapi:*`), not Vampirism. What remains below is
Vampirism-specific.
:::

## Code registries

| Key                            | Type                                  | Used for                                                                 | Register with |
|--------------------------------|---------------------------------------|---------------------------------------------------------------------|---------------|
| `vampirism:oil`                | `IOil`                                | Oils applied to weapons/tools/armor. See [Items](./items#oils).          | `DeferredRegister` |
| `vampirism:converting_handler` | `MapCodec<? extends Converter>`       | Converter types for making creatures vampiric. See [Convertibles](../data/convertibles#converters). | `DeferredRegister` |
| `vampirism:vampire_vision`     | `IVampireVision`                      | Vampire player "visions" (night vision, blood vision, …).               | `DeferredRegister` |

`IVampireVision` implementations provide `onActivated` / `onDeactivated` / `tick(IVampirePlayer)` and a
translation key.

## Data pack registries

| Key                     | Type           | Populated from                                              |
|-------------------------|----------------|--------------------------------------------------------|
| `vampirism:vampire_book` | `IVampireBook` | `data/<namespace>/vampirism/vampire_book/<name>.json`   |

Vampire books are rolled onto book items by the
[`vampirism:set_vampire_book`](../data/lootfunctions/set_vampire_book) loot function via
`vampirism:vampire_book` tags.

## Data maps

Vampirism's [data maps](../data/bloodvalues) (`vampirism:item_blood`, `vampirism:entity_blood`,
`vampirism:fluid_blood_conversion`, `vampirism:entity_converter`, `vampirism:liquid_color`,
`vampirism:garlic_diffuser_fuel`, `vampirism:fog_diffuser_fuel`) have keys in
`VampirismDataMaps.Keys` and value interfaces in `de.teamlapen.vampirism.api.datamaps`.
