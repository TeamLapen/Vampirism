---
sidebar_position: 2
title: Village Life
---

## Faction Behavior

Once a village is controlled by a faction, all workstations and beds will be claimed by the faction. This means that only faction members can destroy them (server admins can allow everybody to destroy them with the FactionApi option `villageAllowPoiDestruction`).

## Villager

### Villager Professions

Each faction has a new Profession that has trades relevant for the respective faction. As workstation for these professions acts the Totem Top when it is controlled by the faction.

- **Vampire Expert** (vampire villages): trades human hearts, pure blood, vampire cloaks, coffins, blood-infused iron, Heartseeker/Heartstriker and maps to crypts. Buys Vampire Orchids.
- **Hunter Expert** (hunter villages): trades garlic, vampire fangs, blood bottles, a map to a crypt and, for vampire soul orbs, hunter armor, quarrels and weapons.
- **Priest** (workstation: Altar of Cleansing): trades candles, garlic, pure salt, honey, holy water, crucifixes and candelabras.

Some vanilla professions get additional trades (e.g. farmers sell garlic, butchers human hearts and masons dark stone), and the wandering trader sells some Vampirism plants/saplings.

### Faction Specifics
#### Neutral Village

A village without a faction has no faction profession, villagers are unchanged. Hunter Trainers in neutral and vampire villages are undercover (see below).

#### Vampire Village

In a Vampire Village, all villager will slowly be converted to Vampire Villagers. Some Villager may be turned/infected upon capturing the village, all others will be infected by Vampires or Vampire Villager.

Vampire Villager will change their day/night cycle to be active during the night and sleep during the day. They also trade blood bottles for emeralds and exchange human hearts and emeralds.

When a vampire village is captured by hunters, converted villagers are cured or replaced.

#### Hunter Village

In a Hunter Village, all villager are protected against vampire bites by poisonous blood. In a raid some will take up arms and fight with pitchforks.

### Hunter Trainer

Villages can contain a Hunter Trainer house with a Hunter Trainer. If the village is not controlled by hunters, the trainer is an **Undercover Hunter Trainer** and does not train you. Capture the village for the hunters to get the real trainer.  
Neutral players (humans) that talk to the Hunter Trainer are told that they must first inject themselves with garlic using the trainer's injection chair to become a hunter.

## Faction Representative

In a controlled village a Faction Representative will eventually spawn. The representative is not a Villager, but a task giver for interested faction player.
He gives out tasks that require the player to complete different actions or collect items. The representative is also relevant in becoming a [Lord](../vampire-hunter-lord-and-minions).


It is important to note that the normal task are bound to the representative, which means that if the representative dies, the tasks can no longer be submitted even if the task is completed. This does not apply to unique tasks, which are available at all representatives of the faction.
