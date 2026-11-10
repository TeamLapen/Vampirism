---
sidebar_position: 11
title: Minions
---

Minions are per-player followers of lord players. A faction brings its own minion entity, and tasks the lord
can assign are separate registry entries.

## Pieces

| Type                          | Registry / role                                                                                      |
|-------------------------------|------------------------------------------------------------------------------------------------------|
| `IMinionEntry<T>`             | `factionapi:minion` – ties a faction to its minion entity type, data factory, stats and appearances.  |
| `IMinionEntity`               | The minion entity (extends `IFactionEntity`).                                                         |
| `IMinionData`                 | Persistent per-minion data (a `MutableDataComponentMap`); lives while the entity is unloaded.         |
| `IMinionInventory`            | The minion's armor, hand and main inventory.                                                          |
| `IMinionTask<T>`              | `factionapi:miniontasks` – a job; `T` is its `IMinionTaskState`.                                      |
| `IMinionTaskCategory`         | `factionapi:miniontask_category` – groups tasks in the selection menu.                                |
| `MinionStat`                  | A levelable stat (health, speed, …) stored as an integer data component.                              |
| `MinionAppearance<T>`         | A customisable look/name option exposed to the create command.                                        |

## Registering a minion entry

`IMinionEntry` is implemented by `MinionEntry` (module `FactionApi`), configured with
`MinionEntryProperties<T>`:

```java
private static final DeferredRegister<IMinionEntry<?>> MINIONS =
        DeferredRegister.create(FactionRegistries.Keys.MINION, MODID);

public static final DeferredHolder<IMinionEntry<?>, IMinionEntry<IMyPlayer>> MY_MINION =
    MINIONS.register("my_faction", () -> new MinionEntry<>(new MinionEntryProperties<IMyPlayer>()
        .withProvider((player, entry) -> new MinionData(entry))   // creates the IMinionData
        .withEntityType(MY_MINION_ENTITY)                         // Holder<EntityType<?>>
        .withFaction(MY_FACTION)
        .withMinionStats(/* MinionStat... */)
        .withMinionAppearances(MinionAppearance.NAME_TYPE, MinionAppearance.SKIN_TYPE,
                               MinionAppearance.MINION_SKIN, MinionAppearance.LORD_SKIN)
        .withMaxLevel(6)));
```

(This follows how Vampirism registers its two minions in `ModFactions`; `withMaxLevel` defaults to `1`.)
`IMinionEntry` exposes `createData`, `type()`, `faction()`, `maxLevel()`, `minionStats()`, `appearances()`
and `minionStat(componentType)`.

## `IMinionEntity`, `IMinionData`, `IMinionInventory`

* `IMinionEntity` – `getCurrentTask()`, `getInventory()`, `getLordOpt()`, `getMinionId()` (all `Optional`,
  empty if the minion data could not be checked out or the lord is not loaded); `recallMinion()` is
  `@ApiStatus.Internal`.
* `IMinionData` – `getActiveTask()` (`IActiveTask<?>` = `task()` holder + `data()` state), `getHealth()`,
  `getMaxHealth()`, `getName()`, `getLevel()`, `getMaxLevel()`, `getRemainingStatPoints()`,
  `hasUsedSkillPoints()`, `isTaskLocked()`, `getInventory()`, `getMinionEntry()`.
* `IMinionInventory` – a vanilla `Container`: `addItemStack(stack)` (reduces the passed stack),
  `getAvailableSize()` (9, 12 or 15 main slots), `getInventoryArmor()`, `getInventoryHands()`,
  `getAllInventories()`, `updateFromData(data)`.

## `MinionStat`

Abstract class with an identifier (an integer data component), a max level and a description.
`apply(level, minion, data)` stores the level, `currentLevel(data)` reads it and `currentValue(minion, data)`
returns the display string. `MinionStat.AttributeBased(identifier, attribute, maxLevel, perLevelValue)` is the
attribute-backed variant (`getAttribute()`, `perLevelValue()`).

## `MinionAppearance<T>`

