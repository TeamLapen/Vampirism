---
sidebar_position: 7
title: Lords
---

A *lord* is a player who has progressed beyond the normal faction levels. Lord levels are a second level
track that is enabled per faction as an [extension](./object-model#faction-extensions).

## Enabling lords for a faction

```java
FACTIONS.registerFaction("my_faction", props -> new PlayableFaction<>(props
        .lord(5)                              // enableLord() + max lord level 5, default titles
        // or: .lord(5, myTitleProvider)      // custom ILordTitleProvider
        ...));
```

`FactionProperties#lord(level)` enables the `ILordPlayer` extension and sets
`FactionDataComponents.MAX_LORD_LEVEL`; `lord(level, provider)` additionally stores a `LordTitles` table in
`FactionDataComponents.LORD_TITLES`. `enableLord()` alone only adds the extension.

## `ILordPlayer`

Per-player lord state (attachment `FactionAttachments.LORD_PLAYER`). Obtain it with
`FactionsApi.factionPlayerHandler(player).getPlayerLord()` or
`handler.getExtension(ILordPlayer.class)`.

| Method                              | Meaning                                                                      |
|-------------------------------------|------------------------------------------------------------------------------|
| `getLordLevel()` / `getMaxLordLevel()` | current / maximum lord level (`0` = not a lord)                             |
| `getMaxMinions()`                   | how many minions the lord may have                                           |
| `getLordTitle()` / `getLordTitleShort()` | title for the current level and gender (`@Nullable`)                      |
| `titleGender()` / `setTitleGender(...)` | `IPlayableFaction.TitleGender` (`UNKNOWN`, `MALE`, `FEMALE`); the boolean overload maps `true` to `FEMALE` |
| `updateMinionAttributes(boolean increasedStats)` | re-applies the minion stat bonuses                              |
| `resetLordTasks()`                  | makes lord tasks (`awards_lord_level` task tag) available again for the current lord level |

Lord levels are changed through a `FactionUpdate` containing a `LordLevelChange`, see the
[events](./events) for `PlayerFactionEvent`.

## Titles

* `ILordTitleProvider` – `getLordTitle(level, gender)` and `getShort(level, gender)`, both `@Nullable`.
* `LordTitles` – immutable tables (`level × gender → Component`) for the long and short title.
  * `LordTitles.provide(maxLevel, provider)` evaluates a provider for levels `1..maxLevel` and all genders.
  * `LordTitles.provideDefault(factionId, maxLevel)` uses translation keys built with
    `id.toLanguageKey("lord_title", "<male|female>.<level>")` and `….<level>.short` for the short form.
  * `get(level, gender)` / `getShort(level, gender)` look titles up; `LordTitles.EMPTY` has none.

## `ILordPlayerBuilder` / `ILordPlayerEntry`

`ILordPlayerBuilder#lordLevel(int)` (0 = the faction has no lords) and `#lordTitle(ILordTitleProvider)`
build an `ILordPlayerEntry` (`maxLevel()`, `lordTitleFunction()`). They belong to the older
`IFactionBuilder` style; with `FactionProperties` you normally use `lord(...)` instead.
