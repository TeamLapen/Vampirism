---
sidebar_position: 6
title: Events
---

All on `NeoForge.EVENT_BUS` unless noted.

## `PlayerFactionEvent`

| Subclass                 | Cancellable | When                                                        |
|--------------------------|-------------|----------------------------------------------------------|
| `FactionLevelChangePre`  | yes         | before a faction/level change; cancel to veto it           |
| `FactionLevelChanged`    | no          | after a faction/level change                               |
| `LevelChanged`           | no          | after any `FactionUpdate` is applied                       |
| `CanJoinFaction`         | no          | join check – set `Behavior` (`DENY` / `ONLY_WHEN_NO_FACTION` / `ALLOW`) |

## `SkillEvents`

| Subclass                | When                                                                       |
|-------------------------|-------------------------------------------------------------------------|
| `SkillUnlockCheckEvent` | before the unlock check; set a `ISkillHandler.Result` to short-circuit    |
| `SkillEnableEvent`      | a skill was enabled (has `isFromLoading()` and the `ISkillTree`)          |
| `SkillDisableEvent`     | a skill was disabled                                                      |

## `ActionEvent`

| Subclass                 | Cancellable | When                                                       |
|--------------------------|-------------|---------------------------------------------------------|
| `ActionActivatedEvent`   | yes         | before an action fires; adjust cooldown/duration/cancel message |
| `ActionDeactivatedEvent` | no          | a lasting action ends; adjust cooldown handling            |
| `ActionUpdateEvent`      | no          | per-tick tick for a lasting action; can force deactivation |

## Others

* `FactionVillageEvent.*` – village capture lifecycle (`SpawnNewVillager`, `MakeAggressive`,
  `InitiateCapture`, `DefineRaidStrength`, `AreaChangedEvent`, …).
* `AddFactionTagEvent` – **mod bus** – register faction-specific tags (see
  [Utilities](./utilities#faction-specific-tags)).
