---
sidebar_position: 2
title: Alchemical Cauldron Recipes
---
:::info

The Alchemical Cauldron can only be used by Hunter Player that have unlocked the Basic Alchemy skill.

:::

Recipes can be made for the [Alchemical Cauldron](../../wiki/content/blocks.mdx#alchemical-cauldron).

## Recipe
You can take a look at the default recipes [here](https://github.com/TeamLapen/Vampirism/blob/7a90925e3859acd964f0ef948c1f914791494dfa/src/generated/resources/data/vampirism/recipes/alchemical_cauldron).

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


## Craft Tweaker

Alchemical Cauldron recipes can be added or changed using Craft Tweaker. But this requires [Vampirism Integration](https://www.curseforge.com/minecraft/mc-mods/vampirism-integrations) to be installed.

The recipe type id is `alchemical_cauldron`

### Adding Recipes
```zenscript
<recipetype:vampirism:alchemical_cauldron>.addRecipe(<recipe-path>, <result-item> , <ingredient>, <item-input>, <required-level>, <cooktime>, <exp>, <required skills>);

<recipetype:vampirism:alchemical_cauldron>.addRecipe(<recipe-path>, <result-item> , <ingredient>, <fluid-input>, <required-level>, <cooktime>, <exp>, <required skills>);
```

- `recipe-path`: String
- `result-item`: ItemStack
- `ingredient`: Ingredient
- `item-input`: Ingredient
- `fluid-input`: FluidStack
- `required-level`: int
- `cooktime`: int
- `exp`: int
- `required skills`: Skill Bracket array