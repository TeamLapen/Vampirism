package de.teamlapen.vampirism.common.world.entity.player.vampire.skills;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillNode;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import de.teamlapen.faction.common.advancements.criterion.PlayerFactionSubPredicate;
import de.teamlapen.faction.common.factions.skills.SkillNode;
import de.teamlapen.faction.common.factions.skills.SkillTree;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.advancements.critereon.DraculaCriterion;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModAttributes;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.tags.ModSkillTreeTags;
import de.teamlapen.vampirism.common.tags.ModSkillTreeTags;
import de.teamlapen.vampirism.common.world.entity.player.lord.skills.LordSkills;
import de.teamlapen.vampirism.common.world.entity.player.skills.ActionSkill;
import de.teamlapen.vampirism.common.world.entity.player.skills.VampirismSkill;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirismVampireVisions;
import de.teamlapen.vampirism.common.world.entity.player.vampire.actions.VampireActions;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Optional;

/**
 * Registers the default vampire skills
 */
@SuppressWarnings("unused")
public class VampireSkills {
    public static final DeferredRegister<ISkill<?>> SKILLS = DeferredRegister.create(FactionRegistries.Keys.SKILL, REFERENCE.MODID);

    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> LEVEL_ROOT = SKILLS.register(ModFactions.VAMPIRE.getKey().identifier().getPath(), () -> new VampirismSkill.SimpleVampireSkill(0, false));

    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> NIGHT_VISION = SKILLS.register("night_vision", () -> new VampirismSkill.SimpleVampireSkill(2, true)
            .setToggleActions(player -> {
                player.unlockVision(VampirismVampireVisions.NIGHT_VISION.getKey());
                player.activateVision(VampirismVampireVisions.NIGHT_VISION.getKey());
            }, player -> player.unUnlockVision(VampirismVampireVisions.NIGHT_VISION.getKey())));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> VAMPIRE_REGENERATION = SKILLS.register("vampire_regeneration", () -> new ActionSkill<>(VampireActions.REGEN, Trees.LEVEL, 2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> FLEDGLING = SKILLS.register("fledgling", () -> new VampirismSkill.SimpleVampireSkill(2, true) {
        @Override
        protected void collectActions(@NonNull Collection<Holder<? extends IAction<IVampirePlayer>>> list) {
            list.add(VampireActions.BAT);
            list.add(VampireActions.INFECT);
        }
    });

    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> VAMPIRE_RAGE = SKILLS.register("vampire_rage", () -> new ActionSkill<>(VampireActions.VAMPIRE_RAGE, Trees.LEVEL, 2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> ADVANCED_BITER = SKILLS.register("advanced_biter", () -> new VampirismSkill.SimpleVampireSkill(1, false).setToggleActions(player -> ((VampirePlayer) player).getSkillProperties().advanced_biter = true, player -> ((VampirePlayer) player).getSkillProperties().advanced_biter = false).setHasDefaultDescription());
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> SWORD_FINISHER = SKILLS.register("sword_finisher", () -> new VampirismSkill.SimpleVampireSkill(2, true).setDescription(() -> Component.translatable("skill.vampirism.sword_finisher.desc", (int) (ModConfig.balance().vsSwordFinisherMaxHealth.get() * 100))));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> DARK_BLOOD_PROJECTILE = SKILLS.register("dark_blood_projectile", () -> new ActionSkill<>(VampireActions.DARK_BLOOD_PROJECTILE, Trees.LEVEL, 2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> BLOOD_CHARGE = SKILLS.register("blood_charge", () -> new VampirismSkill.SimpleVampireSkill(1, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> FREEZE = SKILLS.register("freeze", () -> new ActionSkill<>(VampireActions.FREEZE, Trees.LEVEL, 2, true));

    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> SUNSCREEN = SKILLS.register("sunscreen", () -> new ActionSkill<>(VampireActions.SUNSCREEN, Trees.LEVEL, 2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> VAMPIRE_ATTACK_SPEED = SKILLS.register("vampire_attack_speed", () -> new VampirismSkill.SimpleVampireSkill(2, true).registerAttributeModifier(Attributes.ATTACK_SPEED, () -> ModConfig.balance().vsSmallAttackSpeedModifier.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> VAMPIRE_SPEED = SKILLS.register("vampire_speed", () -> new VampirismSkill.SimpleVampireSkill(2, true).registerAttributeModifier(Attributes.MOVEMENT_SPEED, () -> ModConfig.balance().vsSpeedBoost.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> BLOOD_VISION = SKILLS.register("blood_vision", () -> new VampirismSkill.SimpleVampireSkill(2, true).setToggleActions(player -> player.unlockVision(VampirismVampireVisions.BLOOD_VISION.getKey()), player -> player.unUnlockVision(VampirismVampireVisions.BLOOD_VISION.getKey())));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> BLOOD_VISION_GARLIC = SKILLS.register("blood_vision_garlic", () -> new VampirismSkill.SimpleVampireSkill(1, true).setToggleActions(player -> ((VampirePlayer) player).getSkillProperties().blood_vision_garlic = true, player -> ((VampirePlayer) player).getSkillProperties().blood_vision_garlic = false));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> VAMPIRE_ATTACK_DAMAGE = SKILLS.register("vampire_attack_damage", () -> new VampirismSkill.SimpleVampireSkill(2, true)
            .registerAttributeModifier(Attributes.ATTACK_DAMAGE, () -> ModConfig.balance().vsSmallAttackDamageModifier.get(), AttributeModifier.Operation.ADD_VALUE)
            .registerAttributeModifier(Attributes.ATTACK_DAMAGE, () -> ModConfig.balance().vsSmallAttackDamageMultiplier.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> VAMPIRE_JUMP = SKILLS.register("vampire_jump", () -> new ActionSkill<>(VampireActions.JUMP_BOOST,Trees.LEVEL, 2, false));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> NEONATAL_DECREASE = SKILLS.register("neonatal_decrease", () -> new VampirismSkill.SimpleVampireSkill(2, true).registerAttributeModifier(ModAttributes.NEONATAL_DURATION, () -> ModConfig.balance().vsNeonatalReduction.get() - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> DBNO_DURATION = SKILLS.register("dbno_duration", () -> new VampirismSkill.SimpleVampireSkill(2, true).registerAttributeModifier(ModAttributes.DBNO_DURATION, () -> ModConfig.balance().vsDbnoReduction.get() - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> TELEPORT = SKILLS.register("teleport", () -> new ActionSkill<>(VampireActions.TELEPORT, Trees.LEVEL, 3, true));

    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> SUMMON_BATS = SKILLS.register("summon_bats", () -> new ActionSkill<>(VampireActions.SUMMON_BAT, Trees.LEVEL, 2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> LESS_SUNDAMAGE = SKILLS.register("less_sundamage", () -> new VampirismSkill.SimpleVampireSkill(3, true).registerAttributeModifier(ModAttributes.SUNDAMAGE, () -> ModConfig.balance().vsSundamageReduction1.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> WATER_RESISTANCE = SKILLS.register("water_resistance", () -> new VampirismSkill.SimpleVampireSkill(2, true).setToggleActions(player -> ((VampirePlayer) player).getSkillProperties().waterResistance = true, player -> ((VampirePlayer) player).getSkillProperties().waterResistance = false));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> LESS_BLOOD_THIRST = SKILLS.register("less_blood_thirst", () -> new VampirismSkill.SimpleVampireSkill(1, true).registerAttributeModifier(ModAttributes.BLOOD_EXHAUSTION, () -> ModConfig.balance().vsBloodThirstReduction1.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> VAMPIRE_DISGUISE = SKILLS.register("vampire_disguise", () -> new ActionSkill<>(VampireActions.DISGUISE_VAMPIRE, Trees.LEVEL, 1, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> HALF_INVULNERABLE = SKILLS.register("half_invulnerable", () -> new ActionSkill<>(VampireActions.HALF_INVULNERABLE, Trees.LEVEL, 2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> VAMPIRE_INVISIBILITY = SKILLS.register("vampire_invisibility", () -> new ActionSkill<>(VampireActions.VAMPIRE_INVISIBILITY, Trees.LEVEL, 3));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> DARK_STALKER = SKILLS.register("dark_stalker", () -> new ActionSkill<>(VampireActions.DARK_STALKER, Trees.LEVEL, 2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> HISSING = SKILLS.register("hissing", () -> new ActionSkill<>(VampireActions.HISSING, Trees.LEVEL, 1, true));

    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> LORD_ROOT = SKILLS.register(ModFactions.VAMPIRE.getKey().identifier().withSuffix("_lord").getPath(), () -> new VampirismSkill.SimpleVampireSkill(0, false));

    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> MINION_STATS_INCREASE = SKILLS.register("vampire_minion_stats_increase", () -> new VampirismSkill.VampireLordSkill(3, true).setToggleActions(vampire -> vampire.updateMinionAttributes(true), vampire -> vampire.updateMinionAttributes(false)));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> MINION_COLLECT = SKILLS.register("vampire_minion_collect", () -> new VampirismSkill.VampireLordSkill(2, true));

    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> DRACULA_ROOT = SKILLS.register(ModFactions.VAMPIRE.getKey().identifier().withSuffix("_dracula").getPath(), () -> new VampirismSkill.SimpleVampireSkill(0, true));

    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> WANDER_THE_SUN = SKILLS.register("wander_the_sun", () -> new VampirismSkill.SimpleVampireSkill(1, true));
    public static final DeferredHolder<ISkill<?>, ActionSkill<IVampirePlayer>> BLINDING = SKILLS.register("blinding", () -> new ActionSkill<>(VampireActions.BLINDING, ModSkillTreeTags.DRACULA, 1, true));

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
            context.register(LORD_SKILL5, new SkillNode(LordSkills.MINION_RECOVERY));

            context.register(DRACULA_ROOT, new SkillNode(VampireSkills.DRACULA_ROOT));
            context.register(DRACULA_1, new SkillNode(BLINDING));
            context.register(DRACULA_2, new SkillNode(WANDER_THE_SUN));
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
            context.register(DRACULA, new SkillTree(ModFactions.VAMPIRE, EntityPredicate.Builder.entity().subPredicate(DraculaCriterion.INSTANCE).build(), new ItemStack(ModItems.VAMPIRE_CLOTHING_HAT.get()), Component.translatable("text.vampirism.skills.dracula"), Optional.of(VIdentifier.mod("block/dark_stone_bricks")), ModSkillTreeTags.DRACULA));
        }
    }
}
