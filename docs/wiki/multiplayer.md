---
sidebar_position: 7
title: Multiplayer
---

This mod should play well on multiplayer servers. Players can choose to join the vampire faction or the hunter faction or even mostly ignore this mod if they want.

### Permissions
Certain actions can be controlled via permission. See [Permissions](permissions.md).

### PVP
You can disable PVP within factions, so only players from different factions can hurt each other and the war between vampires and humans can take place organized. Enable `factionPvpOnlyBetweenFactions` in the `factionapi-server.toml` (PVP must be enabled in the `server.properties`). See [Configuration](configuration.md).

### Difficulty
Most hostile creatures appear in different levels.  
The strength of a spawned creature is affected by the level of nearby players. Around strong vampire players, strong hunters will spawn, but around normal humans (not hunter/not vampire) only weak vampires will appear.

### Commands
When using command blocks or similar for things you can use the included [entity selectors](commands/entity_selector/faction) to filter between hunters/vampire, levels etc. in most commands.  
There are also scoreboard criteria that can be used to find out the vampire/hunter/faction level of a player (`vampirism:vampire`, `vampirism:hunter` and `factionapi:faction`)

### Utility
#### Sundamage
For common places (e.g. spawn) you can place (creative/cheat only) sunscreen beacons, so vampires can visit them during the day without taking sun damage. The radius is configurable (`sunscreenBeaconRadius`) and the beacons can only be mined in survival if `sunscreenBeaconMineable` is enabled in the `vampirism-server.toml`.

#### Infection
Also interesting might be the config options to allow/deny players from infecting other human players with vampirism (`playerCanInfectPlayers`, hunters are immune anyway due to their garlic injection), to disable the infection by vampire fangs (`fangInfection`) and by vampire mobs (`mobBiteInfection`).

#### Chat
Player names are colored by faction and get their lord title as prefix in chat. Both can be disabled (`factionColorInChat`, `factionLordPrefixInChat` in the `factionapi-server.toml`).

#### Dracula
Players can enter the Velmorra dimension through portals to fight Dracula, see [Dracula & Velmorra](dracula/intro.md).
