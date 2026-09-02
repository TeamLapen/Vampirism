---
sidebar_position: 5
title: Adjustable Level
---

`vampirism:adjustable_level` matches when the targeted entity implements `IAdjustableLevel` (basic
vampires, basic hunters, barons, …) and its level **exactly equals** `level`.

```json
{ "condition": "vampirism:adjustable_level", "level": 2, "target": "this" }
```

| Field    | Required | Type                                | Description                                                   |
|----------|----------|-----------------------------------|---------------------------------------------------------|
| `level`  | yes      | int                               | The exact entity level to match. `-1` never matches.         |
| `target` | yes      | [Entity target](./intro#entity-targets) | Which entity to check.                                 |
