---
sidebar_position: 6
title: Tasks
---

Tasks are given to faction player to collect items or achieve certain goals. They are given by a [Faction Representative](/docs/wiki/content/entities/village_representative)


## Location
All task files need to be located in the folder `data/<modid>/vampirism/tasks/`
## Id
Every task has an id that is determined by the files' name and directory. E.g., the task in the file `data/vampirism/vampirism/tasks/hunter/hunter_lord2.json` will have the id `vampirism:hunter/hunter_lord2`

## Schema

```json title="taskname.json"
{
  "requirements": {
    "requirements": []
  },
  "rewards": {},
  "title": {},
  "description": {},
  "unlocker": []
}
```
- `requirements`: The requirements to finish the task.
- `rewards`: The rewards for finishing the task.
- `title`: The title of the task. Component

| Field        | Type                                                                   | Description                          |
|--------------|------------------------------------------------------------------------|--------------------------------------|
| requirements | [Requirements](#requirements)                                          | The requirements to finish the task. |
| rewards      | [Rewards](#rewards)                                                    | The reward for finishing the task.   |
| unlocker     | [Unlocker](#unlocker)                                                  | The requirements to accept the task. |
| title        | [Component](https://minecraft.fandom.com/wiki/Raw_JSON_text_format)    | The title of the task.               |
| description  | [Component](https://minecraft.fandom.com/wiki/Raw_JSON_text_format)    | The description of the task.         |

### Requirements

#### Entity Requirement
This requirement checks if the player has killed a certain amount of a specific entity.
```json
{
  "type": "vampirism:entity",
  "amount": <amount>,
  "description": <description>,
  "entityType": "<entity_type>",
  "id": "<id>"
}
```

| Field       | Type                                                                    | Description                                     |
|-------------|-------------------------------------------------------------------------|-------------------------------------------------|
| amount      | int                                                                     | the amount of entities that need to be killed   |
| description | [Component](https://minecraft.fandom.com/wiki/Raw_JSON_text_format)     | the description of the requirement              |
| entity_type | [ResourceLocation](https://minecraft.fandom.com/wiki/Resource_location) | id of the entity                                |
| id          | [ResourceLocation](https://minecraft.fandom.com/wiki/Resource_location) | unique id of the requirement inside of the task |



#### Entity Type Requirement
This requirement checks if the player has killed a certain amount of entity belonging to an entity tag.
```json
{
  "type": "vampirism:entity_type",
  "amount": <amount>,
  "description": <description>,
  "entityType": "<entity_type>",
  "id": "<id>"
}
```

| Field       | Type                                                                    | Description                                     |
|-------------|-------------------------------------------------------------------------|-------------------------------------------------|
| amount      | int                                                                     | the amount of entities that need to be killed   |
| description | [Component](https://minecraft.fandom.com/wiki/Raw_JSON_text_format)     | the description of the requirement              |
| entity_type | [ResourceLocation](https://minecraft.fandom.com/wiki/Resource_location) | id of the entity tag                            |
| id          | [ResourceLocation](https://minecraft.fandom.com/wiki/Resource_location) | unique id of the requirement inside of the task |



#### Stat Requirement
This requirement checks if a given stat was increased by a defined number.

```json
{
  "type": "vampirism:stat",
  "amount": <amount>,
  "description": <description>,
  "id": "<id>",
  "stat": "<stat>"
}
```

| Field       | Type                                                                    | Description                                         |
|-------------|-------------------------------------------------------------------------|-----------------------------------------------------|
| amount      | int                                                                     | the amount with which the stat need to be increased |
| description | [Component](https://minecraft.fandom.com/wiki/Raw_JSON_text_format)     | the description of the requirement                  |
| id          | [ResourceLocation](https://minecraft.fandom.com/wiki/Resource_location) | unique id of the requirement inside of the task     |
| stat        | [ResourceLocation](https://minecraft.fandom.com/wiki/Resource_location) | the id of the stat that should be checked           |



#### Item Requirement
This requirement checks if the player has the itemstack in their inventory.
```json
{
  "type": "vampirism:item",
  "description": <description>,
  "id": "<id>",
  "item": <item>
}
```

| Field       | Type                                                                       | Description                                               |
|-------------|----------------------------------------------------------------------------|-----------------------------------------------------------|
| description | [Component](https://minecraft.fandom.com/wiki/Raw_JSON_text_format)        | the description of the requirement                        |
| id          | [ResourceLocation](https://minecraft.fandom.com/wiki/Resource_location)    | unique id of the requirement inside of the task           |
| item        | [Item](https://minecraft.fandom.com/wiki/Tutorials/Command_NBT_tags#Items) | the item that should be checked. This includes the amount |



#### Boolean Requirement
The Boolean requirement checks a registered funtion, if the requirement is completed. This can only be registered using the api
```json
{
  "type": "vampirism:boolean",
  "description": <description>,
  "id": "<id>",
  "function": "<function>"
}
```

| Field       | Type                                                                    | Description                                              |
|-------------|-------------------------------------------------------------------------|----------------------------------------------------------|
| description | [Component](https://minecraft.fandom.com/wiki/Raw_JSON_text_format)     | the description of the requirement                       |
| id          | [ResourceLocation](https://minecraft.fandom.com/wiki/Resource_location) | unique id of the requirement inside of the task          |
| function    | [ResourceLocation](https://minecraft.fandom.com/wiki/Resource_location) | the id of the registered function that should be checked |

### Rewards
#### Item Reward
A simple item reward that will reward the player with a specific itemstack.

```json
{
  "type": "vampirism:item",
  "item": <item>
}
```

| Field | Type                                                                       | Description                                       |
|-------|----------------------------------------------------------------------------|---------------------------------------------------|
| item  | [Item](https://minecraft.fandom.com/wiki/Tutorials/Command_NBT_tags#Items) | the itemstack that will be rewarded to the player |


#### Refinement Item Reward
This is a special item Reward that rewards an accessory item to the player. When a parameter is not given, it will be randomly chosen from the available refinements.

```json
{
  "type": "vampirism:refinement_item",
  "faction": <faction>,
  "item": <item>,
  "rarity": <rarity>
}
```

| Field   | Optional | Type                                                                       | Description                                               |
|---------|----------|----------------------------------------------------------------------------|-----------------------------------------------------------|
| faction | x        | [Faction](reference#faction)                                               | the faction for which the accessory should be created     |
| item    | x        | [Item](https://minecraft.fandom.com/wiki/Tutorials/Command_NBT_tags#Items) | the accessory item that should be filled with refinements |
| rarity  | x        | [Rarity](reference#refinement-rarity)                                      | a specific rarity for the refinements.                    |
#### Lord Level Reward
The Lord Level Reward will reward the player with a specific lord level.
```json
{
  "type": "vampirism:lord_level",
  "description": <description>,
  "targetLevel": <targetLevel>
}
```

| Field       | Type                                                                    | Description                        |
|-------------|-------------------------------------------------------------------------|------------------------------------|
| description | [Component](https://minecraft.fandom.com/wiki/Raw_JSON_text_format)     | the description of the requirement |
| targetLevel | int                                                                     | the level that should be awarded   |
#### Action Reward

The action reward will execute a specific registered action upon completion.

```json
{
  "type": "vampirism:consumer",
  "description": <description>,
  "consumer": "<action>"
}
```

| Field       | Type                                                                    | Description                                               |
|-------------|-------------------------------------------------------------------------|-----------------------------------------------------------|
| description | [Component](https://minecraft.fandom.com/wiki/Raw_JSON_text_format)     | the description of the requirement                        |
| consumer    | [ResourceLocation](https://minecraft.fandom.com/wiki/Resource_location) | the id of the registered function that should be executed |

### Unlocker
#### Level Unlocker

```json
{
  "type": "vampirism:level",
  "reqLevel": <minLevel>,
  "maxLevel": <maxLevel>
}
```

| Field    | Required | Type | Description                                           |
|----------|----------|------|-------------------------------------------------------|
| reqLevel | x        | int  | the minimum level that is required to unlock the task |
| maxLevel |          | int  | the maximum level that allows this task               |
#### Lord Level Unlocker

```json
{
  "type": "vampirism:lord_level",
  "exact": true,
  "reqLordLevel": <lord-level>
}
```

| Field      | Required | Type | Description                                             |
|------------|----------|------|---------------------------------------------------------|
| exact      | x        | bool | if the lord level should be exactly the given level     |
| lord-level | x        | int  | the lord level to unlock this task, or the minium level |
#### Parent Unlocker

```json
{
  "type": "vampirism:parent",
  "parent": "<task-id>"
}
```

| Field  | Type                                                                    | Description                                                                   |
|--------|-------------------------------------------------------------------------|-------------------------------------------------------------------------------|
| parent | [ResourceLocation](https://minecraft.fandom.com/wiki/Resource_location) | the id of the task that needs to be completed before this task can be started |
## Example

```json
{
  "requirements": {
    "requirements": [
      {
        "type": "vampirism:stat",
        "amount": 6,
        "description": {
          "translate": "stat.vampirism.capture_village"
        },
        "id": "vampirism:capture_village",
        "stat": "vampirism:capture_village"
      },
      {
        "type": "vampirism:item",
        "description": {
          "translate": "item.minecraft.gold_ingot"
        },
        "id": "minecraft:gold_ingot",
        "item": {
          "Count": 64,
          "id": "minecraft:gold_ingot"
        }
      },
      {
        "type": "vampirism:entity_type",
        "amount": 100,
        "description": {
          "translate": "entity_tag.vampirism.vampire"
        },
        "entityType": "vampirism:vampire",
        "id": "vampirism:vampire"
      }
    ]
  },
  "rewards": {
    "type": "vampirism:lord_level",
    "description": {
      "translate": "task_reward.vampirism.lord_level_reward",
      "with": [
        "5"
      ]
    },
    "targetLevel": 5
  },
  "title": {
    "translate": "task.vampirism.hunter_lord5"
  },
  "unlocker": [
    {
      "type": "vampirism:lord_level",
      "exact": true,
      "reqLordLevel": 4
    }
  ]
}
```