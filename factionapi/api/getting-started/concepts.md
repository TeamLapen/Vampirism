---
sidebar_position: 1
title: Concepts
---

Read this page first if you have never worked with FactionApi. It explains what the pieces are and how
they fit together, so the [step-by-step tutorial](./first-faction) makes sense.

## The big picture

FactionApi is a framework for **"a player can join a group, level up in it and get group-specific
features"**. You describe your group (a *faction*) and FactionApi provides everything around it.

| You provide                                                | FactionApi provides                                                                 |
|------------------------------------------------------------|-------------------------------------------------------------------------------------|
| A faction registration (name, colour, max level, …)        | Join / leave / level handling, saving, client sync, `/factions` commands            |
| A small class that holds your per-player state             | Attaching it to every player, calling it on tick / death / respawn / login          |
| Skills, actions, tasks, minions, … (all optional)          | Skill screen, action keys and cooldowns, task board, minion management, villages    |
| Translations and a few data pack files (tags, skill trees) | Loading, validation, syncing of data pack content                                   |

Nothing is player-visible until a player is **in** your faction. By itself FactionApi adds no playable
faction – that is your mod's job (Vampirism adds `vampirism:vampire` and `vampirism:hunter` this way).

## Everyone has a faction

Every player is in exactly one faction at any time. Players that never joined anything are in the built-in
`factionapi:neutral` faction at level `0` (shown as "Human"). A player leaves your faction by joining another
one or by returning to neutral. This has two consequences you will rely on constantly:

* `IFaction.isNeutral(holder)` is true for neutral (and for `null`) – use it instead of `== null` checks.
* Entities that are not players resolve to a faction too: mobs that implement `IFactionEntity`, or mobs whose
  entity type is in the faction's entity tag (see [Faction mobs](./optional-features#faction-mobs-and-targeting));
  everything else is neutral.

## Where things live

A faction mod is made of four kinds of content. Knowing which is which tells you where to look when
something is missing.

| Kind                  | What it is                                                        | Examples                                                     | Where you define it |
|-----------------------|-------------------------------------------------------------------|--------------------------------------------------------------|---------------------|
| **Code registries**   | Java objects registered during mod loading                       | the faction, skills, actions, minion entries, task *types*   | Java (`DeferredRegister` helpers) |
| **Per-player state**  | An attachment holding your faction's data for one player         | your `IFactionPlayer` class                                  | Java (attachment) |
| **Data pack content** | JSON loaded at world load; can be replaced by modpack authors    | skill trees, skill segments, tasks, tags                     | `data/<namespace>/…` JSON |
| **Assets**            | Client resources                                                  | translations, skill / action icons                           | `assets/<namespace>/…` |

A rule of thumb: **behaviour is code, structure and balance are data.** A skill's effect is Java; where it
sits in the skill tree and which tree it belongs to is JSON.

## The two modules

FactionApi is published as two artifacts (see [Depending on FactionApi](../../wiki/gradle-setup)):

| Artifact / package                                      | Contains                                                                                  |
|---------------------------------------------------------|-------------------------------------------------------------------------------------------|
| `FactionApi-api` · `de.teamlapen.faction.api`           | Interfaces, registry keys, events, builders (`IFaction`, `IFactionPlayer`, `FactionProperties`, `FactionUpdate`, `DeferredFactionRegister`, …) |
| `FactionApi` · `de.teamlapen.faction.common` (and others) | The implementation **and the ready-made base classes you extend to create a faction**.   |

