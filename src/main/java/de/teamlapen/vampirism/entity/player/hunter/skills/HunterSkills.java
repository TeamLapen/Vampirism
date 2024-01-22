package de.teamlapen.vampirism.entity.player.hunter.skills;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.advancements.critereon.FactionSubPredicate;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.ISkillNode;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.SkillType;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.entity.player.lord.skills.LordSkills;
import de.teamlapen.vampirism.entity.player.skills.ActionSkill;
import de.teamlapen.vampirism.entity.player.skills.SkillNode;
import de.teamlapen.vampirism.entity.player.skills.SkillTree;
import de.teamlapen.vampirism.entity.player.skills.VampirismSkill;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

/**
 * Registers the default hunter skills
 */
@SuppressWarnings("unused")
public class HunterSkills {
    public static final DeferredRegister<ISkill<?>> SKILLS = DeferredRegister.create(VampirismRegistries.Keys.SKILL, REFERENCE.MODID);

    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> LEVEL_ROOT = SKILLS.register(SkillType.LEVEL.createIdForFaction(VReference.HUNTER_FACTION.getID()).getPath(), () -> new VampirismSkill.SimpleHunterSkill(0, false));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> LORD_ROOT = SKILLS.register(SkillType.LORD.createIdForFaction(VReference.HUNTER_FACTION.getID()).getPath(), () -> new VampirismSkill.SimpleHunterSkill(0, false));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> BASIC_ALCHEMY = SKILLS.register("basic_alchemy", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> GARLIC_DIFFUSER = SKILLS.register("garlic_diffuser", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> GARLIC_DIFFUSER_IMPROVED = SKILLS.register("garlic_diffuser_improved", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> ENHANCED_BLESSING = SKILLS.register("enhanced_blessing", () -> new VampirismSkill.SimpleHunterSkill(3, true));
    @SuppressWarnings({"FunctionalExpressionCanBeFolded", "Convert2MethodRef"})
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> HUNTER_ATTACK_SPEED = SKILLS.register("hunter_attack_speed", () -> new VampirismSkill.SimpleHunterSkill(2, true).registerAttributeModifier(Attributes.ATTACK_SPEED, "d9311f44-a4ba-4ef4-83f2-9274ae1a827e", () -> VampirismConfig.BALANCE.hsSmallAttackSpeedModifier.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> HUNTER_ATTACK_DAMAGE = SKILLS.register("hunter_attack_damage", () -> new VampirismSkill.SimpleHunterSkill(2, true).registerAttributeModifier(Attributes.ATTACK_DAMAGE, "ffafd115-96e2-4d08-9588-d1bc9be0d902", () -> VampirismConfig.BALANCE.hsSmallAttackDamageModifier.get(), AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> HUNTER_AWARENESS = SKILLS.register("hunter_awareness", () -> new ActionSkill<>(HunterActions.AWARENESS_HUNTER, Trees.LEVEL, 2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> HUNTER_DISGUISE = SKILLS.register("hunter_disguise", () -> new ActionSkill<>(HunterActions.DISGUISE_HUNTER, Trees.LEVEL, 1, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> PURIFIED_GARLIC = SKILLS.register("purified_garlic", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> STAKE1 = SKILLS.register("stake1", () -> new VampirismSkill.SimpleHunterSkill(2, false)
            .setDescription(() -> {
                MutableComponent desc = Component.translatable("skill.vampirism.stake1.desc", (int) (VampirismConfig.BALANCE.hsInstantKill1MaxHealth.get() * 100));
                if (VampirismConfig.BALANCE.hsInstantKill1FromBehind.get()) {
                    desc.append(Component.literal(" "));
                    desc.append(Component.translatable("text.vampirism.from_behind"));
                }
                return desc;
            }));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> STAKE2 = SKILLS.register("stake2", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> WEAPON_TABLE = SKILLS.register("weapon_table", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> DURABLE_BREWING = SKILLS.register("durable_brewing", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> CONCENTRATED_BREWING = SKILLS.register("concentrated_brewing", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> MULTITASK_BREWING = SKILLS.register("multitask_brewing", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> EFFICIENT_BREWING = SKILLS.register("efficient_brewing", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> MASTER_BREWER = SKILLS.register("master_brewer", () -> new VampirismSkill.SimpleHunterSkill(3, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> SWIFT_BREWING = SKILLS.register("swift_brewing", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> CONCENTRATED_DURABLE_BREWING = SKILLS.register("concentrated_durable_brewing", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> POTION_RESISTANCE = SKILLS.register("potion_resistance", () -> new ActionSkill<>(HunterActions.POTION_RESISTANCE_HUNTER, Trees.LEVEL, 2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> CRUCIFIX_WIELDER = SKILLS.register("crucifix_wielder", () -> new VampirismSkill.SimpleHunterSkill(1, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> ULTIMATE_CRUCIFIX = SKILLS.register("ultimate_crucifix", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> MINION_COLLECT = SKILLS.register("hunter_minion_collect", () -> new VampirismSkill.HunterLordSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> MINION_STATS_INCREASE = SKILLS.register("hunter_minion_stats_increase", () -> new VampirismSkill.HunterLordSkill(3, true).setToggleActions(hunter -> hunter.updateMinionAttributes(true), hunter -> hunter.updateMinionAttributes(false)));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> MINION_TECH_CROSSBOWS = SKILLS.register("minion_tech_crossbows", () -> new VampirismSkill.HunterLordSkill(1, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> ARMOR_SPEED = SKILLS.register("armor_speed", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> ARMOR_JUMP = SKILLS.register("armor_jump", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> CROSSBOW_TECHNIQUE = SKILLS.register("crossbow_technique", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> DOUBLE_IT = SKILLS.register("double_it", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> MASTER_CRAFTSMANSHIP = SKILLS.register("master_craftsmanship", () -> new VampirismSkill.SimpleHunterSkill(3, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> AXE2 = SKILLS.register("axe2", () -> new VampirismSkill.SimpleHunterSkill(3, true));

    @ApiStatus.Internal
    public static void register(IEventBus bus) {
        SKILLS.register(bus);
    }

    public static class Nodes {

        public static final ResourceKey<ISkillNode> LEVEL_ROOT = node("level_root");
        public static final ResourceKey<ISkillNode> SKILL2 = node("skill2");
        public static final ResourceKey<ISkillNode> SKILL3 = node("skill3");
        public static final ResourceKey<ISkillNode> SKILL4 = node("skill4");
        public static final ResourceKey<ISkillNode> ALCHEMY1 = node("alchemy1");
        public static final ResourceKey<ISkillNode> ALCHEMY2 = node("alchemy2");
        public static final ResourceKey<ISkillNode> ALCHEMY3 = node("alchemy3");
        public static final ResourceKey<ISkillNode> ALCHEMY4 = node("alchemy4");
        public static final ResourceKey<ISkillNode> ALCHEMY5 = node("alchemy5");
        public static final ResourceKey<ISkillNode> ALCHEMY6 = node("alchemy6");
        public static final ResourceKey<ISkillNode> POTION1 = node("potion1");
        public static final ResourceKey<ISkillNode> POTION2 = node("potion2");
        public static final ResourceKey<ISkillNode> POTION3 = node("potion3");
        public static final ResourceKey<ISkillNode> POTION4 = node("potion4");
        public static final ResourceKey<ISkillNode> POTION5 = node("potion5");
        public static final ResourceKey<ISkillNode> POTION6 = node("potion6");
        public static final ResourceKey<ISkillNode> WEAPON1 = node("weapon1");
        public static final ResourceKey<ISkillNode> WEAPON2 = node("weapon2");
        public static final ResourceKey<ISkillNode> WEAPON3 = node("weapon3");
        public static final ResourceKey<ISkillNode> WEAPON4 = node("weapon4");
        public static final ResourceKey<ISkillNode> WEAPON5 = node("weapon5");
        public static final ResourceKey<ISkillNode> WEAPON6 = node("weapon6");

        public static final ResourceKey<ISkillNode> LORD_ROOT = node("lord_root");
        public static final ResourceKey<ISkillNode> LORD_2 = node("lord_2");
        public static final ResourceKey<ISkillNode> LORD_3 = node("lord_3");
        public static final ResourceKey<ISkillNode> LORD_4 = node("lord_4");
        public static final ResourceKey<ISkillNode> LORD_5 = node("lord_5");
        public static final ResourceKey<ISkillNode> LORD_6 = node("lord_6");

        private static ResourceKey<ISkillNode> node(String path) {
            return ResourceKey.create(VampirismRegistries.Keys.SKILL_NODE, new ResourceLocation(REFERENCE.MODID, "hunter/" + path));
        }

        public static void createSkillNodes(BootstapContext<ISkillNode> context) {
            context.register(LEVEL_ROOT, new SkillNode(HunterSkills.LEVEL_ROOT));
            context.register(SKILL2, new SkillNode(STAKE1));
            context.register(SKILL3, new SkillNode(HUNTER_DISGUISE));
            context.register(SKILL4, new SkillNode(WEAPON_TABLE));

            context.register(ALCHEMY1, new SkillNode(BASIC_ALCHEMY));
            context.register(ALCHEMY2, new SkillNode(CRUCIFIX_WIELDER));
            context.register(ALCHEMY3, new SkillNode(GARLIC_DIFFUSER));
            context.register(ALCHEMY4, new SkillNode(PURIFIED_GARLIC, GARLIC_DIFFUSER_IMPROVED));
            context.register(ALCHEMY5, new SkillNode(ENHANCED_BLESSING, ULTIMATE_CRUCIFIX));
            context.register(ALCHEMY6, new SkillNode(HUNTER_AWARENESS));

            context.register(POTION1, new SkillNode(MULTITASK_BREWING));
            context.register(POTION2, new SkillNode(DURABLE_BREWING, CONCENTRATED_BREWING));
            context.register(POTION3, new SkillNode(SWIFT_BREWING, EFFICIENT_BREWING));
            context.register(POTION4, new SkillNode(MASTER_BREWER));
            context.register(POTION5, new SkillNode(POTION_RESISTANCE));
            context.register(POTION6, new SkillNode(CONCENTRATED_DURABLE_BREWING));

            context.register(WEAPON1, new SkillNode(HUNTER_ATTACK_SPEED, HUNTER_ATTACK_DAMAGE));
            context.register(WEAPON2, new SkillNode(ARMOR_SPEED, ARMOR_JUMP));
            context.register(WEAPON3, new SkillNode(CROSSBOW_TECHNIQUE, DOUBLE_IT));
            context.register(WEAPON4, new SkillNode(MASTER_CRAFTSMANSHIP));
            context.register(WEAPON5, new SkillNode(STAKE2));
            context.register(WEAPON6, new SkillNode(AXE2));

            context.register(LORD_ROOT, new SkillNode(HunterSkills.LORD_ROOT));
            context.register(LORD_2, new SkillNode(MINION_STATS_INCREASE));
            context.register(LORD_3, new SkillNode(LordSkills.LORD_SPEED, LordSkills.LORD_ATTACK_SPEED));
            context.register(LORD_4, new SkillNode(MINION_COLLECT));
            context.register(LORD_5, new SkillNode(LordSkills.MINION_RECOVERY));
            context.register(LORD_6, new SkillNode(MINION_TECH_CROSSBOWS));
        }
    }

    public static class Trees {
        public static final ResourceKey<ISkillTree> LEVEL = tree("level");
        public static final ResourceKey<ISkillTree> LORD = tree("lord");

        private static ResourceKey<ISkillTree> tree(String path) {
            return ResourceKey.create(VampirismRegistries.Keys.SKILL_TREE, new ResourceLocation(REFERENCE.MODID, "hunter/" + path));
        }

        public static void createSkillTrees(BootstapContext<ISkillTree> context) {
            HolderGetter<ISkillNode> lookup = context.lookup(VampirismRegistries.Keys.SKILL_NODE);
            context.register(LEVEL, new SkillTree(VReference.HUNTER_FACTION, EntityPredicate.Builder.entity().subPredicate(FactionSubPredicate.faction(VReference.HUNTER_FACTION)).build(), new ItemStack(ModItems.VAMPIRE_BOOK.get()), Component.translatable("text.vampirism.skills.level")));
            context.register(LORD, new SkillTree(VReference.HUNTER_FACTION, EntityPredicate.Builder.entity().subPredicate(FactionSubPredicate.lord(VReference.HUNTER_FACTION)).build(), new ItemStack(ModItems.HUNTER_MINION_EQUIPMENT.get()), Component.translatable("text.vampirism.skills.lord")));
        }

    }

}
