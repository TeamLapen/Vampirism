---
sidebar_position: 3
title: Convertibles
---

The main article about vampiric creatures is in the vampiric creatures wiki page.

By default only some vanilla animals can be turned into a vampiric version. You can make any other
(vanilla or modded) creature convertible from a data pack by adding it to the
`vampirism:entity_converter` [data map](https://docs.neoforged.net/docs/datamaps/structure).

:::tip JSON Schema
[`schemas/entity_converter.schema.json`](https://raw.githubusercontent.com/TeamLapen/Vampirism/refs/heads/dev/schemas/entity_converter.schema.json)
:::

## Requirements

A creature is only convertible when **both** of these are set for its entity type:

1. an [entity blood](./bloodvalues#entity-blood) value greater than `0`, and
2. an `entity_converter` entry (this page).

## Location

```
data/<namespace>/data_maps/entity_type/entity_converter.json
```

Data map `vampirism:entity_converter`, synced to the client. Uses the standard NeoForge data map
wrapper (`replace` / `values` / `remove`); `values` maps an entity type id (or `#tag`) to an entry.

```json title="data/vampirism/data_maps/entity_type/entity_converter.json"
{
  "values": {
    "minecraft:cow": {
      "handler": { "type": "vampirism:special", "converted_type": "vampirism:converted_cow" }
    },
    "examplemod:deer": {
      "handler": { "type": "vampirism:default" }
    }
  }
}
```

| Field     | Required | Type                      | Description                                                              |
|-----------|----------|---------------------------|--------------------------------------------------------------------|
| `handler` | no       | [Converter](#converters)  | How this creature converts. Defaults to `vampirism:default` with the default attribute modifiers. |

:::note Migration
Older versions accepted an `overlay` field on the entry. It has been removed – the overlay texture is
now resolved by convention (see [Overlay texture](#overlay-texture)).
:::

## Converters

`handler` is a typed object dispatched on `type`, registered in the `vampirism:converting_handler`
registry. Add-on mods can register more types.

### `vampirism:default`

Wraps the original creature in a generic converted-creature entity. The original model is reused with
a vampire overlay layer, so source-entity interactions (riding, inventories, breeding, …) are **not**
available on the converted creature.

```json
{
  "type": "vampirism:default",
  "attribute_helper": [ /* optional, see below */ ]
}
```

| Field              | Required | Type                                        | Description                                    |
|--------------------|----------|---------------------------------------------|--------------------------------------------|
| `attribute_helper` | no       | [Attribute modifier](#attribute-modifiers)[] | Overrides the default attribute scaling.     |

### `vampirism:special`

Converts the creature into a dedicated converted entity type that keeps full functionality (e.g. a
vampire horse you can still ride). Requires that converted entity type to be registered by a mod.

```json
{
  "type": "vampirism:special",
  "converted_type": "vampirism:converted_horse",
  "attribute_helper": [ /* optional */ ]
}
```

| Field              | Required | Type                                                            | Description                                        |
|--------------------|----------|--------------------------------------------------------------|------------------------------------------------|
| `converted_type`   | yes      | [Resource location](https://minecraft.wiki/w/Resource_location) | Entity type to spawn as the converted creature.  |
| `attribute_helper` | no       | [Attribute modifier](#attribute-modifiers)[]                   | Overrides the default attribute scaling.          |

## Attribute modifiers

When `attribute_helper` is omitted, the converted creature gets these multipliers:

| Attribute                        | Multiplier |
|----------------------------------|-----------|
| `minecraft:attack_damage`        | ×1.3       |
| `minecraft:knockback_resistance` | ×1.3       |
| `minecraft:max_health`           | ×1.5       |
| `minecraft:movement_speed`       | ×1.2       |

Providing `attribute_helper` replaces the **entire** default list, so re-declare any multiplier you
want to keep. Each entry:

```json
{
  "attribute": "minecraft:attack_damage",
  "modifier": 1.3,
  "fallback_base": 2.0
}
```

| Field           | Required | Type                                                            | Description                                                                                   |
|-----------------|----------|--------------------------------------------------------------|-----------------------------------------------------------------------------------------|
| `attribute`     | yes      | [Resource location](https://minecraft.wiki/w/Resource_location) | The attribute to scale.                                                                     |
| `modifier`      | yes      | [Float provider](https://minecraft.wiki/w/Float_provider_type) | Multiplier applied to the base value. A bare number is a constant; an object rolls a random value per conversion. |
| `fallback_base` | no       | number (default `1.0`)                                        | Base value assumed when the source entity type does not define the attribute.                |

The converted base value is `entity_type_base_value × modifier`.

## Overlay texture

For `vampirism:default` conversions, the vampire overlay is a client resource looked up by
convention:

```
assets/<source_entity_namespace>/textures/entity/overlay/<source_entity_path>.png
```

e.g. `examplemod:deer` needs `assets/examplemod/textures/entity/overlay/deer.png`. Ship it in a
resource pack (or your mod's assets). `vampirism:special` converters use their own entity's model and
texture instead.
