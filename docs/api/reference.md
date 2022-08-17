---
sidebar_position: 3
title: Reference
---

Besides the several interfaces for almost all of Vampirism's functionality, there are some classes that can be used to access Vampirism's functionality.

## [VampirismAPI](https://github.com/TeamLapen/Vampirism/blob/4bf2c73fb860a23de225edbae9c0b1c1ead3dd1a/src/api/java/de/teamlapen/vampirism/api/VampirismAPI.java)

The VampirismAPI class is the central access point for all kind of registries.

## [VReference](https://github.com/TeamLapen/Vampirism/blob/2c54569508455543a62f7ba292e5f389f132f402/src/api/java/de/teamlapen/vampirism/api/VReference.java)

Central location for all kind of static information/objects.
this includes e.g. the faction objects of vampires and hunters, attributes, DamageSources, constants ...

## Vampirism Forge Registries

Currently, Vampirism's custom forge registries are not accessible through the API. They are not declared in the API [ModRegistries](https://github.com/TeamLapen/Vampirism/blob/4bf2c73fb860a23de225edbae9c0b1c1ead3dd1a/src/main/java/de/teamlapen/vampirism/core/ModRegistries.java).  
But you can get them from the RegistryManager with `RegistryManager.ACTIVE.getRegistry(...)`. Either use the RegistryEntry class or the resource location of the registry.

Following registries are currently available:

| Name            | Resource Location           | Class                                                                                                                                                                                       |
|-----------------|-----------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Skills          | `vampirism:skills`          | [ISKill](https://github.com/TeamLapen/Vampirism/blob/4bf2c73fb860a23de225edbae9c0b1c1ead3dd1a/src/api/java/de/teamlapen/vampirism/api/entity/player/skills/ISkill.java)                     |
| Actions         | `vampirism:actions`         | [IAction](https://github.com/TeamLapen/Vampirism/blob/4bf2c73fb860a23de225edbae9c0b1c1ead3dd1a/src/api/java/de/teamlapen/vampirism/api/entity/player/actions/IAction.java)                  |
| Entity Actions  | `vampirism:entityactions`   | [IEntityAction](https://github.com/TeamLapen/Vampirism/blob/4bf2c73fb860a23de225edbae9c0b1c1ead3dd1a/src/api/java/de/teamlapen/vampirism/api/entity/actions/IEntityAction.java)             |
| Minion Tasks    | `vampirism:miniontasks`     | [IMinionTask](https://github.com/TeamLapen/Vampirism/blob/4bf2c73fb860a23de225edbae9c0b1c1ead3dd1a/src/api/java/de/teamlapen/vampirism/api/entity/minion/IMinionTask.java)                  |
| Tasks           | `vampirism:tasks`           | [Task](https://github.com/TeamLapen/Vampirism/blob/4bf2c73fb860a23de225edbae9c0b1c1ead3dd1a/src/api/java/de/teamlapen/vampirism/api/entity/player/task/Task.java)                           |
| Refinements     | `vampirism:refinement`      | [IRefinement](https://github.com/TeamLapen/Vampirism/blob/4bf2c73fb860a23de225edbae9c0b1c1ead3dd1a/src/api/java/de/teamlapen/vampirism/api/entity/player/refinement/IRefinement.java)       |
| Refinement Sets | `vampirism:refinement_set`  | [IRefinementSet](https://github.com/TeamLapen/Vampirism/blob/4bf2c73fb860a23de225edbae9c0b1c1ead3dd1a/src/api/java/de/teamlapen/vampirism/api/entity/player/refinement/IRefinementSet.java) |