---
sidebar_position: 5
title: Tasks
---

Tasks (the quests handed out by a [Faction Representative](../wiki/content/entities/village_representative))
are **not a Vampirism system** – they are provided by **FactionApi**. Vampirism only ships task
content.

## Format

The full JSON format – requirements, rewards, unlockers, the file location
(`data/<namespace>/factionapi/tasks/…`) and the JSON schema – is documented on the FactionApi site:

👉 **[FactionApi → Data → Tasks](/factionapi/data/tasks)**

That page also covers the [task tags](/factionapi/data/tasks#task-assignment) that control which board
and faction a task appears on.

## Vampirism specifics

* **Faction task tags.** Vampirism registers a per-faction task tag for each faction, so a task tagged
  `factionapi:has_faction` is only offered to that faction when it is also listed in:
  * `vampirism:has_faction/vampire` – vampire tasks
  * `vampirism:has_faction/hunter` – hunter tasks
  Put your task id in `data/<yourpack>/tags/factionapi/tasks/has_faction/vampire.json` (or `hunter`)
  **and** in `factionapi:has_faction` to attach it to a Vampirism faction.
* **Task masters.** Vampire and Hunter Representatives are the task-board entities for their faction.
* **Built-in tasks.** Vampirism's own tasks live in
  [`data/vampirism/factionapi/tasks/`](https://github.com/TeamLapen/Vampirism/tree/dev/projects/vampirism/src/generated/resources/data/vampirism/factionapi/tasks)
  and are a good reference; override one by placing a file with the same id in your pack.
* **Custom requirement / reward / unlocker types** must be registered in code through FactionApi – see
  [FactionApi → API → Registering content](/factionapi/api/registering-content).
