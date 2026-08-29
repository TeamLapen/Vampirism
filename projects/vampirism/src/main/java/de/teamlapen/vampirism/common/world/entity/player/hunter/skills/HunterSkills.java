package de.teamlapen.vampirism.common.world.entity.player.hunter.skills;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillSegment;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import de.teamlapen.faction.api.registries.skills.DeferredSkill;
import de.teamlapen.faction.api.registries.skills.DeferredSkillRegister;
import de.teamlapen.faction.api.tags.FactionSkillTreeTags;
import de.teamlapen.faction.common.advancements.criterion.PlayerFactionSubPredicate;
import de.teamlapen.faction.common.core.FactionConsumer;
import de.teamlapen.faction.common.core.FactionSkills;
import de.teamlapen.faction.common.factions.skills.SkillSegment;
import de.teamlapen.faction.common.factions.skills.SkillTree;
import de.teamlapen.faction.common.util.ConfigComponent;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.common.advancements.critereon.MarshallCriterion;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.tags.ModSkillTreeTags;
import de.teamlapen.vampirism.common.world.entity.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.common.world.entity.player.lord.skills.LordSkills;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStackTemplate;
import net.neoforged.bus.api.IEventBus;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Optional;

/**
 * Registers the default hunter skills
 */
@SuppressWarnings("unused")
public class HunterSkills {
    public static final DeferredSkillRegister SKILLS = DeferredSkillRegister.create(REFERENCE.MODID);

    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> LEVEL_ROOT = SKILLS.registerSkill(ModFactions.HUNTER.getKey().identifier().getPath(), HunterSkill::new);

    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> STAKE1 = SKILLS.registerSkill("stake1", props -> new HunterSkill(props.cost(2).withDescription(ConfigComponent.config(ModConfig.balance().hsInstantKill1FromBehind, Component.translatable("skill.vampirism.stake1.desc", ConfigComponent.calculateDouble(ModConfig.balance().hsInstantKill1MaxHealth, 100, ConfigComponent.Operator.MULTIPLY)), Component.translatable("skill.vampirism.stake1.desc.behind", ConfigComponent.calculateDouble(ModConfig.balance().hsInstantKill1MaxHealth, 100, ConfigComponent.Operator.MULTIPLY))))));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> HUNTER_DISGUISE = SKILLS.registerSkill("hunter_disguise", props -> new HunterSkill(props.cost(1).actionSkill(HunterActions.DISGUISE_HUNTER).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> WEAPON_TABLE = SKILLS.registerSkill("weapon_table", props -> new HunterSkill(props.cost(2).withDescription()));

    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> BASIC_ALCHEMY = SKILLS.registerSkill("basic_alchemy", props -> new HunterSkill(props.cost(1).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> CRUCIFIX_WIELDER = SKILLS.registerSkill("crucifix_wielder", props -> new HunterSkill(props.cost(1).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> GARLIC_DIFFUSER = SKILLS.registerSkill("garlic_diffuser", props -> new HunterSkill(props.cost(1).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> PURIFIED_GARLIC = SKILLS.registerSkill("purified_garlic", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> GARLIC_DIFFUSER_IMPROVED = SKILLS.registerSkill("garlic_diffuser_improved", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> ENHANCED_BLESSING = SKILLS.registerSkill("enhanced_blessing", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> ULTIMATE_CRUCIFIX = SKILLS.registerSkill("ultimate_crucifix", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> HUNTER_AWARENESS = SKILLS.registerSkill("hunter_awareness", props -> new HunterSkill(props.cost(2).withDescription().actionSkill(HunterActions.AWARENESS_HUNTER)));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> CRUCIFIX_REPEL = SKILLS.registerSkill("crucifix_repel", props -> new HunterSkill(props.cost(2).withDescription()));

    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> MULTITASK_BREWING = SKILLS.registerSkill("multitask_brewing", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> DURABLE_BREWING = SKILLS.registerSkill("durable_brewing", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> CONCENTRATED_BREWING = SKILLS.registerSkill("concentrated_brewing", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> SWIFT_BREWING = SKILLS.registerSkill("swift_brewing", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> EFFICIENT_BREWING = SKILLS.registerSkill("efficient_brewing", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> MASTER_BREWER = SKILLS.registerSkill("master_brewer", props -> new HunterSkill(props.cost(3).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> POTION_RESISTANCE = SKILLS.registerSkill("potion_resistance", props -> new HunterSkill(props.cost(2).withDescription().actionSkill(HunterActions.POTION_RESISTANCE_HUNTER)));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> CONCENTRATED_DURABLE_BREWING = SKILLS.registerSkill("concentrated_durable_brewing", props -> new HunterSkill(props.cost(2).withDescription()));

    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> HUNTER_ATTACK_SPEED = SKILLS.registerSkill("hunter_attack_speed", props -> new HunterSkill(props.cost(2).withDescription().attribute(Attributes.ATTACK_SPEED, () -> ModConfig.balance().hsSmallAttackSpeedModifier.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> HUNTER_ATTACK_DAMAGE = SKILLS.registerSkill("hunter_attack_damage", props -> new HunterSkill(props.cost(2).withDescription().attribute(Attributes.ATTACK_DAMAGE, () -> ModConfig.balance().hsSmallAttackDamageModifier.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> ARMOR_SPEED = SKILLS.registerSkill("armor_speed", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> ARMOR_JUMP = SKILLS.registerSkill("armor_jump", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> CROSSBOW_TECHNIQUE = SKILLS.registerSkill("crossbow_technique", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> DOUBLE_IT = SKILLS.registerSkill("double_it", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> DUAL_WIELDING = SKILLS.registerSkill("dual_wielding", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> MASTER_CRAFTSMANSHIP = SKILLS.registerSkill("master_craftsmanship", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> NEAR_BREACH_REFORGING = SKILLS.registerSkill("near_breach_reforging", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> STAKE2 = SKILLS.registerSkill("stake2", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> AXE2 = SKILLS.registerSkill("axe2", props -> new HunterSkill(props.cost(3).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> ARTISAN_CRAFTSMANSHIP = SKILLS.registerSkill("artisan_craftsmanship", props -> new HunterSkill(props.cost(3).withDescription()));

    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> LORD_ROOT = SKILLS.registerSkill(ModFactions.HUNTER.getKey().identifier().withSuffix("_lord").getPath(), props -> new HunterSkill(props.tree(FactionSkillTreeTags.LORD)));

    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> MINION_STATS_INCREASE = SKILLS.registerSkill("hunter_minion_stats_increase", props -> new HunterSkill(props.cost(3).withDescription().tree(FactionSkillTreeTags.LORD).onEnable(FactionConsumer.ENABLE_MINION_INCREASED_STATS).onDisable(FactionConsumer.DISABLE_MINION_INCREASED_STATS)));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> MINION_TECH_CROSSBOWS = SKILLS.registerSkill("minion_tech_crossbows", props -> new HunterSkill(props.cost(1).withDescription().tree(FactionSkillTreeTags.LORD)));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> MINION_COLLECT = SKILLS.registerSkill("hunter_minion_collect", props -> new HunterSkill(props.cost(2).withDescription().tree(FactionSkillTreeTags.LORD)));

    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> MARSHALL_ROOT = SKILLS.registerSkill(ModFactions.HUNTER.getKey().identifier().withSuffix("_marshall").getPath(), props -> new HunterSkill(props.withDescription().tree(ModSkillTreeTags.MARSHALL)));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> MASTER_ALCHEMIST = SKILLS.registerSkill("master_alchemist", props -> new HunterSkill(props.cost(1).withDescription().tree(ModSkillTreeTags.MARSHALL)));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> ULTIMATE_BREWER = SKILLS.registerSkill("ultimate_brewer", props -> new HunterSkill(props.cost(1).withDescription().tree(ModSkillTreeTags.MARSHALL)));


    @ApiStatus.Internal
    public static void register(IEventBus bus) {
        SKILLS.register(bus);
    }

    public static class Segments {

        // Level
        public static final ResourceKey<ISkillSegment> KEY_LEVEL_ROOT = segment("level_root");
        public static final ResourceKey<ISkillSegment> KEY_STAKE = segment("stake");
        public static final ResourceKey<ISkillSegment> KEY_DISGUISE = segment("disguise");
        public static final ResourceKey<ISkillSegment> KEY_BASIC_TECHNOLOGY = segment("basic_technology");

        public static final ResourceKey<ISkillSegment> KEY_BASIC_ALCHEMY = segment("basic_alchemy");
        public static final ResourceKey<ISkillSegment> KEY_GARLIC_DIFFUSER = segment("garlic_diffuser");
        public static final ResourceKey<ISkillSegment> KEY_PURIFIED_GARLIC = segment("purified_garlic");
        public static final ResourceKey<ISkillSegment> KEY_GARLIC_DIFFUSER_IMPROVED = segment("garlic_diffuser_improved");
        public static final ResourceKey<ISkillSegment> KEY_CRUCIFIX_WIELDER = segment("crucifix_wielder");
        public static final ResourceKey<ISkillSegment> KEY_HUNTER_AWARENESS = segment("hunter_awareness");
        public static final ResourceKey<ISkillSegment> KEY_ULTIMATE_CRUCIFIX = segment("ultimate_crucifix");
        public static final ResourceKey<ISkillSegment> KEY_CRUCIFIX_REPEL = segment("crucifix_repel");
        public static final ResourceKey<ISkillSegment> KEY_ENHANCED_BLESSING = segment("enhanced_blessing");

        public static final ResourceKey<ISkillSegment> KEY_MULTITASK_BREWING = segment("multitask_brewing");
        public static final ResourceKey<ISkillSegment> KEY_CONCENTRATED_OR_DURABLE_BREWING = segment("concentrated_or_durable_brewing");
        public static final ResourceKey<ISkillSegment> KEY_SWIFT_OR_EFFICIENT_BREWING = segment("swift_or_efficient_brewing");
        public static final ResourceKey<ISkillSegment> KEY_MASTER_BREWER = segment("master_brewer");
        public static final ResourceKey<ISkillSegment> KEY_POTION_RESISTANCE = segment("potion_resistance");
        public static final ResourceKey<ISkillSegment> KEY_CONCENTRATED_AND_DURABLE_BREWING = segment("concentrated_and_durable_brewing");

        public static final ResourceKey<ISkillSegment> KEY_NEAR_BREACH_REFORGING = segment("near_breach_reforging");
        public static final ResourceKey<ISkillSegment> KEY_ATTACK_DAMAGE = segment("attack_damage");
        public static final ResourceKey<ISkillSegment> KEY_ATTACK_SPEED = segment("attack_speed");
        public static final ResourceKey<ISkillSegment> KEY_ARMOR_BOUND_SPEED = segment("armor_bound_speed");
        public static final ResourceKey<ISkillSegment> KEY_ARMOR_BOUND_JUMP = segment("armor_bound_jump");
        public static final ResourceKey<ISkillSegment> KEY_CROSSBOW_TECHNIQUE = segment("crossbow_technique");
        public static final ResourceKey<ISkillSegment> KEY_DOUBLE_IT_OR_DUAL_WIELDING = segment("double_it_or_dual_wielding");
        public static final ResourceKey<ISkillSegment> KEY_MASTER_CRAFTSMANSHIP = segment("master_craftsmanship");
        public static final ResourceKey<ISkillSegment> KEY_ACTUALLY_USE_AXE = segment("actually_use_axe");
        public static final ResourceKey<ISkillSegment> KEY_ACTUALLY_USE_STAKE = segment("actually_use_stake");
        public static final ResourceKey<ISkillSegment> KEY_ARTISAN_CRAFTSMANSHIP = segment("artisan_craftsmanship");

        // Lord
        public static final ResourceKey<ISkillSegment> KEY_LORD_ROOT = segment("lord_root");
        public static final ResourceKey<ISkillSegment> KEY_BETTER_MINIONS = segment("better_minions");
        public static final ResourceKey<ISkillSegment> KEY_MINION_TECHNOLOGY = segment("minion_technology");
        public static final ResourceKey<ISkillSegment> KEY_LORD_MOVEMENT_OR_ATTACK_SPEED = segment("lord_movement_or_attack_speed");
        public static final ResourceKey<ISkillSegment> KEY_SUPPLY_COLLECTION = segment("supply_collection");
        public static final ResourceKey<ISkillSegment> KEY_MINION_RECOVERY = segment("minion_recovery");

        // Marshall
        public static final ResourceKey<ISkillSegment> KEY_MARSHALL_ROOT = segment("marshall_root");
        public static final ResourceKey<ISkillSegment> KEY_MASTER_ALCHEMIST = segment("master_alchemist");
        public static final ResourceKey<ISkillSegment> KEY_ULTIMATE_BREWER = segment("ultimate_brewer");

        private static ResourceKey<ISkillSegment> segment(String path) {
            return ResourceKey.create(FactionRegistries.Keys.SKILL_SEGMENT, VIdentifier.mod("hunter/" + path));
        }

        public static void createSkillSegments(BootstrapContext<ISkillSegment> context) {
            level(KEY_LEVEL_ROOT, LEVEL_ROOT)
                    .register(context);
            level(KEY_STAKE, STAKE1)
                    .parents(KEY_LEVEL_ROOT)
                    .register(context);
            level(KEY_DISGUISE, HUNTER_DISGUISE)
                    .parents(KEY_STAKE)
                    .register(context);

            level(KEY_BASIC_ALCHEMY, BASIC_ALCHEMY)
                    .parents(KEY_DISGUISE)
                    .register(context);
            level(KEY_GARLIC_DIFFUSER, GARLIC_DIFFUSER)
                    .parents(KEY_BASIC_ALCHEMY)
                    .register(context);
            level(KEY_PURIFIED_GARLIC, PURIFIED_GARLIC)
                    .parents(KEY_GARLIC_DIFFUSER)
                    .register(context);
            level(KEY_GARLIC_DIFFUSER_IMPROVED, GARLIC_DIFFUSER_IMPROVED)
                    .parents(KEY_GARLIC_DIFFUSER)
                    .after(KEY_PURIFIED_GARLIC)
                    .register(context);
            level(KEY_CRUCIFIX_WIELDER, CRUCIFIX_WIELDER)
                    .parents(KEY_BASIC_ALCHEMY)
                    .after(KEY_GARLIC_DIFFUSER)
                    .register(context);
            level(KEY_HUNTER_AWARENESS, HUNTER_AWARENESS)
                    .parents(KEY_CRUCIFIX_WIELDER)
                    .register(context);
            level(KEY_ULTIMATE_CRUCIFIX, ULTIMATE_CRUCIFIX)
                    .parents(KEY_HUNTER_AWARENESS)
                    .register(context);
            level(KEY_CRUCIFIX_REPEL, CRUCIFIX_REPEL)
                    .parents(KEY_ULTIMATE_CRUCIFIX)
                    .register(context);
            level(KEY_ENHANCED_BLESSING, ENHANCED_BLESSING)
                    .parents(KEY_ULTIMATE_CRUCIFIX)
                    .after(KEY_CRUCIFIX_REPEL)
                    .register(context);

            level(KEY_MULTITASK_BREWING, MULTITASK_BREWING)
                    .parents(KEY_DISGUISE)
                    .after(KEY_BASIC_ALCHEMY)
                    .register(context);
            level(KEY_CONCENTRATED_OR_DURABLE_BREWING, CONCENTRATED_BREWING, DURABLE_BREWING)
                    .parents(KEY_MULTITASK_BREWING)
                    .register(context);
            level(KEY_SWIFT_OR_EFFICIENT_BREWING, SWIFT_BREWING, EFFICIENT_BREWING)
                    .parents(KEY_CONCENTRATED_OR_DURABLE_BREWING)
                    .register(context);
            level(KEY_MASTER_BREWER, MASTER_BREWER)
                    .parents(KEY_SWIFT_OR_EFFICIENT_BREWING)
                    .register(context);
            level(KEY_POTION_RESISTANCE, POTION_RESISTANCE)
                    .parents(KEY_MASTER_BREWER)
                    .register(context);
            level(KEY_CONCENTRATED_AND_DURABLE_BREWING, CONCENTRATED_DURABLE_BREWING)
                    .parents(KEY_POTION_RESISTANCE)
                    .register(context);

            level(KEY_BASIC_TECHNOLOGY, WEAPON_TABLE)
                    .parents(KEY_DISGUISE)
                    .register(context);
            level(KEY_NEAR_BREACH_REFORGING, NEAR_BREACH_REFORGING)
                    .parents(KEY_BASIC_TECHNOLOGY)
                    .after(KEY_ATTACK_DAMAGE)
                    .register(context);
            level(KEY_ATTACK_DAMAGE, HUNTER_ATTACK_DAMAGE)
                    .parents(KEY_BASIC_TECHNOLOGY)
                    .register(context);
            level(KEY_ATTACK_SPEED, HUNTER_ATTACK_SPEED)
                    .parents(KEY_BASIC_TECHNOLOGY)
                    .after(KEY_NEAR_BREACH_REFORGING)
                    .register(context);
            level(KEY_ARMOR_BOUND_SPEED, ARMOR_SPEED)
                    .parents(KEY_ATTACK_DAMAGE, KEY_ATTACK_SPEED)
                    .register(context);
            level(KEY_ARMOR_BOUND_JUMP, ARMOR_JUMP)
                    .parents(KEY_ATTACK_DAMAGE, KEY_ATTACK_SPEED)
                    .after(KEY_ARMOR_BOUND_SPEED)
                    .register(context);
            level(KEY_CROSSBOW_TECHNIQUE, CROSSBOW_TECHNIQUE)
                    .parents(KEY_ARMOR_BOUND_SPEED, KEY_ARMOR_BOUND_JUMP)
                    .register(context);
            level(KEY_DOUBLE_IT_OR_DUAL_WIELDING, DOUBLE_IT, DUAL_WIELDING)
                    .parents(KEY_CROSSBOW_TECHNIQUE)
                    .register(context);
            level(KEY_MASTER_CRAFTSMANSHIP, MASTER_CRAFTSMANSHIP)
                    .parents(KEY_CROSSBOW_TECHNIQUE)
                    .after(KEY_DOUBLE_IT_OR_DUAL_WIELDING)
                    .register(context);
            level(KEY_ACTUALLY_USE_AXE, AXE2)
                    .parents(KEY_MASTER_CRAFTSMANSHIP)
                    .register(context);
            level(KEY_ACTUALLY_USE_STAKE, STAKE2)
                    .parents(KEY_ACTUALLY_USE_AXE)
                    .register(context);
            level(KEY_ARTISAN_CRAFTSMANSHIP, ARTISAN_CRAFTSMANSHIP)
                    .parents(KEY_ACTUALLY_USE_AXE)
                    .after(KEY_ACTUALLY_USE_STAKE)
                    .register(context);

            lord(KEY_LORD_ROOT, LORD_ROOT)
                    .register(context);
            lord(KEY_BETTER_MINIONS, MINION_STATS_INCREASE)
                    .parents(KEY_LORD_ROOT)
                    .register(context);
            lord(KEY_MINION_TECHNOLOGY, MINION_TECH_CROSSBOWS)
                    .parents(KEY_BETTER_MINIONS)
                    .register(context);
            lord(KEY_LORD_MOVEMENT_OR_ATTACK_SPEED, LordSkills.LORD_SPEED, LordSkills.LORD_ATTACK_SPEED)
                    .parents(KEY_LORD_ROOT)
                    .after(KEY_BETTER_MINIONS)
                    .register(context);
            lord(KEY_SUPPLY_COLLECTION, MINION_COLLECT)
                    .parents(KEY_LORD_ROOT)
                    .after(KEY_LORD_MOVEMENT_OR_ATTACK_SPEED)
                    .register(context);
            lord(KEY_MINION_RECOVERY, FactionSkills.MINION_RECOVERY)
                    .parents(KEY_LORD_ROOT)
                    .after(KEY_SUPPLY_COLLECTION)
                    .register(context);

            marshall(KEY_MARSHALL_ROOT, MARSHALL_ROOT)
                    .register(context);
            marshall(KEY_MASTER_ALCHEMIST, MASTER_ALCHEMIST)
                    .parents(KEY_MARSHALL_ROOT)
                    .register(context);
            marshall(KEY_ULTIMATE_BREWER, ULTIMATE_BREWER)
                    .parents(KEY_MARSHALL_ROOT)
                    .after(KEY_MASTER_ALCHEMIST)
                    .register(context);
        }

        @SafeVarargs
        public static SkillSegment.Builder level(ResourceKey<ISkillSegment> key, Holder<? extends ISkill<?>>... skills) {
            return SkillSegment.Builder.of(Trees.LEVEL, key, skills);
        }

        @SafeVarargs
        public static SkillSegment.Builder lord(ResourceKey<ISkillSegment> key, Holder<? extends ISkill<?>>... skills) {
            return SkillSegment.Builder.of(Trees.LORD, key, skills);
        }

        @SafeVarargs
        public static SkillSegment.Builder marshall(ResourceKey<ISkillSegment> key, Holder<? extends ISkill<?>>... skills) {
            return SkillSegment.Builder.of(Trees.MARSHALL, key, skills);
        }
    }

    public static class Trees {

        public static final ResourceKey<ISkillTree> LEVEL = tree("level");
        public static final ResourceKey<ISkillTree> LORD = tree("lord");
        public static final ResourceKey<ISkillTree> MARSHALL = tree("marshall");

        private static ResourceKey<ISkillTree> tree(String path) {
            return ResourceKey.create(FactionRegistries.Keys.SKILL_TREE, VIdentifier.mod("hunter/" + path));
        }

        public static void createSkillTrees(BootstrapContext<ISkillTree> context) {
            context.register(LEVEL, new SkillTree(ModFactions.HUNTER, EntityPredicate.Builder.entity().subPredicate(PlayerFactionSubPredicate.faction(ModFactions.HUNTER)).build(), new ItemStackTemplate(ModItems.VAMPIRE_BOOK), Component.translatable("gui.vampirism.skills.level"), Optional.of(VIdentifier.mc("block/spruce_planks"))));
            context.register(LORD, new SkillTree(ModFactions.HUNTER, EntityPredicate.Builder.entity().subPredicate(PlayerFactionSubPredicate.lord(ModFactions.HUNTER)).build(), new ItemStackTemplate(ModItems.HUNTER_MINION_EQUIPMENT), Component.translatable("gui.vampirism.skills.lord"), Optional.of(VIdentifier.mc("block/spruce_planks")), List.of(LEVEL)));
            context.register(MARSHALL, new SkillTree(ModFactions.HUNTER, EntityPredicate.Builder.entity().subPredicate(MarshallCriterion.INSTANCE).build(), new ItemStackTemplate(ModItems.STAKE), Component.translatable("gui.vampirism.skills.marshall"), Optional.of(VIdentifier.mc("block/spruce_planks")), ModSkillTreeTags.MARSHALL, List.of(LORD)));
        }
    }
}
