package de.teamlapen.vampirism.data;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.advancements.*;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import net.minecraft.advancements.*;
import net.minecraft.advancements.criterion.*;
import net.minecraft.data.AdvancementProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class AdvancementGenerator extends AdvancementProvider {
    public AdvancementGenerator(DataGenerator generatorIn) {
        super(generatorIn);
        MainAdvancements main = new MainAdvancements();
        this.tabs = ImmutableList.of(main, new HunterAdvancements(main::getRoot), new VampireAdvancements(main::getRoot), new MinionAdvancements(main::getRoot));
    }

    private static class HunterAdvancements implements Consumer<Consumer<Advancement>> {

        private final Supplier<Advancement> root;

        public HunterAdvancements(Supplier<Advancement> root) {
            this.root = root;
        }

        @Override
        public void accept(Consumer<Advancement> consumer) {
            Advancement become_hunter = Advancement.Builder.advancement()
                    .display(ModItems.item_garlic, new TranslationTextComponent("advancement.vampirism.become_hunter"), new TranslationTextComponent("advancement.vampirism.become_hunter.desc"), null, FrameType.TASK, true, false, false)
                    .parent(root.get())
                    .addCriterion("main", TriggerFaction.builder(VReference.HUNTER_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":hunter/become_hunter");
            Advancement stake = Advancement.Builder.advancement()
                    .display(ModItems.stake, new TranslationTextComponent("advancement.vampirism.stake"), new TranslationTextComponent("advancement.vampirism.stake.desc"), null, FrameType.CHALLENGE, true, true, true)
                    .parent(become_hunter)
                    .addCriterion("flower", HunterActionTrigger.builder(HunterActionTrigger.Action.STAKE))
                    .rewards(AdvancementRewards.Builder.experience(100))
                    .save(consumer, REFERENCE.MODID + ":hunter/stake");
            Advancement betrayal = Advancement.Builder.advancement()
                    .display(ModItems.human_heart, new TranslationTextComponent("advancement.vampirism.betrayal"), new TranslationTextComponent("advancement.vampirism.betrayal.desc"), null, FrameType.TASK, true, true, true)
                    .parent(become_hunter)
                    .addCriterion("kill", KilledTrigger.Instance.playerKilledEntity(EntityPredicate.Builder.entity().of(ModTags.Entities.HUNTER)))
                    .addCriterion("faction", TriggerFaction.builder(VReference.HUNTER_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":hunter/betrayal");
            Advancement max_level = Advancement.Builder.advancement()
                    .display(ModItems.item_garlic, new TranslationTextComponent("advancement.vampirism.max_level_hunter"), new TranslationTextComponent("advancement.vampirism.max_level_hunter.desc"), null, FrameType.GOAL, true, true, true)
                    .parent(stake)
                    .addCriterion("level", TriggerFaction.builder(VReference.HUNTER_FACTION, 14))
                    .rewards(AdvancementRewards.Builder.experience(100))
                    .save(consumer, REFERENCE.MODID + ":hunter/max_level");
            Advancement technology = Advancement.Builder.advancement()
                    .display(ModItems.basic_tech_crossbow, new TranslationTextComponent("advancement.vampirism.technology"), new TranslationTextComponent("advancement.vampirism.technology.desc"), null, FrameType.TASK, true, true, true)
                    .parent(become_hunter)
                    .addCriterion("basic", InventoryChangeTrigger.Instance.hasItems(ModItems.basic_tech_crossbow))
                    .addCriterion("advanced", InventoryChangeTrigger.Instance.hasItems(ModItems.enhanced_tech_crossbow))
                    .requirements(IRequirementsStrategy.AND)
                    .save(consumer, REFERENCE.MODID + ":hunter/technology");
            Advancement max_lord = Advancement.Builder.advancement()
                    .display(ModItems.hunter_minion_upgrade_special, new TranslationTextComponent("advancement.vampirism.max_lord_hunter"), new TranslationTextComponent("advancement.vampirism.max_lord_hunter"), null, FrameType.CHALLENGE, true, true, true)
                    .parent(max_level)
                    .addCriterion("level", TriggerFaction.lord(VReference.HUNTER_FACTION, 5))
                    .save(consumer, REFERENCE.MODID + ":hunter/max_lord");
            Advancement cure_vampire = Advancement.Builder.advancement()
                    .display(ModItems.cure_apple, new TranslationTextComponent("advancement.vampirism.cure_vampire_villager"), new TranslationTextComponent("advancement.vampirism.cure_vampire_villager"), null, FrameType.TASK, true, true, true)
                    .parent(become_hunter)
                    .addCriterion("cure", CuredVampireVillagerTrigger.Instance.any())
                    .save(consumer, REFERENCE.MODID + ":hunter/cure_vampire_villager");
        }
    }

    private static class MainAdvancements implements Consumer<Consumer<Advancement>> {
        Advancement root;

        @Override
        public void accept(Consumer<Advancement> consumer) {
            root = Advancement.Builder.advancement()
                    .display(ModItems.vampire_fang, new TranslationTextComponent("itemGroup.vampirism"), new TranslationTextComponent("advancement.vampirism.desc"), new ResourceLocation(REFERENCE.MODID, "textures/block/castle_block_dark_brick.png"), FrameType.TASK, false, false, false)
                    .addCriterion("main", InventoryChangeTrigger.Instance.hasItems(ModItems.vampire_fang))
                    .addCriterion("second", InventoryChangeTrigger.Instance.hasItems(ModItems.item_garlic))
                    .requirements(IRequirementsStrategy.OR)
                    .save(consumer, REFERENCE.MODID + ":main/root");
            Advancement vampire_forest = Advancement.Builder.advancement()
                    .display(Items.OAK_LOG, new TranslationTextComponent("advancement.vampirism.vampire_forest"), new TranslationTextComponent("advancement.vampirism.vampire_forest.desc"), null, FrameType.TASK, true, true, true)
                    .parent(root)
                    .addCriterion("main", PositionTrigger.Instance.located(LocationPredicate.inBiome(ModBiomes.VAMPIRE_FOREST_KEY)))
                    .addCriterion("second", PositionTrigger.Instance.located(LocationPredicate.inBiome(ModBiomes.VAMPIRE_FOREST_HILLS_KEY)))
                    .requirements(IRequirementsStrategy.OR)
                    .save(consumer, REFERENCE.MODID + ":main/vampire_forest");
            Advancement ancient_knowledge = Advancement.Builder.advancement()
                    .display(ModItems.vampire_book, new TranslationTextComponent("advancement.vampirism.ancient_knowledge"), new TranslationTextComponent("advancement.vampirism.ancient_knowledge.desc"), null, FrameType.TASK, true, true, true)
                    .parent(vampire_forest)
                    .addCriterion("blood_container", InventoryChangeTrigger.Instance.hasItems(ModItems.vampire_book))
                    .save(consumer, REFERENCE.MODID + ":main/ancient_knowledge");
            Advancement regicide = Advancement.Builder.advancement()
                    .display(ModItems.pure_blood_0, new TranslationTextComponent("advancement.vampirism.regicide"), new TranslationTextComponent("advancement.vampirism.regicide.desc"), null, FrameType.CHALLENGE, true, true, true)
                    .parent(vampire_forest)
                    .addCriterion("main", KilledTrigger.Instance.playerKilledEntity(EntityPredicate.Builder.entity().of(ModEntities.vampire_baron)))
                    .save(consumer, REFERENCE.MODID + ":main/regicide");
        }

        public Advancement getRoot() {
            return root;
        }

    }

    private static class VampireAdvancements implements Consumer<Consumer<Advancement>> {

        private final Supplier<Advancement> root;

        public VampireAdvancements(Supplier<Advancement> root) {
            this.root = root;
        }

        @Override
        public void accept(Consumer<Advancement> consumer) {
            Advancement become_vampire = Advancement.Builder.advancement()
                    .display(ModItems.vampire_fang, new TranslationTextComponent("advancement.vampirism.become_vampire"), new TranslationTextComponent("advancement.vampirism.become_vampire.desc"), null, FrameType.TASK, true, false, false)
                    .parent(root.get())
                    .addCriterion("main", TriggerFaction.builder(VReference.VAMPIRE_FACTION, 1))
                    .save(consumer, REFERENCE.MODID + ":vampire/become_vampire");
            Advancement bat = Advancement.Builder.advancement()
                    .display(Items.FEATHER, new TranslationTextComponent("advancement.vampirism.bat"), new TranslationTextComponent("advancement.vampirism.bat.desc"), null, FrameType.TASK, true, true, true)
                    .parent(become_vampire)
                    .addCriterion("action", VampireActionTrigger.builder(VampireActionTrigger.Action.BAT))
                    .save(consumer, REFERENCE.MODID + ":vampire/bat");
            Advancement first_blood = Advancement.Builder.advancement()
                    .display(ModItems.blood_bottle, new TranslationTextComponent("advancement.vampirism.sucking_blood"), new TranslationTextComponent("advancement.vampirism.sucking_blood.desc"), null, FrameType.TASK, true, true, true)
                    .parent(become_vampire)
                    .addCriterion("flower", VampireActionTrigger.builder(VampireActionTrigger.Action.SUCK_BLOOD))
                    .save(consumer, REFERENCE.MODID + ":vampire/first_blood");
            Advancement blood_cult = Advancement.Builder.advancement()
                    .display(ModBlocks.altar_infusion, new TranslationTextComponent("advancement.vampirism.blood_cult"), new TranslationTextComponent("advancement.vampirism.blood_cult.desc"), null, FrameType.TASK, true, true, true)
                    .parent(become_vampire)
                    .addCriterion("flower", VampireActionTrigger.builder(VampireActionTrigger.Action.PERFORM_RITUAL_INFUSION))
                    .save(consumer, REFERENCE.MODID + ":vampire/blood_cult");
            Advancement extra_storage = Advancement.Builder.advancement()
                    .display(ModBlocks.blood_container, new TranslationTextComponent("advancement.vampirism.extra_storage"), new TranslationTextComponent("advancement.vampirism.extra_storage.desc"), null, FrameType.TASK, true, true, true)
                    .parent(first_blood)
                    .addCriterion("blood_container", InventoryChangeTrigger.Instance.hasItems(ModBlocks.blood_container))
                    .save(consumer, REFERENCE.MODID + ":vampire/extra_storage");
            Advancement max_level = Advancement.Builder.advancement()
                    .display(ModItems.vampire_fang, new TranslationTextComponent("advancement.vampirism.max_level_vampire"), new TranslationTextComponent("advancement.vampirism.max_level_vampire.desc"), null, FrameType.GOAL, true, true, true)
                    .parent(bat)
                    .addCriterion("level", TriggerFaction.builder(VReference.VAMPIRE_FACTION, 14))
                    .rewards(AdvancementRewards.Builder.experience(100))
                    .save(consumer, REFERENCE.MODID + ":vampire/max_level");
            Advancement sniped = Advancement.Builder.advancement()
                    .display(Items.ARROW, new TranslationTextComponent("advancement.vampirism.sniped"), new TranslationTextComponent("advancement.vampirism.sniped"), null, FrameType.TASK, true, true, true)
                    .parent(bat)
                    .addCriterion("flower", VampireActionTrigger.builder(VampireActionTrigger.Action.SNIPED_IN_BAT))
                    .save(consumer, REFERENCE.MODID + ":vampire/sniped");
            CompoundNBT potion = new ItemStack(Items.POTION).serializeNBT();
            potion.putString("Potion", "minecraft:poison");
            Advancement yuck = Advancement.Builder.advancement()
                    .display(new DisplayInfo(ItemStack.of(potion), new TranslationTextComponent("advancement.vampirism.yuck"), new TranslationTextComponent("advancement.vampirism.yuck"), null, FrameType.TASK, true, true, true))
                    .parent(first_blood)
                    .addCriterion("flower", VampireActionTrigger.builder(VampireActionTrigger.Action.POISONOUS_BITE))
                    .save(consumer, REFERENCE.MODID + ":vampire/yuck");
            Advancement freeze_kill = Advancement.Builder.advancement()
                    .display(new DisplayInfo(new ItemStack(Items.CLOCK), new TranslationTextComponent("advancement.vampirism.freeze_kill"), new TranslationTextComponent("advancement.vampirism.freeze_kill.desc"), null, FrameType.TASK, true, true, true))
                    .parent(blood_cult)
                    .addCriterion("kill", VampireActionTrigger.builder(VampireActionTrigger.Action.KILL_FROZEN_HUNTER))
                    .save(consumer, REFERENCE.MODID + ":vampire/freeze_kill");
            Advancement max_lord = Advancement.Builder.advancement()
                    .display(ModItems.vampire_minion_upgrade_special, new TranslationTextComponent("advancement.vampirism.max_lord_vampire"), new TranslationTextComponent("advancement.vampirism.max_lord_vampire"), null, FrameType.CHALLENGE, true, true, true)
                    .parent(max_level)
                    .addCriterion("level", TriggerFaction.lord(VReference.VAMPIRE_FACTION, 5))
                    .save(consumer, REFERENCE.MODID + ":vampire/max_lord");

        }
    }

    private static class MinionAdvancements implements Consumer<Consumer<Advancement>> {

        private final Supplier<Advancement> root;

        public MinionAdvancements(Supplier<Advancement> root) {
            this.root = root;
        }

        @Override
        public void accept(Consumer<Advancement> consumer) {
            Advancement become_lord = Advancement.Builder.advancement()
                    .display(Items.PAPER, new TranslationTextComponent("advancement.vampirism.become_lord"), new TranslationTextComponent("advancement.vampirism.become_lord"), null, FrameType.TASK, true, true, true)
                    .parent(root.get())
                    .addCriterion("level", TriggerFaction.lord(null, 1))
                    .save(consumer, REFERENCE.MODID + ":minion/become_lord");
            Advancement collect_blood = Advancement.Builder.advancement()
                    .display(ModItems.blood_bottle, new TranslationTextComponent("advancement.vampirism.collect_blood"), new TranslationTextComponent("advancement.vampirism.collect_blood.desc"), null, FrameType.TASK, true, true, true)
                    .parent(become_lord)
                    .addCriterion("task", MinionTaskTrigger.tasks(MinionTasks.collect_blood))
                    .save(consumer, REFERENCE.MODID + ":minion/collect_blood");
            Advancement collect_hunter_items = Advancement.Builder.advancement()
                    .display(Items.GOLD_NUGGET, new TranslationTextComponent("advancement.vampirism.collect_hunter_items"), new TranslationTextComponent("advancement.vampirism.collect_hunter_items.desc"), null, FrameType.TASK, true, true, true)
                    .parent(become_lord)
                    .addCriterion("task", MinionTaskTrigger.tasks(MinionTasks.collect_hunter_items))
                    .save(consumer, REFERENCE.MODID + ":minion/collect_hunter_items");
            Advancement protect_lord = Advancement.Builder.advancement()
                    .display(Items.SHIELD, new TranslationTextComponent("advancement.vampirism.protect_lord"), new TranslationTextComponent("advancement.vampirism.protect_lord.desc"), null, FrameType.TASK, true, true, true)
                    .parent(become_lord)
                    .addCriterion("task", MinionTaskTrigger.tasks(MinionTasks.protect_lord))
                    .save(consumer, REFERENCE.MODID + ":minion/protect_lord");
            Advancement defend_area = Advancement.Builder.advancement()
                    .display(Items.SHIELD, new TranslationTextComponent("advancement.vampirism.defend_area"), new TranslationTextComponent("advancement.vampirism.defend_area.desc"), null, FrameType.TASK, true, true, true)
                    .parent(become_lord)
                    .addCriterion("task", MinionTaskTrigger.tasks(MinionTasks.defend_area))
                    .save(consumer, REFERENCE.MODID + ":minion/defend_area");
        }
    }

}
