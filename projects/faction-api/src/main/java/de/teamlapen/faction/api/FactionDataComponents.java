package de.teamlapen.faction.api;

import de.teamlapen.faction.api.factions.lord.LordTitles;
import de.teamlapen.faction.api.factions.village.TotemPair;
import de.teamlapen.faction.api.factions.village.VillageBanner;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.world.entities.ITaskMasterEntity;
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
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;

import static de.teamlapen.faction.api.registries.ApiRegistryProvider.retrieveDataComponent;

@SuppressWarnings("unused")
public class FactionDataComponents {

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<IEffectiveRefinementSet>> REFINEMENT_SET = retrieveDataComponent(Keys.REFINEMENT_SET);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<IFactionSlayer>> FACTION_SLAYER = retrieveDataComponent(Keys.FACTION_SLAYER);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> IS_FACTION_BANNER = retrieveDataComponent(Keys.IS_FACTION_BANNER);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<IFactionRestriction>> FACTION_RESTRICTION = retrieveDataComponent(Keys.FACTION_RESTRICTION);

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

        public static final Identifier REFINEMENT_SET = FIdentifier.mod("refinement_set");
        public static final Identifier IS_FACTION_BANNER = FIdentifier.mod("is_faction_banner");
        public static final Identifier FACTION_RESTRICTION = FIdentifier.mod("faction_restriction");
        public static final Identifier FACTION_SLAYER = FIdentifier.mod("faction_slayer");
        public static final Identifier SHIFT_DESCRIPTION = FIdentifier.mod("shift_description");
        public static final Identifier BLOCK_DESCRIPTION = FIdentifier.mod("block_description");
        public static final Identifier FACTION_FOOD = FIdentifier.mod("faction_food");
    }
}
