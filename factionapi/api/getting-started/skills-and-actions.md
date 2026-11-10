---
sidebar_position: 3
title: Skills and actions
---

This page extends the [Warden faction](./first-faction) with a **skill tree** and an **action** (an ability the
player triggers with a key). It uses only API that Vampirism's hunters use as well.

## How the pieces relate

```text
Skill tree (JSON)  ──contains──►  Skill segment (JSON)  ──holds──►  Skill (Java registry entry)
   "tab in the skill screen"        "a node / a pick-one choice"        │
                                                                        └─ unlocks ► Action (Java registry entry)
```

* A **skill** is registered in code. It carries the effect (attribute modifiers, enable / disable callbacks) and
  may unlock one or more **actions**.
* **Skill trees** and **skill segments** are data pack JSON. A segment places one skill (or a choice between
  several) into a tree and says which segments must be unlocked first.
* An **action** is an ability with a cooldown (and optionally a duration). It is *unlocked by a skill*; a player
  must have unlocked the skill before the action can be used.
* Skill points come from the player's level (see below). A skill's `cost` is paid from them.

## 1. Make the player class a skill player

Skills and actions need two bookkeeping objects, `SkillHandler` and `ActionHandler`. They belong to a player
type that implements `ISkillPlayer`. Update the interface from the tutorial:

```java
public interface IWardenPlayer extends IFactionPlayer<IWardenPlayer>, ISkillPlayer<IWardenPlayer> {

    @Override
    default Holder<? extends IPlayableFaction<IWardenPlayer>> getFaction() {
        return MyFactions.WARDEN;
    }
}
```

and the class. New or changed parts compared to the tutorial class are marked:

```java
public class WardenPlayer extends FactionBasePlayer<IWardenPlayer> implements IWardenPlayer {

    // NEW: the two handlers
    private final ActionHandler<IWardenPlayer> actionHandler = new ActionHandler<>(this);
    private final SkillHandler<IWardenPlayer> skillHandler = new SkillHandler<>(this);

    // ... get(), constructor, getType(), canLeaveFaction(), getDisguise(), isDisguised(), level displays,
    //     AttachmentOptions: unchanged ...

    // NEW
    @Override
    public ActionHandler<IWardenPlayer> getActionHandler() { return this.actionHandler; }

    // NEW
    @Override
    public SkillHandler<IWardenPlayer> getSkillHandler() { return this.skillHandler; }

    // NEW: persist and sync both handlers together with the player
    @Override
    protected void registerProperties() {
        super.registerProperties();
        this.registerProperty(FIdentifier.loc(MyMod.MODID, "action_handler")).subProperty(() -> this.actionHandler).register();
        this.registerProperty(FIdentifier.loc(MyMod.MODID, "skill_handler")).subProperty(() -> this.skillHandler).register();
    }

    // CHANGED: keep actions running / cooling down
    @Override
    public void onUpdate() {
        if (getLevel() > 0) {
            this.actionHandler.updateActions();
        }
    }

    // NEW
    @Override
    public void onDeath(DamageSource source) {
        this.actionHandler.deactivateAllActions();
    }

    // NEW
    @Override
    public void onJoinWorld() {
        if (getLevel() > 0) {
            this.actionHandler.onActionsReactivated();
        }
    }

    // CHANGED: reset skills and timers when leaving
    @Override
    public void leaveFaction() {
        this.actionHandler.resetTimers();
        this.skillHandler.reset();
        this.sync();
    }
}
```

This is the same bookkeeping Vampirism's `CommonFactionPlayer` does for vampires and hunters. That class lives in
the Vampirism mod, not in FactionApi, so you write (or copy) the few lines yourself. Vampirism's version also
re-applies level attributes on respawn and **auto-enables a skill when exactly one skill is unlockable** after a
level-up (`SkillHandler#unlockableSkills`); add that if you want it.

## 2. Tag key for your faction

Skills and actions state which factions may use them with a faction **tag**. You created
`mymod:is_warden` in the tutorial; add a key for it in code:

```java
public class MyTags {
    public static final TagKey<IFaction<?>> IS_WARDEN =
            TagKey.create(FactionRegistries.Keys.FACTION, Identifier.fromNamespaceAndPath(MyMod.MODID, "is_warden"));
}
```

