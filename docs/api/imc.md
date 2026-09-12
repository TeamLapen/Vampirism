---
sidebar_position: 7
title: IMC messages
---

[Inter-mod communication](https://docs.neoforged.net/docs/misc/imc/) lets a mod send Vampirism simple
compatibility messages without a compile dependency. Send them during `InterModEnqueueEvent` with
`InterModComms.sendTo("vampirism", key, () -> payload)`; Vampirism reads them in
`InterModProcessEvent` (`de.teamlapen.vampirism.common.integration.InterModHandler`).

## Messages

### `nosundamage-biome`

Exempt one of your biomes from vampire sun damage.

| | |
|---|---|
| **key** | `nosundamage-biome` |
| **payload** | the biome id as a `ResourceLocation` |

Equivalent to a [`no_sun_damage.json`](../data/sundamage) `biomes` entry. Per-dimension exemptions are
only available through `ISundamageRegistry` (dimension ids may not exist yet at IMC time).

### `disable-blood-bar`

Ask Vampirism not to replace the food bar with the blood bar (e.g. if your mod renders its own
hunger UI).

| | |
|---|---|
| **key** | `disable-blood-bar` |
| **payload** | none |
