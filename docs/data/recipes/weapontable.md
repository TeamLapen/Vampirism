---
sidebar_position: 1
title: Weapon Table Recipes
---
:::info

The Weapon Table can only be used by Hunter Player that have unlocked the Weapon Table skill.

:::

Shaped or shapeless recipes can be made for the [Weapon Table](../../wiki/content/blocks#weapon-table).

## Shaped Recipe
You can take a look at the default recipes [here](https://github.com/TeamLapen/Vampirism/blob/7a90925e3859acd964f0ef948c1f914791494dfa/src/generated/resources/data/vampirism/recipes/weapontable).

```json title="<recipe-name>.json"
{
  "type": "vampirism:shaped_crafting_weapontable",
  "pattern": [],
  "key": {},
  "result": {
    "item": "",
    "count": int,
    "nbt:": {},
  },
  "lava": int,
  "skill": [],
  "level": int
}
```

- `type`: The type of recipe is `vampirism:shaped_crafting_weapontable` for shaped weapon table recipes.
- `pattern`: The 4x4 pattern of the recipe. Just like the shaped crafting table recipe, but supports a crafting matrix up to 4x4.
- `key`: The key dictionary for the pattern. Just like the shaped crafting table keys.
- `result`: The result of the recipe.
- `lava`: The amount of lava the crafting consumes. Default is 0. Randing from 0 to 5.
- `skill`: The skills the recipe requires. String array of skill registry ids.
- `level`: The faction level the recipe requires. Default is 1.


## Shapeless Recipe

```json title="shapeless_recipe.json"
{
  "type": "vampirism:shapeless_crafting_weapontable",
  "ingredients": [],
  "result": {
    "item": "",
    "count": int,
    "nbt:": {},
  },
  "lava": int,
  "skill": [],
  "level": int
}
```
- `type`: The type of recipe is `vampirism:shapeless_crafting_weapontable` for shapeless weapon table recipes.
- `ingredients`: The ingredients of the recipe. Just like the shapeless crafting table recipe.
- `result`: The result of the recipe.
- `lava`: The amount of lava the crafting consumes. Default is 0. Randing from 0 to 5.
- `skill`: The skills the recipe requires. String array of skill registry ids.
- `level`: The faction level the recipe requires. Default is 1.


## Craft Tweaker

Weapon Table recipes can be added or changed using Craft Tweaker. But this requires [Vampirism Integration](https://www.curseforge.com/minecraft/mc-mods/vampirism-integrations) to be installed.

The recipe type id is `weapontable_crafting`

### Adding Recipes
```zenscript
<recipetype:vampirism:weapontable_crafting>.addShapeless(<recipe-path>, <result-item>, <ingredient-list>, <required-level>, <lava>, <required-skills>);
<recipetype:vampirism:weapontable_crafting>.addShaped(<recipe-path>, <result-item>, <pattern>, <required-level>, <lava>, <required-skills>);
```

- `recipe-path`: String
- `result-item`: ItemStack
- `ingredient-list`: list of Ingredient
- `pattern`: 4 x 4 Ingredient Table
- `required-level`: int
- `required skills`: Skill Bracket array