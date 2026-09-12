---
sidebar_position: 3
title: Has Applied Oil
---

`vampirism:with_oil_item` checks the `tool` loot parameter: it matches when that item has the given
oil applied.

```json
{ "condition": "vampirism:with_oil_item", "oil": "vampirism:smelt" }
```

| Field | Required | Type                                                            | Description                              |
|-------|----------|-------------------------------------------------------------|--------------------------------------|
| `oil` | yes      | [Resource location](https://minecraft.wiki/w/Resource_location) | Id of the oil (`vampirism:oils` registry). |
