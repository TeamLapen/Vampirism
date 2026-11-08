---
sidebar_position: 1
title: Control
---

Villages can be controlled by a faction to spread their control and have access to certain features.
The Village Totem is the controlling block for the village which can be interacted with by the faction members. The totem is part of [FactionApi](../../../api/intro), so other faction mods can use it as well.

## Occurrence of villages

Natural villages can generate with a Village Totem. The totem is either neutral (a *Fragile Village Totem Top* without faction) or, with a chance, already belongs to the vampire or hunter faction, so the village is initially controlled by that faction.  
A village without a faction is a **neutral village**.

## Totem requirements

A totem only works inside a village. If the village is too small, interacting with the totem tells you what is missing. A village needs at least:
- 4 beds
- 2 workstations
- 4 villagers (not required while the village is controlled or a raid is running)

Villages that generated naturally always count as villages. Totems can also be placed in other places as long as these requirements are fulfilled. If there is already another totem in the village the totem does not work ("This totem doesn't work. Search for another one").

## Raids

The faction of a village can be changed by raiding it. Raids can be triggered by 3 causes:
1. Player initiated raids
2. Random raids
3. Bad Omen raids

A raid cannot be started while another raid is running at the totem, against a village of your own faction, or on peaceful difficulty (random and Bad Omen raids). A raid is aborted after 20 minutes (24000 ticks).

### Player initiated raids

Any player of a faction that is able to raid (e.g. vampires and hunters) that has a different faction than the village can right-click on the totem to initiate a raid of the player's faction.  
In Player initiated raids the defending faction will be twice as strong as the attacking faction.

### Random raids

Random raids can occur at any time (after at least 10 minutes without a raid, not on peaceful), but the attacking faction will be weak in comparison to the defending faction.  
In Random raids the defending faction will be twice as strong as the attacking faction.

### Bad Omen raids

Just like with Illager raids, Faction raids can be triggered by a player entering the village with a Bad Omen effect of a different faction.
For each faction there exists a Bad Omen effect which can be obtained by killing a faction member of that faction that carries a Banner of that faction. Killing another Banner holder while having the Bad Omen effect increases the level of the Bad Omen effect (up to level 5).
Just like the vanilla Bad Omen effect, the strength of the raid depends on the level of the Bad Omen effect.

| Bad Omen level | Attacking faction strength compared to defending faction |
|----------------|-----------------------------------------------------------|
| 1              | 1.25x                                                     |
| 2              | ~1.7x                                                     |
| 3              | ~2.1x                                                     |
| 4              | ~2.6x                                                     |
| 5              | 3x                                                        |

## Raiding

A faction raid is seperated in two phases:
1. Gathering phase
2. Final phase

A boss bar shows the progress and the remaining participants.

### Gathering phase

In the gathering phase both the attacking and defending faction gather their forces depending on the raid's strength ratio. This will continuously spawn raiders and defenders. The phase lasts 80 seconds by default (server option `raidPhaseOneDuration`).

### Final phase

Once in the final phase there will spawn no new raiders or defenders. When one faction has no members left the village will be taken over by the other faction or will keep ist defending faction.

If the village is neutral there are no defenders, so the village is taken over by the attacking faction after the gathering phase.

## Neutralize villages

A village can only be neutralized by members of the faction that controls the village by destroying the Totem (not while a raid is running). The totem of a neutral village can be destroyed by anyone.


## Additional notes
- Hunter Trainer / Undercover Hunter Trainer are not counted as Raid participants
- ringing a bell as raid participant will let entities of the other faction glow. Just like with Illagers
- The totem respawns villagers (up to the number of beds, at most 30 by default).
- Mods can add more factions that can raid and be raided.
