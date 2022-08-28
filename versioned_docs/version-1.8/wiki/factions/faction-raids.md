---
sidebar_position: 6
title: Village
---

Villages can be controlled by a faction to spread their control and have access to certain features.

Villages are either controlled by a faction through world generation, raided by player or by random raids.

### Benefits
#### Village Profession
Every faction has a villager profession that has faction specific trades. The Job site block for these professions is always the [Totem Top](../content/blocks#village-totem-top-base) Block of the controlling faction.

#### Faction Representative
Every Faction has a Faction Representative where you can accept and submit tasks.

If you can't find a Representative or the Representative is killed they re-/spawn after some time. (The more Villager the village has the faster)

Noteworthy is that all non-unique tasks are bound the faction Representative. That means the if the Representative is killed you can not hand in the completed tasks of this Representative.

## Faction Raids

To overtake a village it must be raided. Raids can be triggered by different causes, but they have different strength ratios.

Winning a raid as defender rewards a Hero of the Village effect
### Procedure
Raids have two different states.
##### Gather Phase
During the beginning of the Raid both controlling and capturing faction gather their forces depending on the raid's strength ratio.
Strength rations (attacking:defending):
- Player raid 1 : 2
- Random raid 1 : 2
- Bad Omen raid 0.25 + 0.4375 * (badomen level - 1) : 1



When the village is neutral only attacking forces will gather.

##### Final Phase
(This Phase is skipped if the village is neutral)

Once all forces have gathered each faction attacks all opposing members until one faction has no member left.
Then the village is taken over.

### Raid Types
##### Player Raids
If a village is not controlled by the players faction they can right click on the totem to initiate a raid of the player's faction.
##### AI raids
###### Bad Omen
If a player of the controlling faction enters the village with a Bad Omen effect of a different faction an AI raid is triggered which strength depends on the level of the Bad Omen effect.
###### Random
AI Raids can randomly occur, but are weak.

### Additional Notes
- Hunter Trainer / Undercover Hunter Trainer are not counted as Raid participants
- ringing a bell as raid participant will let entities of the other faction glow. Just like with Illagers
