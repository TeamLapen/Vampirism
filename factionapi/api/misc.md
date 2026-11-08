---
sidebar_position: 11
title: Misc
---

Smaller API pieces that do not fit the other pages.

## Description system

Items and blocks can show an extended description in their tooltip while FactionApi's "item description" key binding is held; otherwise a "hold … for info" hint is shown.

| Piece                          | Where                                  | What it does                                                                                  |
|--------------------------------|----------------------------------------|-----------------------------------------------------------------------------------------------|
| `IDescriptionProvider`         | API, on an `Item` or the `Block` of a `BlockItem` | `Object[] getDescriptionParameters()` – arguments for the translatable description text.   |
| `IBlockWithDescription`        | API, on a `Block`                      | `appendHoverText(stack, context, display, flag, tooltips)` – the block adds its own tooltip lines. |
| `factionapi:shift_description` | item component (`ShiftDescription`)    | Shows the description. With neither field set, the translation key `tooltip.<item namespace>.<item path>` (`Identifier#toLanguageKey("tooltip")`) is used and filled from the `IDescriptionProvider` if there is one. Fields: `component` *or* `formatableString` (a translation key). |
| `factionapi:block_description` | item component (`BlockDescription`, unit) | Marks a `BlockItem` whose block implements `IBlockWithDescription`; the block's `appendHoverText` is used. |

