---
sidebar_position: 2
title: Depending on Vampirism
---

Vampirism is a **NeoForge** mod built with
[ModDevGradle](https://github.com/neoforged/ModDevGradle) (`net.neoforged.moddev`). The snippets
below assume the same.

## 1. Repository

```groovy
repositories {
    maven {
        name = "Maxanier Releases"
        url = "https://maven.maxanier.de/releases"
        content { includeGroupAndSubgroups "de.teamlapen" }
    }
    // pre-releases
    maven {
        name = "Maxanier Snapshots"
        url = "https://maven.maxanier.de/snapshots"
        content { includeGroupAndSubgroups "de.teamlapen" }
    }
}
```

Also on the Modrinth maven (`https://api.modrinth.com/maven/`, `maven.modrinth:vampirism:<version-id>`).

## 2. Dependency

Two artifacts under `de.teamlapen.vampirism`, both versioned `<minecraft_version>-<mod_version>`
(e.g. `26.1.2-2.0.0`):

| Coordinate                               | Contents                                                                 | Use when |
|------------------------------------------|-----------------------------------------------------------------------|----------|
| `de.teamlapen.vampirism:Vampirism`       | The full mod – runtime plus published access transformers, interface-injection data and enum extensions. | Your mod calls into Vampirism / you run the game with it. |
| `de.teamlapen.vampirism:Vampirism-api`   | API classes only (`de.teamlapen.vampirism.api`).                       | Compile-only builds that never need the mod present. |

Vampirism publishes access transformers and interface-injection data, so depend on the full jar
wrapped with the ModDevGradle helpers:

```groovy
def vampirism_version = "26.1.2-2.0.0"

dependencies {
    implementation(
        accessTransformers(
            interfaceInjectionData("de.teamlapen.vampirism:Vampirism:${vampirism_version}")
        )
    )
}
```

* Use `api(...)` instead of `implementation(...)` if Vampirism types appear in your own public API.
* Compile-only against the interfaces: `compileOnly "de.teamlapen.vampirism:Vampirism-api:${vampirism_version}"`.
* Vampirism **requires [FactionApi](/factionapi/wiki/gradle-setup)** at runtime; a full-mod dependency
  pulls it transitively, but declare it explicitly if you also use FactionApi's API directly.

## 3. Mod dependency

```toml
[[dependencies.yourmodid]]
modId = "vampirism"
type = "required"          # or "optional"
versionRange = "[2.0.0,)"  # minimum Vampirism (second version part)
ordering = "AFTER"
side = "BOTH"
```

## Mixins

Vampirism uses Mixins. With ModDevGradle no refmap remapping configuration is required – the old
`mixin.env.remapRefMap` / `refMapRemappingFile` run properties from the ForgeGradle era are obsolete.

## Version list

[Maven listing](https://maven.maxanier.de/releases/de/teamlapen/vampirism/) ·
[Modrinth](https://modrinth.com/mod/vampirism/versions) ·
[CurseForge](https://www.curseforge.com/minecraft/mc-mods/vampirism-become-a-vampire/files)
