---
title: Config
sidebar_position: 18
---

With this command you can change some server config values without editing the file. Rerun the command to undo a blacklist (the entry is removed again). Available commands are the following:

## Entity Blood Values

Entities can be blacklisted by looking at them while executing the command or by choosing one from the suggestions. Vampires can then no longer get blood from them (`blacklistedBloodEntities` in the server config).

```
/vampirism config bloodvalues blacklist entity [<entity>]
```

| Parameter | Type          | Description                                                 |
|:----------|:--------------|:------------------------------------------------------------|
| entity    | `entity_type` | Instead of the current target, the specified entity is used |

## Sun Damage

Biomes and dimensions can be blacklisted for vampire sun damage (`noSundamageBiomes` / `noSundamageDimensions` in the server config).

```
/vampirism config sundamage blacklist biome [<biome>]
/vampirism config sundamage blacklist dimension [<dimension>]
```

| Parameter | Type        | Description                                               |
|:----------|:------------|:----------------------------------------------------------|
| biome     | `biome`     | Instead of the current biome, the specified biome is used |
| dimension | `dimension` | Instead of the current dimension, the specified one is used |

## Bat Dimension Blacklist

Toggles a dimension in `batDimensionBlacklist`, in which vampires cannot transform into a bat.

```
/vampirism config bat-dimension-blacklist <dimension>
```

| Parameter | Type        | Description               |
|:----------|:------------|:--------------------------|
| dimension | `dimension` | The dimension to toggle   |

## Permissions

:::info

Requires permission level 3 (admin)

:::
