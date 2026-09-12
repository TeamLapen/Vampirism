---
sidebar_position: 6
title: Set Blood
---

`vampirism:set_blood` sets the blood amount of a blood bottle to a random value in
`[minBlood, maxBlood]`.

```json
{ "function": "vampirism:set_blood", "minBlood": 1, "maxBlood": 5 }
```

| Field        | Required | Type                                       | Description                                            |
|--------------|----------|------------------------------------------|--------------------------------------------------|
| `minBlood`   | yes      | int                                      | Lower bound (inclusive). `0` … `5` (blood bottle max). |
| `maxBlood`   | yes      | int                                      | Upper bound (inclusive).                               |
| `conditions` | no       | [Loot condition](../lootconditions/intro)[] | Standard conditional-function conditions.           |
