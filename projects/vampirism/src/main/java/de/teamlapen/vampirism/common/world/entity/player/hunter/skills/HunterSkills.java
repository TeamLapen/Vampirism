package de.teamlapen.vampirism.common.world.entity.player.hunter.skills;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillNode;
import de.teamlapen.faction.api.factions.skills.ISkillTree;
import de.teamlapen.faction.common.advancements.criterion.PlayerFactionSubPredicate;
import de.teamlapen.faction.common.factions.skills.SkillNode;
import de.teamlapen.faction.common.factions.skills.SkillTree;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.tags.ModSkillTreeTags;
import de.teamlapen.vampirism.common.tags.ModSkillTreeTags;
import de.teamlapen.vampirism.common.world.entity.player.hunter.actions.HunterActions;
import de.teamlapen.vampirism.common.world.entity.player.lord.skills.LordSkills;
import de.teamlapen.vampirism.common.world.entity.player.skills.ActionSkill;
import de.teamlapen.vampirism.common.world.entity.player.skills.VampirismSkill;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStackTemplate;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

/**
 * Registers the default hunter skills
 */
@SuppressWarnings("unused")
public class HunterSkills {
    public static final DeferredRegister<ISkill<?>> SKILLS = DeferredRegister.create(FactionRegistries.Keys.SKILL, REFERENCE.MODID);

    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> LEVEL_ROOT = SKILLS.register(ModFactions.HUNTER.getKey().identifier().getPath(), () -> new VampirismSkill.SimpleHunterSkill(0, false));

    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> STAKE1 = SKILLS.register("stake1", () -> new VampirismSkill.SimpleHunterSkill(2, false).setDescription(() -> Component.translatable(ModConfig.balance().hsInstantKill1FromBehind.get() ? "skill.vampirism.stake1.desc" : "skill.vampirism.stake1.desc.behind", (int) (ModConfig.balance().hsInstantKill1MaxHealth.get() * 100))));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> HUNTER_DISGUISE = SKILLS.register("hunter_disguise", () -> new ActionSkill<>(HunterActions.DISGUISE_HUNTER, Trees.LEVEL, 1, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> WEAPON_TABLE = SKILLS.register("weapon_table", () -> new VampirismSkill.SimpleHunterSkill(2, true));

    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> BASIC_ALCHEMY = SKILLS.register("basic_alchemy", () -> new VampirismSkill.SimpleHunterSkill(1, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> CRUCIFIX_WIELDER = SKILLS.register("crucifix_wielder", () -> new VampirismSkill.SimpleHunterSkill(1, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> GARLIC_DIFFUSER = SKILLS.register("garlic_diffuser", () -> new VampirismSkill.SimpleHunterSkill(1, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> PURIFIED_GARLIC = SKILLS.register("purified_garlic", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> GARLIC_DIFFUSER_IMPROVED = SKILLS.register("garlic_diffuser_improved", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> ENHANCED_BLESSING = SKILLS.register("enhanced_blessing", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> ULTIMATE_CRUCIFIX = SKILLS.register("ultimate_crucifix", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> HUNTER_AWARENESS = SKILLS.register("hunter_awareness", () -> new ActionSkill<>(HunterActions.AWARENESS_HUNTER, ModSkillTreeTags.HUNTER, 2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> CRUCIFIX_REPEL = SKILLS.register("crucifix_repel", () -> new VampirismSkill.SimpleHunterSkill(2, true));

    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> MULTITASK_BREWING = SKILLS.register("multitask_brewing", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> DURABLE_BREWING = SKILLS.register("durable_brewing", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> CONCENTRATED_BREWING = SKILLS.register("concentrated_brewing", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> SWIFT_BREWING = SKILLS.register("swift_brewing", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> EFFICIENT_BREWING = SKILLS.register("efficient_brewing", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> MASTER_BREWER = SKILLS.register("master_brewer", () -> new VampirismSkill.SimpleHunterSkill(3, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> POTION_RESISTANCE = SKILLS.register("potion_resistance", () -> new ActionSkill<>(HunterActions.POTION_RESISTANCE_HUNTER, ModSkillTreeTags.HUNTER, 2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> CONCENTRATED_DURABLE_BREWING = SKILLS.register("concentrated_durable_brewing", () -> new VampirismSkill.SimpleHunterSkill(2, true));

    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> HUNTER_ATTACK_SPEED = SKILLS.register("hunter_attack_speed", () -> new VampirismSkill.SimpleHunterSkill(2, true).registerAttributeModifier(Attributes.ATTACK_SPEED, () -> ModConfig.balance().hsSmallAttackSpeedModifier.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> HUNTER_ATTACK_DAMAGE = SKILLS.register("hunter_attack_damage", () -> new VampirismSkill.SimpleHunterSkill(2, true).registerAttributeModifier(Attributes.ATTACK_DAMAGE, () -> ModConfig.balance().hsSmallAttackDamageModifier.get(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> ARMOR_SPEED = SKILLS.register("armor_speed", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> ARMOR_JUMP = SKILLS.register("armor_jump", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> CROSSBOW_TECHNIQUE = SKILLS.register("crossbow_technique", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> DOUBLE_IT = SKILLS.register("double_it", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> MASTER_CRAFTSMANSHIP = SKILLS.register("master_craftsmanship", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> STAKE2 = SKILLS.register("stake2", () -> new VampirismSkill.SimpleHunterSkill(2, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> AXE2 = SKILLS.register("axe2", () -> new VampirismSkill.SimpleHunterSkill(3, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> ARTISAN_CRAFTSMANSHIP = SKILLS.register("artisan_craftsmanship", () -> new VampirismSkill.SimpleHunterSkill(3, true));

    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> LORD_ROOT = SKILLS.register(ModFactions.HUNTER.getKey().identifier().withSuffix("_lord").getPath(), () -> new VampirismSkill.SimpleHunterSkill(0, false));

    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> MINION_STATS_INCREASE = SKILLS.register("hunter_minion_stats_increase", () -> new VampirismSkill.SimpleHunterSkill(3, true).setToggleActions(hunter -> hunter.updateMinionAttributes(true), hunter -> hunter.updateMinionAttributes(false)));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> MINION_TECH_CROSSBOWS = SKILLS.register("minion_tech_crossbows", () -> new VampirismSkill.SimpleHunterSkill(1, true));
    public static final DeferredHolder<ISkill<?>, ISkill<IHunterPlayer>> MINION_COLLECT = SKILLS.register("hunter_minion_collect", () -> new VampirismSkill.SimpleHunterSkill(2, true));

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
        public static final ResourceKey<ISkillNode> ALCHEMY7 = node("alchemy7");
        public static final ResourceKey<ISkillNode> ALCHEMY8 = node("alchemy8");
        public static final ResourceKey<ISkillNode> ALCHEMY9 = node("alchemy9");
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
        public static final ResourceKey<ISkillNode> WEAPON7 = node("weapon7");
        public static final ResourceKey<ISkillNode> WEAPON8 = node("weapon8");
        public static final ResourceKey<ISkillNode> WEAPON9 = node("weapon9");
        public static final ResourceKey<ISkillNode> WEAPON10 = node("weapon10");

        public static final ResourceKey<ISkillNode> LORD_ROOT = node("lord_root");
        public static final ResourceKey<ISkillNode> LORD_2 = node("lord_2");
        public static final ResourceKey<ISkillNode> LORD_3 = node("lord_3");
        public static final ResourceKey<ISkillNode> LORD_4 = node("lord_4");
        public static final ResourceKey<ISkillNode> LORD_5 = node("lord_5");
        public static final ResourceKey<ISkillNode> LORD_6 = node("lord_6");

        private static ResourceKey<ISkillNode> node(String path) {
            return ResourceKey.create(FactionRegistries.Keys.SKILL_NODE, VIdentifier.mod("hunter/" + path));
        }

        public static void createSkillNodes(BootstrapContext<ISkillNode> context) {
            context.register(LEVEL_ROOT, new SkillNode(HunterSkills.LEVEL_ROOT));
            context.register(SKILL2, new SkillNode(STAKE1));
            context.register(SKILL3, new SkillNode(HUNTER_DISGUISE));
            context.register(SKILL4, new SkillNode(WEAPON_TABLE));

            context.register(ALCHEMY1, new SkillNode(BASIC_ALCHEMY));
            context.register(ALCHEMY2, new SkillNode(PURIFIED_GARLIC));
            context.register(ALCHEMY3, new SkillNode(CRUCIFIX_WIELDER));
            context.register(ALCHEMY4, new SkillNode(GARLIC_DIFFUSER));
            context.register(ALCHEMY5, new SkillNode(GARLIC_DIFFUSER_IMPROVED));
            context.register(ALCHEMY6, new SkillNode(HUNTER_AWARENESS));
            context.register(ALCHEMY7, new SkillNode(ULTIMATE_CRUCIFIX));
            context.register(ALCHEMY8, new SkillNode(CRUCIFIX_REPEL));
            context.register(ALCHEMY9, new SkillNode(ENHANCED_BLESSING));

            context.register(POTION1, new SkillNode(MULTITASK_BREWING));
            context.register(POTION2, new SkillNode(DURABLE_BREWING, CONCENTRATED_BREWING));
            context.register(POTION3, new SkillNode(SWIFT_BREWING, EFFICIENT_BREWING));
            context.register(POTION4, new SkillNode(MASTER_BREWER));
            context.register(POTION5, new SkillNode(POTION_RESISTANCE));
            context.register(POTION6, new SkillNode(CONCENTRATED_DURABLE_BREWING));

            context.register(WEAPON1, new SkillNode(HUNTER_ATTACK_DAMAGE));
            context.register(WEAPON2, new SkillNode(HUNTER_ATTACK_SPEED));
            context.register(WEAPON3, new SkillNode(ARMOR_SPEED));
            context.register(WEAPON4, new SkillNode(ARMOR_JUMP));
            context.register(WEAPON5, new SkillNode(CROSSBOW_TECHNIQUE));
            context.register(WEAPON6, new SkillNode(DOUBLE_IT));
            context.register(WEAPON7, new SkillNode(MASTER_CRAFTSMANSHIP));
            context.register(WEAPON8, new SkillNode(AXE2));
            context.register(WEAPON9, new SkillNode(STAKE2));
            context.register(WEAPON10, new SkillNode(ARTISAN_CRAFTSMANSHIP));

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
            return ResourceKey.create(FactionRegistries.Keys.SKILL_TREE, VIdentifier.mod("hunter/" + path));
        }

        public static void createSkillTrees(BootstrapContext<ISkillTree> context) {
            HolderGetter<ISkillNode> lookup = context.lookup(FactionRegistries.Keys.SKILL_NODE);
            context.register(LEVEL, new SkillTree(ModFactions.HUNTER, EntityPredicate.Builder.entity().subPredicate(PlayerFactionSubPredicate.faction(ModFactions.HUNTER)).build(), new ItemStackTemplate(ModItems.VAMPIRE_BOOK), Component.translatable("gui.vampirism.skills.level"), Optional.of(VIdentifier.mc("block/spruce_planks"))));
            context.register(LORD, new SkillTree(ModFactions.HUNTER, EntityPredicate.Builder.entity().subPredicate(PlayerFactionSubPredicate.lord(ModFactions.HUNTER)).build(), new ItemStackTemplate(ModItems.HUNTER_MINION_EQUIPMENT), Component.translatable("gui.vampirism.skills.lord"), Optional.of(VIdentifier.mc("block/spruce_planks"))));
        }

    }

}
