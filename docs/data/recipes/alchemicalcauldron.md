---
sidebar_position: 3
title: Alchemical Cauldron
---

Recipes for the Alchemical Cauldron. A recipe consumes
one **item** ingredient plus one **fluid or fluid-item** input and cooks a result over time.

:::info
The Alchemical Cauldron can only be used by a Hunter that unlocked the *Basic Alchemy* skill.
:::

:::tip JSON Schema
[`schemas/recipe_alchemical_cauldron.schema.json`](https://raw.githubusercontent.com/TeamLapen/Vampirism/refs/heads/dev/schemas/recipe_alchemical_cauldron.schema.json)
:::

```json title="data/vampirism/recipe/example.json"
{
  "type": "vampirism:alchemical_cauldron",
  "info": {},
  "ingredient": "minecraft:iron_block",
  "fluid": "vampirism:pure_blood_1",
  "result": { "id": "vampirism:blood_infused_iron_block" },
  "cookTime": 180,
  "experience": 0.15,
  "level": 1,
  "skill": []
}
```

| Field         | Required | Type                                                                              | Description                                                                                   |
|---------------|----------|--------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| `ingredient`  | yes      | [Ingredient](https://minecraft.wiki/w/Recipe#Ingredients)                        | The solid input.                                                                             |
| `fluid`       | yes      | [Ingredient](https://minecraft.wiki/w/Recipe#Ingredients) **or** [Fluid stack template](#fluid-stack-template) | The fluid input – **either** a fluid-carrying item (Ingredient) **or** a raw fluid amount. |
| `result`      | yes      | [Item stack template](./intro#result)                                            | Cooked output.                                                                              |
| `info`        | no       | object `{ "show_notification": bool }`                                            | Vanilla `CommonInfo`. Defaults to `{}` → `show_notification: true`.                          |
| `group`       | no       | string                                                                          | Recipe-book grouping.                                                                       |
| `cookTime`    | no       | int (default `200`)                                                             | Ticks to finish.                                                                            |
| `experience`  | no       | float (default `0.2`)                                                           | XP awarded on completion.                                                                    |
| `level`       | no       | int (default `1`)                                                              | Minimum hunter level.                                                                       |
| `skill`       | no       | [Skill](./intro#skill-and-level) id[]                                            | Required unlocked skills.                                                                    |

### Fluid stack template

When `fluid` is a raw fluid rather than an item:

```json
{ "id": "vampirism:blood", "amount": 500, "components": { /* optional */ } }
```

`amount` is in mB and is **required**. A bare fluid id string is also accepted and defaults to one
bucket (`1000` mB).
