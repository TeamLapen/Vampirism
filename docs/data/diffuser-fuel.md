---
sidebar_position: 5
title: Diffuser Fuel & Liquid Colors
---

Three more [NeoForge data maps](https://docs.neoforged.net/docs/datamaps/structure) on the **item** registry
control the garlic and fog diffusers and the colour of items in the alchemical cauldron. They use the same
file wrapper as [Blood Values](./bloodvalues#data-map-file-format) (`replace`, `values`, `remove`,
`neoforge:conditions`) and are all **synced to the client**. There are no JSON schemas for them.

| Data map                   | File                                                             | Value                    |
|----------------------------|------------------------------------------------------------------|--------------------------|
| `vampirism:garlic_diffuser_fuel` | `data/vampirism/data_maps/item/garlic_diffuser_fuel.json` | `{ "burn_duration": int }` |
| `vampirism:fog_diffuser_fuel`    | `data/vampirism/data_maps/item/fog_diffuser_fuel.json`    | `{ "burn_duration": int }` |
| `vampirism:liquid_color`         | `data/vampirism/data_maps/item/liquid_color.json`         | int (RGB colour)         |

:::note File location
A data map file lives in the namespace of the data map itself, so these always go to
`data/vampirism/data_maps/item/<name>.json` (like [`item_blood.json`](./bloodvalues)). Any data pack can ship
such a file to add entries (merged) or `replace` / `remove` them. The generated defaults are in
[`projects/vampirism/src/generated/resources/data/vampirism/data_maps/item/`](https://github.com/TeamLapen/Vampirism/tree/dev/projects/vampirism/src/generated/resources/data/vampirism/data_maps/item).
:::

## Garlic and fog diffuser fuel

Items listed in `garlic_diffuser_fuel` can be put into the fuel slot of the **garlic diffuser** (hunter
block), items in `fog_diffuser_fuel` into the **fog diffuser** (vampire block). When the diffuser is not
burning and has an item with a burn duration above `0` in its fuel slot, it consumes one item and stays active
for `burn_duration` ticks. Items without an entry have a burn duration of `0` and are not used as fuel.

| Field           | Type | Description                                                                 |
|-----------------|------|-----------------------------------------------------------------------------|
| `burn_duration` | int  | How long one item keeps the diffuser burning, in **ticks** (20 ticks = 1 second). |

```json title="data/vampirism/data_maps/item/garlic_diffuser_fuel.json (generated default)"
{
  "values": {
    "vampirism:purified_garlic": { "burn_duration": 108000 }
  }
}
```

```json title="data/vampirism/data_maps/item/fog_diffuser_fuel.json (generated default)"
{
  "values": {
    "vampirism:pure_blood_0": { "burn_duration": 288000 },
    "vampirism:pure_blood_1": { "burn_duration": 432000 },
    "vampirism:pure_blood_2": { "burn_duration": 864000 },
    "vampirism:pure_blood_3": { "burn_duration": 1296000 },
    "vampirism:pure_blood_4": { "burn_duration": 1728000 }
  }
}
```

(108000 ticks = 90 minutes for purified garlic; pure blood 0–4 = 4, 6, 12, 18 and 24 hours.)

Add your own fuel, for example from another mod:

```json title="data/vampirism/data_maps/item/fog_diffuser_fuel.json (in your data pack)"
{
  "values": {
    "mymod:dark_essence": { "burn_duration": 72000 }
  }
}
```

The API exposes the values as `IGarlicDiffuserFuel` / `IFogDiffuserFuel` (both extend `IDiffuserFuel#burnDuration()`)
via `VampirismDataMaps.GARLIC_DIFFUSER_FUEL` and `VampirismDataMaps.FOG_DIFFUSER_FUEL`; see
[API reference](../api/reference).

## Liquid color

`vampirism:liquid_color` assigns a colour to items so the **alchemical cauldron** can tint the liquid it
shows when the item in its first slot is not a fluid container with a fluid tint of its own. The value is a
plain integer containing the RGB colour (decimal, e.g. `0xBBBBBB` = `12303291`). If an item has no entry and no
fluid, the cauldron falls back to `0x00003B`.

```json title="data/vampirism/data_maps/item/liquid_color.json (generated default)"
{
  "values": {
    "#c:crops/garlic": 12303291,
    "#c:crops/garlic/purified": 12303291,
    "#vampirism:holy_water": 6711039,
    "#vampirism:holy_water_splash": 6711039,
    "vampirism:pure_blood_0": 8128517,
    "vampirism:pure_blood_1": 8193283,
    "vampirism:pure_blood_2": 8585216,
    "vampirism:pure_blood_3": 8261134,
    "vampirism:pure_blood_4": 9306112
  }
}
```

Keys can be item ids or `#item_tags`.
