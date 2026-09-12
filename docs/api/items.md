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
| `IHunterCrossbow`      | Hunter crossbow behaviour.                                     |
| `IVampirismQuarrel` / `IEntityQuarrel` | Crossbow bolts / thrown quarrels.             |

"Slayer" bonus damage against a faction is FactionApi's `FACTION_SLAYER` item component, not a
Vampirism interface.

## Brewing

The Alchemy Table's brewing extension is exposed through
`VampirismApi.services().extendedBrewingRecipeRegistry()` (`IExtendedBrewingRecipeRegistry`). Register
`ExtendedPotionMix` entries – build them with `ExtendedPotionMix.Builder(inputPotion, outputPotion)`,
which supports flags like `concentrated()`, `durable()`, `efficient()`, `master()` and the
`blood()` / `draculaBlood()` reagent presets.

## Data components

`VampirismDataComponents` exposes Vampirism's item/blood components (keys in
`VampirismDataComponents.Keys`), e.g. `OIL_CONTENT`, `APPLIED_OIL`, `VAMPIRE_BOOK`, `BLOOD_CONTAINER`,
`BLOOD_CHARGED`, `BOTTLE_BLOOD`, `SELECTED_AMMUNITION`, `VAMPIRE_SWORD` (training).