## 3. An action

Extend `DefaultAction`. It handles the "is this the right faction" check for you.

```java
public class DashAction extends DefaultAction<IWardenPlayer> {

    @Override
    public TagKey<IFaction<?>> factions() {
        return MyTags.IS_WARDEN;                       // who may use it
    }

    @Override
    public boolean isEnabled() {
        return true;                                   // wire this to a config option if you like
    }

    @Override
    public int getCooldown(IWardenPlayer player) {
        return 20 * 10;                                // ticks until it can be used again
    }

    @Override
    protected IActionResult activateServer(IWardenPlayer player, ActivationContext context) {
        player.asEntity().addEffect(new MobEffectInstance(MobEffects.SPEED, 20 * 5));
        return IActionResult.SUCCESS;                  // or IActionResult.fail(Component) to refuse
    }
}
```

An action that lasts for a while (a toggle with a duration, per-tick logic) additionally implements
`ILastingAction`: `getDuration`, `onActivatedClient`, `onDeactivated`, `onReActivatedServer` and optionally
`onUpdate`. Vampirism's `SixthSenseAction` is a compact example.

```java
public class WardenActions {
    public static final DeferredActionRegister<IWardenPlayer> ACTIONS = DeferredActionRegister.create(MyMod.MODID);

    public static final DeferredAction<IWardenPlayer, IAction<IWardenPlayer>, DashAction> DASH =
            ACTIONS.registerAction("dash", DashAction::new);
}
```

## 4. A skill

Define one skill base class that fixes the faction and the tree, so you cannot forget either:

```java
public class WardenSkill extends Skill<IWardenPlayer> {
    public WardenSkill(SkillProperties<IWardenPlayer> properties) {
        super(properties
                .factions(MyTags.IS_WARDEN)        // REQUIRED, otherwise loading fails
                .tree(MyTrees.LEVEL));             // this skill may only be used in your level tree
    }
}
```

```java
public class MyTrees {
    public static final ResourceKey<ISkillTree> LEVEL =
            ResourceKey.create(FactionRegistries.Keys.SKILL_TREE, Identifier.fromNamespaceAndPath(MyMod.MODID, "warden/level"));
}
```

```java
public class WardenSkills {
    public static final DeferredSkillRegister SKILLS = DeferredSkillRegister.create(MyMod.MODID);

    /** Every tree needs a root. Roots are enabled automatically for free once the tree is unlocked. */
    public static final DeferredSkill<IWardenPlayer, ISkill<IWardenPlayer>> WARDEN_ROOT =
            SKILLS.registerSkill("warden_root", WardenSkill::new);

    /** A skill that unlocks the dash action. Named, described and iconified after the action. */
    public static final DeferredSkill<IWardenPlayer, ISkill<IWardenPlayer>> DASH =
            SKILLS.registerSkill("dash", props -> new WardenSkill(props.cost(1).actionSkill(WardenActions.DASH).withDescription()));

    /** A passive skill: attribute modifier while enabled. */
    public static final DeferredSkill<IWardenPlayer, ISkill<IWardenPlayer>> TOUGH =
            SKILLS.registerSkill("tough", props -> new WardenSkill(props.cost(2).withDescription()
                    .attribute(Attributes.MAX_HEALTH, 4.0, AttributeModifier.Operation.ADD_VALUE)));
}
```

`SkillProperties` quick reference (all methods are on `props`):

