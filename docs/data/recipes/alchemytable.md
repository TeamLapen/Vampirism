---
sidebar_position: 2
title: Alchemy Table
---
:::info

The Alchemy can only be used by Hunter Player

:::

Recipes can be made for the [Alchemy Table](../../wiki/content/blocks#alchemy-table).

## Recipe
You can take a look at the default recipes [here](https://github.com/TeamLapen/Vampirism/blob/7a90925e3859acd964f0ef948c1f914791494dfa/src/generated/resources/data/vampirism/recipes/alchemy_table).

```json title="<recipe-name>.json"
{
  "type": "vampirism:alchemical_table",
  "group": "",
  "result": {
    "item": "",
    "count": int,
    "nbt:": {},
  },
  "input": {},
  "ingredient": {},
  "skill": [],
}
```

- `type`: The type of recipe is `vampirism:alchemical_table` for Alchemy Table recipes.
- `result`: The result item of the recipe.
- `ingredient`: The base ingredient of the recipe.
- `input`: The input ingredient of the recipe.
- `skill`: The skills the recipe requires. String array of skill registry ids. *Optional*
- `group`: The recipe group. *Optional*