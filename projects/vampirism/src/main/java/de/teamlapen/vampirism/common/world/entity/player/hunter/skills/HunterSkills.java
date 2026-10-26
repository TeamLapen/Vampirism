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

    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> HUNTER_ROOT = SKILLS.registerSkill("hunter_root", HunterSkill::new);

    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> HOW_TO_USE_STAKE = SKILLS.registerSkill("how_to_use_stake", props -> new HunterSkill(props.cost(2).withDescription(ConfigComponent.config(ModConfig.balance().hsInstantKill1FromBehind, Component.translatable("skill.vampirism.how_to_use_stake.desc", ConfigComponent.calculateDouble(ModConfig.balance().hsInstantKill1MaxHealth, 100, ConfigComponent.Operator.MULTIPLY)), Component.translatable("skill.vampirism.how_to_use_stake.desc.behind", ConfigComponent.calculateDouble(ModConfig.balance().hsInstantKill1MaxHealth, 100, ConfigComponent.Operator.MULTIPLY))))));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> CAMOUFLAGE = SKILLS.registerSkill("camouflage", props -> new HunterSkill(props.cost(1).actionSkill(HunterActions.CAMOUFLAGE).withDescription()));

    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> APPRENTICE_ALCHEMY = SKILLS.registerSkill("apprentice_alchemy", props -> new HunterSkill(props.cost(1).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> VAMPIRE_REPELLING = SKILLS.registerSkill("vampire_repelling", props -> new HunterSkill(props.cost(1).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> REFINED_GARLIC = SKILLS.registerSkill("refined_garlic", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> AUGMENTED_DIFFUSERS = SKILLS.registerSkill("augmented_diffusers", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> CRUCIFIX_WIELDER = SKILLS.registerSkill("crucifix_wielder", props -> new HunterSkill(props.cost(1).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> SIXTH_SENSE = SKILLS.registerSkill("sixth_sense", props -> new HunterSkill(props.cost(2).withDescription().actionSkill(HunterActions.SIXTH_SENSE)));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> CRUCIFIXION = SKILLS.registerSkill("crucifixion", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> CRUCIFIX_REPELLING = SKILLS.registerSkill("crucifix_repelling", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> GREATER_BLESSING = SKILLS.registerSkill("greater_blessing", props -> new HunterSkill(props.cost(2).withDescription()));

    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> BASIC_TECHNOLOGY = SKILLS.registerSkill("basic_technology", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> NEAR_BREACH_REFORGING = SKILLS.registerSkill("near_breach_reforging", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> ATTACK_HEAVY_AS_HAMMER = SKILLS.registerSkill("attack_heavy_as_hammer", props -> new HunterSkill(props.cost(2).withDescription().attribute(Attributes.ATTACK_DAMAGE, () -> ModConfig.balance().hsSmallAttackDamageModifier.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> ATTACK_SWIFT_AS_WIND = SKILLS.registerSkill("attack_swift_as_wind", props -> new HunterSkill(props.cost(2).withDescription().attribute(Attributes.ATTACK_SPEED, () -> ModConfig.balance().hsSmallAttackSpeedModifier.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> ARMOR_BOUND_SWIFTNESS = SKILLS.registerSkill("armor_bound_swiftness", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> ARMOR_BOUND_HIGHVAULT = SKILLS.registerSkill("armor_bound_highvault", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> CROSSBOW_TECHNIQUE = SKILLS.registerSkill("crossbow_technique", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> DOUBLE_IT = SKILLS.registerSkill("double_it", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> DUAL_WIELDING = SKILLS.registerSkill("dual_wielding", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> MASTER_CRAFTSMANSHIP = SKILLS.registerSkill("master_craftsmanship", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> HOW_TO_ACTUALLY_USE_AXE = SKILLS.registerSkill("how_to_actually_use_axe", props -> new HunterSkill(props.cost(3).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> HOW_TO_ACTUALLY_USE_STAKE = SKILLS.registerSkill("how_to_actually_use_stake", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> ARTISAN_CRAFTSMANSHIP = SKILLS.registerSkill("artisan_craftsmanship", props -> new HunterSkill(props.cost(3).withDescription()));

    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> MULTITASK_BREWING = SKILLS.registerSkill("multitask_brewing", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> CONCENTRATED_BREWING = SKILLS.registerSkill("concentrated_brewing", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> DURABLE_BREWING = SKILLS.registerSkill("durable_brewing", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> SWIFT_BREWING = SKILLS.registerSkill("swift_brewing", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> EFFICIENT_BREWING = SKILLS.registerSkill("efficient_brewing", props -> new HunterSkill(props.cost(2).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> FORGOTTEN_FORMULARY = SKILLS.registerSkill("forgotten_formulary", props -> new HunterSkill(props.cost(3).withDescription()));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> POTION_RESISTANCE = SKILLS.registerSkill("potion_resistance", props -> new HunterSkill(props.cost(2).withDescription().actionSkill(HunterActions.POTION_RESISTANCE)));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> CONCENTRATED_DURABLE_BREWING = SKILLS.registerSkill("concentrated_durable_brewing", props -> new HunterSkill(props.cost(2).withDescription()));

    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> HUNTER_LORD_ROOT = SKILLS.registerSkill("hunter_lord_root", props -> new HunterSkill(props.tree(FactionSkillTreeTags.LORD)));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> HUNTER_BETTER_MINIONS = SKILLS.registerSkill("hunter_better_minions", props -> new HunterSkill(props.cost(3).withDescription().tree(FactionSkillTreeTags.LORD).onEnable(FactionConsumer.ENABLE_MINION_INCREASED_STATS).onDisable(FactionConsumer.DISABLE_MINION_INCREASED_STATS)));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> MINION_TECHNOLOGY = SKILLS.registerSkill("minion_technology", props -> new HunterSkill(props.cost(1).withDescription().tree(FactionSkillTreeTags.LORD)));

    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> MARSHALL_ROOT = SKILLS.registerSkill("marshall_root", props -> new HunterSkill(props.withDescription().tree(ModSkillTreeTags.MARSHALL)));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> MASTER_ALCHEMIST = SKILLS.registerSkill("master_alchemist", props -> new HunterSkill(props.cost(1).withDescription().tree(ModSkillTreeTags.MARSHALL)));
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> ULTIMATE_BREWER = SKILLS.registerSkill("ultimate_brewer", props -> new HunterSkill(props.cost(1).withDescription().tree(ModSkillTreeTags.MARSHALL)));


    @ApiStatus.Internal
    public static void register(IEventBus bus) {
        SKILLS.register(bus);
    }

    public static class Segments {

        // Level
        public static final ResourceKey<ISkillSegment> KEY_HUNTER_ROOT = segment("hunter_root");
        public static final ResourceKey<ISkillSegment> KEY_HOW_TO_USE_STAKE = segment("how_to_use_stake");
        public static final ResourceKey<ISkillSegment> KEY_CAMOUFLAGE = segment("camouflage");

        public static final ResourceKey<ISkillSegment> KEY_APPRENTICE_ALCHEMY = segment("apprentice_alchemy");
        public static final ResourceKey<ISkillSegment> KEY_VAMPIRE_REPELLING = segment("vampire_repelling");
        public static final ResourceKey<ISkillSegment> KEY_REFINED_GARLIC = segment("refined_garlic");
        public static final ResourceKey<ISkillSegment> KEY_AUGMENTED_DIFFUSERS = segment("augmented_diffusers");
        public static final ResourceKey<ISkillSegment> KEY_CRUCIFIX_WIELDER = segment("crucifix_wielder");
        public static final ResourceKey<ISkillSegment> KEY_SIXTH_SENSE = segment("sixth_sense");
        public static final ResourceKey<ISkillSegment> KEY_CRUCIFIXION = segment("crucifixion");
        public static final ResourceKey<ISkillSegment> KEY_CRUCIFIX_REPELLING = segment("crucifix_repelling");
        public static final ResourceKey<ISkillSegment> KEY_GREATER_BLESSING = segment("greater_blessing");

        public static final ResourceKey<ISkillSegment> KEY_BASIC_TECHNOLOGY = segment("basic_technology");
        public static final ResourceKey<ISkillSegment> KEY_NEAR_BREACH_REFORGING = segment("near_breach_reforging");
        public static final ResourceKey<ISkillSegment> KEY_ATTACK_HEAVY_AS_HAMMER = segment("attack_heavy_as_hammer");
        public static final ResourceKey<ISkillSegment> KEY_ATTACK_SWIFT_AS_WIND = segment("attack_swift_as_wind");
        public static final ResourceKey<ISkillSegment> KEY_ARMOR_BOUND_SWIFTNESS = segment("armor_bound_swiftness");
        public static final ResourceKey<ISkillSegment> KEY_ARMOR_BOUND_HIGHVAULT = segment("armor_bound_highvault");
        public static final ResourceKey<ISkillSegment> KEY_CROSSBOW_TECHNIQUE = segment("crossbow_technique");
        public static final ResourceKey<ISkillSegment> KEY_DOUBLE_IT_OR_DUAL_WIELDING = segment("double_it_or_dual_wielding");
        public static final ResourceKey<ISkillSegment> KEY_MASTER_CRAFTSMANSHIP = segment("master_craftsmanship");
        public static final ResourceKey<ISkillSegment> KEY_HOW_TO_ACTUALLY_USE_AXE = segment("how_to_actually_use_axe");
        public static final ResourceKey<ISkillSegment> KEY_HOW_TO_ACTUALLY_USE_STAKE = segment("how_to_actually_use_stake");
        public static final ResourceKey<ISkillSegment> KEY_ARTISAN_CRAFTSMANSHIP = segment("artisan_craftsmanship");

        public static final ResourceKey<ISkillSegment> KEY_MULTITASK_BREWING = segment("multitask_brewing");
        public static final ResourceKey<ISkillSegment> KEY_CONCENTRATED_OR_DURABLE_BREWING = segment("concentrated_or_durable_brewing");
        public static final ResourceKey<ISkillSegment> KEY_SWIFT_OR_EFFICIENT_BREWING = segment("swift_or_efficient_brewing");
        public static final ResourceKey<ISkillSegment> KEY_FORGOTTEN_FORMULARY = segment("forgotten_formulary");
        public static final ResourceKey<ISkillSegment> KEY_POTION_RESISTANCE = segment("potion_resistance");
        public static final ResourceKey<ISkillSegment> KEY_CONCENTRATED_DURABLE_BREWING = segment("concentrated_durable_brewing");

        // Lord
        public static final ResourceKey<ISkillSegment> KEY_HUNTER_LORD_ROOT = segment("hunter_lord_root");
        public static final ResourceKey<ISkillSegment> KEY_HUNTER_BETTER_MINIONS = segment("hunter_better_minions");
        public static final ResourceKey<ISkillSegment> KEY_MINION_TECHNOLOGY = segment("minion_technology");
        public static final ResourceKey<ISkillSegment> KEY_LORD_MOVEMENT_OR_ATTACK_SPEED = segment("lord_movement_or_attack_speed");
        public static final ResourceKey<ISkillSegment> KEY_SUPPLY_COLLECTION = segment("supply_collection");
        public static final ResourceKey<ISkillSegment> KEY_MINION_RECOVERY = segment("minion_recovery");
        public static final ResourceKey<ISkillSegment> KEY_RESOURCE_DIVERSITY = segment("resource_diversity");

        // Marshall
        public static final ResourceKey<ISkillSegment> KEY_MARSHALL_ROOT = segment("marshall_root");
        public static final ResourceKey<ISkillSegment> KEY_MASTER_ALCHEMIST = segment("master_alchemist");
        public static final ResourceKey<ISkillSegment> KEY_ULTIMATE_BREWER = segment("ultimate_brewer");

        private static ResourceKey<ISkillSegment> segment(String path) {
            return ResourceKey.create(FactionRegistries.Keys.SKILL_SEGMENT, VIdentifier.mod("hunter/" + path));
        }

        public static void createSkillSegments(BootstrapContext<ISkillSegment> context) {
            level(KEY_HUNTER_ROOT, HUNTER_ROOT)
                    .register(context);
            level(KEY_HOW_TO_USE_STAKE, HOW_TO_USE_STAKE)
                    .parents(KEY_HUNTER_ROOT)
                    .register(context);
            level(KEY_CAMOUFLAGE, CAMOUFLAGE)
                    .parents(KEY_HOW_TO_USE_STAKE)
                    .register(context);

            level(KEY_APPRENTICE_ALCHEMY, APPRENTICE_ALCHEMY)
                    .parents(KEY_CAMOUFLAGE)
                    .register(context);
            level(KEY_VAMPIRE_REPELLING, VAMPIRE_REPELLING)
                    .parents(KEY_APPRENTICE_ALCHEMY)
                    .register(context);
            level(KEY_REFINED_GARLIC, REFINED_GARLIC)
                    .parents(KEY_VAMPIRE_REPELLING)
                    .register(context);
            level(KEY_AUGMENTED_DIFFUSERS, AUGMENTED_DIFFUSERS)
                    .parents(KEY_VAMPIRE_REPELLING)
                    .after(KEY_REFINED_GARLIC)
                    .register(context);
            level(KEY_CRUCIFIX_WIELDER, CRUCIFIX_WIELDER)
                    .parents(KEY_APPRENTICE_ALCHEMY)
                    .after(KEY_VAMPIRE_REPELLING)
                    .register(context);
            level(KEY_SIXTH_SENSE, SIXTH_SENSE)
                    .parents(KEY_CRUCIFIX_WIELDER)
                    .register(context);
            level(KEY_CRUCIFIXION, CRUCIFIXION)
                    .parents(KEY_SIXTH_SENSE)
                    .register(context);
            level(KEY_CRUCIFIX_REPELLING, CRUCIFIX_REPELLING)
                    .parents(KEY_CRUCIFIXION)
                    .register(context);
            level(KEY_GREATER_BLESSING, GREATER_BLESSING)
                    .parents(KEY_CRUCIFIXION)
                    .after(KEY_CRUCIFIX_REPELLING)
                    .register(context);

            level(KEY_BASIC_TECHNOLOGY, BASIC_TECHNOLOGY)
                    .parents(KEY_CAMOUFLAGE)
                    .register(context);
            level(KEY_NEAR_BREACH_REFORGING, NEAR_BREACH_REFORGING)
                    .parents(KEY_BASIC_TECHNOLOGY)
                    .register(context);
            level(KEY_ATTACK_HEAVY_AS_HAMMER, ATTACK_HEAVY_AS_HAMMER)
                    .parents(KEY_BASIC_TECHNOLOGY)
                    .after(KEY_NEAR_BREACH_REFORGING)
                    .register(context);
            level(KEY_ATTACK_SWIFT_AS_WIND, ATTACK_SWIFT_AS_WIND)
                    .parents(KEY_BASIC_TECHNOLOGY)
                    .after(KEY_ATTACK_HEAVY_AS_HAMMER)
                    .register(context);
            level(KEY_ARMOR_BOUND_SWIFTNESS, ARMOR_BOUND_SWIFTNESS)
                    .parents(KEY_ATTACK_HEAVY_AS_HAMMER, KEY_ATTACK_SWIFT_AS_WIND)
                    .register(context);
            level(KEY_ARMOR_BOUND_HIGHVAULT, ARMOR_BOUND_HIGHVAULT)
                    .parents(KEY_ATTACK_HEAVY_AS_HAMMER, KEY_ATTACK_SWIFT_AS_WIND)
                    .after(KEY_ARMOR_BOUND_SWIFTNESS)
                    .register(context);
            level(KEY_CROSSBOW_TECHNIQUE, CROSSBOW_TECHNIQUE)
                    .parents(KEY_ARMOR_BOUND_SWIFTNESS, KEY_ARMOR_BOUND_HIGHVAULT)
                    .register(context);
            level(KEY_DOUBLE_IT_OR_DUAL_WIELDING, DOUBLE_IT, DUAL_WIELDING)
                    .parents(KEY_CROSSBOW_TECHNIQUE)
                    .register(context);
            level(KEY_MASTER_CRAFTSMANSHIP, MASTER_CRAFTSMANSHIP)
                    .parents(KEY_CROSSBOW_TECHNIQUE)
                    .after(KEY_DOUBLE_IT_OR_DUAL_WIELDING)
                    .register(context);
            level(KEY_HOW_TO_ACTUALLY_USE_AXE, HOW_TO_ACTUALLY_USE_AXE)
                    .parents(KEY_MASTER_CRAFTSMANSHIP)
                    .register(context);
            level(KEY_HOW_TO_ACTUALLY_USE_STAKE, HOW_TO_ACTUALLY_USE_STAKE)
                    .parents(KEY_HOW_TO_ACTUALLY_USE_AXE)
                    .register(context);
            level(KEY_ARTISAN_CRAFTSMANSHIP, ARTISAN_CRAFTSMANSHIP)
                    .parents(KEY_HOW_TO_ACTUALLY_USE_AXE)
                    .after(KEY_HOW_TO_ACTUALLY_USE_STAKE)
                    .register(context);

            level(KEY_MULTITASK_BREWING, MULTITASK_BREWING)
                    .parents(KEY_CAMOUFLAGE)
                    .after(KEY_APPRENTICE_ALCHEMY)
                    .register(context);
            level(KEY_CONCENTRATED_OR_DURABLE_BREWING, CONCENTRATED_BREWING, DURABLE_BREWING)
                    .parents(KEY_MULTITASK_BREWING)
                    .register(context);
            level(KEY_SWIFT_OR_EFFICIENT_BREWING, SWIFT_BREWING, EFFICIENT_BREWING)
                    .parents(KEY_CONCENTRATED_OR_DURABLE_BREWING)
                    .register(context);
            level(KEY_FORGOTTEN_FORMULARY, FORGOTTEN_FORMULARY)
                    .parents(KEY_SWIFT_OR_EFFICIENT_BREWING)
                    .register(context);
            level(KEY_POTION_RESISTANCE, POTION_RESISTANCE)
                    .parents(KEY_FORGOTTEN_FORMULARY)
                    .register(context);
            level(KEY_CONCENTRATED_DURABLE_BREWING, CONCENTRATED_DURABLE_BREWING)
                    .parents(KEY_POTION_RESISTANCE)
                    .register(context);

            lord(KEY_HUNTER_LORD_ROOT, HUNTER_LORD_ROOT)
                    .register(context);
            lord(KEY_HUNTER_BETTER_MINIONS, HUNTER_BETTER_MINIONS)
                    .parents(KEY_HUNTER_LORD_ROOT)
                    .register(context);
            lord(KEY_MINION_TECHNOLOGY, MINION_TECHNOLOGY)
                    .parents(KEY_HUNTER_BETTER_MINIONS)
                    .register(context);
            lord(KEY_LORD_MOVEMENT_OR_ATTACK_SPEED, LordSkills.LORD_SPEED, LordSkills.LORD_ATTACK_SPEED)
                    .parents(KEY_HUNTER_LORD_ROOT)
                    .after(KEY_HUNTER_BETTER_MINIONS)
                    .register(context);
            lord(KEY_SUPPLY_COLLECTION, FactionSkills.MINION_COLLECT)
                    .parents(KEY_HUNTER_LORD_ROOT)
                    .after(KEY_LORD_MOVEMENT_OR_ATTACK_SPEED)
                    .register(context);
            lord(KEY_MINION_RECOVERY, FactionSkills.MINION_RECOVERY)
                    .parents(KEY_HUNTER_LORD_ROOT)
                    .after(KEY_SUPPLY_COLLECTION)
                    .register(context);
            lord(KEY_RESOURCE_DIVERSITY, FactionSkills.RESOURCE_DIVERSITY)
                    .parents(KEY_SUPPLY_COLLECTION)
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