| Call                         | Effect                                                                                      |
|------------------------------|---------------------------------------------------------------------------------------------|
| `cost(n)`                    | Skill points to unlock; defaults to `0` (use that for roots). Must not be negative.          |
| `factions(tag)`              | Which factions may use the skill. **Required.**                                              |
| `tree(key or tag)`           | Restrict to a tree or a tree tag. Default is the tag `factionapi:default`.                  |
| `actionSkill(action)`        | Skill *represents* an action: it unlocks it and takes its name and icon from it. Without `withDescription()` the description is the generic "Unlocks action". |
| `unlocks(action)`            | Skill additionally unlocks this action (name and icon stay the skill's own).                 |
| `attribute(attr, amount, op)`| Attribute modifier applied while enabled and removed when disabled.                         |
| `onEnable(consumer)` / `onDisable(consumer)` | Run a registered `FactionPlayerConsumer` when enabled / disabled.            |
| `withDescription()`          | Description from the translation key `<name key>.desc`.                                     |
| `separateSkillName()`        | For an action skill: use `skill.<ns>.<id>` as name instead of the action's key.              |

For custom logic when a skill is enabled, subclass `Skill` and override `onEnabled(player)` / `onDisabled(player)`
(always call `super`).

## 5. Register everything

In `MyMod`'s constructor:

```java
WardenActions.ACTIONS.register(modBus);
WardenSkills.SKILLS.register(modBus);
```

## 6. The skill tree and segments (JSON)

`data/mymod/factionapi/skill_tree/warden/level.json` – id `mymod:warden/level`, the key you used in `MyTrees`:

```json
{
  "faction": "mymod:warden",
  "unlock_predicate": {
    "type_specific": { "type": "factionapi:player_faction", "faction": "mymod:warden" }
  },
  "display": { "id": "minecraft:shield" },
  "name": { "translate": "gui.mymod.skills.level" }
}
```

The tree is unlocked for every player in `mymod:warden`. Required fields are `faction`, `unlock_predicate`,
`display` (icon item) and `name`; see [Skill trees](../../data/skilltrees) for all options and for lord trees.

The segments, one file each:

`data/mymod/factionapi/skill_segment/warden/root.json` – no `parents` makes it the root:

```json
{ "tree": "mymod:warden/level", "skills": [ "mymod:warden_root" ] }
```

`data/mymod/factionapi/skill_segment/warden/dash_or_tough.json` – `skills` is a **pick-one** choice:

```json
{
  "tree": "mymod:warden/level",
  "parents": [ "mymod:warden/root" ],
  "skills": [ "mymod:dash", "mymod:tough" ]
}
```

Put one skill per segment if the player should be able to take all of them.

### Give the tree skill points

Skill points are only handed out for trees in the tag `factionapi:default`, which consists of the tags
`factionapi:type/level` (normal levels) and `factionapi:type/lord` (lord levels). Add your tree to the first one:

`data/factionapi/tags/factionapi/skill_tree/type/level.json`:

```json
{ "values": [ "mymod:warden/level" ] }
```

Without this your skills would cost points that never arrive. A player gets
`(level − 1) × factionSkillPointsPerLevel` points (server option, default `2`).

## 7. Translations and icons

```json
{
  "gui.mymod.skills.level": "Warden Skills",
  "skill.mymod.warden_root": "Becoming a Warden",
  "skill.mymod.tough": "Tough",
  "skill.mymod.tough.desc": "Increases your maximum health by 2 hearts.",
  "action.mymod.dash": "Dash",
  "action.mymod.dash.desc": "Move faster for a few seconds."
}
```

Keys: `skill.<ns>.<skill id>` (+ `.desc`), `action.<ns>.<action id>` (+ `.desc`). A skill made with `actionSkill(...)`
uses the **action** key (`action.…`) for its name and description.

Icons are 16×16 PNGs, drawn in the skill screen from:

* `assets/mymod/textures/skills/<skill id>.png` for normal skills (`warden_root.png`, `tough.png`),
* `assets/mymod/textures/actions/<action id>.png` for action skills (`dash.png`).

A missing file shows the purple-black missing texture.

## 8. Try it

```text
/factions faction set mymod:warden level 4        # (4 - 1) x 2 = 6 skill points with default config
/factions skills enable mymod:dash                # permission level 3; add "force" to ignore points/parents
/factions actions bind ACTION_1 mymod:dash        # bind the action to the first action key
```

Open the skill screen with the *Open Skill Screen* key (controls menu, category *FactionApi*), unlock skills
there, and use the action with the bound *Action 1* key or the *Open Action Radial Menu* key.

If the skill screen shows "You cannot learn skills", none of your trees is unlocked for the player – check
the tree's `unlock_predicate`. See [Troubleshooting](./troubleshooting) for more.

## Further reading

* [Skill trees (data)](../../data/skilltrees) – every field of trees and segments, lord trees, locking segments.
* [Registering content](../registering-content) and [Object model](../object-model).
* [Events](../events) – `SkillEvents` and `ActionEvent` to react to or veto skills and actions.
