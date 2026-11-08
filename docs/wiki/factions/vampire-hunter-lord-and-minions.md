---
sidebar_position: 5
title: Lord & Minions
---

Once your reached the highest level of your faction, you can be make yourself a name and become a vampire lord or hunter lord respectively.  
As a lord you are able to obtain and control minions.
To become a lord you have to fulfill a certain task for your faction. You can accept and complete it at any vampire/hunter representative.

### Ranks

| Level | Hunter Title | Vampire Title   |
|-------|--------------|-----------------|
| 1     | Lieutenant   | Baron(ess)      |
| 2     | Captain      | Viscount(ess)   |
| 3     | Major        | Count(ess)      |
| 4     | Colonel      | Duke/Duchess    |
| 5     | Brigadier    | Prince(ss)      |

Every title also has a short form (e.g. Ltn, Brn).  
The gender used is determined based on your skin type (steve -> male, alex -> female), but this can be changed using [`/vampirism title-gender <true/false>`](../commands/title_gender).

#### Special ranks

Above the lord levels there are two special ranks, one per faction:
- **Dracula** (vampire), see [Dracula](../dracula/intro)
- **Marshall** (hunter)

Both have their own skill tree with its own points (Dracula marks / research points).

### Leveling

To become a lord or to level up as lord you need to complete special tasks for Village Representatives.  
Each lord level gives you [skill points](./skills#skill-points) for the lord skills. The [Better Minions](./skills#lord-skills) skills improve the stats of your minions.

### Minions
Minions have an inventory that can be used to store items but also to equip the minion with weapons and armor. 
Only the lord can open a minion's menu, customise it and give it commands.  
You can have a limited number of minions: by default one per lord level (server option `minionPerLordLevel`).

Minions will stay in the world even when you disconnect from the server or change the dimension. 
If a minion dies or is hurt severely, it needs some time to recover (by default 220 seconds, the *Minion Recovery* skill reduces this). Once recovered you can bring it back using the minion radial menu (*Respawn dead*).

#### Vampire
As a lord you can bind a vampire NPC to you by right-clicking it with a vampire minion binding item. However, many vampires are too strong to be controlled by you, so you have to look for a weak one.
You can improve the capabilities of your minion using vampire minion upgrades.
#### Hunter
As a hunter lord you can recruit hunter NPCs by giving them basic hunter minion equipment. However, not all hunters are willing to serve somebody, so you have to look for weaker ones.
You can upgrade their equipment and improve their capabilities using hunter minion upgrades.

#### Minion stats
Each minion has a level (up to 6). Every upgrade item raises the level and you then choose in which area the minion should improve: each stat point is spent on one of the stats.

| Stat          | Max. level | Vampire | Hunter |
|---------------|------------|---------|--------|
| Health        | 3          | ✔       | ✔      |
| Attack damage | 3          | ✔       | ✔      |
| Speed         | 2          | ✔       | ✔      |
| Inventory size| 2          | ✔       | ✔      |
| Resource efficiency | 2    |         | ✔      |

With the *Better Minions* skill the bonus of the stats is increased by 20%.  
The stats can be viewed and reset (consumes a Potion of Oblivion) in the minion's stats screen.

#### Minion screen
Right-click a minion to open its control screen. It shows the minion's inventory, its current task and lets you
- pick a task for this minion,
- toggle *Ignore global commands* (the minion then ignores commands given to all minions through the radial menu),
- open the appearance screen (name, skin type, hat type, or use your own skin),
- open the stats screen.

#### Controlling minions
You can also give commands through the **Minion Radial Menu**. Its key (*Open Minion Radial Menu*) is unbound by default. Alternatively open the action radial menu (`R`) and press `R` again to switch between both menus.  
Select a task with the mouse. The order of the tasks can be changed in the *Edit Minion Tasks* screen of the faction menu.

##### Tasks
- **Stay here**
- **Follow** (default)
- **Defend Lord** (default)
- **Defend area**
- **Call single** / **Call all**: call one (you select it) / all minions to your position. Minions that are still recovering cannot be called
- **Respawn dead**: bring back minions that currently have no entity in the world (e.g. after they recovered)
- **Collect resources**: requires the lord skill *Blood Collection* (vampire) / *Supply Collection* (hunter)
- **Collect blood** (vampires only)
- **Collect wood**, **Collect ores**, **Catch fish**, **Gather plants**, **Hunt creatures** (mob loot): require the *Resource Diversity* skill

Better tools yield better resources. Collected items are stored in the minion's inventory; if they repeatedly do not fit anymore, the minion finishes the task.