`ShiftDescription` and `BlockDescription` live in `de.teamlapen.faction.common.util` (module `FactionApi`,
not the API module). Items are configured with the injected `Item.Properties` methods
(see [interface injection](#interface-injection-and-access-transformers)): `factions$withShiftDescription()`,
`factions$withShiftDescription(Component)`, `factions$withShiftDescription(ShiftDescription)`,
`factions$withShiftDescriptionWithId(String)` – or set the component directly. Block items set
`.component(FactionDataComponents.BLOCK_DESCRIPTION, BlockDescription.INSTANCE)`. Skills and actions use
`SkillProperties#withDescription()` for their description text.

## Customization

* `ICustomizationHolder` – `int getEntityTextureType()`; `ICustomizationHolder.NONE` returns `0`. For entities
  that choose a texture variant.
* `ICustomizationOption<T>` – `Identifier id()` and `StreamCodec<? super RegistryFriendlyByteBuf, T> codec()`
  describe one customizable value. [`MinionAppearance`](./minions#minionappearancet) implements it.

## Disguise

`IDisguise` (returned by `IFactionPlayer#getDisguise()`) lets a faction player appear as another faction:
`actualFaction()`, `getViewedFaction(viewerFaction)` (what a viewer of the given faction sees),
`getViewedFaction(viewer, ignoreDisguise)`, `disguiseAs(faction)` (nullable), `unDisguise()`, `isDisguised()`.
`IDisguise.None` is the no-op implementation. [`IFactionPredicate`](./utilities#target-selection-ifactionpredicate)
uses it unless `ignoreDisguise()` is set.

## Neutral player

`INeutralPlayer` is the `IFactionPlayer` of players without faction (attachment
`FactionAttachments.NEUTRAL_PLAYER`): faction `Factions.NEUTRAL`, level `0`, max level `0`, can always leave,
never disguised.

## Faction food

`IFactionFoodBehavior` (`factionapi:food_behaviour`) is a functional interface
`apply(level, entity, stack, consumable, foodProperties)` that defines what eating does. The item component
`factionapi:faction_food` (`FactionFoodList`) holds a `defaultFood` and a list of entries
`{ faction: <faction tag>, foodProperties, behaviour }`. When an entity eats the item, all entries whose
faction tag matches the entity's faction apply their behaviour with their own food properties; if none
matches the default food is applied with the built-in `factionapi:default` behaviour (plays the eat/burp
sounds and adds nutrition and saturation to players). Items are set up with the injected
`Item.Properties#factions$factionFood(list[, consumable])`.

## Sync API (`de.teamlapen.sync.api`)

Interfaces for attachments that are saved and synced to clients.

| Type                      | Purpose                                                                                                   |
|---------------------------|-----------------------------------------------------------------------------------------------------------|
| `ISyncable`               | `sync()`, `serializeFullUpdate(ValueOutput)`, `serializeUpdate(ValueOutput)` (defaults to full), `deserializeUpdate(ValueInput)`. Deserializers must not assume all data is present. |
| `IAttachmentSync`         | `ValueIOSerializable` + `ISyncable` + `IEntity` + `MutableDataComponentHolder` with `getType()` (its `AttachmentType`). Base of FactionApi's player attachments. |
| `MutableDataComponentMap` | A `DataComponentMap` with `set` / `remove` (type or supplier) and `set(Supplier<DataComponentType<Unit>>, boolean)` for flag components. `IMinionData` extends it. |

## Attachments

`FactionAttachments` (API) holds lookup holders; the implementation registers the types.

| Key (`factionapi:`)       | Holder in API                  | Type                           |
|---------------------------|--------------------------------|--------------------------------|
| `faction_player_handler`  | `FACTION_PLAYER_HANDLER`       | `IFactionPlayerHandler`        |
| `neutral_player`          | `NEUTRAL_PLAYER`               | `INeutralPlayer`               |
| `task_manager`            | `TASK_MANAGER`                 | `ITaskManager`                 |
| `refinement_handler`      | `REFINEMENT_HANDLER`           | `IRefinementHandler`           |
| `lord_player`             | `LORD_PLAYER`                  | `ILordPlayer`                  |
| `damage_handler`          | – (`Keys.DAMAGE_HANDLER` only) | implementation `LevelDamage`   |
| `minion_data`             | – (`Keys.MINION_DATA` only)    | implementation `EntitySyncHolder<MinionEntity, MinionData>` |

## Data component keys

`FactionDataComponents` (API) exposes lookup holders and the `Keys` ids. Faction, skill and minion task
components are *definition* components of registry entries (set through `FactionProperties`,
`SkillProperties`, `MinionTaskProperties`); the minion data keys are persisted per minion. The rows without
an API holder (all minion data keys, `shift_description`, `block_description`, `faction_food`) are only
registered by the implementation (`de.teamlapen.faction.common.core.FactionDataComponents`); the API exposes
their ids in `FactionDataComponents.Keys`.

**Faction (on `IFaction`)**

| Key (`factionapi:`) | Constant | Value type |
|---|---|---|
| `faction_color` | `FACTION_COLOR` | `Integer` |
| `chat_color` | `CHAT_COLOR` | `TextColor` |
| `faction_name_singular` | `FACTION_NAME_SINGULAR` | `Component` |
| `faction_name_plural` | `FACTION_NAME_PLURAL` | `Component` |
| `max_level` | `MAX_LEVEL` | `Integer` |
| `max_lord_level` | `MAX_LORD_LEVEL` | `Integer` |
| `player_capability` | `PLAYER_CAPABILITY` | `Holder<AttachmentType<?>>` |
| `refinements` | `REFINEMENTS` | `RefinementItems` |
| `lord_titles` | `LORD_TITLES` | `LordTitles` |
| `task_master` | `TASK_MASTER` | `Holder<EntityType<?>>` |
| `village_bad_omen` | `VILLAGE_BAD_OMEN` | `Holder<MobEffect>` |
| `village_totem` | `VILLAGE_TOTEM` | `TotemPair` |
| `village_guards` | `VILLAGE_GUARDS` | `TagKey<EntityType<?>>` |
| `village_banner` | `VILLAGE_BANNER` | `VillageBanner` |

**Skill (on `ISkill`)**

| Key (`factionapi:`) | Constant | Value type |
|---|---|---|
| `skill_name` | `SKILL_NAME` | `Component` |
| `skill_cost` | `SKILL_COST` | `Integer` |
| `skill_description` | `SKILL_DESCRIPTION` | `Component` |
| `skill_action` | `SKILL_ACTION` | `Holder<? extends IAction<?>>` |
| `skill_actions` | `SKILL_ACTIONS` | `List<Holder<? extends IAction<?>>>` |
| `skill_factions` | `SKILL_FACTIONS` | `TagKey<IFaction<?>>` |
| `skill_tree_requirement` | `SKILL_TREE_REQUIREMENT` | `SkillTreeRequirement` |
| `skill_enable_consumable` | `SKILL_ENABLE_CONSUMABLE` | `Holder<FactionPlayerConsumer>` |
| `skill_deactivate_consumable` | `SKILL_DISABLE_CONSUMABLE` | `Holder<FactionPlayerConsumer>` |

**Minion task (on `IMinionTask`)**

| Key (`factionapi:`) | Constant | Value type |
|---|---|---|
| `minion_task_name` | `MINION_TASK_NAME` | `Component` |
| `minion_task_factions` | `MINION_TASK_FACTIONS` | `TagKey<IFaction<?>>` |
| `minion_task_skill_requirement` | `MINION_TASK_SKILL_REQUIREMENT` | `Holder<? extends ISkill<?>>` |
| `minion_task_global_command` | `MINION_TASK_GLOBAL_COMMAND` | `Unit` |
| `minion_task_category` | `MINION_TASK_CATEGORY` | `Holder<IMinionTaskCategory>` |
| `minion_task_category_default` | `MINION_TASK_CATEGORY_DEFAULT` | `Unit` |

**Minion data (on `IMinionData`, persistent)**

| Key (`factionapi:`) | Constant | Value type |
|---|---|---|
| `minion_level` | `MINION_LEVEL` | `Integer` |
| `minion_name` | `MINION_NAME` | `String` |
| `minion_use_lord_skin` | `MINION_USE_LORD_SKIN` | `Boolean` |
| `minion_use_minion_skin` | `MINION_USE_MINION_SKIN` | `Boolean` |
| `minion_has_increased_stats` | `MINION_HAS_INCREASED_STATS` | `Unit` |
| `minion_skin` | `MINION_SKIN` | `Integer` |
| `minion_health_level` | `MINION_HEALTH_LEVEL` | `Integer` |
| `minion_inventory_level` | `MINION_INVENTORY_LEVEL` | `Integer` |
| `minion_speed_level` | `MINION_SPEED_LEVEL` | `Integer` |
| `minion_attack_damage_level` | `MINION_ATTACK_DAMAGE_LEVEL` | `Integer` |
| `minion_resources_level` | `MINION_RESOURCES_LEVEL` | `Integer` |
| `minion_inventory_size` | `MINION_INVENTORY_SLOTS` | `Integer` |

**Item**

| Key (`factionapi:`) | Constant | Value type |
|---|---|---|
| `refinement_set` | `REFINEMENT_SET` | `IEffectiveRefinementSet` |
| `is_faction_banner` | `IS_FACTION_BANNER` | `Unit` |
| `faction_restriction` | `FACTION_RESTRICTION` | `IFactionRestriction` |
| `faction_slayer` | `FACTION_SLAYER` | `IFactionSlayer` |
| `shift_description` | `SHIFT_DESCRIPTION` | `ShiftDescription` |
| `block_description` | `BLOCK_DESCRIPTION` | `BlockDescription` |
| `faction_food` | `FACTION_FOOD` | `FactionFoodList` |

## Interface injection and access transformers

The `FactionApi` module publishes `META-INF/interfaceinjection.json` and `META-INF/accesstransformer.cfg`
(see `projects/faction/build.gradle`: `neoForge { interfaceInjectionData { … publish … } accessTransformers { … publish … } }`).
Mods that depend on it inherit them through ModDevGradle.

Injected interfaces (`interfaceinjection.json`):

| Target class                                         | Injected interface                                   |
|------------------------------------------------------|------------------------------------------------------|
| `net.minecraft.world.item.Item$Properties`           | `IItemPropertiesFactionsMock` → methods `factions$restrictFaction`, `factions$description`, `factions$descriptionWithout`, `factions$withShiftDescription…`, `factions$factionFood`, `factions$addAttributes` |
| `net.minecraft.world.effect.MobEffectInstance`       | `IEffectInstanceWithSourceFactionsMock` (`factions$getHiddenEffect`, `factions$getProperties`, `factions$hasProperty`, …) |
| `StatsScreen`, `ImageWidget$Sprite`, `ImageWidget$Texture`, `BossHealthOverlay` | client-only helper mocks |

The `…FactionsMock` classes only exist so the dev environment compiles against the methods; the real
implementation is provided by mixins. They are `@Deprecated` and throw if called directly, so treat the
`factions$…` methods as part of the *development-time* surface and use them through the injected types only
(the declaring interfaces such as `IItemProperties` are in `de.teamlapen.faction.misc.extensions`, not in the
API module).

The access transformer makes some vanilla client members public (`StatsScreen` sprites,
`BossHealthOverlay.OVERLAY_BACKGROUND_SPRITES`, `ImageWidget$Sprite/Texture`, `AdvancementTabType`, a
`Tooltip` constructor, `ObjectiveCriteria#registerCustom`, `TooltipRenderUtil` sprite getters,
`AbstractSelectionList#repositionEntries`, `SectionStorage` members, `LootContextParamSets.REGISTRY`).
See the file for the exact lines.

## Utilities

* `FIdentifier` – `mod(path)` (`factionapi:<path>`), `modString(path)`, `loc(namespace, path)`,
  `common(path)` (`c:`), `mc(path)` (`minecraft:`). `REFERENCE.MOD_ID` is `"factionapi"`.
* `SafeCast.cast(Object)` – unchecked cast to the inferred type (`<T> T`); only use when you are sure of the type.
* `@FloatRange(from, to)` – `TYPE_USE` annotation (class retention) documenting the allowed range of a `float`
  value; informational only.
* `ModStreamCodecs` – helper stream codecs (`table(...)`, `pair(...)`) used by the API records.
