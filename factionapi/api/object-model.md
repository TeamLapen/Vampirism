---
sidebar_position: 2
title: Object model
---

## Factions

* `IFaction<T extends IFactionEntity>` – any faction an entity can belong to (including the built-in
  `Factions.NEUTRAL`). Carries the display name, colors and *extensions*.
* `IPlayableFaction<T extends IFactionPlayer<T>>` – a faction a **player** can join. Adds max level,
  max lord level, the player capability and accessory support.
* `IFaction.is(a, b)`, `IFaction.is(holder, tag)`, `IFaction.isNeutral(holder)` – null-safe comparison
  helpers you should use instead of comparing holders directly.

## The player handler

`IFactionPlayerHandler` is attached to **every** player (`FactionAttachments.FACTION_PLAYER_HANDLER`)
and is the main runtime access point:

* Membership – `getFaction()`, `isInFaction(holder|tag)`, `canJoin(...)`, `joinFaction(...)`,
  `canLeaveFaction()`, `leaveFaction(die)`, `setFaction(FactionUpdate)`.
* Levels – `getCurrentLevel()`, `getCurrentLevel(faction)`, `getCurrentLevelRelative()`.
* Sub-handlers for the active faction – `factionPlayer()`, `getCurrentSkillPlayer()`,
  `getSkillHandler()`, `getActionHandler()`.
* `checkSkillTreeLocks()` – re-evaluates which skill trees are unlocked.

`IFactionPlayer<T>` is the per-faction capability (Vampirism's `VampirePlayer`, `HunterPlayer`, …).
`ISkillPlayer<T>` extends it and adds `getSkillHandler()` / `getActionHandler()`.

## Faction extensions

A faction opts in to optional sub-systems by declaring **extensions** in its `FactionProperties`.
Each extension is resolved per player and cleaned up when the player leaves the faction:

| Extension            | Enabled by                                   | Runtime access                              |
|----------------------|----------------------------------------------|--------------------------------------------|
| `ITaskManager`       | `FactionProperties#enableTasks()` / `taskMaster(...)` | `handler.getTaskManager()`         |
| `IRefinementHandler` | `enableRefinements()` / `refinements(...)`    | `IRefinementHandler.get(player)`           |
| `ILordPlayer`        | `enableLord()` / `lord(level[, titles])`      | `handler.getPlayerLord()`                  |
| *custom*             | `extension(MyInterface.class, attachmentOrComponent)` | `handler.getExtension(MyInterface.class)` |
