---
sidebar_position: 6
title: IMob Issue
---

### What is IMob
`IMob` is a Java interface that is used to mark entity classes as hostile in vanilla MC. This for example applies to zombies.  
Mods sometimes use this to filter hostile mobs. For example a modded turret might should all `IMob` creatures.

### What is the problem in Vampirism
Since it is an interface it has to be applied statically to the respective entity class.
This means a `VampireEntity` is either `IMob` (hostile) or not.
However, in Vampirism vampires are only hostile to non vampire players and hunters are only hostile towards vampire players.
So if marking them as `IMob` even your friendly neighborhood vampire hunter might be killed by your guarding turret.
If not marking them `IMob`, even hostile vampires attacking your base are not fought off.

### Current solution
All relevant entity classes exist in a non `IMob` version and an `IMob` implementing subclass. They are both registered as different entity types (e.g. `vampire` and `vampire_imob`).
The non `IMob` version is used as default (spawns and other).
Based on a config option it is replaced (create new, copy, insert into world, remove old) with an `IMob` version or vice versa.
There are three options.
- Always `IMob`
- Never `IMob`
- Smart

The smart option only works in SP and falls back to never `IMob` on dedicated servers.
It choose the type based on the vampire status of the hosting/SP player.
So far wee did not get any complaints about this behavior.

### Related issues
Issues from before the "smart" IMob conversion:
https://github.com/TeamLapen/Vampirism/issues/199
https://github.com/TeamLapen/Vampirism/issues/190
