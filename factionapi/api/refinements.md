---
sidebar_position: 8
title: Refinements
---

Refinements are modifiers carried by *accessory* items (amulet, ring, obi belt). A faction opts in with
`FactionProperties#refinements(RefinementItems)` (or `enableRefinements()`), see
[Object model](./object-model#faction-extensions).

| Type                     | Registry / location              | Role                                                                                       |
|--------------------------|----------------------------------|--------------------------------------------------------------------------------------------|
| `IRefinement`            | `factionapi:refinement`          | One effect: an optional `Attribute` plus a modifier value.                                 |
| `IRefinementSet`         | `factionapi:refinement_set`      | A named group of refinements with colour, rarity, faction tag and optional slot restriction. |
| `IRefinementItem`        | implemented by `Item`s           | An accessory item that can hold a set.                                                     |
| `IEffectiveRefinementSet`| item data component `factionapi:refinement_set` | The set that is applied to a stack (`set()`, `@Nullable`).               |
| `IRefinementHandler`     | faction extension / attachment   | Player-side handler of equipped accessories.                                               |

## `IRefinement`

* `getAttribute()` – the modified `Holder<Attribute>` (nullable), `getModifierValue()` – the amount.
* `attributeFactory()` – `BiFunction<Identifier, Double, AttributeModifier>` creating the modifier from an id
  and value (nullable, for refinements that do not modify an attribute).
* `getDescriptionId()` / `getDescription()` – translation key / component.

## `IRefinementSet`

`getColor()`, `getFaction()` (a `TagKey<IFaction<?>>`), `getName()`, `getDescriptionId()`, `getRarity()`,
`getRefinements()` and `getSlotType()` (empty = any slot).

`IRefinementSet.Rarity` – `COMMON`, `UNCOMMON`, `RARE`, `EPIC`, `LEGENDARY`. Each has a `weight`
(4, 3, 3, 2, 1) and a `ChatFormatting` colour (white, green, blue, dark purple, gold); the serialized name is
the lower-case constant, e.g. `"rare"` in the [refinement task reward](../data/tasks#refinement-reward).

## `IRefinementItem`

```java
public class MyRing extends Item implements IRefinementItem {
    public IRefinementSet getRefinementSet(ItemStack stack) { ... }          // may be null
    public AccessorySlotType getSlotType() { return AccessorySlotType.RING; }
    public boolean applyRefinementSet(ItemStack stack, IRefinementSet set) { ... }
    public HolderSet<IFaction<?>> getExclusiveFactions(ItemStack stack) { ... }
}
```

`AccessorySlotType`: `AMULET` (slot 0), `RING` (1), `OBI_BELT` (2); `getSlot()` is the slot index.
`IRefinementItem` should only be implemented by `Item`s.

## `RefinementItems`

Record of the accessory items a faction uses: `RefinementItems(amulet, ring, belt)` – constructor order is
**amulet, ring, belt** – with `List<Holder<Item>>` or single-`Holder<Item>` overloads. `stream()` and
`stream(AccessorySlotType)` return the items that implement `IRefinementItem`. Stored on the faction in
`FactionDataComponents.REFINEMENTS`.

## `IRefinementHandler`

`IRefinementHandler.get(player)` returns `Optional<IRefinementHandler>` (empty if the player's faction has no
refinements).

| Method                             | Meaning                                                                                         |
|------------------------------------|-------------------------------------------------------------------------------------------------|
| `getRefinementItems()`             | equipped accessory stacks                                                                       |
| `equipRefinement(stack)`           | equip the set from the stack; if it has none or is of the wrong faction the slot is cleared only |
| `removeRefinement(slot)`           | remove the refinement of a slot                                                                 |
| `damageRefinements()`, `updateItems()` | damage the items / refresh them                                                             |
| `resetRefinements()`, `reset()`    | remove all refinements / reset skills but keep the faction's root skill                          |
| `isRefinementEquipped(holder)`     | from `IRefinementAccess`                                                                         |

`IRefinementAccess` is the read-only view (`isRefinementEquipped`, `asHandler()`); `IRefinementAccess.EMPTY`
never has anything equipped and `IRefinementAccess.from(handler)` wraps a handler.
