package de.teamlapen.vampirism.common.world.entity.player.vampire.skills;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillNode;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import de.teamlapen.faction.api.registries.skills.DeferredSkill;
import de.teamlapen.faction.api.registries.skills.DeferredSkillRegister;
import de.teamlapen.faction.common.advancements.criterion.PlayerFactionSubPredicate;
import de.teamlapen.faction.common.core.FactionConsumer;
import de.teamlapen.faction.common.core.FactionSkills;
import de.teamlapen.faction.common.factions.skills.SkillNode;
import de.teamlapen.faction.common.factions.skills.SkillTree;
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
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStackTemplate;
import net.neoforged.bus.api.IEventBus;
import org.jetbrains.annotations.ApiStatus;

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
    public static final DeferredSkill<IVampirePlayer, ISkill<IVampirePlayer>> SWORD_FINISHER = SKILLS.registerSkill("sword_finisher", props -> new VampireSkill(props.cost(2).withDescription(Component.translatable("skill.vampirism.sword_finisher.desc", (int) (ModConfig.balance().vsSwordFinisherMaxHealth.getDefault() * 100)))));
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

    public static class Nodes {
        public static final ResourceKey<ISkillNode> LEVEL_ROOT = node("level_root");
        public static final ResourceKey<ISkillNode> SKILL2 = node("skill2");
        public static final ResourceKey<ISkillNode> SKILL3 = node("skill3");
        public static final ResourceKey<ISkillNode> SKILL4 = node("skill4");
        public static final ResourceKey<ISkillNode> DEFENSIVE1 = node("defensive1");
        public static final ResourceKey<ISkillNode> DEFENSIVE2 = node("defensive2");
        public static final ResourceKey<ISkillNode> DEFENSIVE3 = node("defensive3");
        public static final ResourceKey<ISkillNode> DEFENSIVE4 = node("defensive4");
        public static final ResourceKey<ISkillNode> DEFENSIVE5 = node("defensive5");
        public static final ResourceKey<ISkillNode> DEFENSIVE6 = node("defensive6");
        public static final ResourceKey<ISkillNode> DEFENSIVE7 = node("defensive7");
        public static final ResourceKey<ISkillNode> OFFENSIVE1 = node("offensive1");
        public static final ResourceKey<ISkillNode> OFFENSIVE2 = node("offensive2");
        public static final ResourceKey<ISkillNode> OFFENSIVE3 = node("offensive3");
        public static final ResourceKey<ISkillNode> OFFENSIVE4 = node("offensive4");
        public static final ResourceKey<ISkillNode> OFFENSIVE5 = node("offensive5");
        public static final ResourceKey<ISkillNode> OFFENSIVE6 = node("offensive6");
        public static final ResourceKey<ISkillNode> UTIL1 = node("util1");
        public static final ResourceKey<ISkillNode> UTIL2 = node("util2");
        public static final ResourceKey<ISkillNode> UTIL3 = node("util3");
        public static final ResourceKey<ISkillNode> UTIL4 = node("util4");
        public static final ResourceKey<ISkillNode> UTIL5 = node("util5");
        public static final ResourceKey<ISkillNode> UTIL6 = node("util6");
        public static final ResourceKey<ISkillNode> UTIL15 = node("util15");

        public static final ResourceKey<ISkillNode> LORD_ROOT = node("lord_root");
        public static final ResourceKey<ISkillNode> LORD_SKILL2 = node("lord_skill2");
        public static final ResourceKey<ISkillNode> LORD_SKILL3 = node("lord_skill3");
        public static final ResourceKey<ISkillNode> LORD_SKILL4 = node("lord_skill4");
        public static final ResourceKey<ISkillNode> LORD_SKILL5 = node("lord_skill5");

        public static final ResourceKey<ISkillNode> DRACULA_ROOT = node("dracula_root");
        public static final ResourceKey<ISkillNode> DRACULA_1 = node("dracula_1");
        public static final ResourceKey<ISkillNode> DRACULA_2 = node("dracula_2");



        private static ResourceKey<ISkillNode> node(String path) {
            return ResourceKey.create(FactionRegistries.Keys.SKILL_NODE, VIdentifier.mod("vampire/" + path));
        }

        public static void createSkillNodes(BootstrapContext<ISkillNode> context) {
            context.register(LEVEL_ROOT, new SkillNode(VampireSkills.LEVEL_ROOT));
            context.register(SKILL2, new SkillNode(NIGHT_VISION));
            context.register(SKILL3, new SkillNode(VAMPIRE_REGENERATION));
            context.register(SKILL4, new SkillNode(FLEDGLING));
            context.register(DEFENSIVE1, new SkillNode(SUNSCREEN));
            context.register(DEFENSIVE2, new SkillNode(VAMPIRE_ATTACK_SPEED, VAMPIRE_SPEED));
            context.register(DEFENSIVE3, new SkillNode(BLOOD_VISION));
            context.register(DEFENSIVE4, new SkillNode(BLOOD_VISION_GARLIC));
            context.register(DEFENSIVE5, new SkillNode(VAMPIRE_ATTACK_DAMAGE, VAMPIRE_JUMP));
            context.register(DEFENSIVE6, new SkillNode(NEONATAL_DECREASE, DBNO_DURATION));
            context.register(DEFENSIVE7, new SkillNode(TELEPORT));
            context.register(OFFENSIVE1, new SkillNode(VAMPIRE_RAGE));
            context.register(OFFENSIVE2, new SkillNode(ADVANCED_BITER));
            context.register(OFFENSIVE3, new SkillNode(SWORD_FINISHER));
            context.register(OFFENSIVE4, new SkillNode(DARK_BLOOD_PROJECTILE));
            context.register(OFFENSIVE5, new SkillNode(BLOOD_CHARGE));
            context.register(OFFENSIVE6, new SkillNode(FREEZE));
            context.register(UTIL1, new SkillNode(SUMMON_BATS));
            context.register(UTIL2, new SkillNode(LESS_SUNDAMAGE, WATER_RESISTANCE));
            context.register(UTIL3, new SkillNode(LESS_BLOOD_THIRST));
            context.register(UTIL4, new SkillNode(VAMPIRE_DISGUISE));
            context.register(UTIL5, new SkillNode(HALF_INVULNERABLE));
            context.register(UTIL6, new SkillNode(VAMPIRE_INVISIBILITY, DARK_STALKER));
            context.register(UTIL15, new SkillNode(HISSING));

            context.register(LORD_ROOT, new SkillNode(VampireSkills.LORD_ROOT));
            context.register(LORD_SKILL2, new SkillNode(MINION_STATS_INCREASE));
            context.register(LORD_SKILL3, new SkillNode(LordSkills.LORD_SPEED, LordSkills.LORD_ATTACK_SPEED));
            context.register(LORD_SKILL4, new SkillNode(MINION_COLLECT));
            context.register(LORD_SKILL5, new SkillNode(FactionSkills.MINION_RECOVERY));

            context.register(DRACULA_ROOT, new SkillNode(VampireSkills.DRACULA_ROOT));
            context.register(DRACULA_1, new SkillNode(MIST_FORM));
            context.register(DRACULA_2, new SkillNode(AURA_OF_DARKNESS));
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
            HolderGetter<ISkillNode> lookup = context.lookup(FactionRegistries.Keys.SKILL_NODE);

            context.register(LEVEL, new SkillTree(ModFactions.VAMPIRE, EntityPredicate.Builder.entity().subPredicate(PlayerFactionSubPredicate.faction(ModFactions.VAMPIRE)).build(), new ItemStackTemplate(ModItems.VAMPIRE_BOOK.get()), Component.translatable("gui.vampirism.skills.level"), Optional.of(VIdentifier.mod("block/dark_stone_bricks"))));
            context.register(LORD, new SkillTree(ModFactions.VAMPIRE, EntityPredicate.Builder.entity().subPredicate(PlayerFactionSubPredicate.lord(ModFactions.VAMPIRE)).build(), new ItemStackTemplate(ModItems.VAMPIRE_MINION_BINDING.get()), Component.translatable("gui.vampirism.skills.lord"), Optional.of(VIdentifier.mod("block/dark_stone_bricks"))));
            context.register(DRACULA, new SkillTree(ModFactions.VAMPIRE, EntityPredicate.Builder.entity().subPredicate(DraculaCriterion.INSTANCE).build(), new ItemStackTemplate(ModItems.VAMPIRE_CLOTHING_HAT), Component.translatable("gui.vampirism.skills.dracula"), Optional.of(VIdentifier.mod("block/dark_stone_bricks")), ModSkillTreeTags.DRACULA));
        }
    }
}