Implements `ICustomizationOption<T>` (`id()`, `codec()`); wraps a data component plus default value, and
exposes a command argument (`commandName()`, `argumentType()`, `applyCommandArgument(...)`) used when
creating a minion. Built-ins: `NAME_TYPE` (`name`), `SKIN_TYPE` (`skin`), `LORD_SKIN` (`use_lord_skin`),
`MINION_SKIN` (`use_minion_skin`).

## Tasks

```java
private static final DeferredMinionTaskRegister TASKS = DeferredMinionTaskRegister.create(MODID);

public static final DeferredMinionTask<IMinionTask.EmptyState, MyTask> MY_TASK =
    TASKS.registerTask("my_task", MyTask::new,                       // BiFunction<MinionTaskProperties, EmptyState, T> for stateless tasks
        p -> p.factions(MY_FACTION_TAG)                              // tag of allowed factions (default: all)
              .requires(MY_SKILL)                                    // skill the lord needs
              .markGlobal()                                          // may be assigned to all minions at once and is shown in the radial task menu
              .categoryDefault(FactionMinionTaskCategories.MOVEMENT));
```

`DeferredMinionTaskRegister#registerTask` has overloads taking `Function<MinionTaskProperties, T>` (tasks with
their own `IMinionTaskState`) or `BiFunction<MinionTaskProperties, EmptyState, T>` (stateless, uses
`IMinionTask.EmptyState.INSTANCE`), each with an optional `UnaryOperator<MinionTaskProperties>`. The plain
`register(...)` methods are deprecated. `DeferredMinionTask<TState, TTask>` is the typed holder
(`getRawKey()` gives the `ResourceKey<IMinionTask<?>>`).

### `IMinionTask<T extends IMinionTaskState>`

| Method                                   | Meaning                                                                                   |
|------------------------------------------|-------------------------------------------------------------------------------------------|
| `activateTask(lord, minion, data)`       | start the task; returns `ActivateResult<T>` (`failed()` if it cannot start, e.g. lord not loaded) |
| `deactivateTask(state)`                  | called before another task is activated                                                   |
| `tickActive(state, minionGetter, data)`  | tick while the minion is loaded (server only)                                              |
| `tickBackground(state, data)`            | tick while the minion is not loaded (server only)                                          |
| `isFinished(state)` / `getFinishedMessage(state, data)` | checked after each tick; a finished task switches the minion to following its lord and messages the lord |
| `isAvailable(ILordPlayer)`               | whether the lord can currently assign it                                                   |
| `allowedFactions()`                      | `TagKey<IFaction<?>>`                                                                      |
| `stateCodec()`                           | optional `Codec<T>` to persist the state                                                   |
| `getName()`, `getNameId()`               | display name / translation id                                                              |

### `MinionTaskProperties`

Fluent builder that sets data components on the task: `factions(tag)`, `requires(skill)`, `markGlobal()`,
`category(holder)`, `categoryDefault(holder)` plus the generic `component(type, value)`. The task name
component (`minion_task_name`) is set when the registration is finalized. Translation id: `minion_task.<ns>.<path>`.

### Task categories

An `IMinionTaskCategory` (`getDescriptionId()`, `getName()`) is shown as a single entry in the selection
menu; its sub tasks appear on hover. The task marked with `categoryDefault` runs when the category itself is
selected (at most one default per category and faction). The implementation class `MinionTaskCategory`
derives the translation key `minion_task_category.<ns>.<path>`. Built-ins (`FactionMinionTaskCategories`):
`factionapi:collect_resources`, `defend`, `movement`, `recall`.

FactionApi's own tasks: `stay` (movement), `follow_lord` (movement default), `defend_area` (defend),
`protect_lord` (defend default), `collect_resources` (collect default; requires the `minion_collect` skill)
and `collect_wood`, `collect_ores`, `collect_fish`, `collect_plants`, `collect_mob_loot` (require the
`resource_diversity` skill). The collect tasks roll loot tables, see
[Data packs – misc](../data/other#minion-collect-loot-tables).
