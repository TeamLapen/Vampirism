---
sidebar_position: 4
title: Players & world
---

## Player capabilities

Attached to every player (`VampirismAttachments`). Fetch with the `VampirismApi` helpers:

```java
IVampirePlayer vampire = VampirismApi.vampirePlayer(player);
IHunterPlayer  hunter  = VampirismApi.hunterPlayer(player);
```

### `IVampirePlayer`

Extends FactionApi's `IFactionPlayer<IVampirePlayer>` and `ISkillPlayer<IVampirePlayer>` (faction
membership, level, lord level, skills, actions – see [FactionApi](/factionapi/api/object-model)), plus
`IVampire`, `IBiteableEntity` and `IVampireVisionUser`. Vampirism-specific parts:

* **Blood** – `getBloodStats()` returns [`IBloodStats`](#ibloodstats); `drinkBlood(amount, saturation, useRemaining, IDrinkBloodContext)` (from `IVampire`).
* **Vision** – activate / cycle `IVampireVision`s (via `IVampireVisionUser`).
* **Sun & fire** – increased fire damage handling, sun-damage checks.
* **DBNO** – "down but not out" death-prevention state.

### `IHunterPlayer`

Extends `IFactionPlayer<IHunterPlayer>`, `ISkillPlayer<IHunterPlayer>` and `IHunter`. Notable:
`breakDisguise()` – call whenever the player does something that would blow their hunter disguise.

### Faction extensions

Lord-level sub-roles are FactionApi *faction extensions*: `IDraculaPlayer` (vampire) and
`IMarshallPlayer` (hunter), resolved with `handler.getExtension(IDraculaPlayer.class)` etc.

### `IBloodStats`

Blood bar, analogous to vanilla `FoodStats`; implements NeoForge's `ResourceHandler<FluidResource>`
so blood can be moved in/out like a fluid tank. `VReference.FOOD_TO_FLUID_BLOOD` (100) is the
conversion factor between nutrition and blood mB.

## Creature capability

`VampirismApi.extendedCreatureVampirism(mob)` → `IExtendedCreatureVampirism` (on every
`PathfinderMob`): current/max blood, poisonous-blood duration, and `convertToVampire()` /
per-tick hooks. It also implements `IBiteableEntity`.

## World handlers

| Accessor                              | Interface             | Purpose                                               |
|---------------------------------------|-----------------------|--------------------------------------------------|
| `VampirismApi.garlicHandler(level)`   | `IGarlicChunkHandler` | Per-chunk garlic-diffuser strength (`EnumStrength`). |
| `VampirismApi.fogHandler(level)`      | `IFogHandler`         | Vampire fog areas.                                   |

## Services

`VampirismApi.services()` → `IVampirismServices`:

| Method                          | Interface                        | Purpose                                                                    |
|---------------------------------|----------------------------------|----------------------------------------------------------------------|
| `sunDamageRegistry()`           | `ISundamageRegistry`             | Query / register sun-damage exemptions (also from [data packs](../data/sundamage)). |
| `entityRegistry()`              | `IVampirismEntityRegistry`       | Entity blood values + converting; `convert(mob)`, `getBlood(mob)`, auto-calculation. |
| `bloodConversionRegistry()`     | `IBloodConversionRegistry`       | Item→impure-blood and fluid→blood conversion (see [Blood Values](../data/bloodvalues)). |
| `extendedBrewingRecipeRegistry()` | `IExtendedBrewingRecipeRegistry` | Register `ExtendedPotionMix` entries – see [Items](./items#brewing).       |

## Entity interfaces

`de.teamlapen.vampirism.api.world.entity` provides marker/behaviour interfaces implemented by
Vampirism entities and useful for `instanceof` checks or addon entities:

* Vampires – `IVampire`, `IVampireMob`, `IBasicVampire`, `IAdvancedVampire`, `IVampireBaron`.
* Hunters – `IHunter`, `IHunterMob`, `IBasicHunter`, `IAdvancedHunter`, `IVampirismCrossbowUser`.
* Converted – `IConvertedCreature`, `ICurableConvertedCreature`, `IConvertingHandler`.
* Other – `IBiteableEntity`, `IAdjustableLevel` (used by the
  [`vampirism:adjustable_level`](../data/lootconditions/adjustable_level) loot condition),
  `IAggressiveVillager`, `Difficulty`.
