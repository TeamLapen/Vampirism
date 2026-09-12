---
sidebar_position: 4
title: Registering content
---

See [Registries](./registries) for the full list of registry keys.

## Register a faction

```java
private static final DeferredFactionRegister FACTIONS = DeferredFactionRegister.create(MODID);

public static final DeferredFaction<IMyPlayer, IPlayableFaction<IMyPlayer>> MY_FACTION =
    FACTIONS.registerFaction("my_faction", props -> new PlayableFaction<>(props
        .playerAttachment(MOD_ATTACHMENTS.MY_PLAYER)   // the IFactionPlayer capability
        .color(0xRRGGBB)
        .maxLevel(14)
        .lord(5)                                       // enable lord levels + titles
        .enableTasks().taskMaster(MOD_ENTITIES.MY_TASKMASTER)
        .refinements(new RefinementItems(RING, AMULET, BELT))
        .badOmen(MOD_EFFECTS.MY_BAD_OMEN)
        .totem(FRAGILE_TOTEM, CRAFTED_TOTEM)
        .banner(MyVillage.create())
        .extension(IMyLordExtra.class, MOD_ATTACHMENTS.MY_LORD_EXTRA)));
```

`FactionProperties` is a fluent builder; every `.xxx()` call either sets a data component on the
faction or registers an extension. `PlayableFaction` / `Faction` are implementation base classes from
the `FactionApi` module.

## Register skills and actions

```java
private static final DeferredSkillRegister SKILLS = DeferredSkillRegister.create(MODID);
private static final DeferredActionRegister<IMyPlayer> ACTIONS = DeferredActionRegister.create(MODID);

public static final DeferredAction<IMyPlayer, IAction<IMyPlayer>, MyAction> DASH =
    ACTIONS.registerAction("dash", MyAction::new);

public static final DeferredSkill<IMyPlayer, ISkill<IMyPlayer>> DASH_SKILL =
    SKILLS.registerSkill("dash", props -> new MySkill<>(props
        .factions(MY_FACTION_TAG)          // which factions may take it (required)
        .cost(1)
        .tree(MY_SKILL_TREE)               // restrict to a tree or tree tag
        .actionSkill(DASH)                 // this skill just unlocks the action
        .withDescription()));
```

`SkillProperties` covers cost, allowed factions/trees, attribute modifiers
(`attribute(attr, amount, op)`), enable/disable consumers and action wiring.

Which skill trees a skill can appear in is enforced by its `allowedSkillTrees()`
(`SkillTreeRequirement`, set via `SkillProperties#tree`). The tree/segment graph itself is defined in
[data packs](../data/skilltrees).

## Add task requirement / reward / unlocker types

Register a `MapCodec` into `factionapi:task_requirement`, `factionapi:task_reward` or
`factionapi:task_unlocker`; pack makers can then use your `type` in
[task JSON](../data/tasks). Reward types that grant something asynchronously also register an
`ITaskRewardInstance` codec.

## Runtime callbacks referenced from JSON

Register a `FactionPlayerConsumer` (`factionapi:faction_player_consumer`) or
`FactionPlayerBooleanSupplier` (`factionapi:faction_player_boolean_supplier`) and reference it by id
from a task `consumer` reward / `boolean` requirement, or from a skill's enable/disable consumer.

## Minions

Register an `IMinionEntry` (`factionapi:minion`) via `MinionEntryBuilder` to give your faction a
minion (entity type + data + `/minion` command arguments), and `IMinionTask`s
(`factionapi:miniontasks`) for the jobs a lord can assign.
