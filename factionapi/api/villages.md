---
sidebar_position: 10
title: Villages
---

Faction villages are controlled through a **totem**. A faction can capture a village (the totem's
`controllingFaction` changes), raid it and has guards, a task master and a banner. FactionApi provides the
totem implementation (`TotemBlockEntity`); the API gives you the hooks.

## Configuring a faction

All village settings are `FactionProperties` calls that set data components on the faction:

| `FactionProperties` call            | Data component (`FactionDataComponents`) | Meaning                                                          |
|-------------------------------------|------------------------------------------|------------------------------------------------------------------|
| `totem(fragile, crafted)`           | `VILLAGE_TOTEM` (`TotemPair`)            | totem top blocks: world-generated (fragile) and crafted          |
| `banner(VillageBanner)`             | `VILLAGE_BANNER`                         | ominous-banner look that carries the bad omen                    |
| `badOmen(Holder<MobEffect>)`        | `VILLAGE_BAD_OMEN`                       | effect that can trigger a raid of this faction                   |
| `villageGuards(TagKey<EntityType>)` | `VILLAGE_GUARDS`                         | entity types that guard the faction's villages                   |
| `taskMaster(Holder<EntityType>)`    | `TASK_MASTER` (also enables tasks)       | task master entity for the faction                               |

Whether a faction may start a capture or a (random) raid is controlled by the
[`can_raid` and `has_random_raid` faction tags](./utilities#faction-tags-factiontags).

## Types

* **`ITotem`** – implemented by the totem block entity. `getCapturingFaction()` (nullable),
  `getControllingFaction()`, `getVillageArea()`, `getVillageAreaReduced()`, `getTileLevel()`, `position()`,
  `isRaidTriggeredByBadOmen()`, `getVampireForestLocation()` (empty by default). It is the first parameter of
  every [`FactionVillageEvent`](./events#factionvillageevent).
* **`TotemPair(fragile, crafted)`** – record of two `Holder<Block>`; `get(boolean crafted)`;
  `TotemPair.DEFAULT` uses `FactionBlockAccess.TOTEM_TOP` / `TOTEM_TOP_CRAFTED` (`factionapi:totem_top` and
  `factionapi:totem_top_crafted`).
* **`VillageBanner(base, layers)`** – base banner item plus `(ResourceKey<BannerPattern>, DyeColor)` layers.
  `createBanner(HolderLookup.Provider)` builds the stack named "Ominous Banner" and marks it with the
  `IS_FACTION_BANNER` component.
* **`IFactionVillage`** / **`IFactionVillageBuilder`** – older accessor/builder interface for the same data
  (`badOmenEffect()`, `createBanner(...)`, `getVillageGuardTypes()`, `getTaskMasterEntity()`,
  `getTotemTopBlock(boolean crafted)`; builder: `badOmenEffect`, `banner`, `guardTypes`, `taskMaster`, `totem`,
  `build`). Prefer the `FactionProperties` calls above in new code.

## Entity interfaces

| Interface               | Purpose                                                                                           |
|-------------------------|---------------------------------------------------------------------------------------------------|
| `IVillageCaptureEntity` | Entities that attack/defend villages during a capture: `attackVillage(ICaptureAttributes)`, `defendVillage(...)`, `getCaptureInfo()`, `getTargetVillageArea()`, `isAttackingVillage()`, `isDefendingVillage()`, `stopVillageAttackDefense()`. |
| `ICaptureAttributes`    | Handed to those entities: `getAttackingFaction()`, `getDefendingFaction()`, `getPosition()`, `getVillageArea()`, `shouldForceTargets()` (AI should target the opposing factions without line-of-sight restrictions; default `false`). |
| `ICaptureStrengthProvider` | `float getCaptureStrength()` – an entity's weight when the totem computes the strength ratio. |
| `ICaptureIgnore`        | Marker: the totem ignores these entities when counting capture participants.                       |
| `IEntityLeader`         | Leader of followers: `getFollowingCount()`, `getMaxFollowerCount()`, `increaseFollowerCount()` (returns whether more are allowed), `decreaseFollowerCount()`. |
| `IEntityWithHome`       | Extends the vanilla home system with an `AABB` home: `getHome()`, `setHome(AABB)`, `getHomePosition()`, `isWithinHomeDistance(...)`, `setHomeArea(pos, radius)`. |
| `IFactionMob`           | Marker for non-player faction entities (extends `IFactionEntity`).                                  |
| `ITaskMasterEntity`     | Marker for task masters (extends `IEntityWithHome`); returned by the faction's `TASK_MASTER` type.  |

## Village data components

The village related keys of `FactionDataComponents` are `village_totem`, `village_banner`,
`village_bad_omen`, `village_guards`, `task_master` and the item marker `is_faction_banner`; see the full table
in [Misc](./misc#data-component-keys).

## Hooking into captures

Use the [`FactionVillageEvent`](./events#factionvillageevent) subclasses, e.g.:

```java
NeoForge.EVENT_BUS.addListener((FactionVillageEvent.InitiateCapture e) -> {
    if (e.getTotem().getControllingFaction().is(MY_PROTECTED_FACTION)) {
        e.setMessage("message.mymod.village_protected");
        e.disallowCapture();
    }
});
```
