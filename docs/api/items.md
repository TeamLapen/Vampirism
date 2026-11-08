---
sidebar_position: 5
title: Items
---

## Oils

Oils are registry objects (`vampirism:oil`, interface `IOil`) applied to items to grant effects.

| Interface        | Applied to                     |
|------------------|--------------------------------|
| `IOil`           | base – translation key, colour |
| `IApplicableOil` | items in general (duration, tests, apply/consume hooks) |
| `IWeaponOil`     | weapons – attack modifiers      |
| `IToolOil`       | tools                           |
| `IArmorOil`      | armor                           |

The applied oil is stored in the `vampirism:applied_oil` item data component
(`VampirismDataComponents.APPLIED_OIL`); `IOilItem` marks items that can carry one. From data packs,
apply an oil with the [`vampirism:set_oil`](../data/lootfunctions/set_oil) loot function and gate on
one with the [`vampirism:with_oil_item`](../data/lootconditions/has_oil) loot condition.

## Blood-charged items

`IBloodChargeable` items (Blood Seeker, Blood Striker) hold a blood charge in the
`vampirism:blood_charged` component. Fill one from a loot table with
[`vampirism:set_item_blood_charge`](../data/lootfunctions/set_item_blood_charge).

## Other item interfaces

| Interface              | Meaning                                                         |
|------------------------|-------------------------------------------------------------|
| `IBlessableItem`       | Item that can be blessed at a church altar.                    |
| `IItemWithTier`        | Item with a `Tier` (`NORMAL` / `ENHANCED` / `ULTIMATE`).       |
| `IHunterCrossbow`      | A hunter crossbow item – see [Crossbows and quarrels](#crossbows-and-quarrels). |
| `IVampirismQuarrel` / `IEntityQuarrel` | Quarrel (crossbow ammunition) item / quarrel arrow entity – see below. |

"Slayer" bonus damage against a faction is FactionApi's `FACTION_SLAYER` item component, not a
Vampirism interface.

## Crossbows and quarrels

* `IHunterCrossbow` (implemented by crossbow `Item`s) – `getChargeDurationMod(stack, level)` (ticks),
  `canSelectAmmunition(stack)`, `getAmmunition(stack)` / `setAmmunition(stack, item)` (the selected quarrel;
  stored in the `vampirism:selected_ammunition` component, `ISelectedAmmunition#item()`),
  `canUseDoubleCrossbow(entity)` and `getInaccuracy(stack, doubleCrossbow)`.
* `IVampirismQuarrel<T extends AbstractArrow & IEntityQuarrel>` – a quarrel item (`ItemLike`):
  `onHitBlock(arrow, pos, arrowEntity, shooter[, direction])` and `onHitEntity(arrow, entity, arrowEntity, shooter)`.
  The nested `IQuarrelBehavior` describes what a quarrel does: `properties()` (a `QuarrelProperties`) plus the
  optional hooks `onHitEntity`, `onHitBlock`, `onChargeTick(level, shooter, crossbow, chargeProgress)` (server,
  every tick while a crossbow with this quarrel selected is being charged) and
  `modifyArrow(level, stack, shooter, arrow)` (when the arrow entity is created).
* `IEntityQuarrel` – marker for the quarrel arrow entity with `setIgnoreHurtTimer()`.
* `QuarrelProperties` – immutable record built with `QuarrelProperties.of(argbColor)…build()`:
  `effectDescription`, `pickupBehavior` (default `CREATIVE_ONLY`), `baseDamage` (0), `damageMultiplier`,
  `ballistaMultiplier`, `knockbackMultiplier`, `velocityFactor`, `gravityFactor`, `inaccuracyFactor`
  (all default `1`), `extraPierceLevel` (0), `chargeMultiplier` (1; above 1 charges slower) and
  `forcesChunkLoading()` (only meant for the teleport quarrel). `QuarrelProperties.DEFAULT` is white with the
  defaults above.
* `IVampirismCrossbowUser` (entities, see [Players & world](./players-and-world)) – `isHoldingCrossbow()`,
  `isChargingCrossbow()`, `canUseCrossbow(stack)`, `ArmPose`.

Vampirism's own quarrels register `new QuarrelItem(new QuarrelBehavior(QuarrelProperties.of(0xFFFFFFFF)…build()), props)`
(implementation classes, not part of the API); `quarrel_normal` and `quarrel_heavy` in `ModItems` are examples.

## Brewing

The Alchemy Table's brewing extension is exposed through
`VampirismApi.services().extendedBrewingRecipeRegistry()` (`IExtendedBrewingRecipeRegistry`). Register
`ExtendedPotionMix` entries – build them with `ExtendedPotionMix.Builder(inputPotion, outputPotion)`,
which supports flags like `concentrated()`, `durable()`, `efficient()`, `master()` and the
`blood()` / `draculaBlood()` reagent presets.

## Data components

`VampirismDataComponents` exposes Vampirism's item/blood components (keys in
`VampirismDataComponents.Keys`), e.g. `OIL_CONTENT`, `APPLIED_OIL`, `VAMPIRE_BOOK`, `BLOOD_CONTAINER`,
`BLOOD_CHARGED`, `BOTTLE_BLOOD`, `SELECTED_AMMUNITION`, `VAMPIRE_SWORD` (training), `DO_NOT_NAME`,
`TRAINING_CACHE`. `Keys` additionally lists ids such as `quarrel_pouch_contents`, `contained_projectiles`,
`pure_level`, `frugality` that have no lookup holder in the API.
