---
sidebar_position: 2
title: Your first faction
---

In this tutorial you build a minimal playable faction, the **Wardens** (`mymod:warden`), and join it in game.
You end with a faction that shows up in `/factions`, has levels, can be joined from code, and survives
death, relog and restarts. [Skills and actions](./skills-and-actions) and the
[optional features](./optional-features) build on this result.

:::info What you need
* A NeoForge mod project that already builds. FactionApi targets the Minecraft / NeoForge version of the
  [Vampirism `dev` branch](https://github.com/TeamLapen/Vampirism) (currently Minecraft 26.1.2, Java 25).
* FactionApi as a dependency – see [Depending on FactionApi](../../wiki/gradle-setup). Use the **full**
  `de.teamlapen.faction:FactionApi` artifact: this tutorial uses implementation classes such as
  `PlayableFaction` and `FactionBasePlayer` (see [Concepts](./concepts#the-two-modules)).
* The `[[dependencies]]` entry for `factionapi` in your `neoforge.mods.toml`.
:::

The code below uses the mod id `mymod` and the package `com.example.mymod`. Replace both. It is modelled on
how Vampirism registers its hunters (`ModFactions`, `HunterPlayer`, `ModAttachments`).

## What you will create

| #  | File                                         | Purpose                                              |
|----|----------------------------------------------|------------------------------------------------------|
| 1  | `IWardenPlayer.java`                         | Interface of your per-player state                   |
| 2  | `WardenPlayer.java`                          | Implementation of the per-player state               |
| 3  | `MyAttachments.java`                         | Registers the per-player state on every player       |
| 4  | `MyFactions.java`                            | Registers the faction                                |
| 5  | `MyMod.java`                                 | Wires registries and the player event hook           |
| 6  | `assets/mymod/lang/en_us.json`               | Faction names                                        |
| 7  | `data/…/tags/factionapi/faction/*.json`      | Puts the faction into FactionApi's faction tags      |

## 1. The player interface

Every playable faction has a *player type*: an object that exists once per player and holds your
faction-specific state. Start with its interface. Put it in your API package if you want other mods to use it.

```java
public interface IWardenPlayer extends IFactionPlayer<IWardenPlayer> {

    /** Tells FactionApi which faction this player type belongs to. */
    @Override
    default Holder<? extends IPlayableFaction<IWardenPlayer>> getFaction() {
        return MyFactions.WARDEN;
    }
}
```

`IFactionPlayer<T>` is parameterised with itself (`IWardenPlayer`). That type is what ties the faction, the
player object and the generics of skills and actions together.

## 2. The player class

Extend `FactionBasePlayer`. It already implements the level lookup, the owning `Player` and the saving /
syncing machinery; you implement the faction-specific hooks. Hooks you do not need can stay empty.

```java
public class WardenPlayer extends FactionBasePlayer<IWardenPlayer> implements IWardenPlayer {

    /** Convenience accessor. This exists for EVERY player, also for non-Wardens. */
    public static WardenPlayer get(Player player) {
        return player.getData(MyAttachments.WARDEN_PLAYER);
    }

    private final IDisguise disguise = new IDisguise.None(MyFactions.WARDEN);

    public WardenPlayer(Player player) {
        super(player);
    }

    @Override
    public AttachmentType<?> getType() {
        return MyAttachments.WARDEN_PLAYER.get();
    }

    @Override
    public boolean canLeaveFaction() {
        return true;                       // false = nothing can take this player out of the faction
    }

    @Override
    public IDisguise getDisguise() {
        return this.disguise;              // None = never disguised as another faction
    }

    @Override
    public boolean isDisguised() {
        return false;
    }

    @Override
    public void levelChanged(FactionUpdate changes) {
        // Called after the player joined or the level changed. changes.getLevel() is the new level.
        // Typical use: apply level based attribute modifiers.
    }

    @Override
    public void leaveFaction() {
        // Called when the player leaves the faction. Remove everything you applied to the player.
    }

    @Override
    public void onUpdate() {
        // Called every tick for EVERY player, whether or not they are a Warden.
        if (getLevel() > 0) {
            // Warden-only behaviour
        }
    }

    // How the level is shown in the GUI and in chat. null = show nothing.
    @Override
    public @Nullable Component getShortLevelDisplay() {
        return Component.literal(String.valueOf(getLevel()));
    }

    @Override
    public @Nullable Component getLevelDisplay() {
        return Component.translatable("gui.factionapi.level").append(" " + getLevel());
    }

    @Override
    public @Nullable Component getChatDisplay() {
        return null;
    }

    /** Creates the instance for a player. Referenced by the attachment type in step 3. */
    public static class AttachmentOptions extends AttachmentSynchronization.PlayerOptions<WardenPlayer> {
        @Override
        protected WardenPlayer create(Player player) {
            return new WardenPlayer(player);
        }
    }
}
```

Good to know:

* `getLevel()` is the player's level **in this faction** and is `0` while the player is in another faction.
  Always check it before doing faction-only things – the event hooks (`onUpdate`, `onDeath`, `onRespawn`,
  `onJoinWorld`, `onEntityKilled`, …, see `IPlayerEventListener`) are called for every player.
* The hooks run on client **and** server. Use `isRemote()` where it matters.
* To store your own data, override `registerProperties()` (call `super` first) and register properties; they
  are saved with the player and synced to the client:

  ```java
  private int shield;

  @Override
  protected void registerProperties() {
      super.registerProperties();
      this.registerProperty(FIdentifier.loc("mymod", "shield"))
              .simple(0, () -> this.shield, value -> this.shield = value);
  }
  ```

  Change the field and call `sync()` to push the change to the client. `simple(...)` has overloads for `int`,
  `double`, `float`, `boolean`, `String`, enums and `Identifier`; for anything else use
  `simple(Codec)` / `nullable(Codec)` / `subProperty(...)`.

## 3. Register the per-player state as an attachment

The player class is stored as a NeoForge **data attachment**. Register it with the helpers FactionApi
provides. `copyOnDeath()` is important: without it the data would be lost when the player respawns.

```java
public class MyAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MyMod.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<WardenPlayer>> WARDEN_PLAYER =
            ATTACHMENT_TYPES.register("warden_player", () -> {
                var options = new WardenPlayer.AttachmentOptions();
                return AttachmentType.builder(options)   // creates the object for each player
                        .serialize(options)              // saves / loads it with the player
                        .sync(options)                   // syncs it to the client
                        .copyOnDeath()                   // keeps it when the player respawns
                        .build();
            });
}
```

## 4. Register the faction

`DeferredFactionRegister` works like a normal `DeferredRegister`, but hands you a `FactionProperties` builder.
Set at least the player attachment; everything else has defaults.

```java
public class MyFactions {
    private static final DeferredFactionRegister FACTIONS = DeferredFactionRegister.create(MyMod.MODID);

    public static final DeferredFaction<IWardenPlayer, IPlayableFaction<IWardenPlayer>> WARDEN =
            FACTIONS.registerFaction("warden", props -> new PlayableFaction<>(props
                    .playerAttachment(MyAttachments.WARDEN_PLAYER)   // REQUIRED: the player type from step 3
                    .color(0x2E8B57)                                 // GUI colour; also the default chat colour
                    .chatColor(ChatFormatting.DARK_GREEN)            // optional: override the chat colour
                    .maxLevel(10)));                                 // highest level, default is 1

    public static void register(IEventBus modBus) {
        FACTIONS.register(modBus);
    }
}
```

* `playerAttachment` is the only mandatory call – a `PlayableFaction` without it throws
  `A playable faction needs a player capability` while loading.
* The registry id is `mymod:warden` (namespace of the register + name). It is the id you use in commands,
  tags and JSON.
* The returned `DeferredFaction` is a `Holder`. Pass it wherever the API wants a faction `Holder`
  (`handler.joinFaction(MyFactions.WARDEN)`, `IFaction.is(a, b)`, …).
* Every other `props.xxx(...)` call is optional; the [cheat sheet](./optional-features#factionproperties-cheat-sheet)
  lists them all.

## 5. Wire it up in your mod class

```java
@Mod(MyMod.MODID)
public class MyMod {
    public static final String MODID = "mymod";

    public MyMod(IEventBus modBus) {
        MyAttachments.ATTACHMENT_TYPES.register(modBus);
        MyFactions.register(modBus);

        // Without this, FactionApi never calls WardenPlayer#onUpdate, onDeath, onRespawn, ...
        modBus.addListener(PlayerEventHandlerEvent.class,
                event -> event.addAttachmentListener(MyAttachments.WARDEN_PLAYER));
    }
}
```

`PlayerEventHandlerEvent` is a **mod bus** event fired once when loading has finished. Register every
player attachment that implements `IPlayerEventListener` there. If you forget it the game still runs, but
your player class never receives tick / death / respawn / login callbacks.

## 6. Translations

Add to `assets/mymod/lang/en_us.json`:

```json
{
  "faction.mymod.warden": "Warden",
  "faction.mymod.warden.singular": "Warden",
  "faction.mymod.warden.plural": "Wardens"
}
```

The three keys are derived from the faction id: `faction.<namespace>.<name>` plus `.singular` and `.plural`.
`IFaction#getName()` uses the plain key, `getNameSingular()` / `getNamePlural()` the other two (for example the
success message of `/factions faction set` prints the singular). Without translations the raw keys are displayed.

## 7. Add the faction to FactionApi's faction tags

Several FactionApi features look at **faction tags** (data pack tags in the `factionapi:faction` registry)
instead of the faction object. FactionApi ships them empty and expects each faction mod to fill them. For a
minimal faction you need:

`data/mymod/tags/factionapi/faction/is_warden.json` – a tag that contains just your faction (handy later for
skills, actions and recipes):

```json
{ "values": [ "mymod:warden" ] }
```

`data/factionapi/tags/factionapi/faction/all.json` – **register your faction as an existing faction**:

```json
{ "values": [ "#mymod:is_warden" ] }
```

`data/factionapi/tags/factionapi/faction/friendly_towards_neutral.json` – tell FactionApi whether your faction
harms neutral creatures (everything without a faction: players who joined none, ordinary mobs). Every faction in
`factionapi:all` should be in either `friendly_towards_neutral` or `hostile_towards_neutral`:

```json
{ "values": [ "#mymod:is_warden" ] }
```

Tag files merge across mods, so these files add to what other mods put there. The tags `factionapi:all` and
its derived `factionapi:not_neutral` are the default target pool of target selectors
([`IFactionPredicate`](../utilities#target-selection-ifactionpredicate)) and the default faction list of
skills and minion tasks, so a faction that is not in `all` is invisible to them. The other tags
(`can_raid`, `has_random_raid`, `has_lord_skills`) are only needed for the matching optional features, see
[Faction tags](../utilities#faction-tags-factiontags).

## 8. Try it

Start the game (the `runClient` Gradle task), create a creative world with cheats and run:

```text
/factions faction set mymod:warden level 3
/factions faction get @s
```

The first command puts you into the faction at level 3, the second prints faction and level (permission level 2,
see [Commands](../../wiki/commands-and-config#commands)). The command tree is built from the faction registry when
the server starts, so your faction is offered in tab completion automatically. There is no command that returns
a player to neutral; use `handler.leaveFaction(false)` from code or set another faction.

Check that:

* the commands print "Warden", not `faction.mymod.warden`,
* `/factions faction get @s` still reports a level 3 Warden after you relog and after you die.

## Joining from your own code

Server side:

```java
IFactionPlayerHandler handler = FactionsApi.factionPlayerHandler(player);

// Join at level 1 – only succeeds if the player is currently in no faction
// (decided by PlayerFactionEvent.CanJoinFaction, the default is "only from neutral").
handler.joinFaction(MyFactions.WARDEN);

// Set faction and level explicitly. Returns false if the change was vetoed.
boolean ok = handler.setFaction(FactionUpdate.builder().faction(MyFactions.WARDEN).level(5));

// Level up an existing member.
handler.setFaction(FactionUpdate.builder().level(handler.getCurrentLevel() + 1));
```

Reading state, client or server:

```java
FactionsApi.factionPlayerHandler(player).isInFaction(MyFactions.WARDEN);   // is the player a Warden?
WardenPlayer.get(player).getLevel();                                       // 0 if not a Warden
```

Allow or block joining with the `CanJoinFaction` event (game bus):

```java
NeoForge.EVENT_BUS.addListener((PlayerFactionEvent.CanJoinFaction event) -> {
    if (IFaction.is(event.getFactionToJoin(), MyFactions.WARDEN)) {
        event.setBehavior(PlayerFactionEvent.CanJoinFaction.Behavior.ALLOW);   // also from another faction
    }
});
```

Vampirism joins players through game content (altars, the garlic injection, a trainer menu) that calls
exactly these methods. How the player obtains the faction is entirely your design.

## Next steps

* [Skills and actions](./skills-and-actions) – give the Wardens a skill tree and an ability.
* [Optional features](./optional-features) – lords, tasks, refinements, villages, minions, faction mobs.
* [Troubleshooting](./troubleshooting) – if something in this tutorial did not work.
