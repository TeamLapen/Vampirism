---
sidebar_position: 4
title: Alchemy Table
---

Brewing-style recipes for the [Alchemy Table](../../wiki/content/blocks#alchemy-table): a **base
ingredient** is combined with an **input** item to produce a result.

:::info
The Alchemy Table can only be used by a Hunter.
:::

:::tip JSON Schema
[`schemas/recipe_alchemy_table.schema.json`](https://raw.githubusercontent.com/TeamLapen/Vampirism/refs/heads/dev/schemas/recipe_alchemy_table.schema.json)
:::

```json title="data/vampirism/recipe/example.json"
{
  "type": "vampirism:alchemical_table",
  "common_info": {},
  "ingredient": {
    "neoforge:ingredient_type": "neoforge:components",
    "items": "vampirism:oil_bottle",
    "components": { "vampirism:oil_contents": { "oil": "vampirism:plant" } }
  },
  "input": "minecraft:amethyst_shard",
  "result": {
    "id": "vampirism:oil_bottle",
    "components": { "vampirism:oil_contents": { "oil": "vampirism:bleeding" } }
  },
  "skill": []
}
```

| Field         | Required | Type                                                       | Description                                                       |
|---------------|----------|--------------------------------------------------------|-----------------------------------------------------------|
| `ingredient`  | yes      | [Ingredient](https://minecraft.wiki/w/Recipe#Ingredients) | The base item (the one that is transformed).                     |
| `input`       | yes      | [Ingredient](https://minecraft.wiki/w/Recipe#Ingredients) | The reagent consumed alongside it.                              |
| `result`      | yes      | [Item stack template](./intro#result)                    | Output.                                                          |
| `common_info` | no       | object `{ "show_notification": bool }`                    | Vanilla `CommonInfo`. Defaults to `{}` → `show_notification: true`. |
| `group`       | no       | string                                                   | Recipe-book grouping.                                            |
| `skill`       | no       | [Skill](./intro#skill-and-level) id[]                     | Required unlocked skills.                                        |

The Alchemy Table has no `level` field.
