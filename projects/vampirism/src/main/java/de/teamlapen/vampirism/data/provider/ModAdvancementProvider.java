package de.teamlapen.vampirism.data.provider;

import de.teamlapen.faction.common.advancements.criterion.ActionCriterionTrigger;
import de.teamlapen.faction.common.advancements.criterion.FactionCriterionTrigger;
import de.teamlapen.faction.common.advancements.criterion.FactionSubPredicate;
import de.teamlapen.faction.common.advancements.criterion.MinionTaskCriterionTrigger;
import de.teamlapen.faction.common.core.FactionMinionTasks;
import de.teamlapen.faction.common.util.MapUtil;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.items.components.IVampireBook;
import de.teamlapen.vampirism.common.advancements.critereon.*;
import de.teamlapen.vampirism.common.components.predicates.VampireBookPredicate;
import de.teamlapen.vampirism.common.core.*;
import de.teamlapen.vampirism.common.tags.ModEffectTags;
import de.teamlapen.vampirism.common.tags.ModEntityTags;
import de.teamlapen.vampirism.common.util.ItemDataUtils;
import de.teamlapen.vampirism.common.world.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.common.world.entity.player.vampire.actions.VampireActions;
import net.minecraft.advancements.*;
import net.minecraft.advancements.criterion.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends AdvancementProvider {

    public ModAdvancementProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider, List.of(new VampirismAdvancements()));
    }

    private interface VampirismAdvancementSubProvider {
        void generate(AdvancementHolder root, HolderLookup.Provider holderProvider, Consumer<AdvancementHolder> consumer);
    }

    private static class VampirismAdvancements implements AdvancementSubProvider {

        private final List<VampirismAdvancementSubProvider> subProvider = List.of(new MainAdvancements(), new VampireAdvancements(), new HunterAdvancements(), new MinionAdvancements());

        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer) {
            AdvancementHolder root = Advancement.Builder.advancement()
                    .display(ModItems.VAMPIRE_FANG.get(), Component.translatable("advancement.vampirism"), Component.translatable("advancement.vampirism.desc"), VIdentifier.mod("block/dark_stone_bricks"), AdvancementType.TASK, false, false, false)
                    .addCriterion("main", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.VAMPIRE_FANG.get()))
                    .addCriterion("second", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.GARLIC.get()))
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .save(consumer, REFERENCE.MODID + ":main/root");

            this.subProvider.forEach(provider -> provider.generate(root, registries, consumer));
        }
    }

    private static class MainAdvancements implements VampirismAdvancementSubProvider {

        @SuppressWarnings("unused")
        @Override
        public void generate(AdvancementHolder root, HolderLookup.Provider holderProvider, Consumer<AdvancementHolder> consumer) {
            HolderGetter<Item> itemRegistryLookup = holderProvider.lookupOrThrow(Registries.ITEM);
            HolderLookup.RegistryLookup<Biome> biomeRegistryLookup = holderProvider.lookupOrThrow(Registries.BIOME);
            HolderLookup.RegistryLookup<EntityType<?>> entities = holderProvider.lookupOrThrow(Registries.ENTITY_TYPE);
            AdvancementHolder vampire_forest = Advancement.Builder.advancement()
                    .display(ModBlocks.DARK_SPRUCE_SAPLING.get(), Component.translatable("advancement.vampirism.vampire_forest"), Component.translatable("advancement.vampirism.vampire_forest.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(root)
                    .addCriterion("main", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inBiome(biomeRegistryLookup.getOrThrow(ModBiomes.VAMPIRE_FOREST))))
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .save(consumer, REFERENCE.MODID + ":main/vampire_forest");
            AdvancementHolder ancient_knowledge = Advancement.Builder.advancement()
                    .display(ModItems.VAMPIRE_BOOK.get(), Component.translatable("advancement.vampirism.ancient_knowledge"), Component.translatable("advancement.vampirism.ancient_knowledge.desc"), null, AdvancementType.TASK, true, true, false)
                    .parent(vampire_forest)
                    .addCriterion("blood_container", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.VAMPIRE_BOOK.get()))
                    .save(consumer, REFERENCE.MODID + ":main/ancient_knowledge");
            AdvancementHolder patchouli_knowledge = patchouliKnowledge(Advancement.Builder.advancement(), holderProvider)
                    .display(Blocks.BOOKSHELF, Component.translatable("advancement.vampirism.patchouli_knowledge"), Component.translatable("advancement.vampirism.patchouli_knowledge.desc"), null, AdvancementType.CHALLENGE, true, true, false)
                    .parent(ancient_knowledge)
                    .save(consumer, REFERENCE.MODID + ":main/patchouli_knowledge");
            AdvancementHolder domain_of_the_dead = Advancement.Builder.advancement()
                    .display(ModBlocks.CANDLE_STICK_GRAY.get(), Component.translatable("advancement.vampirism.domain_of_the_dead"), Component.translatable("advancement.vampirism.domain_of_the_dead.desc"), null, AdvancementType.TASK, true, true, false)
                    .parent(vampire_forest)
                    .addCriterion("in_crypt", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inStructure(holderProvider.lookupOrThrow(Registries.STRUCTURE).getOrThrow(ModStructures.CRYPT))))
                    .save(consumer, REFERENCE.MODID + ":main/domain_of_the_dead");
            AdvancementHolder reopening_old_wounds = Advancement.Builder.advancement()
                    .display(MapUtil.getPreviewMap(MapUtil.getModTranslation("ancient_remains"), ModMapDecorations.ANCIENT_REMAINS), Component.translatable("advancement.vampirism.reopening_old_wounds"), Component.translatable("advancement.vampirism.reopening_old_wounds.desc"), null, AdvancementType.TASK, true, true, false)
                    .parent(domain_of_the_dead)
                    .addCriterion("map", MapFoundCriterionTrigger.TriggerInstance.foundMap(ModMapDecorations.ANCIENT_REMAINS))
                    .save(consumer, REFERENCE.MODID + ":main/reopening_old_wounds");
            AdvancementHolder regicide = Advancement.Builder.advancement()
                    .display(ModItems.PURE_BLOOD_0.get(), Component.translatable("advancement.vampirism.regicide"), Component.translatable("advancement.vampirism.regicide.desc"), null, AdvancementType.CHALLENGE, true, true, false)
                    .parent(vampire_forest)
                    .addCriterion("main", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(entities, ModEntities.VAMPIRE_BARON.get())))
                    .save(consumer, REFERENCE.MODID + ":main/regicide");
            AdvancementHolder jumpScare = Advancement.Builder.advancement()
                    .display(Items.SKELETON_SKULL, Component.translatable("advancement.vampirism.jump_scare"), Component.translatable("advancement.vampirism.jump_scare.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(vampire_forest)
                    .addCriterion("main", KilledTrigger.TriggerInstance.entityKilledPlayer(EntityPredicate.Builder.entity().of(entities, ModEntities.GHOST.get())))
                    .save(consumer, REFERENCE.MODID + ":main/jump_scare");
        }
    }

    private static Advancement.Builder patchouliKnowledge(Advancement.Builder builder, HolderLookup.Provider holderProvider) {
        builder.requirements(AdvancementRequirements.Strategy.AND);

        Optional<? extends HolderLookup.RegistryLookup<IVampireBook>> registryLookup = holderProvider.lookup(VampirismRegistries.Keys.VAMPIRE_BOOK);
        registryLookup.ifPresent(registry -> registry.listElements().sorted(Comparator.comparing(Holder.Reference::key)).forEach(vampireBook ->
                builder.addCriterion(
                        "has_" + vampireBook.value().id().getPath(),
                        InventoryChangeTrigger.TriggerInstance.hasItems(
                                ItemPredicate.Builder.item()
                                        .of(BuiltInRegistries.ITEM, ModItems.VAMPIRE_BOOK)
                                        .withComponents(DataComponentMatchers.Builder.components().partial(ModDataComponents.VAMPIRE_BOOK_PREDICATE.get(), new VampireBookPredicate(vampireBook.value().id())).build())
                        )
                )
        ));

        return builder;
    }

    private static class VampireAdvancements implements VampirismAdvancementSubProvider {

        @SuppressWarnings("unused")
        @Override
        public void generate(AdvancementHolder root, HolderLookup.Provider holderProvider, Consumer<AdvancementHolder> consumer) {
            AdvancementHolder become_vampire = Advancement.Builder.advancement()
                    .display(ModItems.VAMPIRE_FANG.get(), Component.translatable("advancement.vampirism.become_vampire"), Component.translatable("advancement.vampirism.become_vampire.desc"), null, AdvancementType.TASK, true, false, true)
                    .parent(root)
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(ModFactions.VAMPIRE, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/become_vampire");
            AdvancementHolder bat = Advancement.Builder.advancement()
                    .display(Items.FEATHER, Component.translatable("advancement.vampirism.bat"), Component.translatable("advancement.vampirism.bat.desc"), null, AdvancementType.TASK, true, true, false)
                    .parent(become_vampire)
                    .addCriterion("action", ActionCriterionTrigger.TriggerInstance.of(VampireActions.BAT))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(ModFactions.VAMPIRE, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/bat");
            AdvancementHolder first_blood = Advancement.Builder.advancement()
                    .display(new DisplayInfo(ItemDataUtils.createFilledBloodBottle(), Component.translatable("advancement.vampirism.sucking_blood"), Component.translatable("advancement.vampirism.sucking_blood.desc"), Optional.empty(), AdvancementType.TASK, true, true, false))
                    .parent(become_vampire)
                    .addCriterion("flower", VampireActionCriterionTrigger.TriggerInstance.of(VampireActionCriterionTrigger.Action.SUCK_BLOOD))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(ModFactions.VAMPIRE, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/first_blood");
            AdvancementHolder baptism_of_blood = Advancement.Builder.advancement()
                    .display(ModBlocks.ALTAR_INSPIRATION.get(), Component.translatable("advancement.vampirism.baptism_of_blood"), Component.translatable("advancement.vampirism.baptism_of_blood.desc"), null, AdvancementType.TASK, true, true, false)
                    .parent(become_vampire)
                    .addCriterion("flower", VampireActionCriterionTrigger.TriggerInstance.of(VampireActionCriterionTrigger.Action.PERFORM_RITUAL_INSPIRATION))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(ModFactions.VAMPIRE, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/baptism_of_blood");
            AdvancementHolder blood_cult = Advancement.Builder.advancement()
                    .display(ModBlocks.ALTAR_INFUSION.get(), Component.translatable("advancement.vampirism.blood_cult"), Component.translatable("advancement.vampirism.blood_cult.desc"), null, AdvancementType.TASK, true, true, false)
                    .parent(baptism_of_blood)
                    .addCriterion("flower", VampireActionCriterionTrigger.TriggerInstance.of(VampireActionCriterionTrigger.Action.PERFORM_RITUAL_INFUSION))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(ModFactions.VAMPIRE, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/blood_cult");
            AdvancementHolder resurrect = Advancement.Builder.advancement()
                    .display(ModItems.SOUL_ORB_VAMPIRE.get(), Component.translatable("advancement.vampirism.resurrect"), Component.translatable("advancement.vampirism.resurrect.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(become_vampire)
                    .addCriterion("resurrected", VampireActionCriterionTrigger.TriggerInstance.of(VampireActionCriterionTrigger.Action.RESURRECT))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(ModFactions.VAMPIRE, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/resurrect");
            AdvancementHolder extra_storage = Advancement.Builder.advancement()
                    .display(new DisplayInfo(ItemDataUtils.createFilledBloodContainer(), Component.translatable("advancement.vampirism.extra_storage"), Component.translatable("advancement.vampirism.extra_storage.desc"), Optional.empty(), AdvancementType.TASK, true, true, false))
                    .parent(first_blood)
                    .addCriterion("blood_container", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.BLOOD_CONTAINER.get()))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(ModFactions.VAMPIRE, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/extra_storage");
            AdvancementHolder plague_inc = Advancement.Builder.advancement()
                    .display(ModItems.VAMPIRE_BLOOD_BOTTLE.get(), Component.translatable("advancement.vampirism.plague_inc"), Component.translatable("advancement.vampirism.plague_inc.desc"), null, AdvancementType.TASK, true, true, false)
                    .parent(first_blood)
                    .addCriterion("infected", ActionCriterionTrigger.TriggerInstance.of(VampireActions.INFECT))
                    .save(consumer, REFERENCE.MODID + ":vampire/plague_inc");
            AdvancementHolder texas_massacre = Advancement.Builder.advancement()
                    .display(ModItems.HUMAN_HEART.get(), Component.translatable("advancement.vampirism.texas_massacre"), Component.translatable("advancement.vampirism.texas_massacre.desc"), null, AdvancementType.TASK, true, true, false)
                    .parent(first_blood)
                    .addCriterion("blood_food_consumed", ModAdvancements.TRIGGER_BLOOD_FOOD_CONSUMED.get().createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty())))
                    .save(consumer, REFERENCE.MODID + ":vampire/texas_massacre");
            AdvancementHolder max_level = Advancement.Builder.advancement()
                    .display(ModItems.VAMPIRE_FANG.get(), Component.translatable("advancement.vampirism.max_level_vampire"), Component.translatable("advancement.vampirism.max_level_vampire.desc"), null, AdvancementType.GOAL, true, true, false)
                    .parent(blood_cult)
                    .addCriterion("level", FactionCriterionTrigger.TriggerInstance.level(ModFactions.VAMPIRE, 14))
                    .rewards(AdvancementRewards.Builder.experience(100))
                    .save(consumer, REFERENCE.MODID + ":vampire/max_level");
            AdvancementHolder sniped = Advancement.Builder.advancement()
                    .display(Items.ARROW, Component.translatable("advancement.vampirism.sniped"), Component.translatable("advancement.vampirism.sniped.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(bat)
                    .addCriterion("flower", VampireActionCriterionTrigger.TriggerInstance.of(VampireActionCriterionTrigger.Action.SNIPED_IN_BAT))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(ModFactions.VAMPIRE, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/sniped");
            AdvancementHolder yuck = Advancement.Builder.advancement()
                    .display(new DisplayInfo(ItemDataUtils.template(Potions.POISON), Component.translatable("advancement.vampirism.yuck"), Component.translatable("advancement.vampirism.yuck.desc"), Optional.empty(), AdvancementType.TASK, true, true, true))
                    .parent(first_blood)
                    .addCriterion("flower", VampireActionCriterionTrigger.TriggerInstance.of(VampireActionCriterionTrigger.Action.POISONOUS_BITE))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(ModFactions.VAMPIRE, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/yuck");
            AdvancementHolder freeze_kill = Advancement.Builder.advancement()
                    .display(Items.CLOCK, Component.translatable("advancement.vampirism.freeze_kill"), Component.translatable("advancement.vampirism.freeze_kill.desc"), null, AdvancementType.TASK, true, true, false)
                    .parent(become_vampire)
                    .addCriterion("kill", VampireActionCriterionTrigger.TriggerInstance.of(VampireActionCriterionTrigger.Action.KILL_FROZEN_HUNTER))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(ModFactions.VAMPIRE, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/freeze_kill");
            AdvancementHolder max_lord = Advancement.Builder.advancement()
                    .display(ModItems.VAMPIRE_MINION_UPGRADE_SPECIAL.get(), Component.translatable("advancement.vampirism.max_lord_vampire"), Component.translatable("advancement.vampirism.max_lord_vampire.desc"), null, AdvancementType.CHALLENGE, true, true, false)
                    .parent(max_level)
                    .addCriterion("level", FactionCriterionTrigger.TriggerInstance.lord(ModFactions.VAMPIRE, 5))
                    .save(consumer, REFERENCE.MODID + ":vampire/max_lord");
            AdvancementHolder kill_mother = Advancement.Builder.advancement()
                    .display(ModItems.MOTHER_CORE.get(), Component.translatable("advancement.vampirism.vampire_kill_mother"), Component.translatable("advancement.vampirism.vampire_kill_mother.desc"), null, AdvancementType.CHALLENGE, true, true, true)
                    .parent(become_vampire)
                    .addCriterion("killed", ModAdvancements.TRIGGER_MOTHER_WIN.get().createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty())))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(ModFactions.VAMPIRE, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/kill_mother");
        }
    }

    private static class HunterAdvancements implements VampirismAdvancementSubProvider {

        @SuppressWarnings("unused")
        @Override
        public void generate(AdvancementHolder root, HolderLookup.Provider holderProvider, Consumer<AdvancementHolder> consumer) {
            HolderLookup.RegistryLookup<EntityType<?>> entities = holderProvider.lookupOrThrow(Registries.ENTITY_TYPE);
            AdvancementHolder become_hunter = Advancement.Builder.advancement()
                    .display(ModBlocks.GARLIC.get(), Component.translatable("advancement.vampirism.become_hunter"), Component.translatable("advancement.vampirism.become_hunter.desc"), null, AdvancementType.TASK, true, false, true)
                    .parent(root)
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(ModFactions.HUNTER, 1))
                    .save(consumer, REFERENCE.MODID + ":hunter/become_hunter");
            AdvancementHolder stake = Advancement.Builder.advancement()
                    .display(ModItems.STAKE.get(), Component.translatable("advancement.vampirism.stake"), Component.translatable("advancement.vampirism.stake.desc"), null, AdvancementType.CHALLENGE, true, true, false)
                    .parent(become_hunter)
                    .addCriterion("flower", HunterActionCriterionTrigger.TriggerInstance.of(HunterActionCriterionTrigger.Action.STAKE))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(ModFactions.HUNTER, 1))
                    .rewards(AdvancementRewards.Builder.experience(100))
                    .save(consumer, REFERENCE.MODID + ":hunter/stake");
            AdvancementHolder kill_resurrected_vampire = Advancement.Builder.advancement()
                    .display(ModItems.SOUL_ORB_VAMPIRE.get(), Component.translatable("advancement.vampirism.kill_resurrected_vampire"), Component.translatable("advancement.vampirism.kill_resurrected_vampire.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(stake)
                    .addCriterion("killed", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().effects(MobEffectsPredicate.Builder.effects().and(ModEffects.NEONATAL)).subPredicate(FactionSubPredicate.faction(ModFactions.VAMPIRE))))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(ModFactions.HUNTER, 1))
                    .save(consumer, REFERENCE.MODID + ":hunter/kill_resurrected_vampire");
            AdvancementHolder max_level = Advancement.Builder.advancement()
                    .display(ModBlocks.GARLIC.get(), Component.translatable("advancement.vampirism.max_level_hunter"), Component.translatable("advancement.vampirism.max_level_hunter.desc"), null, AdvancementType.GOAL, true, true, false)
                    .parent(stake)
                    .addCriterion("level", FactionCriterionTrigger.TriggerInstance.level(ModFactions.HUNTER, 14))
                    .rewards(AdvancementRewards.Builder.experience(100))
                    .save(consumer, REFERENCE.MODID + ":hunter/max_level");
            AdvancementHolder betrayal = Advancement.Builder.advancement()
                    .display(ModItems.HUMAN_HEART.get(), Component.translatable("advancement.vampirism.betrayal"), Component.translatable("advancement.vampirism.betrayal.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(become_hunter)
                    .addCriterion("kill", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(entities, ModEntityTags.HUNTER)))
                    .addCriterion("faction", FactionCriterionTrigger.TriggerInstance.level(ModFactions.HUNTER, 1))
                    .save(consumer, REFERENCE.MODID + ":hunter/betrayal");
            AdvancementHolder technology = Advancement.Builder.advancement()
                    .display(ModItems.BASIC_TECH_CROSSBOW, Component.translatable("advancement.vampirism.technology"), Component.translatable("advancement.vampirism.technology.desc"), null, AdvancementType.TASK, true, true, false)
                    .parent(become_hunter)
                    .addCriterion("basic", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.BASIC_TECH_CROSSBOW))
                    .addCriterion("advanced", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.ENHANCED_TECH_CROSSBOW))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(ModFactions.HUNTER, 1))
                    .requirements(AdvancementRequirements.Strategy.AND)
                    .save(consumer, REFERENCE.MODID + ":hunter/technology");
            AdvancementHolder mainline = Advancement.Builder.advancement()
                    .display(ItemDataUtils.template(Potions.REGENERATION, ModItems.SERUM_INJECTION), Component.translatable("advancement.vampirism.mainline"), Component.translatable("advancement.vampirism.mainline.desc"), null, AdvancementType.TASK, true, true, false)
                    .parent(become_hunter)
                    .addCriterion("injection", SerumInjectedCriterionTrigger.TriggerInstance.injectedSerumAny())
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(ModFactions.HUNTER, 1))
                    .save(consumer, REFERENCE.MODID + ":hunter/mainline");
            AdvancementHolder worth_it = Advancement.Builder.advancement()
                    .display(ModBlocks.TOMBSTONE_MEDIUM, Component.translatable("advancement.vampirism.worth_it"), Component.translatable("advancement.vampirism.worth_it.desc"), null, AdvancementType.CHALLENGE, true, true, true)
                    .parent(mainline)
                    .addCriterion("injection", SerumInjectedCriterionTrigger.TriggerInstance.injectedSerum(ModEffectTags.SELF_HARM_SERUMS))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(ModFactions.HUNTER, 1))
                    .save(consumer, REFERENCE.MODID + ":hunter/worth_it");
            AdvancementHolder max_lord = Advancement.Builder.advancement()
                    .display(ModItems.HUNTER_MINION_UPGRADE_SPECIAL.get(), Component.translatable("advancement.vampirism.max_lord_hunter"), Component.translatable("advancement.vampirism.max_lord_hunter.desc"), null, AdvancementType.CHALLENGE, true, true, false)
                    .parent(max_level)
                    .addCriterion("level", FactionCriterionTrigger.TriggerInstance.lord(ModFactions.HUNTER, 5))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(ModFactions.HUNTER, 1))
                    .save(consumer, REFERENCE.MODID + ":hunter/max_lord");
            AdvancementHolder cure_vampire = Advancement.Builder.advancement()
                    .display(Items.GOLDEN_APPLE, Component.translatable("advancement.vampirism.cure_vampire_villager"), Component.translatable("advancement.vampirism.cure_vampire_villager.desc"), null, AdvancementType.TASK, true, true, false)
                    .parent(become_hunter)
                    .addCriterion("cure", CuredVampireVillagerCriterionTrigger.TriggerInstance.any())
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(ModFactions.HUNTER, 1))
                    .save(consumer, REFERENCE.MODID + ":hunter/cure_vampire_villager");
            AdvancementHolder kill_mother = Advancement.Builder.advancement()
                    .display(ModItems.MOTHER_CORE.get(), Component.translatable("advancement.vampirism.hunter_kill_mother"), Component.translatable("advancement.vampirism.hunter_kill_mother.desc"), null, AdvancementType.CHALLENGE, true, true, true)
                    .parent(become_hunter)
                    .addCriterion("killed", ModAdvancements.TRIGGER_MOTHER_WIN.get().createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty())))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(ModFactions.HUNTER, 1))
                    .save(consumer, REFERENCE.MODID + ":hunter/kill_mother");
        }
    }

    private static class MinionAdvancements implements VampirismAdvancementSubProvider {

        @SuppressWarnings("unused")
        @Override
        public void generate(AdvancementHolder root, HolderLookup.Provider holderProvider, Consumer<AdvancementHolder> consumer) {
            AdvancementHolder become_lord = Advancement.Builder.advancement()
                    .display(ModItems.VAMPIRE_CLOTHING_CROWN.get(), Component.translatable("advancement.vampirism.become_lord"), Component.translatable("advancement.vampirism.become_lord.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(root)
                    .addCriterion("level", FactionCriterionTrigger.TriggerInstance.lord(null, 1))
                    .save(consumer, REFERENCE.MODID + ":minion/become_lord");
            AdvancementHolder collect_blood = Advancement.Builder.advancement()
                    .display(new DisplayInfo(ItemDataUtils.createFilledBloodBottle(), Component.translatable("advancement.vampirism.collect_blood"), Component.translatable("advancement.vampirism.collect_blood.desc"), Optional.empty(), AdvancementType.TASK, true, true, false))
                    .parent(become_lord)
                    .addCriterion("task", MinionTaskCriterionTrigger.TriggerInstance.tasks(MinionTasks.COLLECT_BLOOD.get()))
                    .save(consumer, REFERENCE.MODID + ":minion/collect_blood");
            AdvancementHolder collect_hunter_items = Advancement.Builder.advancement()
                    .display(Items.BUNDLE, Component.translatable("advancement.vampirism.collect_hunter_items"), Component.translatable("advancement.vampirism.collect_hunter_items.desc"), null, AdvancementType.TASK, true, true, false)
                    .parent(become_lord)
                    .addCriterion("task", MinionTaskCriterionTrigger.TriggerInstance.tasks(MinionTasks.COLLECT_HUNTER_ITEMS.get()))
                    .save(consumer, REFERENCE.MODID + ":minion/collect_hunter_items");
            AdvancementHolder protect_lord = Advancement.Builder.advancement()
                    .display(Items.SHIELD, Component.translatable("advancement.vampirism.protect_lord"), Component.translatable("advancement.vampirism.protect_lord.desc"), null, AdvancementType.TASK, true, true, false)
                    .parent(become_lord)
                    .addCriterion("task", MinionTaskCriterionTrigger.TriggerInstance.tasks(FactionMinionTasks.PROTECT_LORD.get()))
                    .save(consumer, REFERENCE.MODID + ":minion/protect_lord");
            AdvancementHolder defend_area = Advancement.Builder.advancement()
                    .display(Blocks.RED_BANNER, Component.translatable("advancement.vampirism.defend_area"), Component.translatable("advancement.vampirism.defend_area.desc"), null, AdvancementType.TASK, true, true, false)
                    .parent(become_lord)
                    .addCriterion("task", MinionTaskCriterionTrigger.TriggerInstance.tasks(FactionMinionTasks.DEFEND_AREA.get()))
                    .save(consumer, REFERENCE.MODID + ":minion/defend_area");
        }
    }

}
