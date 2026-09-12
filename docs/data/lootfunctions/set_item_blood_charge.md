---
sidebar_position: 3
title: Charge Blood
---

`vampirism:set_item_blood_charge` charges an `IBloodChargeable` item (Blood Seeker, Blood Striker).

```json
{ "function": "vampirism:set_item_blood_charge", "charge": { "type": "minecraft:uniform", "min": 100, "max": 400 } }
```

| Field        | Required | Type                                                                | Description                                        |
|--------------|----------|----------------------------------------------------------------|----------------------------------------------|
| `charge`     | yes      | [Number provider](https://minecraft.wiki/w/Loot_table#Number_provider) | Blood charge in mB. A bare number is a constant. |
| `conditions` | no       | [Loot condition](../lootconditions/intro)[]                    | Standard conditional-function conditions.          |
