---
sidebar_position: 6
title: Utilities
---

## Faction tags (`FactionTags`)

`de.teamlapen.faction.api.tags.FactionTags` holds tag keys in the `factionapi:faction` registry. They are
ordinary data pack tags (`data/factionapi/tags/factionapi/faction/<name>.json`); FactionApi generates all
of them mostly empty and consumer mods (Vampirism) fill them.

| Tag                         | Meaning / what reads it                                                                                                   |
|-----------------------------|----------------------------------------------------------------------------------------------------------------------------|
| `factionapi:all`            | Every registered faction (generated content: `#factionapi:is_neutral`). Must be a union of `not_neutral` and `is_neutral`. Default pool for `IFactionPredicate` targets, default `factions` of skills and minion tasks and `FactionRestriction.ALL`. |
| `factionapi:not_neutral`    | `all` minus `is_neutral`. Used by `IFactionPredicate.Builder#notNeutral()`.                                                  |
| `factionapi:is_neutral`     | Only `factionapi:neutral`. `IFaction.isNeutral(holder)` (also true for `null`) and the totem capture checks test it.          |
| `factionapi:hostile_towards_neutral` | Factions that harm neutral creatures. A predicate built for a faction **in** this tag may target neutral entities; for all other factions `neutral` is removed from the target set (`FactionPredicates`). |
| `factionapi:friendly_towards_neutral` | Factions that do not harm neutral creatures. Declarative counterpart of the above; the code of FactionApi itself does not read it, Vampirism uses it for iron-golem targeting. |
| `factionapi:has_lord_skills`| Factions that get the generic lord skills (`minion_recovery`, `minion_collect`, `resource_diversity`, lord speed / attack speed). Those skills use it as their `factions` tag. |
| `factionapi:can_raid`       | Factions whose players/entities may initiate a village capture or raid at a totem (`TotemBlockEntity#initiateCapture`, bad omen handling). |
| `factionapi:has_random_raid`| Factions that can start a **random** raid on a village they do not control (picked at random from this tag, when `raidRandomChance` triggers). |

:::info
The tag description comes from the Javadoc of `FactionTags`: a faction in `all` should be in either
`hostile_towards_neutral` or `friendly_towards_neutral`.
:::

## Skill tree tags (`FactionSkillTreeTags`)

Tags in the `factionapi:skill_tree` registry (`data/<ns>/tags/factionapi/skill_tree/…`).

| Tag                      | Meaning                                                                                                                  |
|--------------------------|--------------------------------------------------------------------------------------------------------------------------|
| `factionapi:type/level`  | Trees spent from normal faction levels. Vampirism adds its `level` trees.                                                  |
| `factionapi:type/lord`   | Lord skill trees. Vampirism adds its `lord` trees.                                                                         |
| `factionapi:default`     | The default skill point pool: contains `type/level` and `type/lord`. It is the default `skill_point_tag` of a [skill tree](../data/skilltrees) and the default tree requirement of a skill (`SkillProperties` falls back to `new SkillTreeRequirement(FactionSkillTreeTags.DEFAULT)`). The built-in `leveling` / `lord_leveling` skill point providers only hand out points for trees in this tag. |

## Faction-specific tags

Some tags are keyed per faction ("the entity tag *of this faction*"). Register them on the **mod bus**
with `AddFactionTagEvent`:

```java
bus.addListener((AddFactionTagEvent event) ->
    event.faction(MY_FACTION)
         .add(Registries.ENTITY_TYPE, MY_HUNTABLE_MOBS_TAG)        // one tag per registry
         .addCustom(MY_CUSTOM_KEY, MY_CUSTOM_TAG)                  // or per custom ResourceKey
         .defaultTargets(MY_DEFAULT_TARGETS_TAG));                 // see below
```

Read them back with `IFactionSpecificTags.get()`:

| Method                                   | Returns                                             |
|------------------------------------------|-----------------------------------------------------|
| `get(faction, registryKey)` / `get(faction, registryKey, fallback)` | the tag registered via `add(registry, tag)` (`Optional` / fallback) |
| `getCustom(faction, key)` / `getCustom(faction, key, fallback)`     | the tag registered via `addCustom(key, tag)`        |
| `all(registryKey)` / `allCustom(key)`    | `Map<faction, tag>` of everything registered        |

