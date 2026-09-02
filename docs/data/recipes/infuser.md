---
sidebar_position: 5
title: Infuser
---

Recipes for the **Blood Infuser**: a central item is infused using up to four side ingredients,
optionally producing byproducts in three extra output slots.

:::tip JSON Schema
[`schemas/recipe_infuser.schema.json`](https://raw.githubusercontent.com/TeamLapen/Vampirism/refs/heads/dev/schemas/recipe_infuser.schema.json)
:::

Recipe type: `vampirism:infuser`.

```json title="data/vampirism/recipe/example.json"
{
  "type": "vampirism:infuser",
  "commoninfo": {},
  "item": "vampirism:diamond_heart_seeker",
  "ingredient1": "vampirism:pure_blood_4",
  "ingredient2": "vampirism:pure_blood_4",
  "ingredient3": "vampirism:pure_blood_4",
  "ingredient4": "vampirism:pure_blood_4",
  "result": { "id": "vampirism:diamond_heart_seeker" },
  "cookingtime": 200
}
```

| Field                       | Required | Type                                                       | Description                                                                                                    |
|-----------------------------|----------|--------------------------------------------------------|--------------------------------------------------------------------------------------------------------|
| `item`                      | yes      | [Ingredient](https://minecraft.wiki/w/Recipe#Ingredients) | The central item being infused.                                                                              |
| `ingredient1`–`ingredient4` | –        | [Ingredient](https://minecraft.wiki/w/Recipe#Ingredients) | The four side slots. **At least one** must be present. A slot with no ingredient must be left empty in-world. |
| `result`                    | no       | [Item stack template](./intro#result)                    | Main output. If omitted, the output is a copy of `item` with pure-blood level applied (min of the inputs' pure levels). |
| `result1`, `result2`, `result3` | –    | [Item stack template](./intro#result)                    | Optional byproducts placed in the three extra output slots.                                                   |
| `commoninfo`                | no       | object `{ "show_notification": bool }`                    | Vanilla `CommonInfo`. Defaults to `{}` → `show_notification: true`.                                            |
| `group`                     | no       | string                                                   | Recipe-book grouping.                                                                                        |
| `cookingtime`               | no       | int (default `200`)                                      | Ticks to finish.                                                                                             |
