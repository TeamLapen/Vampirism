---
sidebar_position: 3
title: Registries
---

All Faction API registries live in the `factionapi` namespace and are listed in
`FactionRegistries.Keys`. Access a registry at runtime through
`FactionRegistries.<NAME>.get()`.

## Code registries

Registered with a `DeferredRegister` (or the dedicated helpers) during mod construction. See
[Registering content](./registering-content) for examples.

| Registry key                                | Type                                               | Used for                                                                                   | Register with |
|---------------------------------------------|--------------------------------------------------|-----------------------------------------------------------------------------------------|---------------|
| `factionapi:faction`                        | `IFaction<?>`                                     | All factions.                                                                             | `DeferredFactionRegister#registerFaction(name, props -> …)` |
| `factionapi:skills`                         | `ISkill<?>`                                       | Player skills (nodes shown in skill trees).                                               | `DeferredSkillRegister#registerSkill(name, props -> …)` |
| `factionapi:actions`                        | `IAction<?>` / `ILastingAction<?>`               | Activatable player abilities (with cooldown, optional duration).                          | `DeferredActionRegister#registerAction(name, …)` |
| `factionapi:skill_point_provider`           | `ISkillPointProvider`                             | Computes how many skill points a player has for a given skill tree.                       | `DeferredRegister` |
| `factionapi:faction_player_consumer`        | `FactionPlayerConsumer`                           | Named `Consumer<IFactionPlayer>` callbacks – referenced by the task `consumer` reward and skill enable/disable consumables. | `DeferredRegister` |
| `factionapi:faction_player_boolean_supplier`| `FactionPlayerBooleanSupplier`                    | Named player predicates – referenced by the task `boolean` requirement.                   | `DeferredRegister` |
| `factionapi:minion`                         | `IMinionEntry<?, ?>`                              | The minion type belonging to a faction (entity type, data factory, command arguments).   | `DeferredRegister` |
| `factionapi:miniontasks`                    | `IMinionTask<?, ?>`                               | Tasks a lord can assign to minions.                                                       | `DeferredRegister` |
| `factionapi:refinement`                     | `IRefinement`                                     | A single accessory effect (usually an attribute modifier).                               | `DeferredRegister` |
| `factionapi:refinement_set`                 | `IRefinementSet`                                  | A named group of refinements with a rarity, faction and optional slot restriction.       | `DeferredRegister` |
| `factionapi:task_requirement`               | `MapCodec<? extends TaskRequirement.Requirement>` | Task requirement *types* (`entity`, `item`, `stat`, …). See [Tasks](../data/tasks).       | `DeferredRegister` |
| `factionapi:task_reward`                    | `MapCodec<? extends TaskReward>`                  | Task reward *types* (`item`, `lord_level`, `refinement`, …).                              | `DeferredRegister` |
| `factionapi:task_unlocker`                  | `MapCodec<? extends TaskUnlocker>`               | Task unlocker *types* (`level`, `lord_level`, `parent`).                                  | `DeferredRegister` |
| `factionapi:task_reward_instance`           | `MapCodec<? extends ITaskRewardInstance>`         | Serialized runtime state of a granted-but-not-collected reward.                           | `DeferredRegister` |
| `factionapi:food_behaviour`                 | `IFactionFoodBehavior`                            | Extra behaviour applied when a faction player eats a tagged food.                         | `DeferredRegister` |

## Data pack registries

Populated from JSON (or datagen). Covered in the [Data Packs](../data/intro) section.

| Registry key               | Type            | Page                                        |
|----------------------------|-----------------|--------------------------------------------|
| `factionapi:tasks`         | `Task`          | [Tasks](../data/tasks)                      |
| `factionapi:skill_tree`    | `ISkillTree`    | [Skill Trees](../data/skilltrees)           |
| `factionapi:skill_segment` | `ISkillSegment` | [Skill Segments](../data/skilltrees#skill-segment) |
