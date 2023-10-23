package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.advancements.critereon.*;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementGenerator extends AdvancementProvider { //TODO 1.20 move to de.teamlapen.vampirism.data.provider

    public AdvancementGenerator(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider, List.of(new VampirismAdvancements()));
    }

    private interface VampirismAdvancementSubProvider {
        void generate(@NotNull Advancement root, @NotNull HolderLookup.Provider holderProvider, @NotNull Consumer<Advancement> consumer);
    }

    private static class VampirismAdvancements implements AdvancementSubProvider {

        private final List<VampirismAdvancementSubProvider> subProvider = List.of(new MainAdvancements(), new HunterAdvancements(), new VampireAdvancements(), new MinionAdvancements());

        @Override
        public void generate(HolderLookup.@NotNull Provider holderProvider, @NotNull Consumer<Advancement> consumer) {
            Advancement root = Advancement.Builder.advancement()
                    .display(ModItems.VAMPIRE_FANG.get(), Component.translatable("advancement.vampirism"), Component.translatable("advancement.vampirism.desc"), new ResourceLocation(REFERENCE.MODID, "textures/block/castle_block_dark_brick.png"), FrameType.TASK, false, false, false) //TODO BREAKING: change background texture to "textures/gui/advancements/backgrounds/vampirism.png"
                    .addCriterion("main", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.VAMPIRE_FANG.get()))
                    .addCriterion("second", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.ITEM_GARLIC.get()))
                    .requirements(RequirementsStrategy.OR)
                    .save(consumer, REFERENCE.MODID + ":main/root");

            this.subProvider.forEach(provider -> provider.generate(root, holderProvider, consumer));
        }

    }

    private static class HunterAdvancements implements VampirismAdvancementSubProvider {

        @SuppressWarnings("unused")
        @Override
        public void generate(@NotNull Advancement root, HolderLookup.@NotNull Provider holderProvider, @NotNull Consumer<Advancement> consumer) {
            Advancement become_hunter = Advancement.Builder.advancement()
                    .display(ModItems.ITEM_GARLIC.get(), Component.translatable("advancement.vampirism.become_hunter"), Component.translatable("advancement.vampirism.become_hunter.desc"), null, FrameType.TASK, true, false, false)
                    .parent(root)
                    .addCriterion("main", FactionCriterionTrigger.level(VReference.HUNTER_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":hunter/become_hunter");
            Advancement stake = Advancement.Builder.advancement()
                    .display(ModItems.STAKE.get(), Component.translatable("advancement.vampirism.stake"), Component.translatable("advancement.vampirism.stake.desc"), null, FrameType.CHALLENGE, true, true, true)
                    .parent(become_hunter)
                    .addCriterion("flower", HunterActionCriterionTrigger.builder(HunterActionCriterionTrigger.Action.STAKE))
                    .addCriterion("main", FactionCriterionTrigger.level(VReference.HUNTER_FACTION, 1))
                    .rewards(AdvancementRewards.Builder.experience(100))
                    .save(consumer, REFERENCE.MODID + ":hunter/stake");
            Advancement betrayal = Advancement.Builder.advancement()
                    .display(ModItems.HUMAN_HEART.get(), Component.translatable("advancement.vampirism.betrayal"), Component.translatable("advancement.vampirism.betrayal.desc"), null, FrameType.TASK, true, true, true)
                    .parent(become_hunter)
                    .addCriterion("kill", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(ModTags.Entities.HUNTER)))
                    .addCriterion("faction", FactionCriterionTrigger.level(VReference.HUNTER_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":hunter/betrayal");
            Advancement max_level = Advancement.Builder.advancement()
                    .display(ModItems.ITEM_GARLIC.get(), Component.translatable("advancement.vampirism.max_level_hunter"), Component.translatable("advancement.vampirism.max_level_hunter.desc"), null, FrameType.GOAL, true, true, true)
                    .parent(stake)
                    .addCriterion("level", FactionCriterionTrigger.level(VReference.HUNTER_FACTION, 14))
                    .rewards(AdvancementRewards.Builder.experience(100))
                    .save(consumer, REFERENCE.MODID + ":hunter/max_level");
            Advancement technology = Advancement.Builder.advancement()
                    .display(ModItems.BASIC_TECH_CROSSBOW.get(), Component.translatable("advancement.vampirism.technology"), Component.translatable("advancement.vampirism.technology.desc"), null, FrameType.TASK, true, true, true)
                    .parent(become_hunter)
                    .addCriterion("basic", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.BASIC_TECH_CROSSBOW.get()))
                    .addCriterion("advanced", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.ENHANCED_TECH_CROSSBOW.get()))
                    .addCriterion("main", FactionCriterionTrigger.level(VReference.HUNTER_FACTION, 1))
                    .requirements(RequirementsStrategy.AND)
                    .save(consumer, REFERENCE.MODID + ":hunter/technology");
            Advancement max_lord = Advancement.Builder.advancement()
                    .display(ModItems.HUNTER_MINION_UPGRADE_SPECIAL.get(), Component.translatable("advancement.vampirism.max_lord_hunter"), Component.translatable("advancement.vampirism.max_lord_hunter.desc"), null, FrameType.CHALLENGE, true, true, true)
                    .parent(max_level)
                    .addCriterion("level", FactionCriterionTrigger.lord(VReference.HUNTER_FACTION, 5))
                    .addCriterion("main", FactionCriterionTrigger.level(VReference.HUNTER_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":hunter/max_lord");
            Advancement cure_vampire = Advancement.Builder.advancement()
                    .display(Items.GOLDEN_APPLE, Component.translatable("advancement.vampirism.cure_vampire_villager"), Component.translatable("advancement.vampirism.cure_vampire_villager.desc"), null, FrameType.TASK, true, true, true)
                    .parent(become_hunter)
                    .addCriterion("cure", CuredVampireVillagerCriterionTrigger.Instance.any())
                    .addCriterion("main", FactionCriterionTrigger.level(VReference.HUNTER_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":hunter/cure_vampire_villager");
            Advancement kill_mother = Advancement.Builder.advancement()
                    .display(ModItems.MOTHER_CORE.get(), Component.translatable("advancement.vampirism.hunter_kill_mother"), Component.translatable("advancement.vampirism.hunter_kill_mother.desc"), null, FrameType.CHALLENGE, true, true, true)
                    .parent(become_hunter)
                    .addCriterion("killed", new PlayerTrigger.TriggerInstance(ModAdvancements.TRIGGER_MOTHER_WIN.getId(), ContextAwarePredicate.ANY))
                    .addCriterion("main", FactionCriterionTrigger.level(VReference.HUNTER_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":hunter/kill_mother");
        }
    }

    private static class MainAdvancements implements VampirismAdvancementSubProvider {

        @SuppressWarnings("unused")
        @Override
        public void generate(@NotNull Advancement root, HolderLookup.@NotNull Provider holderProvider, @NotNull Consumer<Advancement> consumer) {
            Advancement vampire_forest = Advancement.Builder.advancement()
                    .display(Items.OAK_LOG, Component.translatable("advancement.vampirism.vampire_forest"), Component.translatable("advancement.vampirism.vampire_forest.desc"), null, FrameType.TASK, true, true, true)
                    .parent(root)
                    .addCriterion("main", PlayerTrigger.TriggerInstance.located(LocationPredicate.inBiome(ModBiomes.VAMPIRE_FOREST)))
                    .requirements(RequirementsStrategy.OR)
                    .save(consumer, REFERENCE.MODID + ":main/vampire_forest");
            Advancement ancient_knowledge = Advancement.Builder.advancement()
                    .display(ModItems.VAMPIRE_BOOK.get(), Component.translatable("advancement.vampirism.ancient_knowledge"), Component.translatable("advancement.vampirism.ancient_knowledge.desc"), null, FrameType.TASK, true, true, true)
                    .parent(vampire_forest)
                    .addCriterion("blood_container", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.VAMPIRE_BOOK.get()))
                    .save(consumer, REFERENCE.MODID + ":main/ancient_knowledge");
            Advancement regicide = Advancement.Builder.advancement()
                    .display(ModItems.PURE_BLOOD_0.get(), Component.translatable("advancement.vampirism.regicide"), Component.translatable("advancement.vampirism.regicide.desc"), null, FrameType.CHALLENGE, true, true, true)
                    .parent(vampire_forest)
                    .addCriterion("main", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(ModEntities.VAMPIRE_BARON.get())))
                    .save(consumer, REFERENCE.MODID + ":main/regicide");
            Advancement jumpScare = Advancement.Builder.advancement()
                    .display(Items.SKELETON_SKULL, Component.translatable("advancement.vampirism.jump_scare"), Component.translatable("advancement.vampirism.jump_scare.desc"), null, FrameType.TASK, true, true, true)
                    .parent(vampire_forest)
                    .addCriterion("main", KilledTrigger.TriggerInstance.entityKilledPlayer(EntityPredicate.Builder.entity().of(ModEntities.GHOST.get())))
                    .save(consumer, REFERENCE.MODID + ":main/jump_scare");
        }
    }

    private static class VampireAdvancements implements VampirismAdvancementSubProvider {

        @SuppressWarnings("unused")
        @Override
        public void generate(@NotNull Advancement root, HolderLookup.@NotNull Provider holderProvider, @NotNull Consumer<Advancement> consumer) {
            Advancement become_vampire = Advancement.Builder.advancement()
                    .display(ModItems.VAMPIRE_FANG.get(), Component.translatable("advancement.vampirism.become_vampire"), Component.translatable("advancement.vampirism.become_vampire.desc"), null, FrameType.TASK, true, false, false)
                    .parent(root)
                    .addCriterion("main", FactionCriterionTrigger.level(VReference.VAMPIRE_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/become_vampire");
            Advancement bat = Advancement.Builder.advancement()
                    .display(Items.FEATHER, Component.translatable("advancement.vampirism.bat"), Component.translatable("advancement.vampirism.bat.desc"), null, FrameType.TASK, true, true, true)
                    .parent(become_vampire)
                    .addCriterion("action", VampireActionCriterionTrigger.builder(VampireActionCriterionTrigger.Action.BAT))
                    .addCriterion("main", FactionCriterionTrigger.level(VReference.VAMPIRE_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/bat");
            Advancement first_blood = Advancement.Builder.advancement()
                    .display(ModItems.BLOOD_BOTTLE.get(), Component.translatable("advancement.vampirism.sucking_blood"), Component.translatable("advancement.vampirism.sucking_blood.desc"), null, FrameType.TASK, true, true, true)
                    .parent(become_vampire)
                    .addCriterion("flower", VampireActionCriterionTrigger.builder(VampireActionCriterionTrigger.Action.SUCK_BLOOD))
                    .addCriterion("main", FactionCriterionTrigger.level(VReference.VAMPIRE_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/first_blood");
            Advancement blood_cult = Advancement.Builder.advancement()
                    .display(ModBlocks.ALTAR_INFUSION.get(), Component.translatable("advancement.vampirism.blood_cult"), Component.translatable("advancement.vampirism.blood_cult.desc"), null, FrameType.TASK, true, true, true)
                    .parent(become_vampire)
                    .addCriterion("flower", VampireActionCriterionTrigger.builder(VampireActionCriterionTrigger.Action.PERFORM_RITUAL_INFUSION))
                    .addCriterion("main", FactionCriterionTrigger.level(VReference.VAMPIRE_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/blood_cult");
            Advancement extra_storage = Advancement.Builder.advancement()
                    .display(ModBlocks.BLOOD_CONTAINER.get(), Component.translatable("advancement.vampirism.extra_storage"), Component.translatable("advancement.vampirism.extra_storage.desc"), null, FrameType.TASK, true, true, true)
                    .parent(first_blood)
                    .addCriterion("blood_container", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.BLOOD_CONTAINER.get()))
                    .addCriterion("main", FactionCriterionTrigger.level(VReference.VAMPIRE_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/extra_storage");
            Advancement max_level = Advancement.Builder.advancement()
                    .display(ModItems.VAMPIRE_FANG.get(), Component.translatable("advancement.vampirism.max_level_vampire"), Component.translatable("advancement.vampirism.max_level_vampire.desc"), null, FrameType.GOAL, true, true, true)
                    .parent(bat)
                    .addCriterion("level", FactionCriterionTrigger.level(VReference.VAMPIRE_FACTION, 14))
                    .rewards(AdvancementRewards.Builder.experience(100))
                    .save(consumer, REFERENCE.MODID + ":vampire/max_level");
            Advancement sniped = Advancement.Builder.advancement()
                    .display(Items.ARROW, Component.translatable("advancement.vampirism.sniped"), Component.translatable("advancement.vampirism.sniped.desc"), null, FrameType.TASK, true, true, true)
                    .parent(bat)
                    .addCriterion("flower", VampireActionCriterionTrigger.builder(VampireActionCriterionTrigger.Action.SNIPED_IN_BAT))
                    .addCriterion("main", FactionCriterionTrigger.level(VReference.VAMPIRE_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/sniped");
            CompoundTag potion = new ItemStack(Items.POTION).serializeNBT();
            potion.putString("Potion", "minecraft:poison");
            Advancement yuck = Advancement.Builder.advancement()
                    .display(new DisplayInfo(ItemStack.of(potion), Component.translatable("advancement.vampirism.yuck"), Component.translatable("advancement.vampirism.yuck.desc"), null, FrameType.TASK, true, true, true))
                    .parent(first_blood)
                    .addCriterion("flower", VampireActionCriterionTrigger.builder(VampireActionCriterionTrigger.Action.POISONOUS_BITE))
                    .addCriterion("main", FactionCriterionTrigger.level(VReference.VAMPIRE_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/yuck");
            Advancement freeze_kill = Advancement.Builder.advancement()
                    .display(new DisplayInfo(new ItemStack(Items.CLOCK), Component.translatable("advancement.vampirism.freeze_kill"), Component.translatable("advancement.vampirism.freeze_kill.desc"), null, FrameType.TASK, true, true, true))
                    .parent(blood_cult)
                    .addCriterion("kill", VampireActionCriterionTrigger.builder(VampireActionCriterionTrigger.Action.KILL_FROZEN_HUNTER))
                    .addCriterion("main", FactionCriterionTrigger.level(VReference.VAMPIRE_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/freeze_kill");
            Advancement max_lord = Advancement.Builder.advancement()
                    .display(ModItems.VAMPIRE_MINION_UPGRADE_SPECIAL.get(), Component.translatable("advancement.vampirism.max_lord_vampire"), Component.translatable("advancement.vampirism.max_lord_vampire.desc"), null, FrameType.CHALLENGE, true, true, true)
                    .parent(max_level)
                    .addCriterion("level", FactionCriterionTrigger.lord(VReference.VAMPIRE_FACTION, 5))
                    .save(consumer, REFERENCE.MODID + ":vampire/max_lord");
            Advancement kill_mother = Advancement.Builder.advancement()
                    .display(ModItems.MOTHER_CORE.get(), Component.translatable("advancement.vampirism.vampire_kill_mother"), Component.translatable("advancement.vampirism.vampire_kill_mother.desc"), null, FrameType.CHALLENGE, true, true, true)
                    .parent(become_vampire)
                    .addCriterion("killed", new PlayerTrigger.TriggerInstance(ModAdvancements.TRIGGER_MOTHER_WIN.getId(), ContextAwarePredicate.ANY))
                    .addCriterion("main", FactionCriterionTrigger.level(VReference.VAMPIRE_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/kill_mother");
        }
    }

    private static class MinionAdvancements implements VampirismAdvancementSubProvider {

        @Override
        public void generate(@NotNull Advancement root, HolderLookup.@NotNull Provider holderProvider, @NotNull Consumer<Advancement> consumer) {
            Advancement become_lord = Advancement.Builder.advancement()
                    .display(Items.PAPER, Component.translatable("advancement.vampirism.become_lord"), Component.translatable("advancement.vampirism.become_lord.desc"), null, FrameType.TASK, true, true, true)
                    .parent(root)
                    .addCriterion("level", FactionCriterionTrigger.lord(null, 1))
                    .save(consumer, REFERENCE.MODID + ":minion/become_lord");
            Advancement collect_blood = Advancement.Builder.advancement()
                    .display(ModItems.BLOOD_BOTTLE.get(), Component.translatable("advancement.vampirism.collect_blood"), Component.translatable("advancement.vampirism.collect_blood.desc"), null, FrameType.TASK, true, true, true)
                    .parent(become_lord)
                    .addCriterion("task", MinionTaskCriterionTrigger.tasks(MinionTasks.COLLECT_BLOOD.get()))
                    .save(consumer, REFERENCE.MODID + ":minion/collect_blood");
            Advancement collect_hunter_items = Advancement.Builder.advancement()
                    .display(Items.GOLD_NUGGET, Component.translatable("advancement.vampirism.collect_hunter_items"), Component.translatable("advancement.vampirism.collect_hunter_items.desc"), null, FrameType.TASK, true, true, true)
                    .parent(become_lord)
                    .addCriterion("task", MinionTaskCriterionTrigger.tasks(MinionTasks.COLLECT_HUNTER_ITEMS.get()))
                    .save(consumer, REFERENCE.MODID + ":minion/collect_hunter_items");
            Advancement protect_lord = Advancement.Builder.advancement()
                    .display(Items.SHIELD, Component.translatable("advancement.vampirism.protect_lord"), Component.translatable("advancement.vampirism.protect_lord.desc"), null, FrameType.TASK, true, true, true)
                    .parent(become_lord)
                    .addCriterion("task", MinionTaskCriterionTrigger.tasks(MinionTasks.PROTECT_LORD.get()))
                    .save(consumer, REFERENCE.MODID + ":minion/protect_lord");
            Advancement defend_area = Advancement.Builder.advancement()
                    .display(Items.SHIELD, Component.translatable("advancement.vampirism.defend_area"), Component.translatable("advancement.vampirism.defend_area.desc"), null, FrameType.TASK, true, true, true)
                    .parent(become_lord)
                    .addCriterion("task", MinionTaskCriterionTrigger.tasks(MinionTasks.DEFEND_AREA.get()))
                    .save(consumer, REFERENCE.MODID + ":minion/defend_area");
        }
    }

}
