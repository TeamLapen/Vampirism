package de.teamlapen.vampirism.common.world.entity.player.vampire.skills;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillSegment;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import de.teamlapen.faction.api.registries.skills.DeferredSkill;
import de.teamlapen.faction.api.registries.skills.DeferredSkillRegister;
import de.teamlapen.faction.common.advancements.criterion.PlayerFactionSubPredicate;
import de.teamlapen.faction.common.core.FactionConsumer;
import de.teamlapen.faction.common.core.FactionSkills;
import de.teamlapen.faction.common.factions.skills.SkillSegment;
import de.teamlapen.faction.common.factions.skills.SkillTree;
import de.teamlapen.faction.common.util.ConfigComponent;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.advancements.critereon.DraculaCriterion;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModAttributes;
import de.teamlapen.vampirism.common.core.ModConsumer;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.tags.ModSkillTreeTags;
import de.teamlapen.vampirism.common.world.entity.player.lord.skills.LordSkills;
import de.teamlapen.vampirism.common.world.entity.player.vampire.actions.VampireActions;
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
 * Registers the default vampire skills
 */
@SuppressWarnings("unused")
public class VampireSkills {
    public static final DeferredSkillRegister SKILLS = DeferredSkillRegister.create(REFERENCE.MODID);

    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> VAMPIRE_ROOT = SKILLS.registerSkill("vampire_root", VampireSkill::new);

    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> NIGHT_VISION = SKILLS.registerSkill("night_vision", props -> new VampireSkill(props.cost(2).withDescription().onEnable(ModConsumer.ENABLE_AND_ACTIVATE_VAMPIRE_NIGHT_VISION).onDisable(ModConsumer.DISABLE_VAMPIRE_NIGHT_VISION)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> REGENERATION = SKILLS.registerSkill("regeneration", props -> new VampireSkill(props.cost(2).withDescription().actionSkill(VampireActions.REGENERATION)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> NO_LONGER_FLEDGLING = SKILLS.registerSkill("no_longer_fledgling", props -> new VampireSkill(props.cost(2).withDescription().unlocks(VampireActions.BAT_FORM).unlocks(VampireActions.INFECT)));

    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> PERMANENT_SPEED_BOOST = SKILLS.registerSkill("permanent_speed_boost", props -> new VampireSkill(props.cost(2).withDescription().attribute(Attributes.MOVEMENT_SPEED, () -> ModConfig.balance().vsSpeedBoost.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> PERMANENT_JUMP_BOOST = SKILLS.registerSkill("permanent_jump_boost", props -> new VampireSkill(props.cost(2).actionSkill(VampireActions.JUMP_BOOST)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> TOUGH_SKIN = SKILLS.registerSkill("tough_skin", props -> new VampireSkill(props.cost(2).withDescription().attribute(ModAttributes.SUNDAMAGE, () -> ModConfig.balance().vsSundamageReduction1.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> WATER_RESISTANCE = SKILLS.registerSkill("water_resistance", props -> new VampireSkill(props.cost(1).withDescription().onEnable(ModConsumer.ENABLE_VAMPIRE_WATER_RESISTANCE).onDisable(ModConsumer.DISABLE_VAMPIRE_WATER_RESISTANCE)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> FAST_RECOVERY = SKILLS.registerSkill("fast_recovery", props -> new VampireSkill(props.cost(1).withDescription().attribute(ModAttributes.NEONATAL_DURATION, () -> ModConfig.balance().vsNeonatalReduction.get() - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> SWIFT_RESURRECTION = SKILLS.registerSkill("swift_resurrection", props -> new VampireSkill(props.cost(1).withDescription().attribute(ModAttributes.DBNO_DURATION, () -> ModConfig.balance().vsDbnoReduction.get() - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> DAMAGE_LIMITER = SKILLS.registerSkill("damage_limiter", props -> new VampireSkill(props.cost(2).withDescription().actionSkill(VampireActions.DAMAGE_LIMITER)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> TELEPORT = SKILLS.registerSkill("teleport", props -> new VampireSkill(props.cost(3).withDescription().actionSkill(VampireActions.TELEPORT)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> SUNSCREEN = SKILLS.registerSkill("sunscreen", props -> new VampireSkill(props.cost(2).withDescription().actionSkill(VampireActions.SUNSCREEN)));

    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> SUMMON_BATS = SKILLS.registerSkill("summon_bats", props -> new VampireSkill(props.cost(1).withDescription().actionSkill(VampireActions.SUMMON_BATS)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> DISGUISE = SKILLS.registerSkill("disguise", props -> new VampireSkill(props.cost(1).withDescription().actionSkill(VampireActions.DISGUISE)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> ADVANCED_BITER = SKILLS.registerSkill("advanced_biter", props -> new VampireSkill(props.cost(1).withDescription().onEnable(ModConsumer.ENABLE_VAMPIRE_ADVANCED_BITER).onDisable(ModConsumer.DISABLE_VAMPIRE_ADVANCED_BITER)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> FRUGAL_VAMPIRE = SKILLS.registerSkill("frugal_vampire", props -> new VampireSkill(props.cost(1).withDescription().attribute(ModAttributes.BLOOD_EXHAUSTION, () -> ModConfig.balance().vsBloodThirstReduction1.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> BLOOD_VISION = SKILLS.registerSkill("blood_vision", props -> new VampireSkill(props.cost(2).withDescription().onEnable(ModConsumer.ENABLE_VAMPIRE_BLOOD_VISION).onDisable(ModConsumer.DISABLE_VAMPIRE_BLOOD_VISION)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> HISSING = SKILLS.registerSkill("hissing", props -> new VampireSkill(props.cost(1).withDescription().actionSkill(VampireActions.HISSING)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> DARK_STALKER = SKILLS.registerSkill("dark_stalker", props -> new VampireSkill(props.cost(2).withDescription().actionSkill(VampireActions.DARK_STALKER)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> INVISIBILITY = SKILLS.registerSkill("invisibility", props -> new VampireSkill(props.cost(3).withDescription().actionSkill(VampireActions.INVISIBILITY)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> GARLIC_BLOOD_VISION = SKILLS.registerSkill("garlic_blood_vision", props -> new VampireSkill(props.cost(1).withDescription().onEnable(ModConsumer.ENABLE_VAMPIRE_GARLIC_VISION).onDisable(ModConsumer.DISABLE_VAMPIRE_GARLIC_VISION)));

    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> VAMPIRE_RAGE = SKILLS.registerSkill("vampire_rage", props -> new VampireSkill(props.cost(2).withDescription().actionSkill(VampireActions.VAMPIRE_RAGE)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> PERMANENT_ATTACK_DAMAGE_BOOST = SKILLS.registerSkill("permanent_attack_damage_boost", props -> new VampireSkill(props.cost(2).withDescription()
            .attribute(Attributes.ATTACK_DAMAGE, () -> ModConfig.balance().vsSmallAttackDamageModifier.get(), AttributeModifier.Operation.ADD_VALUE)
            .attribute(Attributes.ATTACK_DAMAGE, () -> ModConfig.balance().vsSmallAttackDamageMultiplier.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> PERMANENT_ATTACK_SPEED_BOOST = SKILLS.registerSkill("permanent_attack_speed_boost", props -> new VampireSkill(props.cost(2).withDescription().attribute(Attributes.ATTACK_SPEED, () -> ModConfig.balance().vsSmallAttackSpeedModifier.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> BLOOD_CHARGE = SKILLS.registerSkill("blood_charge", props -> new VampireSkill(props.cost(1).withDescription()));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> FINISHER = SKILLS.registerSkill("finisher", props -> new VampireSkill(props.cost(3).withDescription(Component.translatable("skill.vampirism.finisher.desc", ConfigComponent.calculateDouble(ModConfig.balance().vsSwordFinisherMaxHealth, 100, ConfigComponent.Operator.MULTIPLY)))));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> DARK_BLOOD_PROJECTILE = SKILLS.registerSkill("dark_blood_projectile", props -> new VampireSkill(props.cost(2).withDescription().actionSkill(VampireActions.DARK_BLOOD_PROJECTILE)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> FREEZE = SKILLS.registerSkill("freeze", props -> new VampireSkill(props.cost(2).withDescription().actionSkill(VampireActions.FREEZE)));

    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> VAMPIRE_LORD_ROOT = SKILLS.registerSkill("vampire_lord_root", VampireLordSkill::new);

    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> VAMPIRE_BETTER_MINIONS = SKILLS.registerSkill("vampire_better_minions", props -> new VampireLordSkill(props.cost(3).withDescription().onEnable(FactionConsumer.ENABLE_MINION_INCREASED_STATS).onDisable(FactionConsumer.DISABLE_MINION_INCREASED_STATS)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> BLOOD_COLLECTION = SKILLS.registerSkill("blood_collection", props -> new VampireLordSkill(props.cost(2).withDescription()));

    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> DRACULA_ROOT = SKILLS.registerSkill("dracula_root", props -> new DraculaSkill(props.withDescription()));

    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> MIST = SKILLS.registerSkill("mist", props -> new DraculaSkill(props.cost(1).withDescription().actionSkill(VampireActions.MIST)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> AURA_OF_DARKNESS = SKILLS.registerSkill("aura_of_darkness", props -> new DraculaSkill(props.cost(1).withDescription().actionSkill(VampireActions.AURA_OF_DARKNESS)));

    @ApiStatus.Internal
    public static void register(IEventBus bus) {
        SKILLS.register(bus);
    }

    public static class Segments {

        // Level
        public static final ResourceKey<ISkillSegment> KEY_VAMPIRE_ROOT = segment("vampire_root");
        public static final ResourceKey<ISkillSegment> KEY_NIGHT_VISION = segment("night_vision");
        public static final ResourceKey<ISkillSegment> KEY_REGENERATION = segment("regeneration");
        public static final ResourceKey<ISkillSegment> KEY_NO_LONGER_FLEDGLING = segment("no_longer_fledgling");

        public static final ResourceKey<ISkillSegment> KEY_PERMANENT_SPEED_OR_JUMP_BOOST = segment("permanent_speed_or_jump_boost");
        public static final ResourceKey<ISkillSegment> KEY_TOUGH_SKIN = segment("tough_skin");
        public static final ResourceKey<ISkillSegment> KEY_WATER_RESISTANCE = segment("water_resistance");
        public static final ResourceKey<ISkillSegment> KEY_FAST_RECOVERY_OR_SWIFT_RESURRECTION = segment("fast_recovery_or_swift_resurrection");
        public static final ResourceKey<ISkillSegment> KEY_DAMAGE_LIMITER = segment("damage_limiter");
        public static final ResourceKey<ISkillSegment> KEY_TELEPORT = segment("teleport");
        public static final ResourceKey<ISkillSegment> KEY_SUNSCREEN = segment("sunscreen");

        public static final ResourceKey<ISkillSegment> KEY_SUMMON_BATS = segment("summon_bats");
        public static final ResourceKey<ISkillSegment> KEY_DISGUISE = segment("disguise");
        public static final ResourceKey<ISkillSegment> KEY_ADVANCED_BITER = segment("advanced_biter");
        public static final ResourceKey<ISkillSegment> KEY_FRUGAL_VAMPIRE = segment("frugal_vampire");
        public static final ResourceKey<ISkillSegment> KEY_BLOOD_VISION = segment("blood_vision");
        public static final ResourceKey<ISkillSegment> KEY_HISSING = segment("hissing");
        public static final ResourceKey<ISkillSegment> KEY_DARK_STALKER = segment("dark_stalker");
        public static final ResourceKey<ISkillSegment> KEY_INVISIBILITY = segment("invisibility");
        public static final ResourceKey<ISkillSegment> KEY_GARLIC_BLOOD_VISION = segment("garlic_blood_vision");

        public static final ResourceKey<ISkillSegment> KEY_VAMPIRE_RAGE = segment("vampire_rage");
        public static final ResourceKey<ISkillSegment> KEY_PERMANENT_ATTACK_DAMAGE_BOOST = segment("permanent_attack_damage_boost");
        public static final ResourceKey<ISkillSegment> KEY_PERMANENT_ATTACK_SPEED_BOOST = segment("permanent_attack_speed_boost");
        public static final ResourceKey<ISkillSegment> KEY_BLOOD_CHARGE = segment("blood_charge");
        public static final ResourceKey<ISkillSegment> KEY_FINISHER = segment("finisher");
        public static final ResourceKey<ISkillSegment> KEY_DARK_BLOOD_PROJECTILE = segment("dark_blood_projectile");
        public static final ResourceKey<ISkillSegment> KEY_FREEZE = segment("freeze");

        // Lord
        public static final ResourceKey<ISkillSegment> KEY_VAMPIRE_LORD_ROOT = segment("vampire_lord_root");
        public static final ResourceKey<ISkillSegment> KEY_VAMPIRE_BETTER_MINIONS = segment("vampire_better_minions");
        public static final ResourceKey<ISkillSegment> KEY_LORD_MOVEMENT_OR_ATTACK_SPEED = segment("lord_movement_or_attack_speed");
        public static final ResourceKey<ISkillSegment> KEY_BLOOD_COLLECTION = segment("blood_collection");
        public static final ResourceKey<ISkillSegment> KEY_MINION_RECOVERY = segment("minion_recovery");

        // Dracula
        public static final ResourceKey<ISkillSegment> KEY_DRACULA_ROOT = segment("dracula_root");
        public static final ResourceKey<ISkillSegment> KEY_MIST = segment("mist");
        public static final ResourceKey<ISkillSegment> KEY_AURA_OF_DARKNESS = segment("aura_of_darkness");

        private static ResourceKey<ISkillSegment> segment(String path) {
            return ResourceKey.create(FactionRegistries.Keys.SKILL_SEGMENT, VIdentifier.mod("vampire/" + path));
        }

        public static void createSkillSegments(BootstrapContext<ISkillSegment> context) {
            level(KEY_VAMPIRE_ROOT, VAMPIRE_ROOT)
                    .register(context);
            level(KEY_NIGHT_VISION, NIGHT_VISION)
                    .parents(KEY_VAMPIRE_ROOT)
                    .register(context);
            level(KEY_REGENERATION, REGENERATION)
                    .parents(KEY_NIGHT_VISION)
                    .register(context);
            level(KEY_NO_LONGER_FLEDGLING, NO_LONGER_FLEDGLING)
                    .parents(KEY_REGENERATION)
                    .register(context);

            level(KEY_PERMANENT_SPEED_OR_JUMP_BOOST, PERMANENT_SPEED_BOOST, PERMANENT_JUMP_BOOST)
                    .parents(KEY_NO_LONGER_FLEDGLING)
                    .register(context);
            level(KEY_TOUGH_SKIN, TOUGH_SKIN)
                    .parents(KEY_PERMANENT_SPEED_OR_JUMP_BOOST)
                    .register(context);
            level(KEY_WATER_RESISTANCE, WATER_RESISTANCE)
                    .parents(KEY_PERMANENT_SPEED_OR_JUMP_BOOST)
                    .after(KEY_TOUGH_SKIN)
                    .register(context);
            level(KEY_FAST_RECOVERY_OR_SWIFT_RESURRECTION, FAST_RECOVERY, SWIFT_RESURRECTION)
                    .parents(KEY_TOUGH_SKIN, KEY_WATER_RESISTANCE)
                    .register(context);
            level(KEY_DAMAGE_LIMITER, DAMAGE_LIMITER)
                    .parents(KEY_FAST_RECOVERY_OR_SWIFT_RESURRECTION)
                    .register(context);
            level(KEY_TELEPORT, TELEPORT)
                    .parents(KEY_DAMAGE_LIMITER)
                    .register(context);
            level(KEY_SUNSCREEN, SUNSCREEN)
                    .parents(KEY_DAMAGE_LIMITER)
                    .after(KEY_TELEPORT)
                    .register(context);

            level(KEY_SUMMON_BATS, SUMMON_BATS)
                    .parents(KEY_NO_LONGER_FLEDGLING)
                    .after(KEY_PERMANENT_SPEED_OR_JUMP_BOOST)
                    .register(context);
            level(KEY_DISGUISE, DISGUISE)
                    .parents(KEY_SUMMON_BATS)
                    .register(context);
            level(KEY_ADVANCED_BITER, ADVANCED_BITER)
                    .parents(KEY_DISGUISE)
                    .register(context);
            level(KEY_FRUGAL_VAMPIRE, FRUGAL_VAMPIRE)
                    .parents(KEY_ADVANCED_BITER)
                    .register(context);
            level(KEY_BLOOD_VISION, BLOOD_VISION)
                    .parents(KEY_ADVANCED_BITER)
                    .after(KEY_FRUGAL_VAMPIRE)
                    .register(context);
            level(KEY_HISSING, HISSING)
                    .parents(KEY_BLOOD_VISION)
                    .register(context);
            level(KEY_DARK_STALKER, DARK_STALKER)
                    .parents(KEY_HISSING)
                    .register(context);
            level(KEY_INVISIBILITY, INVISIBILITY)
                    .parents(KEY_HISSING)
                    .after(KEY_DARK_STALKER)
                    .register(context);
            level(KEY_GARLIC_BLOOD_VISION, GARLIC_BLOOD_VISION)
                    .parents(KEY_BLOOD_VISION)
                    .after(KEY_HISSING)
                    .register(context);

            level(KEY_VAMPIRE_RAGE, VAMPIRE_RAGE)
                    .parents(KEY_NO_LONGER_FLEDGLING)
                    .after(KEY_SUMMON_BATS)
                    .register(context);
            level(KEY_PERMANENT_ATTACK_DAMAGE_BOOST, PERMANENT_ATTACK_DAMAGE_BOOST)
                    .parents(KEY_VAMPIRE_RAGE)
                    .register(context);
            level(KEY_PERMANENT_ATTACK_SPEED_BOOST, PERMANENT_ATTACK_SPEED_BOOST)
                    .parents(KEY_VAMPIRE_RAGE)
                    .after(KEY_PERMANENT_ATTACK_DAMAGE_BOOST)
                    .register(context);
            level(KEY_BLOOD_CHARGE, BLOOD_CHARGE)
                    .parents(KEY_PERMANENT_ATTACK_DAMAGE_BOOST, KEY_PERMANENT_ATTACK_SPEED_BOOST)
                    .register(context);
            level(KEY_FINISHER, FINISHER)
                    .parents(KEY_BLOOD_CHARGE)
                    .register(context);
            level(KEY_DARK_BLOOD_PROJECTILE, DARK_BLOOD_PROJECTILE)
                    .parents(KEY_BLOOD_CHARGE)
                    .after(KEY_FINISHER)
                    .register(context);
            level(KEY_FREEZE, FREEZE)
                    .parents(KEY_DARK_BLOOD_PROJECTILE)
                    .register(context);


            lord(KEY_VAMPIRE_LORD_ROOT, VAMPIRE_LORD_ROOT)
                    .register(context);
            lord(KEY_VAMPIRE_BETTER_MINIONS, VAMPIRE_BETTER_MINIONS)
                    .parents(KEY_VAMPIRE_LORD_ROOT)
                    .register(context);
            lord(KEY_LORD_MOVEMENT_OR_ATTACK_SPEED, LordSkills.LORD_SPEED, LordSkills.LORD_ATTACK_SPEED)
                    .parents(KEY_VAMPIRE_LORD_ROOT)
                    .after(KEY_VAMPIRE_BETTER_MINIONS)
                    .register(context);
            lord(KEY_BLOOD_COLLECTION, BLOOD_COLLECTION)
                    .parents(KEY_VAMPIRE_LORD_ROOT)
                    .after(KEY_LORD_MOVEMENT_OR_ATTACK_SPEED)
                    .register(context);
            lord(KEY_MINION_RECOVERY, FactionSkills.MINION_RECOVERY)
                    .parents(KEY_VAMPIRE_LORD_ROOT)
                    .after(KEY_BLOOD_COLLECTION)
                    .register(context);

            dracula(KEY_DRACULA_ROOT, DRACULA_ROOT)
                    .register(context);
            dracula(KEY_MIST, MIST)
                    .parents(KEY_DRACULA_ROOT)
                    .register(context);
            dracula(KEY_AURA_OF_DARKNESS, AURA_OF_DARKNESS)
                    .parents(KEY_DRACULA_ROOT)
                    .after(KEY_MIST)
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
        public static SkillSegment.Builder dracula(ResourceKey<ISkillSegment> key, Holder<? extends ISkill<?>>... skills) {
            return SkillSegment.Builder.of(Trees.DRACULA, key, skills);
        }
    }

    public static class Trees {

        public static final ResourceKey<ISkillTree> LEVEL = tree("level");
        public static final ResourceKey<ISkillTree> LORD = tree("lord");
        public static final ResourceKey<ISkillTree> DRACULA = tree("dracula");

        private static ResourceKey<ISkillTree> tree(String path) {
            return ResourceKey.create(FactionRegistries.Keys.SKILL_TREE, VIdentifier.mod("vampire/" + path));
        }

        public static void createSkillTrees(BootstrapContext<ISkillTree> context) {
            context.register(LEVEL, new SkillTree(ModFactions.VAMPIRE, EntityPredicate.Builder.entity().subPredicate(PlayerFactionSubPredicate.faction(ModFactions.VAMPIRE)).build(), new ItemStackTemplate(ModItems.VAMPIRE_BOOK.get()), Component.translatable("gui.vampirism.skills.level"), Optional.of(VIdentifier.mod("block/dark_stone_bricks"))));
            context.register(LORD, new SkillTree(ModFactions.VAMPIRE, EntityPredicate.Builder.entity().subPredicate(PlayerFactionSubPredicate.lord(ModFactions.VAMPIRE)).build(), new ItemStackTemplate(ModItems.VAMPIRE_MINION_BINDING.get()), Component.translatable("gui.vampirism.skills.lord"), Optional.of(VIdentifier.mod("block/dark_stone_bricks")), List.of(LEVEL)));
            context.register(DRACULA, new SkillTree(ModFactions.VAMPIRE, EntityPredicate.Builder.entity().subPredicate(DraculaCriterion.INSTANCE).build(), new ItemStackTemplate(ModItems.VAMPIRE_CLOTHING_HAT), Component.translatable("gui.vampirism.skills.dracula"), Optional.of(VIdentifier.mod("block/dark_stone_bricks")), ModSkillTreeTags.DRACULA, List.of(LORD)));
        }
    }
}