FactionApi itself reads these keys:

| Key (`FactionTagKeys` / registry)                | Registered with                              | Effect                                                                                                                             |
|--------------------------------------------------|----------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| `factionapi:default_targets` (entity type)       | `Builder#defaultTargets(tag)`                | Entity types the faction targets regardless of the target's faction – see [default targets](#default-targets).                      |
| `factionapi:action_disable` (mob effect)         | `Builder#addCustom(FactionTagKeys.ACTION_DISABLES, tag)` | Mob effects that block the faction player's actions. Without it the shared tag `factionapi:disables_actions` (contains `factionapi:resurrection_fatigue`) is used (`ActionHelper#checkActionDisableEffect`). |
| the `factionapi:tasks` registry key              | `Builder#add(FactionRegistries.Keys.TASK, tag)` | Tasks from the shared `factionapi:has_faction` task tag are only offered to the faction if they are in *its* task tag ([Tasks](../data/tasks#task-assignment)). |

## Default targets

Commit "Introduce default target tags for factions and refactor `IFactionPredicate`" added a per-faction
entity-type tag of mobs a faction always attacks (e.g. Vampirism's `HUNTER_DEFAULT_TARGETS` /
`VAMPIRE_DEFAULT_TARGETS`). It only takes effect for predicates built with `defaultTargets()`:

```java
// faction + default target tag + "other" factions that are not neutral
IFactionPredicate p = IFactionPredicate.defaultTargets(MY_FACTION)   // = builder(MY_FACTION).defaultTargets()
        .notNeutral()
        .onlyNonPlayers()
        .build();

goalSelector.addGoal(5, new NearestAttackableTargetGoal<>(mob, PathfinderMob.class, 5, true, false, p));
```

Evaluation order in `FactionPredicate#test`: (1) the player/non-player filter and the `and(...)` predicate
must pass, (2) if the entity type is in the faction's default target tag the entity is a target, (3)
otherwise its faction (disguise aware) must be in the target set. The tag is looked up lazily; no tag
registered means no extra targets.

## Target selection (`IFactionPredicate`)

`IFactionPredicate` is a `Predicate<LivingEntity>` **and** a `TargetingConditions.Selector`; `forEntity()`
adapts it to `Predicate<Entity>`.

```java
IFactionPredicate hostiles = IFactionPredicate.builder(myFaction)   // own faction (optional)
        .notNeutral()          // target every faction except neutral (targetFaction(FactionTags.NOT_NEUTRAL))
        .onlyPlayer()          // players only (or onlyNonPlayers())
        .ignoreDisguise()      // judge by the real faction, not the disguise
        .build();
```

| Builder method                  | Effect                                                                                                         |
|---------------------------------|----------------------------------------------------------------------------------------------------------------|
| `onlyPlayer()` / `onlyNonPlayers()` | restrict by entity kind (default: both)                                                                   |
| `ignoreDisguise()`              | use the actual faction of disguised players                                                                    |
| `allowOwnFaction()`             | the own faction is excluded by default; this includes it                                                       |
| `defaultTargets()`              | also target the faction's [default target](#default-targets) entity types                                     |
| `notNeutral()`                  | target set = `#factionapi:not_neutral`                                                                         |
| `targetFaction(holder)` / `targetFaction(tag)` | explicit target factions (either holders or one tag, not both). Without any, the target is `#factionapi:all`. |
| `and(predicate)`                | extra condition; **replaces** the default `EntitySelector.NO_CREATIVE_OR_SPECTATOR`                             |

With an own faction that is not in `hostile_towards_neutral`, the neutral faction is removed from the
target set.

## Item data components

`FactionDataComponents` exposes the components Vampirism puts on items, e.g.:

* `FACTION_SLAYER` (`IFactionSlayer`) – bonus damage against listed factions.
* `FACTION_RESTRICTION` (`IFactionRestriction`) – item only usable by certain factions.
* `REFINEMENT_SET` (`IEffectiveRefinementSet`) – the refinement set applied to an accessory.

Faction and skill definitions are themselves data-component driven – see the `FactionDataComponents`
`Keys` for the full list.
