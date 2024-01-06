package de.teamlapen.vampirism.entity.player.vampire.skills;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.advancements.critereon.FactionSubPredicate;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.ISkillNode;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.SkillType;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAttributes;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.player.lord.skills.LordSkills;
import de.teamlapen.vampirism.entity.player.skills.ActionSkill;
import de.teamlapen.vampirism.entity.player.skills.SkillNode;
import de.teamlapen.vampirism.entity.player.skills.SkillTree;
import de.teamlapen.vampirism.entity.player.skills.VampirismSkill;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.actions.VampireActions;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Registers the default vampire skills
 */
@SuppressWarnings("unused")
public class VampireSkills {
    public static final DeferredRegister<ISkill<?>> SKILLS = DeferredRegister.create(VampirismRegistries.SKILLS_ID, REFERENCE.MODID);

    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> LEVEL_ROOT = SKILLS.register(SkillType.LEVEL.createIdForFaction(VReference.VAMPIRE_FACTION.getID()).getPath(), () -> new VampirismSkill.SimpleVampireSkill(2, false));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> LORD_ROOT = SKILLS.register(SkillType.LORD.createIdForFaction(VReference.VAMPIRE_FACTION.getID()).getPath(), () -> new VampirismSkill.SimpleVampireSkill(2, false));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> ADVANCED_BITER = SKILLS.register("advanced_biter", () -> new VampirismSkill.SimpleVampireSkill(2, false).setToggleActions(player -> ((VampirePlayer) player).getSpecialAttributes().advanced_biter = true, player -> ((VampirePlayer) player).getSpecialAttributes().advanced_biter = false).setHasDefaultDescription());
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> FLEDGLING = SKILLS.register("fledgling", () -> new VampirismSkill.SimpleVampireSkill(2, true) {
        @Override
        protected void getActions(@NotNull Collection<IAction<IVampirePlayer>> list) {
            list.add(VampireActions.BAT.get());
            list.add(VampireActions.INFECT.get());
        }
    });
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> BLOOD_CHARGE = SKILLS.register("blood_charge", () -> new VampirismSkill.SimpleVampireSkill(1, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> BLOOD_VISION = SKILLS.register("blood_vision", () -> new VampirismSkill.SimpleVampireSkill(3, true).setToggleActions(player -> player.unlockVision(VReference.vision_bloodVision), player -> player.unUnlockVision(VReference.vision_bloodVision)));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> BLOOD_VISION_GARLIC = SKILLS.register("blood_vision_garlic", () -> new VampirismSkill.SimpleVampireSkill(1, true).setToggleActions(player -> ((VampirePlayer) player).getSpecialAttributes().blood_vision_garlic = true, player -> ((VampirePlayer) player).getSpecialAttributes().blood_vision_garlic = false));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> DARK_BLOOD_PROJECTILE = SKILLS.register("dark_blood_projectile", () -> new ActionSkill<>(VampireActions.DARK_BLOOD_PROJECTILE, Trees.LEVEL, 2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> FREEZE = SKILLS.register("freeze", () -> new ActionSkill<>(VampireActions.FREEZE, Trees.LEVEL, 2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> HALF_INVULNERABLE = SKILLS.register("half_invulnerable", () -> new ActionSkill<>(VampireActions.HALF_INVULNERABLE, Trees.LEVEL, 2, true));
    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> LESS_BLOOD_THIRST = SKILLS.register("less_blood_thirst", () -> new VampirismSkill.SimpleVampireSkill(1, true).registerAttributeModifier(ModAttributes.BLOOD_EXHAUSTION.get(), "980ad86f-fe76-433b-b26a-c4060e0e6751", () -> VampirismConfig.BALANCE.vsBloodThirstReduction1.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> LESS_SUNDAMAGE = SKILLS.register("less_sundamage", () -> new VampirismSkill.SimpleVampireSkill(3, true).registerAttributeModifier(ModAttributes.SUNDAMAGE.get(), "EB47EDC1-ED4E-4CD8-BDDC-BE40956042A2", () -> VampirismConfig.BALANCE.vsSundamageReduction1.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> NIGHT_VISION = SKILLS.register("night_vision", () -> new VampirismSkill.SimpleVampireSkill(2, false)
            .setToggleActions(player -> {
                player.unlockVision(VReference.vision_nightVision);
                player.activateVision(VReference.vision_nightVision);
            }, player -> player.unUnlockVision(VReference.vision_nightVision)));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> SUNSCREEN = SKILLS.register("sunscreen", () -> new ActionSkill<>(VampireActions.SUNSCREEN, Trees.LEVEL, 2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> SUMMON_BATS = SKILLS.register("summon_bats", () -> new ActionSkill<>(VampireActions.SUMMON_BAT, Trees.LEVEL, 2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> SWORD_FINISHER = SKILLS.register("sword_finisher", () -> new VampirismSkill.SimpleVampireSkill(2, true).setDescription(() -> Component.translatable("skill.vampirism.sword_finisher.desc", (int) (VampirismConfig.BALANCE.vsSwordFinisherMaxHealth.get() * 100))));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> TELEPORT = SKILLS.register("teleport", () -> new ActionSkill<>(VampireActions.TELEPORT, Trees.LEVEL, 3, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> VAMPIRE_DISGUISE = SKILLS.register("vampire_disguise", () -> new ActionSkill<>(VampireActions.DISGUISE_VAMPIRE, Trees.LEVEL, 2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> VAMPIRE_INVISIBILITY = SKILLS.register("vampire_invisibility", () -> new ActionSkill<>(VampireActions.VAMPIRE_INVISIBILITY, Trees.LEVEL, 3));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> VAMPIRE_JUMP = SKILLS.register("vampire_jump", () -> new VampirismSkill.SimpleVampireSkill(2, false).setToggleActions(player -> ((VampirePlayer) player).getSpecialAttributes().setJumpBoost(VampirismConfig.BALANCE.vsJumpBoost.get() + 1), player -> ((VampirePlayer) player).getSpecialAttributes().setJumpBoost(0)));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> VAMPIRE_RAGE = SKILLS.register("vampire_rage", () -> new ActionSkill<>(VampireActions.VAMPIRE_RAGE, Trees.LEVEL, 2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> VAMPIRE_REGENERATION = SKILLS.register("vampire_regeneration", () -> new ActionSkill<>(VampireActions.REGEN, Trees.LEVEL, 2, true));
    //Config null, so cannot get method ref
    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> VAMPIRE_SPEED = SKILLS.register("vampire_speed", () -> new VampirismSkill.SimpleVampireSkill(2, false).registerAttributeModifier(Attributes.MOVEMENT_SPEED, "96dc968d-818f-4271-8dbf-6b799d603ad8", () -> VampirismConfig.BALANCE.vsSpeedBoost.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> WATER_RESISTANCE = SKILLS.register("water_resistance", () -> new VampirismSkill.SimpleVampireSkill(2, true).setToggleActions(player -> ((VampirePlayer) player).getSpecialAttributes().waterResistance = true, player -> ((VampirePlayer) player).getSpecialAttributes().waterResistance = false));
    //Config null, so cannot get method ref
    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> VAMPIRE_ATTACK_SPEED = SKILLS.register("vampire_attack_speed", () -> new VampirismSkill.SimpleVampireSkill(2, false).registerAttributeModifier(Attributes.ATTACK_SPEED, "d4aa1d08-5e0e-4946-86dc-95a1e6f5be20", () -> VampirismConfig.BALANCE.vsSmallAttackSpeedModifier.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
    //Config null, so cannot get method ref
    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> VAMPIRE_ATTACK_DAMAGE = SKILLS.register("vampire_attack_damage", () -> new VampirismSkill.SimpleVampireSkill(2, false).registerAttributeModifier(Attributes.ATTACK_DAMAGE, "f2acc818-dc3a-4696-ba63-c3294290ad86", () -> VampirismConfig.BALANCE.vsSmallAttackDamageModifier.get(), AttributeModifier.Operation.ADDITION));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> NEONATAL_DECREASE = SKILLS.register("neonatal_decrease", () -> new VampirismSkill.SimpleVampireSkill(2, true).registerAttributeModifier(ModAttributes.NEONATAL_DURATION.get(), "74ecad40-5674-4ee0-8224-f871cdd6543d", () -> VampirismConfig.BALANCE.vsNeonatalReduction.get() - 1, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> DBNO_DURATION = SKILLS.register("dbno_duration", () -> new VampirismSkill.SimpleVampireSkill(2, true).registerAttributeModifier(ModAttributes.DBNO_DURATION.get(), "7e5b2fb1-ba74-4838-83e1-68fd419af787", () -> VampirismConfig.BALANCE.vsDbnoReduction.get() - 1, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> HISSING = SKILLS.register("hissing", () -> new ActionSkill<>(VampireActions.HISSING, Trees.LEVEL,1, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> MINION_COLLECT = SKILLS.register("vampire_minion_collect", () -> new VampirismSkill.VampireLordSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IVampirePlayer>> MINION_STATS_INCREASE = SKILLS.register("vampire_minion_stats_increase", () -> new VampirismSkill.VampireLordSkill(3, true).setToggleActions(vampire -> vampire.updateMinionAttributes(true), vampire -> vampire.updateMinionAttributes(false)));

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

        private static ResourceKey<ISkillNode> node(String path) {
            return ResourceKey.create(VampirismRegistries.SKILL_NODE_ID, new ResourceLocation(REFERENCE.MODID, "vampire/" + path));
        }

        public static void createSkillNodes(BootstapContext<ISkillNode> context) {
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
            context.register(UTIL6, new SkillNode(VAMPIRE_INVISIBILITY));
            context.register(UTIL15, new SkillNode(HISSING));

            context.register(LORD_ROOT, new SkillNode(VampireSkills.LORD_ROOT));
            context.register(LORD_SKILL2, new SkillNode(MINION_STATS_INCREASE));
            context.register(LORD_SKILL3, new SkillNode(LordSkills.LORD_SPEED, LordSkills.LORD_ATTACK_SPEED));
            context.register(LORD_SKILL4, new SkillNode(MINION_COLLECT));
            context.register(LORD_SKILL5, new SkillNode(LordSkills.MINION_RECOVERY));
        }
    }

    public static class Trees {
        public static final ResourceKey<ISkillTree> LEVEL = tree("level");
        public static final ResourceKey<ISkillTree> LORD = tree("lord");

        private static ResourceKey<ISkillTree> tree(String path) {
            return ResourceKey.create(VampirismRegistries.SKILL_TREE_ID, new ResourceLocation(REFERENCE.MODID, "vampire/" + path));
        }

        public static void createSkillTrees(BootstapContext<ISkillTree> context) {
            HolderGetter<ISkillNode> lookup = context.lookup(VampirismRegistries.SKILL_NODE_ID);

            context.register(LEVEL, new SkillTree(VReference.VAMPIRE_FACTION, EntityPredicate.Builder.entity().subPredicate(FactionSubPredicate.faction(VReference.VAMPIRE_FACTION)).build(), new ItemStack(ModItems.VAMPIRE_BOOK.get()), Component.translatable("text.vampirism.skills.level")));
            context.register(LORD, new SkillTree(VReference.VAMPIRE_FACTION, EntityPredicate.Builder.entity().subPredicate(FactionSubPredicate.lord(VReference.VAMPIRE_FACTION)).build(), new ItemStack(ModItems.VAMPIRE_MINION_BINDING.get()), Component.translatable("text.vampirism.skills.lord")));
        }
    }
}
