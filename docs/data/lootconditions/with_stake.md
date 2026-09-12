---
sidebar_position: 4
title: Holding Stake
---

`vampirism:with_stake` matches when the targeted entity is a player holding a stake item in their main
hand.

```json
{ "condition": "vampirism:with_stake", "target": "attacking_player" }
```

| Field    | Required | Type                                | Description                                     |
|----------|----------|-----------------------------------|-------------------------------------------|
| `target` | yes      | [Entity target](./intro#entity-targets) | Which entity to check.                 |
