---
sidebar_position: 2
title: Alchemical Cauldron Recipes
---
:::info

The Alchemical Cauldron can only be used by Hunter Player that have unlocked the Basic Alchemy skill.

:::

Recipes can be made for the [Alchemical Cauldron](../../wiki/content/blocks.mdx#alchemical-cauldron).

## Recipe

```json title="cauldron.json"
{
  "type": "vampirism:alchemical_cauldron",
  "result": {
    "item": "",
    "count": int,
    "nbt:": {},
  },
  "ingredient": {},
  "fluidItem": {},
  "fluid": {
    "fluid": ""
  },
  "skill": [],
  "cookTime": int,
  "experience": float,
  "reqLevel": int
}
```

`fluidItem` and `fluid` are mutual exclusive.
- `type`: The type of recipe is `vampirism:alchemical_cauldron` for alchemical cauldron recipes.
- `result`: The result item of the recipe.
- `ingredient`: The input ingredient of the recipe.
- `fluidItem`: Fluid ingredient of the recipe.
- `fluid`: The fluid input of the recipe.
- `skill`: The skills the recipe requires. String array of skill registry ids.
- `cookTime`: The cooking time of the recipe. Default is 200.
- `experience`: The experience gained by the recipe. Default is 0.2.
- `reqLevel`: The faction level the recipe requires. Default is 1.