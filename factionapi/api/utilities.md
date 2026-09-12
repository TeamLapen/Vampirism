---
sidebar_position: 5
title: Utilities
---

## Faction-specific tags

Some tags are keyed per faction. Register them on the mod bus:

```java
bus.addListener((AddFactionTagEvent event) ->
    event.faction(MY_FACTION)
         .add(Registries.ENTITY_TYPE, MY_HUNTABLE_MOBS_TAG)
         .addCustom(MY_CUSTOM_KEY, MY_CUSTOM_TAG));
```

and read them back with `IFactionSpecificTags.get().get(faction, registryKey, fallback)`.

## Target selection

`IFactionPredicate.Builder` builds a `Predicate<LivingEntity>` /
`TargetingConditions.Selector` for "attack anything not my faction" style checks:

```java
IFactionPredicate hostiles = IFactionPredicate.builder(myFaction)
        .notNeutral()          // ignore factionless creatures
        .onlyPlayer()          // players only (optional)
        .ignoreDisguise()      // see through disguises
        .build();
```

## Item data components

`FactionDataComponents` exposes the components Vampirism puts on items, e.g.:

* `FACTION_SLAYER` (`IFactionSlayer`) – bonus damage against listed factions.
* `FACTION_RESTRICTION` (`IFactionRestriction`) – item only usable by certain factions.
* `REFINEMENT_SET` (`IEffectiveRefinementSet`) – the refinement set applied to an accessory.

Faction and skill definitions are themselves data-component driven – see the `FactionDataComponents`
`Keys` for the full list.
