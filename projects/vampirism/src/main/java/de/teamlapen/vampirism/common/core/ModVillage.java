package de.teamlapen.vampirism.common.core;

import com.google.common.collect.ImmutableSet;
import de.teamlapen.faction.common.core.FactionBlocks;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.tags.ModPoiTypeTags;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.attribute.AttributeTypes;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.timeline.Timeline;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.Set;

public class ModVillage {
    public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(Registries.VILLAGER_PROFESSION, REFERENCE.MODID);
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, REFERENCE.MODID);
    public static final DeferredRegister<EnvironmentAttribute<?>> SCHEDULES = DeferredRegister.create(Registries.ENVIRONMENT_ATTRIBUTE, REFERENCE.MODID);

    public static final DeferredHolder<PoiType, PoiType> HUNTER_TOTEM = POI_TYPES.register("hunter_totem", () -> new PoiType(getAllStates(ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER.get(), ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER_CRAFTED.get()), 1, 1));
    public static final DeferredHolder<PoiType, PoiType> VAMPIRE_TOTEM = POI_TYPES.register("vampire_totem", () -> new PoiType(getAllStates(ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE.get(), ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE_CRAFTED.get()), 1, 1));
    public static final DeferredHolder<PoiType, PoiType> NO_FACTION_TOTEM = POI_TYPES.register("no_faction_totem", () -> new PoiType(getAllStates(FactionBlocks.TOTEM_TOP.get(), FactionBlocks.TOTEM_TOP_CRAFTED.get()), 1, 1));
    public static final DeferredHolder<PoiType, PoiType> ALTAR_CLEANSING = POI_TYPES.register("altar_cleansing", () -> new PoiType(getAllStates(ModBlocks.ALTAR_CLEANSING.get()), 1, 1));
    public static final DeferredHolder<PoiType, PoiType> CREEPER_REPELLENT = POI_TYPES.register("creeper_repellent", () -> new PoiType(getAllStates(ModBlocks.VAMPIRE_SOUL_LANTERN.get()), 1, 1));

    public static final DeferredHolder<EnvironmentAttribute<?>, EnvironmentAttribute<Activity>> CONVERTED_DEFAULT = SCHEDULES.register("converted_default", () -> EnvironmentAttribute.builder(AttributeTypes.ACTIVITY).defaultValue(Activity.IDLE).build());

    public static final ResourceKey<Timeline> VAMPIRE_VILLAGER_SCHEDULE = ResourceKey.create(Registries.TIMELINE, VIdentifier.mod("vampire_villager_schedule"));

    public static final DeferredHolder<VillagerProfession, VillagerProfession> VAMPIRE_EXPERT = PROFESSIONS.register("vampire_expert", () ->
            new VillagerProfession(Component.translatable("entity.minecraft.villager.vampirism.vampire_expert"), (holder) -> holder.is(ModPoiTypeTags.IS_VAMPIRE), (holder) -> holder.is(ModPoiTypeTags.IS_VAMPIRE), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_CARTOGRAPHER, Int2ObjectMap.ofEntries(
                    Int2ObjectMap.entry(1, ModTrades.VAMPIRE_EXPERT_LEVEL_1),
                    Int2ObjectMap.entry(2, ModTrades.VAMPIRE_EXPERT_LEVEL_2),
                    Int2ObjectMap.entry(3, ModTrades.VAMPIRE_EXPERT_LEVEL_3),
                    Int2ObjectMap.entry(4, ModTrades.VAMPIRE_EXPERT_LEVEL_4),
                    Int2ObjectMap.entry(5, ModTrades.VAMPIRE_EXPERT_LEVEL_5)
            )));
    public static final DeferredHolder<VillagerProfession, VillagerProfession> HUNTER_EXPERT = PROFESSIONS.register("hunter_expert", () ->
            new VillagerProfession(Component.translatable("entity.minecraft.villager.vampirism.hunter_expert"), (holder) -> holder.is(ModPoiTypeTags.IS_HUNTER), (holder) -> holder.is(ModPoiTypeTags.IS_HUNTER), ImmutableSet.of(), ImmutableSet.of(ModBlocks.HUNTER_TABLE.get(), ModBlocks.WEAPON_TABLE.get(), ModBlocks.GARLIC.get()), SoundEvents.VILLAGER_WORK_ARMORER, Int2ObjectMap.ofEntries(
                    Int2ObjectMap.entry(1, ModTrades.HUNTER_EXPERT_LEVEL_1),
                    Int2ObjectMap.entry(2, ModTrades.HUNTER_EXPERT_LEVEL_2),
                    Int2ObjectMap.entry(3, ModTrades.HUNTER_EXPERT_LEVEL_3),
                    Int2ObjectMap.entry(4, ModTrades.HUNTER_EXPERT_LEVEL_4),
                    Int2ObjectMap.entry(5, ModTrades.HUNTER_EXPERT_LEVEL_5)
            )));
    public static final DeferredHolder<VillagerProfession, VillagerProfession> PRIEST = PROFESSIONS.register("priest", () ->
            new VillagerProfession(Component.translatable("entity.minecraft.villager.vampirism.priest"), holder -> holder.is(ALTAR_CLEANSING.getKey()), holder -> holder.is(ALTAR_CLEANSING.getKey()), ImmutableSet.of(), ImmutableSet.of(), ModSounds.BLESSING_MUSIC.get(), Int2ObjectMap.ofEntries(
            Int2ObjectMap.entry(1, ModTrades.PRIEST_LEVEL_1),
            Int2ObjectMap.entry(2, ModTrades.PRIEST_LEVEL_2),
            Int2ObjectMap.entry(3, ModTrades.PRIEST_LEVEL_3),
            Int2ObjectMap.entry(4, ModTrades.PRIEST_LEVEL_4),
            Int2ObjectMap.entry(5, ModTrades.PRIEST_LEVEL_5)
    )));

    static void register(IEventBus bus) {
        POI_TYPES.register(bus);
        PROFESSIONS.register(bus);
        SCHEDULES.register(bus);
    }

    static void createTimelines(BootstrapContext<Timeline> bootstrapContext) {
        HolderGetter<WorldClock> clocks = bootstrapContext.lookup(Registries.WORLD_CLOCK);
        Holder.Reference<WorldClock> overworldClock = clocks.getOrThrow(WorldClocks.OVERWORLD);
        bootstrapContext.register(VAMPIRE_VILLAGER_SCHEDULE, Timeline.builder(overworldClock)
                .setPeriodTicks(24000)
                .addTrack(CONVERTED_DEFAULT.get(),
                        builder -> builder
                                .addKeyframe(10, Activity.REST)
                                .addKeyframe(12000, Activity.IDLE)
                                .addKeyframe(14000, Activity.WORK)
                                .addKeyframe(21000, Activity.MEET)
                                .addKeyframe(23000, Activity.IDLE)
                )
                .build());
    }

    private static Set<BlockState> getAllStates(Block... blocks) {
        return Arrays.stream(blocks).flatMap(block -> block.getStateDefinition().getPossibleStates().stream()).collect(ImmutableSet.toImmutableSet());
    }

}
