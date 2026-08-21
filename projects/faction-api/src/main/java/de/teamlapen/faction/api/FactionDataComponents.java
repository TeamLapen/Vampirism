package de.teamlapen.faction.api;

import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.factions.lord.LordTitles;
import de.teamlapen.faction.api.factions.skills.SkillTreeRequirement;
import de.teamlapen.faction.api.factions.village.TotemPair;
import de.teamlapen.faction.api.factions.village.VillageBanner;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.world.entities.ITaskMasterEntity;
import de.teamlapen.faction.api.world.entities.player.FactionPlayerConsumer;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.api.world.items.RefinementItems;
import de.teamlapen.faction.api.world.items.components.IEffectiveRefinementSet;
import de.teamlapen.faction.api.world.items.components.IFactionRestriction;
import de.teamlapen.faction.api.world.items.components.IFactionSlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;

import static de.teamlapen.faction.api.registries.ApiRegistryProvider.retrieveDataComponent;

@SuppressWarnings("unused")
public class FactionDataComponents {



    //<editor-fold desc="Items">

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<IEffectiveRefinementSet>> REFINEMENT_SET = retrieveDataComponent(Keys.REFINEMENT_SET);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<IFactionSlayer>> FACTION_SLAYER = retrieveDataComponent(Keys.FACTION_SLAYER);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> IS_FACTION_BANNER = retrieveDataComponent(Keys.IS_FACTION_BANNER);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<IFactionRestriction>> FACTION_RESTRICTION = retrieveDataComponent(Keys.FACTION_RESTRICTION);

    //</editor-fold>

    //<editor-fold desc="Factions">

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FACTION_COLOR = retrieveDataComponent(Keys.FACTION_COLOR);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TextColor>> CHAT_COLOR = retrieveDataComponent(Keys.CHAT_COLOR);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Component>> FACTION_NAME_SINGULAR = retrieveDataComponent(Keys.FACTION_NAME_SINGULAR);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Component>> FACTION_NAME_PLURAL = retrieveDataComponent(Keys.FACTION_NAME_PLURAL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAX_LEVEL = retrieveDataComponent(Keys.MAX_LEVEL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAX_LORD_LEVEL = retrieveDataComponent(Keys.MAX_LORD_LEVEL);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<AttachmentType<?>>>> PLAYER_CAPABILITY = retrieveDataComponent(Keys.PLAYER_CAPABILITY);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RefinementItems>> REFINEMENTS = retrieveDataComponent(Keys.REFINEMENTS);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LordTitles>> LORD_TITLES = retrieveDataComponent(Keys.LORD_TITLES);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<EntityType<?>>>> TASK_MASTER = retrieveDataComponent(Keys.TASK_MASTER);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TagKey<EntityType<?>>>> VILLAGE_GUARDS = retrieveDataComponent(Keys.VILLAGE_GUARDS);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<MobEffect>>> VILLAGE_BAD_OMEN = retrieveDataComponent(Keys.VILLAGE_BAD_OMEN);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TotemPair>> VILLAGE_TOTEM = retrieveDataComponent(Keys.VILLAGE_TOTEM);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<VillageBanner>> VILLAGE_BANNER = retrieveDataComponent(Keys.VILLAGE_BANNER);

    //</editor-fold>

    //<editor-fold desc="Skills">

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Component>> SKILL_NAME = retrieveDataComponent(Keys.SKILL_NAME);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SKILL_COST = retrieveDataComponent(Keys.SKILL_COST);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Component>> SKILL_DESCRIPTION = retrieveDataComponent(Keys.SKILL_DESCRIPTION);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Holder<? extends IAction<?>>>>> SKILL_ACTIONS = retrieveDataComponent(Keys.SKILL_ACTIONS);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<? extends IAction<?>>>> SKILL_ACTION = retrieveDataComponent(Keys.SKILL_ACTION);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TagKey<IFaction<?>>>> SKILL_FACTIONS = retrieveDataComponent(Keys.SKILL_FACTIONS);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SkillTreeRequirement>> SKILL_TREE_REQUIREMENT = retrieveDataComponent(Keys.SKILL_TREE_REQUIREMENT);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<FactionPlayerConsumer>>> SKILL_ENABLE_CONSUMABLE = retrieveDataComponent(Keys.SKILL_ENABLE_CONSUMABLE);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<FactionPlayerConsumer>>> SKILL_DISABLE_CONSUMABLE = retrieveDataComponent(Keys.SKILL_DISABLE_CONSUMABLE);

    //</editor-fold>



    public static class Keys {

        public static final Identifier FACTION_COLOR = FIdentifier.mod("faction_color");
        public static final Identifier CHAT_COLOR = FIdentifier.mod("chat_color");
        public static final Identifier FACTION_NAME_SINGULAR = FIdentifier.mod("faction_name_singular");
        public static final Identifier FACTION_NAME_PLURAL = FIdentifier.mod("faction_name_plural");
        public static final Identifier MAX_LEVEL = FIdentifier.mod("max_level");
        public static final Identifier MAX_LORD_LEVEL = FIdentifier.mod("max_lord_level");
        public static final Identifier PLAYER_CAPABILITY = FIdentifier.mod("player_capability");
        public static final Identifier REFINEMENTS = FIdentifier.mod("refinements");
        public static final Identifier LORD_TITLES = FIdentifier.mod("lord_titles");
        public static final Identifier TASK_MASTER = FIdentifier.mod("task_master");
        public static final Identifier VILLAGE_BAD_OMEN = FIdentifier.mod("village_bad_omen");
        public static final Identifier VILLAGE_TOTEM = FIdentifier.mod("village_totem");
        public static final Identifier VILLAGE_GUARDS = FIdentifier.mod("village_guards");
        public static final Identifier VILLAGE_BANNER = FIdentifier.mod("village_banner");

        public static final Identifier SKILL_NAME = FIdentifier.mod("skill_name");
        public static final Identifier SKILL_COST = FIdentifier.mod("skill_cost");
        public static final Identifier SKILL_DESCRIPTION = FIdentifier.mod("skill_description");
        public static final Identifier SKILL_ACTION = FIdentifier.mod("skill_action");
        public static final Identifier SKILL_ACTIONS = FIdentifier.mod("skill_actions");
        public static final Identifier SKILL_FACTIONS = FIdentifier.mod("skill_factions");
        public static final Identifier SKILL_TREE_REQUIREMENT = FIdentifier.mod("skill_tree_requirement");
        public static final Identifier SKILL_ENABLE_CONSUMABLE = FIdentifier.mod("skill_enable_consumable");
        public static final Identifier SKILL_DISABLE_CONSUMABLE = FIdentifier.mod("skill_deactivate_consumable");

        public static final Identifier REFINEMENT_SET = FIdentifier.mod("refinement_set");
        public static final Identifier IS_FACTION_BANNER = FIdentifier.mod("is_faction_banner");
        public static final Identifier FACTION_RESTRICTION = FIdentifier.mod("faction_restriction");
        public static final Identifier FACTION_SLAYER = FIdentifier.mod("faction_slayer");
        public static final Identifier SHIFT_DESCRIPTION = FIdentifier.mod("shift_description");
        public static final Identifier BLOCK_DESCRIPTION = FIdentifier.mod("block_description");
        public static final Identifier FACTION_FOOD = FIdentifier.mod("faction_food");
        public static final Identifier OBLIVION_CHARGES = FIdentifier.mod("oblivion_charges");
    }
}
