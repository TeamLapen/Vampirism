package de.teamlapen.faction.common.core;

import com.mojang.serialization.Codec;
import de.teamlapen.faction.api.FactionDataComponents.Keys;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.factions.lord.LordTitles;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.SkillTreeRequirement;
import de.teamlapen.faction.api.factions.village.TotemPair;
import de.teamlapen.faction.api.factions.village.VillageBanner;
import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.api.util.SafeCast;
import de.teamlapen.faction.api.world.entities.player.FactionPlayerConsumer;
import de.teamlapen.faction.api.world.items.RefinementItems;
import de.teamlapen.faction.common.components.EffectiveRefinementSet;
import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.faction.common.components.FactionSlayer;
import de.teamlapen.faction.common.util.BlockDescription;
import de.teamlapen.faction.common.util.ShiftDescription;
import de.teamlapen.faction.common.world.items.consume.FactionFoodList;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;


public class FactionDataComponents {

    public static final DeferredRegister.DataComponents ITEM_DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, REFERENCE.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FactionRestriction>> FACTION_RESTRICTION = ITEM_DATA_COMPONENTS.registerComponentType(Keys.FACTION_RESTRICTION.getPath(), builder -> builder.persistent(FactionRestriction.CODEC).networkSynchronized(FactionRestriction.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FactionSlayer>>  FACTION_SLAYER = ITEM_DATA_COMPONENTS.registerComponentType(Keys.FACTION_SLAYER.getPath(), builder -> builder.persistent(FactionSlayer.CODEC).networkSynchronized(FactionSlayer.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> IS_FACTION_BANNER = ITEM_DATA_COMPONENTS.registerComponentType(Keys.IS_FACTION_BANNER.getPath(), builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EffectiveRefinementSet>> REFINEMENT_SET = ITEM_DATA_COMPONENTS.registerComponentType(Keys.REFINEMENT_SET.getPath(), builder -> builder.persistent(EffectiveRefinementSet.CODEC).networkSynchronized(EffectiveRefinementSet.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ShiftDescription>> SHIFT_DESCRIPTION = ITEM_DATA_COMPONENTS.registerComponentType(Keys.SHIFT_DESCRIPTION.getPath(), builder -> builder.persistent(ShiftDescription.CODEC).networkSynchronized(ShiftDescription.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockDescription>> BLOCK_DESCRIPTION = ITEM_DATA_COMPONENTS.registerComponentType(Keys.BLOCK_DESCRIPTION.getPath(), builder -> builder.persistent(BlockDescription.CODEC).networkSynchronized(BlockDescription.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FactionFoodList>> FACTION_FOOD = ITEM_DATA_COMPONENTS.registerComponentType(Keys.FACTION_FOOD.getPath(), builder -> builder.persistent(FactionFoodList.CODEC).networkSynchronized(FactionFoodList.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FACTION_COLOR = ITEM_DATA_COMPONENTS.registerComponentType(Keys.FACTION_COLOR.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TextColor>> CHAT_COLOR = ITEM_DATA_COMPONENTS.registerComponentType(Keys.CHAT_COLOR.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.VAR_INT.map(TextColor::fromRgb, TextColor::getValue)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Component>> FACTION_NAME_SINGULAR = ITEM_DATA_COMPONENTS.registerComponentType(Keys.FACTION_NAME_SINGULAR.getPath(), builder -> builder.networkSynchronized(ComponentSerialization.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Component>> FACTION_NAME_PLURAL = ITEM_DATA_COMPONENTS.registerComponentType(Keys.FACTION_NAME_PLURAL.getPath(), builder -> builder.networkSynchronized(ComponentSerialization.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAX_LEVEL = ITEM_DATA_COMPONENTS.registerComponentType(Keys.MAX_LEVEL.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAX_LORD_LEVEL = ITEM_DATA_COMPONENTS.registerComponentType(Keys.MAX_LORD_LEVEL.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<AttachmentType<?>>>> PLAYER_CAPABILITY = ITEM_DATA_COMPONENTS.registerComponentType(Keys.PLAYER_CAPABILITY.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.holderRegistry(NeoForgeRegistries.Keys.ATTACHMENT_TYPES)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RefinementItems>> REFINEMENTS = ITEM_DATA_COMPONENTS.registerComponentType(Keys.REFINEMENTS.getPath(), builder -> builder.networkSynchronized(RefinementItems.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LordTitles>> LORD_TITLES = ITEM_DATA_COMPONENTS.registerComponentType(Keys.LORD_TITLES.getPath(), builder -> builder.networkSynchronized(LordTitles.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<EntityType<?>>>> TASK_MASTER = ITEM_DATA_COMPONENTS.registerComponentType(Keys.TASK_MASTER.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.holderRegistry(Registries.ENTITY_TYPE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<MobEffect>>> VILLAGE_BAD_OMEN = ITEM_DATA_COMPONENTS.registerComponentType(Keys.VILLAGE_BAD_OMEN.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TotemPair>> VILLAGE_TOTEM = ITEM_DATA_COMPONENTS.registerComponentType(Keys.VILLAGE_TOTEM.getPath(), builder -> builder.networkSynchronized(TotemPair.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TagKey<EntityType<?>>>> VILLAGE_GUARDS = ITEM_DATA_COMPONENTS.registerComponentType(Keys.VILLAGE_GUARDS.getPath(), builder -> builder.networkSynchronized(TagKey.streamCodec(Registries.ENTITY_TYPE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<VillageBanner>> VILLAGE_BANNER = ITEM_DATA_COMPONENTS.registerComponentType(Keys.VILLAGE_BANNER.getPath(), builder -> builder.networkSynchronized(VillageBanner.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Component>> SKILL_NAME = ITEM_DATA_COMPONENTS.registerComponentType(Keys.SKILL_NAME.getPath(), builder -> builder.networkSynchronized(ComponentSerialization.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SKILL_COST = ITEM_DATA_COMPONENTS.registerComponentType(Keys.SKILL_COST.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Component>> SKILL_DESCRIPTION = ITEM_DATA_COMPONENTS.registerComponentType(Keys.SKILL_DESCRIPTION.getPath(), builder -> builder.networkSynchronized(ComponentSerialization.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Holder<? extends IAction<?>>>>> SKILL_ACTIONS = ITEM_DATA_COMPONENTS.registerComponentType(Keys.SKILL_ACTIONS.getPath(), builder -> builder.networkSynchronized(SafeCast.<StreamCodec<RegistryFriendlyByteBuf, Holder<? extends IAction<?>>>>cast(ByteBufCodecs.holderRegistry(FactionRegistries.Keys.ACTION)).apply(ByteBufCodecs.list())));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<? extends IAction<?>>>> SKILL_ACTION = ITEM_DATA_COMPONENTS.registerComponentType(Keys.SKILL_ACTION.getPath(), builder -> builder.networkSynchronized(SafeCast.<StreamCodec<RegistryFriendlyByteBuf, Holder<? extends IAction<?>>>>cast(ByteBufCodecs.holderRegistry(FactionRegistries.Keys.ACTION))));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TagKey<IFaction<?>>>> SKILL_FACTIONS = ITEM_DATA_COMPONENTS.registerComponentType(Keys.SKILL_FACTIONS.getPath(), builder -> builder.networkSynchronized(TagKey.streamCodec(FactionRegistries.Keys.FACTION)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SkillTreeRequirement>> SKILL_TREE_REQUIREMENT = ITEM_DATA_COMPONENTS.registerComponentType(Keys.SKILL_TREE_REQUIREMENT.getPath(), builder -> builder.networkSynchronized(SkillTreeRequirement.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<FactionPlayerConsumer>>> SKILL_ENABLE_CONSUMABLE = ITEM_DATA_COMPONENTS.registerComponentType(Keys.SKILL_ENABLE_CONSUMABLE.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.holderRegistry(FactionRegistries.Keys.FACTION_PLAYER_CONSUMER)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<FactionPlayerConsumer>>> SKILL_DISABLE_CONSUMABLE = ITEM_DATA_COMPONENTS.registerComponentType(Keys.SKILL_DISABLE_CONSUMABLE.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.holderRegistry(FactionRegistries.Keys.FACTION_PLAYER_CONSUMER)));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Component>> MINION_TASK_NAME = ITEM_DATA_COMPONENTS.registerComponentType(Keys.MINION_TASK_NAME.getPath(), builder -> builder.networkSynchronized(ComponentSerialization.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TagKey<IFaction<?>>>> MINION_TASK_FACTIONS = ITEM_DATA_COMPONENTS.registerComponentType(Keys.MINION_TASK_FACTIONS.getPath(), builder -> builder.networkSynchronized(TagKey.streamCodec(FactionRegistries.Keys.FACTION)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<? extends ISkill<?>>>> MINION_TASK_SKILL_REQUIREMENT = ITEM_DATA_COMPONENTS.registerComponentType(Keys.MINION_TASK_SKILL_REQUIREMENT.getPath(), builder -> builder.networkSynchronized(SafeCast.cast(ByteBufCodecs.holderRegistry(FactionRegistries.Keys.SKILL))));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> MINION_TASK_GLOBAL_COMMAND = ITEM_DATA_COMPONENTS.registerComponentType(Keys.MINION_TASK_GLOBAL_COMMAND.getPath(), builder -> builder.networkSynchronized(Unit.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MINION_LEVEL = ITEM_DATA_COMPONENTS.registerComponentType(Keys.MINION_LEVEL.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.VAR_INT).persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MINION_MAX_LEVEL = ITEM_DATA_COMPONENTS.registerComponentType(Keys.MINION_MAX_LEVEL.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.VAR_INT).persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> MINION_NAME = ITEM_DATA_COMPONENTS.registerComponentType(Keys.MINION_NAME.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.STRING_UTF8).persistent(Codec.STRING));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> MINION_USE_LORD_SKIN = ITEM_DATA_COMPONENTS.registerComponentType(Keys.MINION_USE_LORD_SKIN.getPath(), builder -> builder.networkSynchronized(Unit.STREAM_CODEC).persistent(Unit.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> MINION_HAS_INCREASED_STATS = ITEM_DATA_COMPONENTS.registerComponentType(Keys.MINION_HAS_INCREASED_STATS.getPath(), builder -> builder.networkSynchronized(Unit.STREAM_CODEC).persistent(Unit.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MINION_HEALTH_LEVEL = ITEM_DATA_COMPONENTS.registerComponentType(Keys.MINION_HEALTH_LEVEL.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.VAR_INT).persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MINION_INVENTORY_LEVEL = ITEM_DATA_COMPONENTS.registerComponentType(Keys.MINION_INVENTORY_LEVEL.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.VAR_INT).persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MINION_SPEED_LEVEL = ITEM_DATA_COMPONENTS.registerComponentType(Keys.MINION_SPEED_LEVEL.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.VAR_INT).persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MINION_STRENGTH_LEVEL = ITEM_DATA_COMPONENTS.registerComponentType(Keys.MINION_STRENGTH_LEVEL.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.VAR_INT).persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MINION_RESOURCES_LEVEL = ITEM_DATA_COMPONENTS.registerComponentType(Keys.MINION_RESOURCES_LEVEL.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.VAR_INT).persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MINION_INVENTORY_SLOTS = ITEM_DATA_COMPONENTS.registerComponentType(Keys.MINION_INVENTORY_SLOTS.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.VAR_INT).persistent(Codec.INT));

    static void register(IEventBus eventBus) {
        ITEM_DATA_COMPONENTS.register(eventBus);
    }
}
