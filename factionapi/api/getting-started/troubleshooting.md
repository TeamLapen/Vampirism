---
sidebar_position: 5
title: Troubleshooting
---

## Checklist for a new faction

Work through this list before debugging deeper. Each item links to where it is explained.

- [ ] Gradle depends on the **full** `FactionApi` artifact and `neoforge.mods.toml` lists `factionapi` as a
      dependency ([setup](../../wiki/gradle-setup)).
- [ ] `MyFactions.register(modBus)` and `MyAttachments.ATTACHMENT_TYPES.register(modBus)` are called
      (and `ACTIONS` / `SKILLS` if used) ([tutorial step 5](./first-faction#5-wire-it-up-in-your-mod-class)).
- [ ] The faction has `playerAttachment(...)`.
- [ ] The attachment uses `.serialize(options).sync(options).copyOnDeath()`.
- [ ] The attachment is added in a `PlayerEventHandlerEvent` listener on the **mod** bus.
- [ ] Translations exist: `faction.<ns>.<id>`, `.singular`, `.plural` ([step 6](./first-faction#6-translations)).
- [ ] The faction is in `factionapi:all` and in one of `friendly_towards_neutral` / `hostile_towards_neutral`
      ([step 7](./first-faction#7-add-the-faction-to-factionapis-faction-tags)).
- [ ] Skills: `factions(tag)` is set, the faction tag file exists, the tree and a root segment exist, and the tree is in
      `factionapi:type/level` ([Skills and actions](./skills-and-actions#give-the-tree-skill-points)).

## Crashes and log errors

| Message / symptom                                                | Cause and fix |
|------------------------------------------------------------------|---------------|
| `A playable faction needs a player capability`                   | `registerFaction` without `props.playerAttachment(...)`. |
| `Skill factions are not set for: <skill id>`                     | The skill's `SkillProperties` has no `factions(tag)`. Set it, e.g. in a shared base class. |
| `Skill cost must be positive for: <skill id>`                    | `cost(n)` was called with a negative number. |
| `skill is not allowed for player for skill <id>` (`IllegalArgumentException`) | A skill was enabled for a player whose faction is not in the skill's `factions` tag. Check the tag file, and that the root skill uses the right tag too. |
| `Faction player is not allowed to use action`                    | An action was activated by a player whose faction is not in `factions()`. |
| Errors mentioning your skill tree / segment while loading a world | Invalid JSON in `data/<ns>/factionapi/skill_tree` or `skill_segment`, or a segment points to an unknown tree, skill or parent. Check `latest.log`. A segment whose declared parents cannot be resolved is removed together with everything below it, and an error is logged. |

## Faction looks wrong or is missing

| Symptom | Cause and fix |
|---------|---------------|
| Raw keys like `faction.mymod.warden.singular` in chat or messages | The three translation keys are missing or in the wrong namespace ([step 6](./first-faction#6-translations)). |
| Faction is not offered by `/factions faction set` | It is not registered: `FACTIONS.register(modBus)` is missing, or the mod id / registry name differs from what you type. The command tree is built when the server starts, so restart after adding the faction. |
| Lord title is a raw key | Missing `lord_title.<ns>.<id>.male.<n>` / `.female.<n>` and `.short` keys for a level ([Lord levels](./optional-features#lord-levels)). |
| Faction colour in the GUI or in chat is white | No `color(...)` / `chatColor(...)` set. White is the default. |

## Player data and callbacks

| Symptom | Cause and fix |
|---------|---------------|
| `onUpdate`, `onDeath`, `onRespawn`, `onJoinWorld`, … on your player class are never called | The attachment is not registered in a `PlayerEventHandlerEvent` listener (mod bus). |
| Your hook also runs for players who are not in the faction | By design: hooks are called for every player. Guard with `getLevel() > 0` (or `IFaction.is(...)` on the handler's faction). |
| Faction level survives death but your own data or skills are reset | The attachment is missing `.copyOnDeath()`. |
| Your data is not on the client | Register the value with `registerProperties()` / `registerProperty(...)` (properties are synced), and call `sync()` after changing it. The attachment needs `.sync(options)`. |
| `handler.joinFaction(...)` does nothing | `PlayerFactionEvent.CanJoinFaction` decides. By default only players in no faction can join. Allow it in a listener with `Behavior.ALLOW`, or use `setFaction`, which does not run that check. |
| `handler.setFaction(...)` returns `false` | The old faction's player returned `false` from `canLeaveFaction()`, or a `FactionLevelChangePre` listener cancelled the change. |
| Lord level is `0` after setting it | A lord level only exists at the faction's maximum level; a lower level resets it. Use `FactionUpdate.maxLevel(faction).lordLevel(n)`. |
| An event listener is never called | Mod-bus events: `PlayerEventHandlerEvent`, `AddFactionTagEvent`. Game-bus events (`NeoForge.EVENT_BUS`): `PlayerFactionEvent`, `FactionVillageEvent`, `ActionEvent`, `SkillEvents`. Check you used the right bus ([Events](../events)). |

## Skills and actions

| Symptom | Cause and fix |
|---------|---------------|
| Skill screen says "You cannot learn skills" | No skill tree is unlocked for the player. Check the tree's `unlock_predicate` (faction, level, lord level). The unlock is re-evaluated whenever level or lord level changes. |
| Tree is visible but you never have skill points | The tree is not in `factionapi:type/level` (or `type/lord`), so the built-in providers give it `0` points. Add it to the tag. |
| A skill cannot be unlocked | Its segment has parents that are not unlocked yet, a locking segment is unlocked, or you lack points. `/factions skills enable <skill> force` ignores all three for testing. |
| Skill icon is purple-black | The 16×16 texture is missing: `assets/<ns>/textures/skills/<skill id>.png`, or `textures/actions/<action id>.png` for skills made with `actionSkill`. |
| Action cannot be activated | The result message tells you: not unlocked (the skill is not enabled), wrong faction, on cooldown, `isEnabled()` is `false`, or blocked by an effect. FactionApi gives faction players *Resurrection Fatigue* for 15 seconds after respawning, which is in the tag `factionapi:disables_actions` and blocks actions. If the server enables `usePermissions`, the node `factionapi.action` can deny it as well ([Commands, permissions & config](../../wiki/commands-and-config#permissions)). |

## Targeting and entities

| Symptom | Cause and fix |
|---------|---------------|
| Your mobs are treated as neutral | The entity neither implements `IFactionEntity` / `IFactionMob` nor is in the faction's entity-type tag registered through `AddFactionTagEvent` ([Faction mobs](./optional-features#faction-mobs-and-targeting)). |
| A target selector (`IFactionPredicate`) ignores your faction | The faction is not in `factionapi:all` / `factionapi:not_neutral`. |
| Your faction attacks (or spares) neutral mobs unexpectedly | Check `hostile_towards_neutral` / `friendly_towards_neutral`. A predicate built for a faction outside `hostile_towards_neutral` removes neutral from its target set. |
| A task shows up for every faction | A task in no task tag is offered to all factions. Use `factionapi:has_faction` plus a faction task tag ([Tasks](../../data/tasks#task-assignment)). |

## Still stuck?

* Compare with how Vampirism does it: `ModFactions`, `ModAttachments`, `HunterPlayer`, `HunterSkills`, `HunterActions` in
  [`projects/vampirism`](https://github.com/TeamLapen/Vampirism/tree/dev/projects/vampirism/src/main/java/de/teamlapen/vampirism)
  and the generated skill tree JSON under `src/generated/resources/data/vampirism/factionapi`.
* Ask on [Discord](https://discord.gg/wuamm4P) or open an issue on [GitHub](https://github.com/TeamLapen/Vampirism).
