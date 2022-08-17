---
sidebar_position: 3
title: Blood Values
---

Blood values can be configured for items, entities and fluids.

## Items
Item blood values are use in the grinder to determine the amount of impure blood they produce.

#### Location
Item blood values are placed in the `data/<modid>/vampirism_blood_values/items` folder.

#### Schema
```text title="<modid>.txt"
multiplier=100

<item-id>=<value>
<item-id>=<value>
<item-id>=<value>
<item-id>=<value>
```
- `multiplier`: The multiplier for the specified blood values. The pre-specified value is 100 (and no need to override)
- `<item-id>`: The registry id of the item.
- `<value>`: The integer blood value in mb.

## Entities
Entity blood values are used for the amount of blood an entity will have. This is used for drinking from entities or to mark them as undrinkable from.

#### Location
Entity blood values are placed in the `data/<modid>/vampirism_blood_values/entities` folder.

#### Schema
```text title="<modid>.txt"
multiplier=10

<entity-id>=<value>
<entity-id>=<value>
<entity-id>=<value>
<entity-id>=<value>
```
- `multiplier`: The multiplier for the specified blood values. It is decided by 10 before multiplying. The pre-specified value is 10 (and no need to override)
- `<entity-id>`: The registry id of the entity.
- `<value>`: The integer blood value.

## Fluids
Fluid blood values are used by the [Blood Grinder](../wiki/content/fluids.md#blood) to determine how the conversion rate of a fluid is to blood. For [Impure Blood](../wiki/content/fluids.md#impure-blood) this is 0.75.

#### Location
Fluid blood values are placed in the `data/<modid>/vampirism_blood_values/fluids` folder.

### Schema

```text title="<modid>.txt"
divider=100

<fluidid>=<value>
<fluidid>=<value>
<fluidid>=<value>
<fluidid>=<value>
```
- `divider`: The divider for the conversion rate that is applied to all values after loading. The pre-specified value is 100 (and no need to override)
- `<fluidid>`: The registry name of the fluid.
- `<value>`: The conversion rate of the fluid to blood. As integer percentage considering the divider.