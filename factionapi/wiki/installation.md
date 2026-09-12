---
sidebar_position: 3
title: Installation
---

FactionApi is a normal NeoForge mod jar – drop it in your `mods` folder.

## Requirements

* **NeoForge** for the matching Minecraft version.
* No other mods. FactionApi has no mod dependencies beyond NeoForge and Minecraft.

## Getting the jar

Download the build that matches your Minecraft/NeoForge version from **Modrinth** (project
`factionapi`). Pick the same Minecraft version as the rest of your pack.

FactionApi is **not** bundled inside Vampirism (no jar-in-jar), so when you install Vampirism you have
to add FactionApi as a separate file. Vampirism lists it as a `required` dependency and will refuse to
load without it.

## Versioning

Release versions look like `<minecraft_version>-<factionapi_version>`, e.g. `26.1.2-1.0.0`:

* the first part is the targeted Minecraft version,
* the second part (`1.0.0`, …) is FactionApi's own semantic version.

Mods that depend on FactionApi declare a minimum of the second part; keep FactionApi at or above the
version your other mods ask for.

## Configuration

FactionApi has no gameplay config of its own. On its own it adds the faction framework but no
playable factions – those come from Vampirism or another faction mod. Installing FactionApi alone
does nothing player-visible.

## Servers

FactionApi must be installed on **both** client and server (`side = "BOTH"`).
