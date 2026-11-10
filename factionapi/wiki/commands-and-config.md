---
sidebar_position: 5
title: Commands, permissions & config
---

Reference for server admins and players of the **FactionApi** mod itself. Vampirism adds its own commands and
options on top; those are covered in the Vampirism wiki.

## Commands

All FactionApi commands live under the root literal **`/factions`**
(`FactionCommands#registerCommands`, registered on `RegisterCommandsEvent`). In non-production
(development) environments the aliases `/f`, `/level` and `/lord` also exist.

Permission levels are the vanilla ones (`Commands.LEVEL_GAMEMASTERS` = 2, `LEVEL_ADMINS` = 3). A node
without a requirement is available to everyone.

| Command                                                       | Level | Effect |
|---------------------------------------------------------------|-------|--------|
| `/factions faction set <faction id>`                          | 2     | Puts you into the faction at level `1`. Only playable factions are offered. |
| `/factions faction set <faction id> level [<level> [<players>]]` | 2  | Sets the faction level for you or the given players (min. `1`; capped at the faction's highest reachable level). |
| `/factions faction set <faction id> lord [<level> [<players>]]`  | 2  | Sets the lord level (default `1`) – puts the player into the faction at max level with that lord level; fails if the faction has no lords. |
| `/factions faction get <player>`                              | 2     | Prints the player's faction, level and lord level. |
| `/factions skills enable <skill\|all> [<player(s)>] [force]`  | 3     | Enables a skill (or all skills of the current faction). Without `force` the normal unlock rules apply (points, parents, locks); `force` ignores them. |
| `/factions skills disable <skill\|all> [<player(s)>]`         | 3     | Disables a skill, or all skills (`all` also resets refinements). |
| `/factions skills toggle <skill> [<player>] [force]`          | 3     | Disables the skill if enabled, otherwise enables it. |
| `/factions actions bind <key> <action>`                       | –     | Binds an action to the action key `ACTION_1` … `ACTION_9` of the executing player (sent to the client as a binding packet). |
| `/factions actions reset [<players>]`                         | 3     | Resets all action cooldown/duration timers. |
| `/factions customization gender <gender>`                     | –     | Sets the lord title gender (`unknown`, `male`, `female`) of the executing player. |
| `/factions village capture <faction>`                         | 2     | Forces the given faction to start capturing the village at your position. |
| `/factions village abort`                                     | 2     | Aborts the running capture of the totem near your position. |
| `/factions minion create <faction> [<appearance args…>]`      | 2     | Creates a new minion of the faction's minion entry for yourself. The optional trailing arguments are the entry's `MinionAppearance`s in order (Vampirism's entries use `name`, `skin`, `use_minion_skin`, `use_lord_skin`). You must be in the faction, be a lord and have a free minion slot. |
| `/factions minion recall [<player>]`                          | 2     | Recalls all minions to the player. |
| `/factions minion respawn [<player>]`                         | 2     | Respawns minions without an entity (unclaimed). |
| `/factions minion purge [<player>]`                           | 2     | Deletes the player's minion controller data (all minions). |
| `/factions minion inventory <minion> list`                    | 3     | Lists a minion's inventory. |
| `/factions minion inventory <minion> add <item> [count]`      | 3     | Adds items to a minion's inventory. |
| `/factions minion inventory <minion> remove <item> [count]`   | 3     | Removes items and gives them to you. |

The faction ids offered by `faction set` also include `factionapi:neutral` (its highest level defaults to `1`, so this
puts you into the neutral faction at level 1). Use it only for testing.

`<minion>` uses the custom `factionapi:minion_id` argument (`<player>:<id>`, optionally with `| name`). The
suggestions are sent to the client at login, so minions created afterwards only show up after rejoining or
reloading. `<faction id>` tab-completes registered faction ids.

:::note
`<players>` accepts an entity selector such as `@a` (`faction set … level/lord`, `skills enable all`,
`skills disable all`, `actions reset`); `<player>` is a single player. The optional `<player>` of
`skills enable|disable|toggle <skill>` and `minion recall|respawn|purge` defaults to yourself. The `force`
flag of `enable`/`toggle` can also be given directly after the skill.
:::

## Permissions

FactionApi can check the NeoForge permission API for some actions. The check is only active if the server
option [`usePermissions`](#server-config) is `true`; otherwise everything is allowed.

| Node (`PermissionNode`) | Default | Meaning |
|---|---|---|
| `factionapi.action` | allowed for everyone | Whether a player may use a faction action. Evaluated with the dynamic context key `action` (value: the action id, e.g. `vampirism:rage`), so a permission handler can allow or deny individual actions. |
| `factionapi.check`  | allowed for everyone | General check node. It is registered but nothing in FactionApi's code currently queries it. |

## Config

NeoForge writes the files `factionapi-server.toml` (per world, `serverconfig/`) and `factionapi-client.toml`
(`config/`) using the default naming scheme for a `SERVER` / `CLIENT` config. Section names below are the
TOML tables.

### Server config

| Option | Section | Default | Range | Description |
|---|---|---|---|---|
| `villageMaxSpawnableVillagers` | `world.village` | `30` | `0..` | Maximum number of villagers the totem can respawn. |
| `villageAllowPoiDestruction`   | `world.village` | `false` | – | Allow destroying point-of-interest blocks in faction villages the player does not own. |
| `villageMaximumTotemRadius`    | `world.village` | `100` | `0..` | Maximum range of a totem used to grow the village. |
| `raidRandomChance`             | `world.raid` | `2.77777777778E-4` | `0..1` | Chance per second of a faction raid occurring. |
| `raidPhaseOneDuration`         | `world.raid` | `80` | `1..1000` | Duration of capture phase one, seconds. |
| `raidPhaseTwoMembersForceTargetTime` | `world.raid` | `80` | `1..1000` | Seconds into phase two after which capture entities seek a target regardless of distance. |
| `raidNotifyDistance`           | `world.raid` | `40000` | `0..100000` | **Squared** distance within which players are notified of a village capture. |
| `raidDisableVillageGuards`     | `world.raid` | `false` | – | Villagers in controlled villages do not turn into guards when attacked. |
| `factionColorInChat`           | `faction` | `true` | – | Colour player names in chat by faction. |
| `factionLordPrefixInChat`      | `faction` | `true` | – | Prefix the lord title to player names in chat. |
| `factionPvpOnlyBetweenFactions`| `faction` | `false` | – | PVP only between members of different factions (needs server PVP; "not guaranteed to always protect teammates"). |
| `factionUnlockAllSkills`       | `faction` | `false` | – | Allow unlocking all skills when the max level is reached. |
| `factionSkillPointsPerLevel`   | `faction` | `2.0` | `1..20` | Skill points per level-up (other values are unbalanced). |
| `factionSkillPointsPerLordLevel` | `faction` | `2.0` | `1..20` | Skill points per lord level-up. |
| `minionDeathRecoveryTime`      | `faction.minions` | `220` | `1..` | Seconds a minion needs to recover after death. |
| `minionPerLordLevel`           | `faction.minions` | `1` | `0..100` | Minions a player may have per lord level. |
| `minionResourceCooldown`       | `faction.minions` | `1500` | `20..` | Ticks between collect-resources loot rolls. |
| `minionOfflineResourceCooldownMultiplier` | `faction.minions` | `20.0` | `1..100000` | Cooldown multiplier for collect tasks while the owner is offline. |
| `minionCollectFailuresUntilFinished` | `faction.minions` | `3` | `1..` | Consecutive collections that do not fit into the inventory before a collect task finishes. |
| `minionEquipmentRepairAmount`  | `faction.minions` | `10` | `1..` | Equipment repair amount when completing resource tasks. |
| `taskDurationSinglePlayer`     | `faction.tasks` | `120` | `1..` | Minutes a task may take in singleplayer. |
| `taskDurationDedicatedServer`  | `faction.tasks` | `1440` | `1..` | Minutes a task may take on a dedicated server. |
| `taskMasterMaxTasks`           | `faction.tasks` | `3` | `1..` | Maximum tasks shown at a task master (excluding unique tasks). |
| `entitiesIMob`                 | `admin` | `SMART` | `ALWAYS_IMOB`, `NEVER_IMOB`, `SMART` | Whether faction entities count as hostile (`Enemy`) for other mods; `SMART` falls back to `NEVER_IMOB` on servers. |
| `preventDebugBoundingBoxes`    | `admin` | `false` | – | Stops players from enabling debug bounding boxes, which can reveal disguised entities. |
| `usePermissions`               | `admin` | `false` | – | Use the NeoForge permission system for [permission nodes](#permissions). |
| `enableFactionLogging`         | `admin` | `false` | – | Write a custom log file of specific faction actions (e.g. minion inventory commands). Dedicated server only, needs a restart. |

### Client config

| Option | Section | Default | Range | Description |
|---|---|---|---|---|
| `renderTotemFactionName`        | *(root)* | `true` | – | Render the owning faction's name above totem blocks. |
| `addFactionMenuButtonToInventory` | `gui` | `true` | – | Add a shortcut button to the faction menu in the inventory screen. |
| `factionMenuButtonXPos`         | `gui` | `125` | int | Horizontal offset from the screen centre, px. |
| `factionMenuButtonYPos`         | `gui` | `-22` | int | Vertical offset from the screen centre, px. |
| `showFactionLevelOverlay`       | `overlays` | `true` | – | Show the faction level indicator. |
| `factionLevelOverlayXPos`       | `overlays` | `0` | `-250..250` | Horizontal offset from the screen centre, px. |
| `factionLevelOverlayYPos`       | `overlays` | `47` | `0..270` | Vertical offset from the bottom, px. |
| `showFactionRaidBarOverlay`     | `overlays` | `true` | – | Show the raid bar. |
| `showActionCooldownOverlay`     | `overlays` | `true` | – | Show cooldowns of actions in the [`show_cooldown_in_hud` tag](../data/other#action-tags). |
| `showActionDurationOverlay`     | `overlays` | `true` | – | Show durations of actions in the [`show_duration_in_hud` tag](../data/other#action-tags). |

Besides the config file, the client keeps per-world *preferences* (action key bindings, action order, minion
task order) which are edited in-game.
