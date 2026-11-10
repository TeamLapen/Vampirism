---
sidebar_position: 4
title: Optional features
---

A faction from the [tutorial](./first-faction) can level up. Everything on this page is opt-in. Each section
tells you what to switch on, what you must provide yourself, and where the details are. The examples continue
the Warden faction.

## `FactionProperties` cheat sheet

Everything you can set in `registerFaction("name", props -> new PlayableFaction<>(props…))`
(`de.teamlapen.faction.api.factions.FactionProperties`):

| Call                                  | Meaning                                                                                   | Required |
|---------------------------------------|-------------------------------------------------------------------------------------------|----------|
| `playerAttachment(holder)`            | The per-player attachment of your player type                                             | **yes**  |
| `color(rgb)`                          | GUI colour; also sets the chat colour                                                     | no       |
| `chatColor(TextColor / ChatFormatting)` | Chat colour (overrides `color`'s)                                                       | no       |
| `maxLevel(n)`                         | Highest level (default `1`)                                                               | no       |
| `lord(max)` / `lord(max, titleProvider)` | Enables lord levels (+ titles)                                                         | no       |
| `enableLord()`                        | Lord extension without setting a max level                                                | no       |
| `taskMaster(entityTypeHolder)`        | Enables tasks and sets the task master entity                                             | no       |
| `enableTasks()`                       | Tasks without a task master                                                               | no       |
| `refinements(RefinementItems)`        | Enables refinements and sets the accessory items                                          | no       |
| `enableRefinements()`                 | Refinements without items                                                                 | no       |
| `totem(fragile, crafted)`             | Village totem top blocks                                                                  | no       |
| `banner(VillageBanner)`               | Ominous banner look                                                                       | no       |
| `badOmen(Holder<MobEffect>)`          | Effect that can trigger a raid by this faction                                            | no       |
| `villageGuards(TagKey<EntityType>)`   | Entity types that guard the faction's villages                                            | no       |
| `extension(Class, attachment)` / `extension(Class, component, default)` | Your own extension, see below            | no       |
| `component(type, value)`              | Set any data component directly                                                           | no       |
| `withValidator(consumer)`             | Check the final component map; throw to reject a faction                                  | no       |

## Lord levels

Lord levels are a second progression track on top of the normal levels, with titles, lord skill trees and
minions.

```java
.maxLevel(10)
.lord(5)           // 5 lord levels, titles from translation keys
```

Provide the titles. With `lord(5)` FactionApi reads (replace `mymod` / `warden` with your faction id):

```json
{
  "lord_title.mymod.warden.male.1": "Squire",
  "lord_title.mymod.warden.male.1.short": "Sqr",
  "lord_title.mymod.warden.female.1": "Squire",
  "lord_title.mymod.warden.female.1.short": "Sqr"
}
```

for every level `1..5`, in a `male` and a `female` variant, each with a `.short` form. The default titles use
`male` for every gender except `female`. For your own scheme implement `ILordTitleProvider` and use
`lord(5, provider)`.

How a player becomes a lord is up to your game design. FactionApi provides:

* `handler.setFaction(FactionUpdate.maxLevel(WARDEN).lordLevel(1))` – a lord level exists only at the faction's maximum level; any level update below it resets the lord level to `0`.
* the task reward `lord_level` ([Tasks](../../data/tasks)) and the `awards_lord_level` task tag,
* `/factions faction set mymod:warden lord 2` for testing.

Add your faction to the tag `factionapi:has_lord_skills` to get the generic lord skills (better minions,
resource collection, lord speed). A lord skill tree is a normal [skill tree](../../data/skilltrees) that is added to
`factionapi:type/lord` instead of `type/level` and unlocks with `"lord_level": 1` in its `unlock_predicate`.

More: [Lords](../lords).

## Tasks

Tasks are quests handed out by a **task master**. The tasks themselves are
[data pack JSON](../../data/tasks); the task master is an entity you provide.

```java
.taskMaster(MyEntities.WARDEN_TASK_MASTER)    // Holder<EntityType<?>>; also enables the task extension
```

* FactionApi does **not** ship a task master entity. Create one (implementing the marker interface
  `ITaskMasterEntity`, which extends `IEntityWithHome`) and let players interact with it. The board itself is
  FactionApi's `TaskBoardMenu`. Vampirism's `IDefaultTaskMasterEntity#processInteraction` is the reference:
  it checks `taskManager.hasAvailableTasks(entity.getUUID())`, opens a `TaskBoardMenu` with
  `player.openMenu(...)` and calls `taskManager.openTaskBoardScreen(entity.getUUID())`.
* **Assign tasks to your faction.** A task that is in *no* task tag is offered to **every** faction. To restrict
  a task to the Wardens, put it in the shared `factionapi:has_faction` task tag **and** in a task tag of your
  faction that you register with `AddFactionTagEvent`:

  ```java
  modBus.addListener(AddFactionTagEvent.class, event ->
          event.faction(MyFactions.WARDEN).add(FactionRegistries.Keys.TASK, MyTags.WARDEN_TASKS));
  ```

  Details and the other task tags: [Tasks › task assignment](../../data/tasks#task-assignment).
* New requirement / reward / unlocker *types* are registered in code, see
  [Registering content](../registering-content#add-task-requirement--reward--unlocker-types).

## Refinements

Refinements are random modifier sets on three accessory items (amulet, ring, belt).

```java
.refinements(new RefinementItems(MyItems.WARDEN_AMULET, MyItems.WARDEN_RING, MyItems.WARDEN_BELT))
```

You provide the three items (implementing `IRefinementItem`) and the `IRefinement` / `IRefinementSet`
registry entries. See [Refinements](../refinements).

## Villages

Factions can capture and raid villages through a totem block.

```java
.totem(MyBlocks.WARDEN_TOTEM_FRAGILE, MyBlocks.WARDEN_TOTEM_CRAFTED)
.banner(MyVillageBanner.create())
.badOmen(MyEffects.WARDEN_BAD_OMEN)
.villageGuards(MyTags.WARDEN_GUARDS)
```

Add the faction to the tags `factionapi:can_raid` (may start a capture or raid) and optionally
`factionapi:has_random_raid`. `TotemPair.DEFAULT` refers to FactionApi's own totem top blocks. Details: [Villages](../villages).

## Minions

Lords can own minions. Register an `IMinionEntry` that connects your faction, the minion entity type, the
data and the stats, and register the entity yourself. See [Minions](../minions) for the registration example.

## Faction mobs and targeting

Players are put into factions by joining them. **Mobs** belong to a faction in one of two ways:

1. Implement `IFactionMob` (extends `IFactionEntity`) on the entity class:

   ```java
   public class WardenGuard extends PathfinderMob implements IFactionMob {
       @Override
       public Holder<? extends IFaction<?>> getFaction() {
           return MyFactions.WARDEN;
       }

       @Override
       public LivingEntity asEntity() {
           return this;
       }
   }
   ```

2. Or – for entities you do not control, including other mods' mobs – put the entity type into the
   faction's entity tag. Register the tag, then fill it with a normal `data/<ns>/tags/entity_type/….json`:

   ```java
   modBus.addListener(AddFactionTagEvent.class, event ->
           event.faction(MyFactions.WARDEN).add(Registries.ENTITY_TYPE, MyTags.WARDEN_MOBS));
   ```

Entities without either belong to `factionapi:neutral`. Resolve any entity's faction with
`IFactionHelper.get().getFaction(entity)` or test with `isEntityOfFaction(entity, faction)`.

To make mobs fight other factions use `IFactionPredicate` as the target selector of the vanilla goals.
Combined with the faction's *default targets* tag this gives a faction "always attacks these mobs" rules; see
[Target selection](../utilities#target-selection-ifactionpredicate) and
[Default targets](../utilities#default-targets).

## Your own extension

A faction can carry extra per-player state that only exists while the player is a member. Declare it with
`extension(Interface.class, ATTACHMENT)`:

```java
.extension(IWardenOath.class, MyAttachments.WARDEN_OATH)
// ...
FactionsApi.factionPlayerHandler(player).getExtension(IWardenOath.class);   // Optional; empty if the player's faction has none
```

* The attachment is registered like the player attachment in the [tutorial](./first-faction#3-register-the-per-player-state-as-an-attachment).
* When the player leaves the faction, FactionApi calls `onLeaveFaction(player)` if the value implements
  `IFactionExtension`; otherwise the attachment data is simply removed. `IFactionExtension#setLevel` is called
  on every faction update for members.
* The base class `FactionExtension` (module `FactionApi`) provides the player, the faction and the saving /
  syncing of properties. Add the attachment to `PlayerEventHandlerEvent` as well if it needs tick / death
  callbacks.
* A second overload, `extension(Class, DataComponentType, defaultValue)`, stores the value in the player's
  faction handler instead of a separate attachment.

Vampirism uses this for the Dracula and Marshall roles (`IDraculaPlayer`, `IMarshallPlayer`).
