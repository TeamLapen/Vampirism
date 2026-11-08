---
sidebar_position: 4
title: Other data
---

Besides the [dynamic registries](./intro) (tasks, skill trees, skill segments), FactionApi ships further
data pack content: tags, loot tables and damage types. All of it is generated into
[`projects/faction/src/generated/resources/data/factionapi`](https://github.com/TeamLapen/Vampirism/tree/dev/projects/faction/src/generated/resources/data/factionapi)
and can be extended or overridden by any data pack.

## Tags

Tags for FactionApi's own registries live under `data/<ns>/tags/factionapi/<registry>/…`; tags for vanilla
registries use the usual folder (`tags/item`, `tags/mob_effect`, …).

| Tag file | Registry | Contents (FactionApi) | Effect |
|---|---|---|---|
| `factionapi/faction/*.json` | `factionapi:faction` | see [Faction tags](../api/utilities#faction-tags-factiontags) | `all`, `not_neutral`, `is_neutral`, `hostile_towards_neutral`, `friendly_towards_neutral`, `has_lord_skills`, `can_raid`, `has_random_raid` |
| `factionapi/skill_tree/…` | `factionapi:skill_tree` | `default` = `type/level` + `type/lord` | see [Skill tree tags](../api/utilities#skill-tree-tags-factionskilltreetags) |
| `factionapi/tasks/…` | `factionapi:tasks` | `is_unique` includes `awards_lord_level`; `has_faction` | see [Task assignment](./tasks#task-assignment) |
| `factionapi/actions/…` | `factionapi:actions` | `show_cooldown_in_hud`, `show_duration_in_hud` | see [Action tags](#action-tags) |
| `item/minion_tools/*` | `item` | see [Minion tool tags](#minion-tool-tags) | decides which loot pools a minion can roll |
| `mob_effect/disables_actions` | `mob_effect` | `factionapi:resurrection_fatigue` | effects that block faction actions |
| `villager_profession/has_faction` | `villager_profession` | empty in FactionApi (Vampirism fills it) | see [below](#villager-and-poi-tags) |
| `point_of_interest_type/has_faction` | `point_of_interest_type` | empty in FactionApi (Vampirism adds its three totem POIs) | see [below](#villager-and-poi-tags) |
| `block/totem_top`, `totem_top_fragile`, `totem_top_crafted` | `block` | `totem_top` = fragile + crafted tags; fragile = `factionapi:totem_top`, crafted = `factionapi:totem_top_crafted` | marks totem top blocks (added by factions through `FactionProperties#totem`) |

### Action tags

Tags in the `factionapi:actions` registry (`data/<ns>/tags/factionapi/actions/<name>.json`). Both tags are
empty by default; Vampirism fills them. They only affect the HUD of the **client**:

* `factionapi:show_cooldown_in_hud` – unlocked actions in this tag are drawn as a faded icon with a
  shrinking overlay in the bottom-right corner while they are on cooldown
  (`ActionCooldownOverlay`, can be disabled with the client option `showActionCooldownOverlay`).
* `factionapi:show_duration_in_hud` – the same for the remaining duration of active lasting actions
  (`ActionDurationOverlay`, option `showActionDurationOverlay`).

```json title="data/mypack/tags/factionapi/actions/show_cooldown_in_hud.json"
{ "values": [ "mypack:my_action" ] }
```

### Skill tree and task tags

Skill tree tags (`type/level`, `type/lord`, `default`) are documented under
[Skill tree tags](../api/utilities#skill-tree-tags-factionskilltreetags); they select which trees take part
in the shared skill point pool. Task tags (`has_faction`, `is_unique`, `awards_lord_level`) are described in
[Tasks](./tasks#task-assignment).

### Minion tool tags

The tool a minion holds in its main hand is passed to the collect-resource loot tables as
`minecraft:match_tool` context. FactionApi defines item tags for four tool types plus a fishing rod tag; each
type has a base tag and two tiers where **tier 2 is contained in tier 1**, which is contained in the base tag:

| Base tag | Contains | Tier 1 | Tier 2 |
|---|---|---|---|
| `factionapi:minion_tools/axe` | `#minecraft:axes`, `#…/axe/tier_1` | `minecraft:iron_axe`, `#…/axe/tier_2` | `minecraft:diamond_axe`, `minecraft:netherite_axe` |
| `factionapi:minion_tools/pickaxe` | `#minecraft:pickaxes`, `#…/pickaxe/tier_1` | `minecraft:iron_pickaxe`, `#…/pickaxe/tier_2` | `minecraft:diamond_pickaxe`, `minecraft:netherite_pickaxe` |
| `factionapi:minion_tools/shovel` | `#minecraft:shovels`, `#…/shovel/tier_1` | `minecraft:iron_shovel`, `#…/shovel/tier_2` | `minecraft:diamond_shovel`, `minecraft:netherite_shovel` |
| `factionapi:minion_tools/sword` | `#minecraft:swords`, `#…/sword/tier_1` | `minecraft:iron_sword`, `#…/sword/tier_2` | `minecraft:diamond_sword`, `minecraft:netherite_sword` |
| `factionapi:minion_tools/fishing_rod` | `#c:tools/fishing_rod` | – | – |

Add an item to one of these tags to let minions use a modded tool.

### Villager and POI tags

* `villager_profession/factionapi:has_faction` – when a village capture finishes, villagers in the village whose
  profession is in this tag are reset to the `none` profession (`TotemBlockEntity#updateCreaturesOnCapture`).
  Use it for faction-specific professions that must not survive a change of ownership.
* `point_of_interest_type/factionapi:has_faction` – POI types in this tag are **ignored** when the totem
  collects the points of interest that make up a village area (`TotemHelper#getVillagePointsOfInterest`).
  Add the POI types of faction totems here so they do not extend the village themselves.

### Mob effect tag

`mob_effect/factionapi:disables_actions` lists effects that prevent a faction player from using actions
(`ActionHelper#checkActionDisableEffect`). A faction can replace it with its own tag via the
`factionapi:action_disable` [faction-specific tag](../api/utilities#faction-specific-tags).

## Minion collect loot tables

The `collect_*` [minion tasks](../api/minions#tasks) roll a loot table every cooldown and put the result into
the minion's inventory. The tables are in
`data/factionapi/loot_table/minion_tasks/<name>.json` and use the loot table type
**`factionapi:minion_collect`**, whose context provides the minion's main hand item as `minecraft:tool`
(required) and the lord player as `factionapi:lord_player` (optional, only if the lord is online).

| Loot table | Task |
|---|---|
| `factionapi:minion_tasks/collect_resources` | `collect_resources` (default of the *collect resources* category) |
| `…/collect_wood` | `collect_wood` |
| `…/collect_ores` | `collect_ores` |
| `…/collect_fish` | `collect_fish` |
| `…/collect_plants` | `collect_plants` |
| `…/collect_mob_loot` | `collect_mob_loot` |

The generated tables show the pattern: a first pool without conditions (always rolled), followed by pools
guarded by `minecraft:match_tool` with the [minion tool tags](#minion-tool-tags) (and, for fishing, the Luck of
the Sea enchantment) so that better tools unlock better drops:

```json title="excerpt: collect_wood.json"
{
  "type": "factionapi:minion_collect",
  "pools": [
    { "rolls": 1.0, "entries": [ { "type": "minecraft:item", "name": "minecraft:stick", "weight": 30,
        "functions": [ { "function": "minecraft:set_count", "count": { "type": "minecraft:uniform", "min": 4, "max": 10 } } ] } ] },
    { "rolls": 1.0,
      "conditions": [ { "condition": "minecraft:match_tool", "predicate": { "items": "#factionapi:minion_tools/axe" } } ],
      "entries": [ { "type": "minecraft:item", "name": "minecraft:oak_log", "weight": 30 } ] }
  ]
}
```

Override a table (same path in your data pack) to change what minions collect.
Collection speed and failure handling are controlled by the
[server config](../wiki/commands-and-config#server-config) (`minionResourceCooldown`,
`minionOfflineResourceCooldownMultiplier`, `minionCollectFailuresUntilFinished`).

## Block loot tables

`loot_table/blocks/totem_base`, `totem_top` and `totem_top_crafted` drop the block itself (`totem_base` only if
it survives an explosion).

## Damage types

| Damage type | `message_id` | Scaling / exhaustion | Used for |
|---|---|---|---|
| `factionapi:minion` | `factionapi.minion` | never / `0.1` | damage dealt by minions (`MinionDamageSource`) |
| `factionapi:leave_faction` | `factionapi.leave_faction` | when caused by living non-player / `0.0` | the 10000 damage a player takes when leaving a faction with `die = true` (`FactionPlayerHandler#leaveFaction`); it is in the vanilla tags `bypasses_armor` and `bypasses_effects` |
