---
sidebar_position: 4
title: Convertibles
---

Main article about Vampiric Creatures can be found [here](../wiki/content/entities/bitten_animal).

By default, only vanilla animals can be converted to a vampiric version and Vampirism Integrations adds support for some other mods.
But if you want to make your own modded creatures convertible, or you are a modpack creator who wants more compatibility you can easily create simple vampiric version of other creatures.

## Prerequisites
First of all the entity must have blood values configured for them to be convertible. See [Blood Values](bloodvalues.md) for more information.


## Different converter
There are two build-in converters available. Others can be added by other mods.

### Default Converter
The default converter can be used for any entity. It will create a vampiric version of the entity. The problem with this variant is that the converted entity acts as dummy entity, which does not support the interactions of the source entity.
What this means is that if you convert a horse with this converter, you will no longer be able to ride it, access the inventory, etc ...

```json title="data/<modid>/vampirism/converter/<entity-modid>/<entity-id-path>.json"
{
  "handler": {
    "type": "vampirism:default"
  }
}
```

### Special Converter
The special converter is used to convert an entity into a specific vampiric entity. This is used, for example, to convert a horse into a vampire horse with full functionality. But this requires a custom converted entity to be registered.
```json title="data/<modid>/vampirism/converter/<entity-modid>/<entity-id-path>.json"
{
  "handler": {
    "type": "vampirism:special",
    "converted_type": "<converted-entity-id>"
  }
}
```

## Overlays

Additionally, a converted entity requires an overlay texture to distinguish it from the original entity. This overlay texture is applied to the original entities texture and can be specified with the `overlay` property.

```json title="data/<modid>/vampirism/converter/<entity-modid>/<entity-id-path>.json"
{
  "handler": {...},
  "overlay": "<path-to-overlay-texture>"
}
```

## Attribute modifiers
If no attribute modifier are defined the following default modifiers will be applied to the converted entity:

- Attack Damage: x1.3
- Knockback Resistance: x1.3
- Max Health: x1.5 
- Movement Speed: x1.2

They can be configured with the `attribute_helper` property. The following example shows how to configure the attack damage modifier.

```json title="data/<modid>/vampirism/converter/<entity-modid>/<entity-id-path>.json"
{
  "handler": {
    "type": ...,
    "attribute_helper": [
      {
        "attribute": "minecraft:generic.attack_damage",
        "fallback_base": 2.0,
        "modifier": 1.3
      }
    ]
  }
}
```

- `attribute`: The attribute to modify. See [here](https://minecraft.fandom.com/wiki/Attribute) for all vanilla attributes.
- `modifier`: The modifier to apply to the attribute. The final value will be `base * modifier`.
- `fallback_base`: The base value to use if the entity does not have the attribute. This is useful for modded entities which do not have the vanilla attributes.