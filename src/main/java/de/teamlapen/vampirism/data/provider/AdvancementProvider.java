package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.advancements.critereon.*;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.util.ItemDataUtils;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementProvider extends net.neoforged.neoforge.common.data.AdvancementProvider {

    public AdvancementProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, existingFileHelper, List.of(new VampirismAdvancements()));
    }

    private interface VampirismAdvancementSubProvider {
        void generate(@NotNull AdvancementHolder root, @NotNull HolderLookup.Provider holderProvider, @NotNull Consumer<AdvancementHolder> consumer);
    }

    private static class VampirismAdvancements implements net.neoforged.neoforge.common.data.AdvancementProvider.AdvancementGenerator {

        private final List<VampirismAdvancementSubProvider> subProvider = List.of(new MainAdvancements(), new HunterAdvancements(), new VampireAdvancements(), new MinionAdvancements());

        @Override
        public void generate(HolderLookup.@NotNull Provider registries, @NotNull Consumer<AdvancementHolder> consumer, @NotNull ExistingFileHelper existingFileHelper) {

            AdvancementHolder root = Advancement.Builder.advancement()
                    .display(ModItems.VAMPIRE_FANG.get(), Component.translatable("advancement.vampirism"), Component.translatable("advancement.vampirism.desc"), VResourceLocation.mod("textures/block/dark_stone_bricks.png"), AdvancementType.TASK, false, false, false)
                    .addCriterion("main", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.VAMPIRE_FANG.get()))
                    .addCriterion("second", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.ITEM_GARLIC.get()))
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .save(consumer, REFERENCE.MODID + ":main/root");

            this.subProvider.forEach(provider -> provider.generate(root, registries, consumer));
        }
    }

    private static class HunterAdvancements implements VampirismAdvancementSubProvider {

        @SuppressWarnings("unused")
        @Override
        public void generate(@NotNull AdvancementHolder root, HolderLookup.@NotNull Provider holderProvider, @NotNull Consumer<AdvancementHolder> consumer) {
            AdvancementHolder become_hunter = Advancement.Builder.advancement()
                    .display(ModItems.ITEM_GARLIC.get(), Component.translatable("advancement.vampirism.become_hunter"), Component.translatable("advancement.vampirism.become_hunter.desc"), null, AdvancementType.TASK, true, false, false)
                    .parent(root)
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(VReference.HUNTER_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":hunter/become_hunter");
            AdvancementHolder stake = Advancement.Builder.advancement()
                    .display(ModItems.STAKE.get(), Component.translatable("advancement.vampirism.stake"), Component.translatable("advancement.vampirism.stake.desc"), null, AdvancementType.CHALLENGE, true, true, true)
                    .parent(become_hunter)
                    .addCriterion("flower", HunterActionCriterionTrigger.TriggerInstance.of(HunterActionCriterionTrigger.Action.STAKE))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(VReference.HUNTER_FACTION, 1))
                    .rewards(AdvancementRewards.Builder.experience(100))
                    .save(consumer, REFERENCE.MODID + ":hunter/stake");
            AdvancementHolder betrayal = Advancement.Builder.advancement()
                    .display(ModItems.HUMAN_HEART.get(), Component.translatable("advancement.vampirism.betrayal"), Component.translatable("advancement.vampirism.betrayal.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(become_hunter)
                    .addCriterion("kill", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(ModTags.Entities.HUNTER)))
                    .addCriterion("faction", FactionCriterionTrigger.TriggerInstance.level(VReference.HUNTER_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":hunter/betrayal");
            AdvancementHolder max_level = Advancement.Builder.advancement()
                    .display(ModItems.ITEM_GARLIC.get(), Component.translatable("advancement.vampirism.max_level_hunter"), Component.translatable("advancement.vampirism.max_level_hunter.desc"), null, AdvancementType.GOAL, true, true, true)
                    .parent(stake)
                    .addCriterion("level", FactionCriterionTrigger.TriggerInstance.level(VReference.HUNTER_FACTION, 14))
                    .rewards(AdvancementRewards.Builder.experience(100))
                    .save(consumer, REFERENCE.MODID + ":hunter/max_level");
            AdvancementHolder technology = Advancement.Builder.advancement()
                    .display(ModItems.BASIC_TECH_CROSSBOW, Component.translatable("advancement.vampirism.technology"), Component.translatable("advancement.vampirism.technology.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(become_hunter)
                    .addCriterion("basic", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.BASIC_TECH_CROSSBOW))
                    .addCriterion("advanced", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.ENHANCED_TECH_CROSSBOW))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(VReference.HUNTER_FACTION, 1))
                    .requirements(AdvancementRequirements.Strategy.AND)
                    .save(consumer, REFERENCE.MODID + ":hunter/technology");
            AdvancementHolder max_lord = Advancement.Builder.advancement()
                    .display(ModItems.HUNTER_MINION_UPGRADE_SPECIAL.get(), Component.translatable("advancement.vampirism.max_lord_hunter"), Component.translatable("advancement.vampirism.max_lord_hunter.desc"), null, AdvancementType.CHALLENGE, true, true, true)
                    .parent(max_level)
                    .addCriterion("level", FactionCriterionTrigger.TriggerInstance.lord(VReference.HUNTER_FACTION, 5))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(VReference.HUNTER_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":hunter/max_lord");
            AdvancementHolder cure_vampire = Advancement.Builder.advancement()
                    .display(Items.GOLDEN_APPLE, Component.translatable("advancement.vampirism.cure_vampire_villager"), Component.translatable("advancement.vampirism.cure_vampire_villager.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(become_hunter)
                    .addCriterion("cure", CuredVampireVillagerCriterionTrigger.TriggerInstance.any())
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(VReference.HUNTER_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":hunter/cure_vampire_villager");
            AdvancementHolder kill_mother = Advancement.Builder.advancement()
                    .display(ModItems.MOTHER_CORE.get(), Component.translatable("advancement.vampirism.hunter_kill_mother"), Component.translatable("advancement.vampirism.hunter_kill_mother.desc"), null, AdvancementType.CHALLENGE, true, true, true)
                    .parent(become_hunter)
                    .addCriterion("killed", ModAdvancements.TRIGGER_MOTHER_WIN.get().createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty())))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(VReference.HUNTER_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":hunter/kill_mother");
            AdvancementHolder kill_resurrected_vampire = Advancement.Builder.advancement()
                    .display(ModItems.SOUL_ORB_VAMPIRE.get(), Component.translatable("advancement.vampirism.kill_resurrected_vampire"), Component.translatable("advancement.vampirism.kill_resurrected_vampire").append("\n").append(Component.translatable("advancement.vampirism.kill_resurrected_vampire.desc")), null, AdvancementType.TASK, true, true, true)
                    .parent(become_hunter)
                    .addCriterion("killed", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().effects(MobEffectsPredicate.Builder.effects().and(ModEffects.NEONATAL)).subPredicate(PlayerFactionSubPredicate.faction(VReference.VAMPIRE_FACTION))))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(VReference.HUNTER_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":hunter/kill_resurrected_vampire");
        }
    }

    private static class MainAdvancements implements VampirismAdvancementSubProvider {

        @SuppressWarnings("unused")
        @Override
        public void generate(@NotNull AdvancementHolder root, HolderLookup.@NotNull Provider holderProvider, @NotNull Consumer<AdvancementHolder> consumer) {
            HolderLookup.RegistryLookup<Biome> biomeRegistryLookup = holderProvider.lookupOrThrow(Registries.BIOME);
            AdvancementHolder vampire_forest = Advancement.Builder.advancement()
                    .display(Items.OAK_LOG, Component.translatable("advancement.vampirism.vampire_forest"), Component.translatable("advancement.vampirism.vampire_forest.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(root)
                    .addCriterion("main", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inBiome(biomeRegistryLookup.getOrThrow(ModBiomes.VAMPIRE_FOREST))))
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .save(consumer, REFERENCE.MODID + ":main/vampire_forest");
            AdvancementHolder ancient_knowledge = Advancement.Builder.advancement()
                    .display(ModItems.VAMPIRE_BOOK.get(), Component.translatable("advancement.vampirism.ancient_knowledge"), Component.translatable("advancement.vampirism.ancient_knowledge.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(vampire_forest)
                    .addCriterion("blood_container", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.VAMPIRE_BOOK.get()))
                    .save(consumer, REFERENCE.MODID + ":main/ancient_knowledge");
            AdvancementHolder regicide = Advancement.Builder.advancement()
                    .display(ModItems.PURE_BLOOD_0.get(), Component.translatable("advancement.vampirism.regicide"), Component.translatable("advancement.vampirism.regicide.desc"), null, AdvancementType.CHALLENGE, true, true, true)
                    .parent(vampire_forest)
                    .addCriterion("main", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(ModEntities.VAMPIRE_BARON.get())))
                    .save(consumer, REFERENCE.MODID + ":main/regicide");
            AdvancementHolder jumpScare = Advancement.Builder.advancement()
                    .display(Items.SKELETON_SKULL, Component.translatable("advancement.vampirism.jump_scare"), Component.translatable("advancement.vampirism.jump_scare.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(vampire_forest)
                    .addCriterion("main", KilledTrigger.TriggerInstance.entityKilledPlayer(EntityPredicate.Builder.entity().of(ModEntities.GHOST.get())))
                    .save(consumer, REFERENCE.MODID + ":main/jump_scare");
        }
    }

    private static class VampireAdvancements implements VampirismAdvancementSubProvider {

        @SuppressWarnings("unused")
        @Override
        public void generate(@NotNull AdvancementHolder root, HolderLookup.@NotNull Provider holderProvider, @NotNull Consumer<AdvancementHolder> consumer) {
            AdvancementHolder become_vampire = Advancement.Builder.advancement()
                    .display(ModItems.VAMPIRE_FANG.get(), Component.translatable("advancement.vampirism.become_vampire"), Component.translatable("advancement.vampirism.become_vampire.desc"), null, AdvancementType.TASK, true, false, false)
                    .parent(root)
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(VReference.VAMPIRE_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/become_vampire");
            AdvancementHolder bat = Advancement.Builder.advancement()
                    .display(Items.FEATHER, Component.translatable("advancement.vampirism.bat"), Component.translatable("advancement.vampirism.bat.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(become_vampire)
                    .addCriterion("action", VampireActionCriterionTrigger.TriggerInstance.of(VampireActionCriterionTrigger.Action.BAT))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(VReference.VAMPIRE_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/bat");
            AdvancementHolder first_blood = Advancement.Builder.advancement()
                    .display(ModItems.BLOOD_BOTTLE.get(), Component.translatable("advancement.vampirism.sucking_blood"), Component.translatable("advancement.vampirism.sucking_blood.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(become_vampire)
                    .addCriterion("flower", VampireActionCriterionTrigger.TriggerInstance.of(VampireActionCriterionTrigger.Action.SUCK_BLOOD))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(VReference.VAMPIRE_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/first_blood");
            AdvancementHolder blood_cult = Advancement.Builder.advancement()
                    .display(ModBlocks.ALTAR_INFUSION.get(), Component.translatable("advancement.vampirism.blood_cult"), Component.translatable("advancement.vampirism.blood_cult.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(become_vampire)
                    .addCriterion("flower", VampireActionCriterionTrigger.TriggerInstance.of(VampireActionCriterionTrigger.Action.PERFORM_RITUAL_INFUSION))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(VReference.VAMPIRE_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/blood_cult");
            AdvancementHolder resurrect = Advancement.Builder.advancement()
                    .display(ModItems.SOUL_ORB_VAMPIRE.get(), Component.translatable("advancement.vampirism.resurrect"), Component.translatable("advancement.vampirism.resurrect.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(become_vampire)
                    .addCriterion("resurrected", VampireActionCriterionTrigger.TriggerInstance.of(VampireActionCriterionTrigger.Action.RESURRECT))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(VReference.VAMPIRE_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/resurrect");
            AdvancementHolder extra_storage = Advancement.Builder.advancement()
                    .display(ModBlocks.BLOOD_CONTAINER.get(), Component.translatable("advancement.vampirism.extra_storage"), Component.translatable("advancement.vampirism.extra_storage.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(first_blood)
                    .addCriterion("blood_container", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.BLOOD_CONTAINER.get()))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(VReference.VAMPIRE_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/extra_storage");
            AdvancementHolder max_level = Advancement.Builder.advancement()
                    .display(ModItems.VAMPIRE_FANG.get(), Component.translatable("advancement.vampirism.max_level_vampire"), Component.translatable("advancement.vampirism.max_level_vampire.desc"), null, AdvancementType.GOAL, true, true, true)
                    .parent(bat)
                    .addCriterion("level", FactionCriterionTrigger.TriggerInstance.level(VReference.VAMPIRE_FACTION, 14))
                    .rewards(AdvancementRewards.Builder.experience(100))
                    .save(consumer, REFERENCE.MODID + ":vampire/max_level");
            AdvancementHolder sniped = Advancement.Builder.advancement()
                    .display(Items.ARROW, Component.translatable("advancement.vampirism.sniped"), Component.translatable("advancement.vampirism.sniped.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(bat)
                    .addCriterion("flower", VampireActionCriterionTrigger.TriggerInstance.of(VampireActionCriterionTrigger.Action.SNIPED_IN_BAT))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(VReference.VAMPIRE_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/sniped");
            AdvancementHolder yuck = Advancement.Builder.advancement()
                    .display(new DisplayInfo(ItemDataUtils.createPotion(Potions.POISON), Component.translatable("advancement.vampirism.yuck"), Component.translatable("advancement.vampirism.yuck.desc"), Optional.empty(), AdvancementType.TASK, true, true, true))
                    .parent(first_blood)
                    .addCriterion("flower", VampireActionCriterionTrigger.TriggerInstance.of(VampireActionCriterionTrigger.Action.POISONOUS_BITE))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(VReference.VAMPIRE_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/yuck");
            AdvancementHolder freeze_kill = Advancement.Builder.advancement()
                    .display(new DisplayInfo(new ItemStack(Items.CLOCK), Component.translatable("advancement.vampirism.freeze_kill"), Component.translatable("advancement.vampirism.freeze_kill.desc"), Optional.empty(), AdvancementType.TASK, true, true, true))
                    .parent(blood_cult)
                    .addCriterion("kill", VampireActionCriterionTrigger.TriggerInstance.of(VampireActionCriterionTrigger.Action.KILL_FROZEN_HUNTER))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(VReference.VAMPIRE_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/freeze_kill");
            AdvancementHolder max_lord = Advancement.Builder.advancement()
                    .display(ModItems.VAMPIRE_MINION_UPGRADE_SPECIAL.get(), Component.translatable("advancement.vampirism.max_lord_vampire"), Component.translatable("advancement.vampirism.max_lord_vampire.desc"), null, AdvancementType.CHALLENGE, true, true, true)
                    .parent(max_level)
                    .addCriterion("level", FactionCriterionTrigger.TriggerInstance.lord(VReference.VAMPIRE_FACTION, 5))
                    .save(consumer, REFERENCE.MODID + ":vampire/max_lord");
            AdvancementHolder kill_mother = Advancement.Builder.advancement()
                    .display(ModItems.MOTHER_CORE.get(), Component.translatable("advancement.vampirism.vampire_kill_mother"), Component.translatable("advancement.vampirism.vampire_kill_mother.desc"), null, AdvancementType.CHALLENGE, true, true, true)
                    .parent(become_vampire)
                    .addCriterion("killed", ModAdvancements.TRIGGER_MOTHER_WIN.get().createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty())))
                    .addCriterion("main", FactionCriterionTrigger.TriggerInstance.level(VReference.VAMPIRE_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/kill_mother");
        }
    }

    private static class MinionAdvancements implements VampirismAdvancementSubProvider {

        @Override
        public void generate(@NotNull AdvancementHolder root, HolderLookup.@NotNull Provider holderProvider, @NotNull Consumer<AdvancementHolder> consumer) {
            AdvancementHolder become_lord = Advancement.Builder.advancement()
                    .display(Items.PAPER, Component.translatable("advancement.vampirism.become_lord"), Component.translatable("advancement.vampirism.become_lord.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(root)
                    .addCriterion("level", FactionCriterionTrigger.TriggerInstance.lord(null, 1))
                    .save(consumer, REFERENCE.MODID + ":minion/become_lord");
            AdvancementHolder collect_blood = Advancement.Builder.advancement()
                    .display(ModItems.BLOOD_BOTTLE.get(), Component.translatable("advancement.vampirism.collect_blood"), Component.translatable("advancement.vampirism.collect_blood.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(become_lord)
                    .addCriterion("task", MinionTaskCriterionTrigger.TriggerInstance.tasks(MinionTasks.COLLECT_BLOOD.get()))
                    .save(consumer, REFERENCE.MODID + ":minion/collect_blood");
            AdvancementHolder collect_hunter_items = Advancement.Builder.advancement()
                    .display(Items.GOLD_NUGGET, Component.translatable("advancement.vampirism.collect_hunter_items"), Component.translatable("advancement.vampirism.collect_hunter_items.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(become_lord)
                    .addCriterion("task", MinionTaskCriterionTrigger.TriggerInstance.tasks(MinionTasks.COLLECT_HUNTER_ITEMS.get()))
                    .save(consumer, REFERENCE.MODID + ":minion/collect_hunter_items");
            AdvancementHolder protect_lord = Advancement.Builder.advancement()
                    .display(Items.SHIELD, Component.translatable("advancement.vampirism.protect_lord"), Component.translatable("advancement.vampirism.protect_lord.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(become_lord)
                    .addCriterion("task", MinionTaskCriterionTrigger.TriggerInstance.tasks(MinionTasks.PROTECT_LORD.get()))
                    .save(consumer, REFERENCE.MODID + ":minion/protect_lord");
            AdvancementHolder defend_area = Advancement.Builder.advancement()
                    .display(Items.SHIELD, Component.translatable("advancement.vampirism.defend_area"), Component.translatable("advancement.vampirism.defend_area.desc"), null, AdvancementType.TASK, true, true, true)
                    .parent(become_lord)
                    .addCriterion("task", MinionTaskCriterionTrigger.TriggerInstance.tasks(MinionTasks.DEFEND_AREA.get()))
                    .save(consumer, REFERENCE.MODID + ":minion/defend_area");
        }
    }

}
