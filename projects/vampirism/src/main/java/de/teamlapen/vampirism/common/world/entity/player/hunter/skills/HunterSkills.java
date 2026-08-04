package de.teamlapen.vampirism.common.world.entity.player.hunter.skills;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.*;
import de.teamlapen.faction.api.registries.skills.DeferredSkill;
import de.teamlapen.faction.api.registries.skills.DeferredSkillRegister;
import de.teamlapen.faction.api.tags.FactionSkillTreeTags;
import de.teamlapen.faction.common.advancements.criterion.PlayerFactionSubPredicate;
import de.teamlapen.faction.common.core.FactionConsumer;
import de.teamlapen.faction.common.core.FactionSkills;
import de.teamlapen.faction.common.factions.skills.SkillNode;
import de.teamlapen.faction.common.factions.skills.SkillTree;
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
 * Registers the default hunter skills
 */
@SuppressWarnings("unused")
public class HunterSkills {
    public static final DeferredSkillRegister SKILLS = DeferredSkillRegister.create(REFERENCE.MODID);

    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> LEVEL_ROOT = SKILLS.registerSkill(ModFactions.HUNTER.getKey().identifier().getPath(), HunterSkill::new);

    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> STAKE1 = SKILLS.registerSkill("stake1", props -> new HunterSkill(props.cost(2).withDescription(Component.translatable(ModConfig.balance().hsInstantKill1FromBehind.getDefault() ? "skill.vampirism.stake1.desc" : "skill.vampirism.stake1.desc.behind", (int) (ModConfig.balance().hsInstantKill1MaxHealth.getDefault() * 100)))));
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
    public static final DeferredSkill<IHunterPlayer, ISkill<IHunterPlayer>> DESTRUCTION_DEFERMENT = SKILLS.registerSkill("destruction_deferment", props -> new HunterSkill(props.cost(2).withDescription(ModConfig.balance().hsDestructionDefermentDuration)));
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
        public static final ResourceKey<ISkillNode> WEAPON11 = node("weapon11");

        public static final ResourceKey<ISkillNode> LORD_ROOT = node("lord_root");
        public static final ResourceKey<ISkillNode> LORD_2 = node("lord_2");
        public static final ResourceKey<ISkillNode> LORD_3 = node("lord_3");
        public static final ResourceKey<ISkillNode> LORD_4 = node("lord_4");
        public static final ResourceKey<ISkillNode> LORD_5 = node("lord_5");
        public static final ResourceKey<ISkillNode> LORD_6 = node("lord_6");

        public static final ResourceKey<ISkillNode> MARSHALL_ROOT = node("marshall_root");
        public static final ResourceKey<ISkillNode> MARSHALL_2 = node("marshall_2");
        public static final ResourceKey<ISkillNode> MARSHALL_3 = node("marshall_3");

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
            context.register(WEAPON6, new SkillNode(DOUBLE_IT, DUAL_WIELDING));
            context.register(WEAPON7, new SkillNode(MASTER_CRAFTSMANSHIP));
            context.register(WEAPON8, new SkillNode(DESTRUCTION_DEFERMENT));
            context.register(WEAPON9, new SkillNode(AXE2));
            context.register(WEAPON10, new SkillNode(STAKE2));
            context.register(WEAPON11, new SkillNode(ARTISAN_CRAFTSMANSHIP));

            context.register(LORD_ROOT, new SkillNode(HunterSkills.LORD_ROOT));
            context.register(LORD_2, new SkillNode(MINION_STATS_INCREASE));
            context.register(LORD_3, new SkillNode(LordSkills.LORD_SPEED, LordSkills.LORD_ATTACK_SPEED));
            context.register(LORD_4, new SkillNode(MINION_COLLECT));
            context.register(LORD_5, new SkillNode(FactionSkills.MINION_RECOVERY));
            context.register(LORD_6, new SkillNode(MINION_TECH_CROSSBOWS));

            context.register(MARSHALL_ROOT, new SkillNode(HunterSkills.MARSHALL_ROOT));
            context.register(MARSHALL_2, new SkillNode(MASTER_ALCHEMIST));
            context.register(MARSHALL_3, new SkillNode(ULTIMATE_BREWER));
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
            HolderGetter<ISkillNode> lookup = context.lookup(FactionRegistries.Keys.SKILL_NODE);
            context.register(LEVEL, new SkillTree(ModFactions.HUNTER, EntityPredicate.Builder.entity().subPredicate(PlayerFactionSubPredicate.faction(ModFactions.HUNTER)).build(), new ItemStackTemplate(ModItems.VAMPIRE_BOOK), Component.translatable("gui.vampirism.skills.level"), Optional.of(VIdentifier.mc("block/spruce_planks"))));
            context.register(LORD, new SkillTree(ModFactions.HUNTER, EntityPredicate.Builder.entity().subPredicate(PlayerFactionSubPredicate.lord(ModFactions.HUNTER)).build(), new ItemStackTemplate(ModItems.HUNTER_MINION_EQUIPMENT), Component.translatable("gui.vampirism.skills.lord"), Optional.of(VIdentifier.mc("block/spruce_planks"))));
            context.register(MARSHALL, new SkillTree(ModFactions.HUNTER, EntityPredicate.Builder.entity().subPredicate(MarshallCriterion.INSTANCE).build(), new ItemStackTemplate(ModItems.STAKE), Component.translatable("gui.vampirism.skills.marshall"), Optional.of(VIdentifier.mc("block/spruce_planks")), ModSkillTreeTags.MARSHALL));
        }

    }

}
