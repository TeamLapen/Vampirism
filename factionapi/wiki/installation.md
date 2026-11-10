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
version your other mods ask for. Vampirism currently requires a `1.0.0-beta.1` build or newer, so pre-release
builds may be needed until the final `1.0.0` is available.

## Configuration

On its own FactionApi adds the faction framework but no playable factions – those come from Vampirism
or another faction mod. Installing FactionApi alone gives players no faction to join (the only built-in faction is
the neutral "Human" one every player starts in).

FactionApi does have its own options (village, raid, faction, minion and task settings on the server; HUD
options on the client). They apply to every faction mod using it and are listed in
[Commands, permissions & config](./commands-and-config#config).

## Servers

FactionApi must be installed on **both** client and server (`side = "BOTH"`).
