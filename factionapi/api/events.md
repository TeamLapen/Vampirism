---
sidebar_position: 7
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

## `FactionVillageEvent`

Game bus. Every subclass exposes the `ITotem` (`getTotem()`), the `getCapturingFaction()` /
`getControllingFaction()` holders, `getVillageArea()` / `getVillageAreaReduced()` and `getLevel()`.

| Subclass                              | Cancellable | When                                                                                                   |
|---------------------------------------|-------------|--------------------------------------------------------------------------------------------------------|
| `SpawnNewVillager`                    | no          | a new (or replacement) villager is about to be spawned after a capture. Replace it via `setNewVillager(...)`; don't add it to the world yourself. `getEntityToReplace()` / `isReplace()` tell whether it replaces an existing entity. |
| `MakeAggressive`                      | yes         | a normal villager should be turned into an aggressive villager; cancel to handle it yourself.          |
| `VillagerCaptureFinish.Pre`           | no          | capture finished, before villagers in the village are changed (`getVillager()`, `isForced()`). `setDisableEntityConversion(true)` skips the entity conversion entirely. |
| `InitiateCapture`                     | no          | a capture is about to start. Call `disallowCapture()` (optionally `setMessage(translationKey)`) to refuse it. |
| `DefineRaidStrength`                  | no          | raid strength ratio is computed; adjust `setAttackStrength` / `setDefendStrength`. `getBadOmenLevel()` is `-2` (player-triggered), `-1` (random), `0..4` (bad omen amplifier). |
| `AreaChangedEvent`                    | no          | the totem's village area changed or was reloaded (`getArea()` may be `null`).                          |
| `RemovedEvent`                        | no          | the totem block entity was removed.                                                                     |
| `BreakCaptureEvent`                   | no          | a running capture was aborted (`TotemBlockEntity#breakCapture`).                                        |
| `UpdateCreaturesOnCaptureFinishEvent` | no          | after the capture villager profession reset; call `requestReplacement(entity)` or `requestKill(entity)` for entities that should be replaced (via `SpawnNewVillager`) or discarded. `KILL` wins over `REPLACE`. |
| `SpawnCaptureEntityEvent`             | no          | a capture/defence entity for `getFaction()` is needed; supply it with `setEntity(entity)`. If it implements `IVillageCaptureEntity` it is told to attack or defend the village. |
| `SpawnVillagerEvent`                  | no          | declared in the API with only the base totem accessors; the current implementation does not post it. |

## `RegisterItemBarsEvent` (client, mod bus)

Lets mods add extra progress bars under an item's icon (stacked below the vanilla durability bar).
Fired once during client setup.

```java
@SubscribeEvent // mod bus, client only
public static void registerItemBars(RegisterItemBarsEvent event) {
    event.register(MyMod.id("my_bar"), (stack, bars) -> {
        if (stack.getItem() instanceof MyChargeable c) {
            bars.accept(new ItemBar(c.chargePercentage(stack), 0xFF55AAFF));
        }
    });
}
```

* `ItemBarProvider` – `void addBars(ItemStack stack, Consumer<ItemBar> bars)`; called for every rendered
  stack, call `bars.accept(...)` once per bar to show.
* `ItemBar(float progress, int color)` – `progress` is clamped to `0..1`, `color` is ARGB.
* `register(id, provider)` throws `IllegalArgumentException` for a duplicate id; providers are rendered in
  id order.

`FactionOverlays` (`de.teamlapen.faction.api.client`) only holds the ids of FactionApi's own HUD layers:
`faction_level`, `action_cooldown`, `raid_bar` and `action_duration` (all in the `factionapi` namespace),
so other mods can order their own `RegisterGuiLayersEvent` layers relative to them.

## Others

* `AddFactionTagEvent` – **mod bus** – register faction-specific tags (see
  [Utilities](./utilities#faction-specific-tags)).
