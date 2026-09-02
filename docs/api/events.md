---
sidebar_position: 6
title: Events
---

Vampirism fires a small set of game-bus events (`NeoForge.EVENT_BUS`) from
`de.teamlapen.vampirism.api.event`. Faction / skill / action / village events are
**[FactionApi's](/factionapi/api/events)**.

## `BloodDrinkEvent`

Fired when blood is drained by a bite. Common getters/setters: `getVampire()`, `getAmount()` /
`setAmount(int)`, `getSaturation()` / `setSaturationModifier(float)`, `getBloodSource()`
(`IDrinkBloodContext`).

| Subclass                | When                                          | Extra                                   |
|-------------------------|-----------------------------------------------|-------------------------------------|
| `PlayerDrinkBloodEvent` | a **vampire player** drinks blood             | `useRemaining()` / `setUseRemaining(boolean)` |
| `EntityDrinkBloodEvent` | a **non-player vampire** drinks blood         | `useRemaining()` / `setUseRemaining(boolean)` |

Adjusting `amount` / `saturation` changes what the drinker actually gains.

## `VampireFogEvent`

Fired when the vampire fog density is computed. `getFogDistanceMultiplier()` /
`setFogDistanceMultiplier(float)` to strengthen or weaken the fog.

## `VampirismEventFactory`

`de.teamlapen.vampirism.api.util.VampirismEventFactory` is what Vampirism uses to post these events
(`fireVampirePlayerDrinkBloodEvent`, `fireVampireDrinkBlood`, `fireVampireFogEvent`); useful if an
addon re-implements a code path and wants to keep firing the events.
