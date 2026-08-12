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

    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> LEVEL_ROOT = SKILLS.registerSkill(ModFactions.VAMPIRE.getKey().identifier().getPath(), VampireSkill::new);

    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> NIGHT_VISION = SKILLS.registerSkill("night_vision", props -> new VampireSkill(props.cost(2).withDescription().onEnable(ModConsumer.ENABLE_AND_ACTIVATE_VAMPIRE_NIGHT_VISION).onDisable(ModConsumer.DISABLE_VAMPIRE_NIGHT_VISION)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> VAMPIRE_REGENERATION = SKILLS.registerSkill("vampire_regeneration", props -> new VampireSkill(props.cost(2).withDescription().actionSkill(VampireActions.REGEN)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> FLEDGLING = SKILLS.registerSkill("fledgling", props -> new VampireSkill(props.cost(2).withDescription().unlocks(VampireActions.BAT).unlocks(VampireActions.INFECT)));

    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> VAMPIRE_RAGE = SKILLS.registerSkill("vampire_rage", props -> new VampireSkill(props.cost(2).withDescription().actionSkill(VampireActions.VAMPIRE_RAGE)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> ADVANCED_BITER = SKILLS.registerSkill("advanced_biter", props -> new VampireSkill(props.cost(1).withDescription().onEnable(ModConsumer.ENABLE_VAMPIRE_ADVANCED_BITER).onDisable(ModConsumer.DISABLE_VAMPIRE_ADVANCED_BITER)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> SWORD_FINISHER = SKILLS.registerSkill("sword_finisher", props -> new VampireSkill(props.cost(2).withDescription(Component.translatable("skill.vampirism.sword_finisher.desc", ConfigComponent.calculateDouble(ModConfig.balance().vsSwordFinisherMaxHealth, 100, ConfigComponent.Operator.MULTIPLY)))));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> DARK_BLOOD_PROJECTILE = SKILLS.registerSkill("dark_blood_projectile", props -> new VampireSkill(props.cost(2).withDescription().actionSkill(VampireActions.DARK_BLOOD_PROJECTILE)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> BLOOD_CHARGE = SKILLS.registerSkill("blood_charge", props -> new VampireSkill(props.cost(1).withDescription()));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> FREEZE = SKILLS.registerSkill("freeze", props -> new VampireSkill(props.cost(2).withDescription().actionSkill(VampireActions.FREEZE)));

    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> SUNSCREEN = SKILLS.registerSkill("sunscreen", props -> new VampireSkill(props.cost(2).withDescription().actionSkill(VampireActions.SUNSCREEN)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> VAMPIRE_ATTACK_SPEED = SKILLS.registerSkill("vampire_attack_speed", props -> new VampireSkill(props.cost(2).withDescription().attribute(Attributes.ATTACK_SPEED, () -> ModConfig.balance().vsSmallAttackSpeedModifier.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> VAMPIRE_SPEED = SKILLS.registerSkill("vampire_speed", props -> new VampireSkill(props.cost(2).withDescription().attribute(Attributes.MOVEMENT_SPEED, () -> ModConfig.balance().vsSpeedBoost.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> BLOOD_VISION = SKILLS.registerSkill("blood_vision", props -> new VampireSkill(props.cost(2).withDescription().onEnable(ModConsumer.ENABLE_VAMPIRE_BLOOD_VISION).onDisable(ModConsumer.DISABLE_VAMPIRE_BLOOD_VISION)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> BLOOD_VISION_GARLIC = SKILLS.registerSkill("blood_vision_garlic", props -> new VampireSkill(props.cost(1).withDescription().onEnable(ModConsumer.ENABLE_VAMPIRE_GARLIC_VISION).onDisable(ModConsumer.DISABLE_VAMPIRE_GARLIC_VISION)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> VAMPIRE_ATTACK_DAMAGE = SKILLS.registerSkill("vampire_attack_damage", props -> new VampireSkill(props.cost(2).withDescription()
            .attribute(Attributes.ATTACK_DAMAGE, () -> ModConfig.balance().vsSmallAttackDamageModifier.get(), AttributeModifier.Operation.ADD_VALUE)
            .attribute(Attributes.ATTACK_DAMAGE, () -> ModConfig.balance().vsSmallAttackDamageMultiplier.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> VAMPIRE_JUMP = SKILLS.registerSkill("vampire_jump", props -> new VampireSkill(props.cost(2).actionSkill(VampireActions.JUMP_BOOST)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> NEONATAL_DECREASE = SKILLS.registerSkill("neonatal_decrease", props -> new VampireSkill(props.cost(2).withDescription().attribute(ModAttributes.NEONATAL_DURATION, () -> ModConfig.balance().vsNeonatalReduction.get() - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> DBNO_DURATION = SKILLS.registerSkill("dbno_duration", props -> new VampireSkill(props.cost(2).withDescription().attribute(ModAttributes.DBNO_DURATION, () -> ModConfig.balance().vsDbnoReduction.get() - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> TELEPORT = SKILLS.registerSkill("teleport", props -> new VampireSkill(props.cost(3).withDescription().actionSkill(VampireActions.TELEPORT)));

    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> SUMMON_BATS = SKILLS.registerSkill("summon_bats", props -> new VampireSkill(props.cost(2).withDescription().actionSkill(VampireActions.SUMMON_BAT)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> LESS_SUNDAMAGE = SKILLS.registerSkill("less_sundamage", props -> new VampireSkill(props.cost(3).withDescription().attribute(ModAttributes.SUNDAMAGE, () -> ModConfig.balance().vsSundamageReduction1.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> WATER_RESISTANCE = SKILLS.registerSkill("water_resistance", props -> new VampireSkill(props.cost(2).withDescription().onEnable(ModConsumer.ENABLE_VAMPIRE_WATER_RESISTANCE).onDisable(ModConsumer.DISABLE_VAMPIRE_WATER_RESISTANCE)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> LESS_BLOOD_THIRST = SKILLS.registerSkill("less_blood_thirst", props -> new VampireSkill(props.cost(1).withDescription().attribute(ModAttributes.BLOOD_EXHAUSTION, () -> ModConfig.balance().vsBloodThirstReduction1.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> VAMPIRE_DISGUISE = SKILLS.registerSkill("vampire_disguise", props -> new VampireSkill(props.cost(1).withDescription().actionSkill(VampireActions.DISGUISE_VAMPIRE)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> HALF_INVULNERABLE = SKILLS.registerSkill("half_invulnerable", props -> new VampireSkill(props.cost(2).withDescription().actionSkill(VampireActions.HALF_INVULNERABLE)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> VAMPIRE_INVISIBILITY = SKILLS.registerSkill("vampire_invisibility", props -> new VampireSkill(props.cost(3).withDescription().actionSkill(VampireActions.VAMPIRE_INVISIBILITY)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> DARK_STALKER = SKILLS.registerSkill("dark_stalker", props -> new VampireSkill(props.cost(2).withDescription().actionSkill(VampireActions.DARK_STALKER)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> HISSING = SKILLS.registerSkill("hissing", props -> new VampireSkill(props.cost(1).withDescription().actionSkill(VampireActions.HISSING)));

    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> LORD_ROOT = SKILLS.registerSkill(ModFactions.VAMPIRE.getKey().identifier().withSuffix("_lord").getPath(), VampireLordSkill::new);

    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> MINION_STATS_INCREASE = SKILLS.registerSkill("vampire_minion_stats_increase", props -> new VampireLordSkill(props.cost(3).withDescription().onEnable(FactionConsumer.ENABLE_MINION_INCREASED_STATS).onDisable(FactionConsumer.DISABLE_MINION_INCREASED_STATS)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> MINION_COLLECT = SKILLS.registerSkill("vampire_minion_collect", props -> new VampireLordSkill(props.cost(2).withDescription()));

    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> DRACULA_ROOT = SKILLS.registerSkill(ModFactions.VAMPIRE.getKey().identifier().withSuffix("_dracula").getPath(), props -> new DraculaSkill(props.withDescription()));

    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> MIST_FORM = SKILLS.registerSkill("mist_form", props -> new DraculaSkill(props.cost(1).withDescription().actionSkill(VampireActions.MIST_FORM)));
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> AURA_OF_DARKNESS = SKILLS.registerSkill("aura_of_darkness", props -> new DraculaSkill(props.cost(1).withDescription().actionSkill(VampireActions.AURA_OF_DARKNESS)));

    @ApiStatus.Internal
    public static void register(IEventBus bus) {
        SKILLS.register(bus);
    }

    public static class Segments {

        // Level
        public static final ResourceKey<ISkillSegment> KEY_LEVEL_ROOT = segment("level_root");
        public static final ResourceKey<ISkillSegment> KEY_NIGHT_VISION = segment("night_vision");
        public static final ResourceKey<ISkillSegment> KEY_REGENERATION = segment("regeneration");
        public static final ResourceKey<ISkillSegment> KEY_NO_LONGER_FLEDGLING = segment("no_longer_fledgling");

        public static final ResourceKey<ISkillSegment> KEY_VAMPIRE_RAGE = segment("vampire_rage");
        public static final ResourceKey<ISkillSegment> KEY_ADVANCED_BITER = segment("advanced_biter");
        public static final ResourceKey<ISkillSegment> KEY_FINISHER = segment("finisher");
        public static final ResourceKey<ISkillSegment> KEY_DARK_BLOOD_PROJECTILE = segment("dark_blood_projectile");
        public static final ResourceKey<ISkillSegment> KEY_BLOOD_CHARGE = segment("blood_charge");
        public static final ResourceKey<ISkillSegment> KEY_FREEZE = segment("freeze");

        public static final ResourceKey<ISkillSegment> KEY_SUNSCREEN = segment("sunscreen");
        public static final ResourceKey<ISkillSegment> KEY_ATTACK_OR_MOVEMENT_SPEED = segment("attack_or_movement_speed");
        public static final ResourceKey<ISkillSegment> KEY_BLOOD_VISION = segment("blood_vision");
        public static final ResourceKey<ISkillSegment> KEY_GARLIC_BLOOD_VISION = segment("garlic_blood_vision");
        public static final ResourceKey<ISkillSegment> KEY_DAMAGE_OR_JUMP = segment("damage_or_jump");
        public static final ResourceKey<ISkillSegment> KEY_FAST_RECOVERY_OR_RESURRECTION = segment("fast_recovery_or_resurrection");
        public static final ResourceKey<ISkillSegment> KEY_TELEPORT = segment("teleport");

        public static final ResourceKey<ISkillSegment> KEY_SUMMON_BATS = segment("summon_bats");
        public static final ResourceKey<ISkillSegment> KEY_HISSING = segment("hissing");
        public static final ResourceKey<ISkillSegment> KEY_TOUGH_SKIN_OR_WATER_RESISTANCE = segment("tough_skin_or_water_resistance");
        public static final ResourceKey<ISkillSegment> KEY_FRUGAL_VAMPIRE = segment("frugal_vampire");
        public static final ResourceKey<ISkillSegment> KEY_HUMAN_DISGUISE = segment("human_disguise");
        public static final ResourceKey<ISkillSegment> KEY_DAMAGE_LIMITER = segment("damage_limiter");
        public static final ResourceKey<ISkillSegment> KEY_INVISIBILITY_OR_DARK_STALKER = segment("invisibility_or_dark_stalker");

        // Lord
        public static final ResourceKey<ISkillSegment> KEY_LORD_ROOT = segment("lord_root");
        public static final ResourceKey<ISkillSegment> KEY_BETTER_MINIONS = segment("better_minions");
        public static final ResourceKey<ISkillSegment> KEY_LORD_MOVEMENT_OR_ATTACK_SPEED = segment("lord_movement_or_attack_speed");
        public static final ResourceKey<ISkillSegment> KEY_BLOOD_COLLECTION = segment("blood_collection");
        public static final ResourceKey<ISkillSegment> KEY_MINION_RECOVERY = segment("minion_recovery");

        // Dracula
        public static final ResourceKey<ISkillSegment> KEY_DRACULA_ROOT = segment("dracula_root");
        public static final ResourceKey<ISkillSegment> KEY_MIST_FORM = segment("mist_form");
        public static final ResourceKey<ISkillSegment> KEY_AURA_OF_DARKNESS = segment("aura_of_darkness");

        private static ResourceKey<ISkillSegment> segment(String path) {
            return ResourceKey.create(FactionRegistries.Keys.SKILL_SEGMENT, VIdentifier.mod("vampire/" + path));
        }

        public static void createSkillSegments(BootstrapContext<ISkillSegment> context) {
            level(KEY_LEVEL_ROOT, LEVEL_ROOT)
                    .register(context);
            level(KEY_NIGHT_VISION, NIGHT_VISION)
                    .parents(KEY_LEVEL_ROOT)
                    .register(context);
            level(KEY_REGENERATION, VAMPIRE_REGENERATION)
                    .parents(KEY_NIGHT_VISION)
                    .register(context);
            level(KEY_NO_LONGER_FLEDGLING, FLEDGLING)
                    .parents(KEY_REGENERATION)
                    .register(context);

            level(KEY_VAMPIRE_RAGE, VAMPIRE_RAGE)
                    .parents(KEY_NO_LONGER_FLEDGLING)
                    .priority(0)
                    .register(context);
            level(KEY_ADVANCED_BITER, ADVANCED_BITER)
                    .parents(KEY_VAMPIRE_RAGE)
                    .register(context);
            level(KEY_FINISHER, SWORD_FINISHER)
                    .parents(KEY_ADVANCED_BITER)
                    .register(context);
            level(KEY_DARK_BLOOD_PROJECTILE, DARK_BLOOD_PROJECTILE)
                    .parents(KEY_FINISHER)
                    .register(context);
            level(KEY_BLOOD_CHARGE, BLOOD_CHARGE)
                    .parents(KEY_DARK_BLOOD_PROJECTILE)
                    .register(context);
            level(KEY_FREEZE, FREEZE)
                    .parents(KEY_BLOOD_CHARGE)
                    .register(context);

            level(KEY_SUNSCREEN, SUNSCREEN)
                    .parents(KEY_NO_LONGER_FLEDGLING)
                    .priority(1)
                    .register(context);
            level(KEY_ATTACK_OR_MOVEMENT_SPEED, VAMPIRE_ATTACK_SPEED, VAMPIRE_SPEED)
                    .parents(KEY_SUNSCREEN)
                    .register(context);
            level(KEY_BLOOD_VISION, BLOOD_VISION)
                    .parents(KEY_ATTACK_OR_MOVEMENT_SPEED)
                    .register(context);
            level(KEY_GARLIC_BLOOD_VISION, BLOOD_VISION_GARLIC)
                    .parents(KEY_BLOOD_VISION)
                    .priority(0)
                    .register(context);
            level(KEY_DAMAGE_OR_JUMP, VAMPIRE_ATTACK_DAMAGE, VAMPIRE_JUMP)
                    .parents(KEY_BLOOD_VISION)
                    .priority(1)
                    .register(context);
            level(KEY_FAST_RECOVERY_OR_RESURRECTION, NEONATAL_DECREASE, DBNO_DURATION)
                    .parents(KEY_DAMAGE_OR_JUMP)
                    .register(context);
            level(KEY_TELEPORT, TELEPORT)
                    .parents(KEY_FAST_RECOVERY_OR_RESURRECTION)
                    .register(context);

            level(KEY_SUMMON_BATS, SUMMON_BATS)
                    .parents(KEY_NO_LONGER_FLEDGLING)
                    .priority(2)
                    .register(context);
            level(KEY_HISSING, HISSING)
                    .parents(KEY_SUMMON_BATS)
                    .priority(0)
                    .register(context);
            level(KEY_TOUGH_SKIN_OR_WATER_RESISTANCE, LESS_SUNDAMAGE, WATER_RESISTANCE)
                    .parents(KEY_SUMMON_BATS)
                    .priority(1)
                    .register(context);
            level(KEY_FRUGAL_VAMPIRE, LESS_BLOOD_THIRST)
                    .parents(KEY_TOUGH_SKIN_OR_WATER_RESISTANCE)
                    .register(context);
            level(KEY_HUMAN_DISGUISE, VAMPIRE_DISGUISE)
                    .parents(KEY_FRUGAL_VAMPIRE)
                    .register(context);
            level(KEY_DAMAGE_LIMITER, HALF_INVULNERABLE)
                    .parents(KEY_HUMAN_DISGUISE)
                    .register(context);
            level(KEY_INVISIBILITY_OR_DARK_STALKER, VAMPIRE_INVISIBILITY, DARK_STALKER)
                    .parents(KEY_DAMAGE_LIMITER)
                    .register(context);

            lord(KEY_LORD_ROOT, LORD_ROOT)
                    .register(context);
            lord(KEY_BETTER_MINIONS, MINION_STATS_INCREASE)
                    .parents(KEY_LORD_ROOT)
                    .priority(0)
                    .register(context);
            lord(KEY_LORD_MOVEMENT_OR_ATTACK_SPEED, LordSkills.LORD_SPEED, LordSkills.LORD_ATTACK_SPEED)
                    .parents(KEY_LORD_ROOT)
                    .priority(1)
                    .register(context);
            lord(KEY_BLOOD_COLLECTION, MINION_COLLECT)
                    .parents(KEY_LORD_ROOT)
                    .priority(2)
                    .register(context);
            lord(KEY_MINION_RECOVERY, FactionSkills.MINION_RECOVERY)
                    .parents(KEY_LORD_ROOT)
                    .priority(3)
                    .register(context);

            dracula(KEY_DRACULA_ROOT, DRACULA_ROOT)
                    .register(context);
            dracula(KEY_MIST_FORM, MIST_FORM)
                    .parents(KEY_DRACULA_ROOT)
                    .priority(0)
                    .register(context);
            dracula(KEY_AURA_OF_DARKNESS, AURA_OF_DARKNESS)
                    .parents(KEY_DRACULA_ROOT)
                    .priority(1)
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
