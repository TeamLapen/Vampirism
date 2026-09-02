---
sidebar_position: 2
title: Faction
---

`vampirism:faction` checks the `this_entity` loot parameter: it matches when that entity is a player
whose faction state satisfies `type`.

```json
{ "condition": "vampirism:faction", "type": "faction", "faction": "vampirism:hunter", "min_level": 3 }
```

| Field       | Required                    | Type                                                            | Description                                                       |
|-------------|-----------------------------|-------------------------------------------------------------|-----------------------------------------------------------|
| `type`      | yes                         | `no_faction` \| `any_faction` \| `faction`                   | Which check to run.                                              |
| `faction`   | when `type` = `faction`     | [Resource location](https://minecraft.wiki/w/Resource_location) | Faction id the player must be in.                            |
| `min_level` | no                          | int                                                         | Player's faction level must be ≥ this. Ignored for `no_faction`. |
| `max_level` | no                          | int                                                         | Player's faction level must be ≤ this. Ignored for `no_faction`. |

* `no_faction` – matches only if the player is factionless (neutral). `faction` / `min_level` /
  `max_level` are ignored.
* `any_faction` – matches if the player is in **any** faction (and within the level range if given).
* `faction` – matches if the player is in the given `faction` (and within the level range if given).
