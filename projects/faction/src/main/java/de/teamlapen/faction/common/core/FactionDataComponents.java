package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.factions.lord.LordTitles;
import de.teamlapen.faction.api.factions.skills.SkillTreeRequirement;
import de.teamlapen.faction.api.factions.village.TotemPair;
import de.teamlapen.faction.api.factions.village.VillageBanner;
import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.api.util.SafeCast;
import de.teamlapen.faction.api.world.entities.player.FactionPlayerConsumer;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.api.world.items.RefinementItems;
import de.teamlapen.faction.common.components.EffectiveRefinementSet;
import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.faction.common.components.FactionSlayer;
import de.teamlapen.faction.common.util.BlockDescription;
import de.teamlapen.faction.common.util.ShiftDescription;
import de.teamlapen.faction.common.world.items.consume.FactionFoodList;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

import static de.teamlapen.faction.api.registries.ApiRegistryProvider.retrieveDataComponent;

public class FactionDataComponents {

    public static final DeferredRegister.DataComponents ITEM_DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, REFERENCE.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FactionRestriction>> FACTION_RESTRICTION = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.FACTION_RESTRICTION.getPath(), builder -> builder.persistent(FactionRestriction.CODEC).networkSynchronized(FactionRestriction.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FactionSlayer>>  FACTION_SLAYER = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.FACTION_SLAYER.getPath(), builder -> builder.persistent(FactionSlayer.CODEC).networkSynchronized(FactionSlayer.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Unit>> IS_FACTION_BANNER = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.IS_FACTION_BANNER.getPath(), builder -> builder.persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EffectiveRefinementSet>> REFINEMENT_SET = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.REFINEMENT_SET.getPath(), builder -> builder.persistent(EffectiveRefinementSet.CODEC).networkSynchronized(EffectiveRefinementSet.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ShiftDescription>> SHIFT_DESCRIPTION = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.SHIFT_DESCRIPTION.getPath(), builder -> builder.persistent(ShiftDescription.CODEC).networkSynchronized(ShiftDescription.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockDescription>> BLOCK_DESCRIPTION = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.BLOCK_DESCRIPTION.getPath(), builder -> builder.persistent(BlockDescription.CODEC).networkSynchronized(BlockDescription.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FactionFoodList>> FACTION_FOOD = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.FACTION_FOOD.getPath(), builder -> builder.persistent(FactionFoodList.CODEC).networkSynchronized(FactionFoodList.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> FACTION_COLOR = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.FACTION_COLOR.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TextColor>> CHAT_COLOR = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.CHAT_COLOR.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.VAR_INT.map(TextColor::fromRgb, TextColor::getValue)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Component>> FACTION_NAME_SINGULAR = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.FACTION_NAME_SINGULAR.getPath(), builder -> builder.networkSynchronized(ComponentSerialization.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Component>> FACTION_NAME_PLURAL = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.FACTION_NAME_PLURAL.getPath(), builder -> builder.networkSynchronized(ComponentSerialization.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAX_LEVEL = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.MAX_LEVEL.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAX_LORD_LEVEL = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.MAX_LORD_LEVEL.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<AttachmentType<?>>>> PLAYER_CAPABILITY = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.PLAYER_CAPABILITY.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.holderRegistry(NeoForgeRegistries.Keys.ATTACHMENT_TYPES)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RefinementItems>> REFINEMENTS = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.REFINEMENTS.getPath(), builder -> builder.networkSynchronized(RefinementItems.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LordTitles>> LORD_TITLES = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.LORD_TITLES.getPath(), builder -> builder.networkSynchronized(LordTitles.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<EntityType<?>>>> TASK_MASTER = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.TASK_MASTER.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.holderRegistry(Registries.ENTITY_TYPE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<MobEffect>>> VILLAGE_BAD_OMEN = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.VILLAGE_BAD_OMEN.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TotemPair>> VILLAGE_TOTEM = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.VILLAGE_TOTEM.getPath(), builder -> builder.networkSynchronized(TotemPair.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TagKey<EntityType<?>>>> VILLAGE_GUARDS = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.VILLAGE_GUARDS.getPath(), builder -> builder.networkSynchronized(TagKey.streamCodec(Registries.ENTITY_TYPE)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<VillageBanner>> VILLAGE_BANNER = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.VILLAGE_BANNER.getPath(), builder -> builder.networkSynchronized(VillageBanner.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Component>> SKILL_NAME = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.SKILL_NAME.getPath(), builder -> builder.networkSynchronized(ComponentSerialization.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> SKILL_COST = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.SKILL_COST.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Component>> SKILL_DESCRIPTION = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.SKILL_DESCRIPTION.getPath(), builder -> builder.networkSynchronized(ComponentSerialization.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Holder<? extends IAction<?>>>>> SKILL_ACTIONS = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.SKILL_ACTIONS.getPath(), builder -> builder.networkSynchronized(SafeCast.<StreamCodec<RegistryFriendlyByteBuf, Holder<? extends IAction<?>>>>cast(ByteBufCodecs.holderRegistry(FactionRegistries.Keys.ACTION)).apply(ByteBufCodecs.list())));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<? extends IAction<?>>>> SKILL_ACTION = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.SKILL_ACTION.getPath(), builder -> builder.networkSynchronized(SafeCast.<StreamCodec<RegistryFriendlyByteBuf, Holder<? extends IAction<?>>>>cast(ByteBufCodecs.holderRegistry(FactionRegistries.Keys.ACTION))));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TagKey<IFaction<?>>>> SKILL_FACTIONS = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.SKILL_FACTIONS.getPath(), builder -> builder.networkSynchronized(TagKey.streamCodec(FactionRegistries.Keys.FACTION)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SkillTreeRequirement>> SKILL_TREE_REQUIREMENT = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.SKILL_TREE_REQUIREMENT.getPath(), builder -> builder.networkSynchronized(SkillTreeRequirement.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<FactionPlayerConsumer>>> SKILL_ENABLE_CONSUMABLE = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.SKILL_ENABLE_CONSUMABLE.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.holderRegistry(FactionRegistries.Keys.FACTION_PLAYER_CONSUMER)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<FactionPlayerConsumer>>> SKILL_DISABLE_CONSUMABLE = ITEM_DATA_COMPONENTS.registerComponentType(de.teamlapen.faction.api.FactionDataComponents.Keys.SKILL_DISABLE_CONSUMABLE.getPath(), builder -> builder.networkSynchronized(ByteBufCodecs.holderRegistry(FactionRegistries.Keys.FACTION_PLAYER_CONSUMER)));


    static void register(IEventBus eventBus) {
        ITEM_DATA_COMPONENTS.register(eventBus);
    }
}
