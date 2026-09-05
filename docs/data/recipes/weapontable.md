---
sidebar_position: 2
title: Weapon Table
---

Shaped or shapeless recipes for the Weapon Table. The Weapon
Table has a **4×4** grid.

:::info
The Weapon Table can only be used by a Hunter that unlocked the *Weapon Table* skill.
:::

:::tip JSON Schema
[`schemas/recipe_weapon_table.schema.json`](https://raw.githubusercontent.com/TeamLapen/Vampirism/refs/heads/dev/schemas/recipe_weapon_table.schema.json)
:::

Recipe type: `vampirism:weapon_table`. See [Overview](./intro) for `result`, `skill`, `level` and
`show_notification`.

## Shaped

```json title="data/vampirism/recipe/example.json"
{
  "type": "vampirism:shaped_crafting_weapontable",
  "category": "equipment",
  "pattern": [
    "XXXX",
    "XYYX",
    "XZZX",
    "X  X"
  ],
  "key": {
    "X": "#c:leathers",
    "Y": "#c:crops/garlic",
    "Z": "#c:gems/diamond"
  },
  "result": { "id": "vampirism:armor_of_swiftness_legs_ultimate" },
  "lava": 5,
  "level": 1,
  "skill": [ "vampirism:artisan_craftsmanship" ]
}
```

| Field               | Required | Type                                                             | Description                                                        |
|---------------------|----------|--------------------------------------------------------------|--------------------------------------------------------------|
| `pattern`           | yes      | string[]                                                     | Up to 4 rows, up to 4 chars each. Space = empty. Same rules as vanilla shaped crafting. |
| `key`               | yes      | object&lt;char, [Ingredient](https://minecraft.wiki/w/Recipe#Ingredients)&gt; | Maps each pattern character to an ingredient.        |
| `result`            | yes      | [Item stack template](./intro#result)                        | Crafting output.                                                  |
| `category`          | no       | `equipment` \| `building` \| `redstone` \| `misc`            | Recipe-book category.                                            |
| `group`             | no       | string                                                       | Recipe-book grouping.                                            |
| `lava`              | no       | int (default `0`)                                            | Lava (mB-equivalent fuel) consumed per craft. Typically `0`–`5`. |
| `level`             | no       | int (default `1`)                                            | Minimum hunter level.                                            |
| `skill`             | no       | [Skill](./intro#skill-and-level) id[]                        | Required unlocked skills.                                        |
| `show_notification` | no       | bool (default `true`)                                        | Vanilla recipe-toast toggle.                                     |

## Shapeless

```json
{
  "type": "vampirism:shapeless_crafting_weapontable",
  "category": "equipment",
  "ingredients": [
    "vampirism:quarrel_normal",
    { "neoforge:ingredient_type": "neoforge:components", "items": "vampirism:oil_bottle", "components": { "vampirism:oil_contents": { "oil": "vampirism:bleeding" } } }
  ],
  "result": { "id": "vampirism:quarrel_bleeding" }
}
```

| Field         | Required | Type                                                             | Description                                    |
|---------------|----------|--------------------------------------------------------------|------------------------------------------|
| `ingredients` | yes      | [Ingredient](https://minecraft.wiki/w/Recipe#Ingredients)[] (1–16) | Unordered inputs.                     |
| `result`      | yes      | [Item stack template](./intro#result)                        | Crafting output.                             |

`category`, `group`, `lava`, `level`, `skill` and `show_notification` are the same as for the shaped
variant.
