---
sidebar_position: 4
title: Depending on FactionApi
---

This page shows how to add FactionApi to a **NeoForge mod built with
[ModDevGradle](https://github.com/neoforged/ModDevGradle)** (`net.neoforged.moddev`), which is the
setup Vampirism itself uses.

## 1. Add the Maven repository

FactionApi is published to the Maxanier Maven under the `de.teamlapen` group:

```groovy
repositories {
    maven {
        name = "Maxanier Releases"
        url = "https://maven.maxanier.de/releases"
        content { includeGroupAndSubgroups "de.teamlapen" }
    }
    // optional: pre-release builds
    maven {
        name = "Maxanier Snapshots"
        url = "https://maven.maxanier.de/snapshots"
        content { includeGroupAndSubgroups "de.teamlapen" }
    }
}
```

It is also mirrored on the Modrinth Maven (`https://api.modrinth.com/maven/`, coordinate
`maven.modrinth:factionapi:<version-id>`).

## 2. Add the dependency

There are two artifacts, both at version `<minecraft_version>-<factionapi_version>` (e.g.
`26.1.2-1.0.0`):

| Coordinate                                   | Contents                                                     | Use when                                                              |
|----------------------------------------------|-------------------------------------------------------------|--------------------------------------------------------------------|
| `de.teamlapen.faction:FactionApi`            | The full mod (runtime + published access transformers + interface-injection data). | You want to run the game with it / your mod calls into it at runtime. |
| `de.teamlapen.faction:FactionApi-api`        | API classes only (`de.teamlapen.faction.api`).             | Compile-only builds that never need the mod present.                 |

### Recommended: full mod, wrapped

FactionApi ships published **access transformers** and **interface-injection data**. ModDevGradle
exposes helper functions that pull those into your compile classpath so the widened members and
injected interfaces resolve. Mirror how Vampirism declares it:

```groovy
def factionapi_version = "26.1.2-1.0.0" // match your Minecraft version

dependencies {
    implementation(
        accessTransformers(
            interfaceInjectionData("de.teamlapen.faction:FactionApi:${factionapi_version}")
        )
    )
}
```

* Use `api(...)` instead of `implementation(...)` if FactionApi types appear in **your** mod's public
  API.
* If you only compile against interfaces and never touch a widened vanilla member, the plain
  `FactionApi-api` artifact without the wrappers is enough:

  ```groovy
  compileOnly "de.teamlapen.faction:FactionApi-api:${factionapi_version}"
  ```

## 3. Declare the mod dependency

In `META-INF/neoforge.mods.toml`:

```toml
[[dependencies.yourmodid]]
modId = "factionapi"
type = "required"           # or "optional" if FactionApi integration is opt-in
versionRange = "[1.0.0,)"   # minimum FactionApi (second version part)
ordering = "AFTER"          # load after FactionApi so its registries exist
side = "BOTH"
```

## 4. Next steps

* [API documentation](../api/intro) – registries, builders, events.
* [Data pack documentation](../data/intro) – tasks and skill trees.

Vampirism's own build is the reference implementation: see
[`projects/vampirism/dependencies.gradle`](https://github.com/TeamLapen/Vampirism/blob/dev/projects/vampirism/dependencies.gradle).