To **use** an existing faction (read a player's faction, check a skill) the `api` package is enough. To
**create** a faction you additionally need these implementation classes, so depend on the full `FactionApi`
artifact:

| Class                                                                 | Used for                                                              |
|-----------------------------------------------------------------------|-----------------------------------------------------------------------|
| `de.teamlapen.faction.common.factions.PlayableFaction`                | The faction object you register                                       |
| `de.teamlapen.faction.common.factions.FactionBasePlayer`              | Base class of your per-player state class                             |
| `de.teamlapen.faction.common.util.AttachmentSynchronization`          | Saves and syncs your per-player class                                 |
| `de.teamlapen.faction.common.event.PlayerEventHandlerEvent`           | Makes FactionApi call your player class (tick, death, …)              |
| `de.teamlapen.faction.common.factions.skills.Skill` / `…actions.DefaultAction` | Base classes for skills and actions (only if you add them)  |
| `de.teamlapen.faction.common.factions.skills.SkillHandler` / `…actions.ActionHandler` | Skill and action bookkeeping (only if you add skills / actions) |

These live outside the `api` package, so they are implementation classes. Vampirism uses exactly the same
ones.

## What a faction is, in objects

```text
DeferredFaction<IWardenPlayer, IPlayableFaction<IWardenPlayer>>   ← handle to your registered faction
 └─ PlayableFaction                                              ← the faction object (+ data components)
      ├─ playerAttachment ─────────────► AttachmentType<WardenPlayer>  ← per-player state, exists on EVERY player
      ├─ extensions (optional) ────────► lord levels, tasks, refinements, your own
      └─ data components (optional) ───► colour, max level, totem, banner, task master, …

Every player also has:  IFactionPlayerHandler  ← knows the player's current faction + level
```

* **`IFaction<T>`** – any faction. `T` is the kind of entity that can belong to it.
* **`IPlayableFaction<T>`** – a faction players can join. `T` is your player interface (`IWardenPlayer`).
* **`IFactionPlayer<T>`** – the per-player object of one playable faction. It is attached to **every**
  player, but is only "active" while the player is in that faction (its `getLevel()` is `0` otherwise).
* **`IFactionPlayerHandler`** – attached to every player by FactionApi itself. It is the single source of
  truth for "which faction is this player in and at which level". Get it with
  `FactionsApi.factionPlayerHandler(player)`.
* **`FactionUpdate`** – the object describing a change (new faction, level, lord level). All changes go
  through `handler.setFaction(FactionUpdate…)`; the `/factions` commands and Vampirism's altars use it too.
* **Extension** – an optional sub-system a faction opts into: lords, tasks, refinements, or your own.
  Enabled with a `FactionProperties` call; see [Object model](../object-model#faction-extensions).
* **Data component** – a typed value stored on the faction (`FACTION_COLOR`, `MAX_LEVEL`, …). The
  `FactionProperties` builder methods set them; you rarely touch them directly.

## Life of a player in your faction

1. Your code (or `/factions faction set mymod:warden`) calls `handler.joinFaction(faction)` or
   `handler.setFaction(FactionUpdate…)`. `joinFaction` first asks `PlayerFactionEvent.CanJoinFaction` (by default a
   player may only join from the neutral faction) and then sets level `1`; `setFaction` skips that check, but
   refuses to change faction if the *current* faction's player class returns `false` from `canLeaveFaction()`, and
   fires the cancellable `FactionLevelChangePre`. The level is clamped to `0..maxLevel`.
2. When the faction changes, the old faction's player class gets **`leaveFaction()`** and its extensions are
   cleaned up. Then the handler stores the new faction + level and calls **`levelChanged(FactionUpdate)`** on the
   new faction's player class.
3. While the player is in the faction, FactionApi calls your player class on tick, death, respawn, login, …
   (`IPlayerEventListener`). These calls are made for **every** player, so guard with `getLevel() > 0`.
4. Levels hand out skill points (`(level − 1) × factionSkillPointsPerLevel`, default 2 per level), skill trees
   unlock depending on level / lord level, and – if enabled – lord levels become available.

## Glossary

| Term                  | Meaning                                                                                              |
|-----------------------|------------------------------------------------------------------------------------------------------|
| Playable faction      | A faction a player can join; has levels and a player class.                                           |
| Neutral               | The built-in faction of players without faction (`factionapi:neutral`, shown as "Human").             |
| Level                 | Main progression number of a faction, `1..maxLevel` (0 = not in the faction).                         |
| Lord level            | Optional second progression track after the max level; unlocks lord skills, titles and minions.       |
| Skill                 | An unlockable perk. Code object (effect) + position in a skill tree (data).                           |
| Skill tree / segment  | A tab in the skill screen / one node (or a pick-one choice) in it. Data pack JSON.                    |
| Action                | An ability the player activates with a key; has a cooldown, optionally a duration. Unlocked by a skill. |
| Task                  | A small quest from a task master with requirements and a reward. Data pack JSON.                      |
| Refinement            | A random modifier set carried by accessory items (amulet, ring, belt).                                |
| Minion                | A follower of a lord player with assignable jobs.                                                     |
| Totem / village       | The block that lets factions capture and raid villages.                                               |
| Faction tag           | A normal data pack tag in the `factionapi:faction` registry, e.g. `factionapi:all`.                   |

## What is required and what is optional

For a working faction you need exactly: the **faction**, the **per-player class + attachment**, the
**event hook**, **translations** and the **faction tags**. Everything else is opt-in:

| Feature          | Opt in with                              | Guide |
|------------------|------------------------------------------|-------|
| Skills & actions | implement `ISkillPlayer`, register skills | [Skills and actions](./skills-and-actions) |
| Lord levels      | `FactionProperties#lord(max)`             | [Optional features](./optional-features#lord-levels) |
| Tasks            | `FactionProperties#taskMaster(entity)`    | [Optional features](./optional-features#tasks) |
| Refinements      | `FactionProperties#refinements(...)`      | [Optional features](./optional-features#refinements) |
| Villages         | `totem`, `banner`, `badOmen`, `villageGuards` | [Optional features](./optional-features#villages) |
| Minions          | register an `IMinionEntry`                | [Optional features](./optional-features#minions) |

Stuck later? See [Troubleshooting](./troubleshooting).
