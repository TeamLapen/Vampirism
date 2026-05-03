package de.teamlapen.vampirism.common.world.items.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.common.util.TotemHelper;
import de.teamlapen.vampirism.common.world.attachments.NearestVillage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Set;

public class BiomeMapFunction extends LootItemConditionalFunction  {
    public static final TagKey<Biome> DEFAULT_DESTINATION = BiomeTags.HAS_SWAMP_HUT;
    public static final Holder<MapDecorationType> DEFAULT_DECORATION = MapDecorationTypes.WOODLAND_MANSION;
    public static final byte DEFAULT_ZOOM = 2;
    public static final int DEFAULT_SEARCH_RADIUS = 50;
    public static final boolean DEFAULT_SKIP_EXISTING = true;
    public static final MapCodec<BiomeMapFunction> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> commonFields(i)
                    .and(
                            i.group(
                                    TagKey.codec(Registries.BIOME).optionalFieldOf("destination", DEFAULT_DESTINATION).forGetter(f -> f.destination),
                                    MapDecorationType.CODEC.optionalFieldOf("decoration", DEFAULT_DECORATION).forGetter(f -> f.mapDecoration),
                                    Codec.BYTE.optionalFieldOf("zoom", (byte)2).forGetter(f -> f.zoom),
                                    Codec.INT.optionalFieldOf("search_radius", 50).forGetter(f -> f.searchRadius),
                                    Codec.BOOL.optionalFieldOf("skip_existing_chunks", true).forGetter(f -> f.skipKnownStructures)
                            )
                    )
                    .apply(i, BiomeMapFunction::new)
    );
    private final TagKey<Biome> destination;
    private final Holder<MapDecorationType> mapDecoration;
    private final byte zoom;
    private final int searchRadius;
    private final boolean skipKnownStructures;

    private BiomeMapFunction(
            List<LootItemCondition> predicates,
            TagKey<Biome> destination,
            Holder<MapDecorationType> mapDecoration,
            byte zoom,
            int searchRadius,
            boolean skipKnownStructures
    ) {
        super(predicates);
        this.destination = destination;
        this.mapDecoration = mapDecoration;
        this.zoom = zoom;
        this.searchRadius = searchRadius;
        this.skipKnownStructures = skipKnownStructures;
    }

    @Override
    public MapCodec<BiomeMapFunction> codec() {
        return MAP_CODEC;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.ORIGIN);
    }

    @Override
    public ItemStack run(ItemStack itemStack, LootContext context) {
        if (!itemStack.is(Items.MAP)) {
            return itemStack;
        } else {
            Vec3 lootPos = context.getOptionalParameter(LootContextParams.ORIGIN);
            if (lootPos != null) {
                ServerLevel level = context.getLevel();
                BlockPos nearestMapStructure = TotemHelper.getTotemNearPos(level, new BlockPos((int) lootPos.x, (int) lootPos.y, (int) lootPos.z), true)
                        .flatMap(x -> NearestVillage.get(x).getClosestVampireForest())
                        .orElse(null);

                if (nearestMapStructure != null) {
                    ItemStack map = MapItem.create(level, nearestMapStructure.getX(), nearestMapStructure.getZ(), this.zoom, true, true);
                    MapItem.renderBiomePreviewMap(level, map);
                    MapItemSavedData.addTargetDecoration(map, nearestMapStructure, "+", this.mapDecoration);
                    return map;
                }
            }

            return itemStack;
        }
    }

    public static Builder makeBiomeMap() {
        return new Builder();
    }

    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        private TagKey<Biome> destination = BiomeMapFunction.DEFAULT_DESTINATION;
        private Holder<MapDecorationType> mapDecoration = BiomeMapFunction.DEFAULT_DECORATION;
        private byte zoom = 2;
        private int searchRadius = 50;
        private boolean skipKnownStructures = true;

        protected Builder getThis() {
            return this;
        }

        public Builder setDestination(TagKey<Biome> destination) {
            this.destination = destination;
            return this;
        }

        public Builder setMapDecoration(Holder<MapDecorationType> mapDecoration) {
            this.mapDecoration = mapDecoration;
            return this;
        }

        public Builder setZoom(byte zoom) {
            this.zoom = zoom;
            return this;
        }

        public Builder setSearchRadius(int searchRadius) {
            this.searchRadius = searchRadius;
            return this;
        }

        public Builder setSkipKnownStructures(boolean skipKnownStructures) {
            this.skipKnownStructures = skipKnownStructures;
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new BiomeMapFunction(
                    this.getConditions(), this.destination, this.mapDecoration, this.zoom, this.searchRadius, this.skipKnownStructures
            );
        }
    }
}
