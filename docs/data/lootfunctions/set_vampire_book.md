---
sidebar_position: 2
title: Set Vampire Book
---

`vampirism:set_vampire_book` fills a vampire book item with book content chosen at random from the
given tag. If the `this_entity` loot parameter is an entity that provides a specific book (advanced
hunters / advanced vampires), that book is used instead; otherwise the tag is narrowed to the
entity's faction.

```json
{ "function": "vampirism:set_vampire_book", "tag": "vampirism:is_general" }
```

| Field        | Required | Type                                                            | Description                                                       |
|--------------|----------|-----------------------------------------------------------|-----------------------------------------------------------|
| `tag`        | yes      | [Resource location](https://minecraft.wiki/w/Resource_location) | A `vampirism:vampire_book` tag id (without `#`). The book is rolled from this tag. |
| `conditions` | no       | [Loot condition](../lootconditions/intro)[]                | Standard conditional-function conditions.                        |
