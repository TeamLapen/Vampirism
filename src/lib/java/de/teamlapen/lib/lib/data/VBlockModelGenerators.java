package de.teamlapen.lib.lib.data;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.data.models.BlockModelGenerators;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.blockstates.*;
import net.minecraft.client.data.models.model.*;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class VBlockModelGenerators extends BlockModelGenerators {

    private static final ResourceLocation CUTOUT = ResourceLocation.withDefaultNamespace("cutout");

    public VBlockModelGenerators(Consumer<BlockStateGenerator> blockStateGenerator, ItemModelOutput itemModelOutput, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        super(blockStateGenerator, itemModelOutput, modelOutput);
    }

    protected void createFamilies(Collection<BlockFamily> families) {
        for (BlockFamily family : families) {
            if (family.shouldGenerateModel()) {
                this.family(family.getBaseBlock()).generateFor(family);
                createDefaultBlockItem(family.getBaseBlock());
                Stream.of(BlockFamily.Variant.PRESSURE_PLATE, BlockFamily.Variant.FENCE_GATE, BlockFamily.Variant.CRACKED, BlockFamily.Variant.CHISELED).forEach(variant -> {
                    Block block = family.get(variant);
                    //noinspection ConstantValue
                    if (block != null) {
                        createDefaultBlockItem(block, ModelLocationUtils.getModelLocation(block));
                    }
                });
            }
        }
    }

    protected void createTrivialBlockWithItem(Block block) {
        createTrivialCube(block);
        createDefaultBlockItem(block);
    }

    protected void createNonTemplateBlockWithItem(Block block) {
        if (block.getStateDefinition().getProperty(BlockStateProperties.HORIZONTAL_FACING.getName()) != null) {
            createNonTemplateHorizontalBlock(block);
        } else {
            createNonTemplateModelBlock(block);
        }
        createDefaultBlockItem(block);
    }

    private final Set<Item> createdItemModels = new HashSet<>();

    protected void createDefaultBlockItem(Block block, ResourceLocation model) {
        var item = block.asItem();
        if (item != Items.AIR && !createdItemModels.contains(item)) {
            this.createdItemModels.add(item);
            this.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(model));
        }
    }

    protected void createDefaultBlockItem(Block block) {
        createDefaultBlockItem(block, ModelLocationUtils.getModelLocation(block));
    }

    protected void createDefaultBlockItem(Block block, Block model) {
        createDefaultBlockItem(block, ModelLocationUtils.getModelLocation(model));
    }

    /**
     * The normal methods in the base class don't add the cutout render type, but it must be here so that the texture is rendered in a correct way.
     */
    @Override
    public void createCrossBlock(@NotNull Block block, PlantType type, @NotNull TextureMapping textureMapping) {
        ResourceLocation resourcelocation = type.getCross().extend().renderType(CUTOUT).build().create(block, textureMapping, this.modelOutput);
        this.blockStateOutput.accept(createSimpleBlock(block, resourcelocation));
    }

    @Override
    public @NotNull List<ResourceLocation> createFloorFireModels(@NotNull Block fireBlock) {
        ModelTemplate fire_floor = copy(ModelTemplates.FIRE_FLOOR, CUTOUT);
        ResourceLocation resourcelocation = fire_floor
                .create(ModelLocationUtils.getModelLocation(fireBlock, "_floor0"), TextureMapping.fire0(fireBlock), this.modelOutput);
        ResourceLocation resourcelocation1 = fire_floor
                .create(ModelLocationUtils.getModelLocation(fireBlock, "_floor1"), TextureMapping.fire1(fireBlock), this.modelOutput);
        return ImmutableList.of(resourcelocation, resourcelocation1);
    }

    @Override
    public @NotNull List<ResourceLocation> createSideFireModels(@NotNull Block fireBlock) {
        var fireSide = copy(ModelTemplates.FIRE_SIDE, CUTOUT);
        var fireSideAlt = copy(ModelTemplates.FIRE_SIDE_ALT, CUTOUT);
        ResourceLocation resourcelocation = fireSide
                .create(ModelLocationUtils.getModelLocation(fireBlock, "_side0"), TextureMapping.fire0(fireBlock), this.modelOutput);
        ResourceLocation resourcelocation1 = fireSide
                .create(ModelLocationUtils.getModelLocation(fireBlock, "_side1"), TextureMapping.fire1(fireBlock), this.modelOutput);
        ResourceLocation resourcelocation2 = fireSideAlt
                .create(ModelLocationUtils.getModelLocation(fireBlock, "_side_alt0"), TextureMapping.fire0(fireBlock), this.modelOutput);
        ResourceLocation resourcelocation3 = fireSideAlt
                .create(ModelLocationUtils.getModelLocation(fireBlock, "_side_alt1"), TextureMapping.fire1(fireBlock), this.modelOutput);
        return ImmutableList.of(resourcelocation, resourcelocation1, resourcelocation2, resourcelocation3);
    }

    @Override
    public @NotNull List<ResourceLocation> createTopFireModels(@NotNull Block fireBlock) {
        var fireUp = copy(ModelTemplates.FIRE_UP, CUTOUT);
        var fireUpAlt = copy(ModelTemplates.FIRE_UP_ALT, CUTOUT);
        ResourceLocation resourcelocation = fireUp
                .create(ModelLocationUtils.getModelLocation(fireBlock, "_up0"), TextureMapping.fire0(fireBlock), this.modelOutput);
        ResourceLocation resourcelocation1 = fireUp
                .create(ModelLocationUtils.getModelLocation(fireBlock, "_up1"), TextureMapping.fire1(fireBlock), this.modelOutput);
        ResourceLocation resourcelocation2 = fireUpAlt
                .create(ModelLocationUtils.getModelLocation(fireBlock, "_up_alt0"), TextureMapping.fire0(fireBlock), this.modelOutput);
        ResourceLocation resourcelocation3 = fireUpAlt
                .create(ModelLocationUtils.getModelLocation(fireBlock, "_up_alt1"), TextureMapping.fire1(fireBlock), this.modelOutput);
        return ImmutableList.of(resourcelocation, resourcelocation1, resourcelocation2, resourcelocation3);
    }

    @Override
    public void createPlant(@NotNull Block block, @NotNull Block pottedBlock, @NotNull PlantType plantType) {
        this.createCrossBlock(block, plantType);
        TextureMapping texturemapping = plantType.getPlantTextureMapping(block);
        ResourceLocation resourcelocation = plantType.getCrossPot().extend().renderType(CUTOUT).build().create(pottedBlock, texturemapping, this.modelOutput);
        this.blockStateOutput.accept(createSimpleBlock(pottedBlock, resourcelocation));
    }

    @Override
    public void createCropBlock(@NotNull Block cropBlock, Property<Integer> ageProperty, int... ageToVisualStageMapping) {
        if (ageProperty.getPossibleValues().size() != ageToVisualStageMapping.length) {
            throw new IllegalArgumentException();
        } else {
            Int2ObjectMap<ResourceLocation> int2objectmap = new Int2ObjectOpenHashMap<>();
            PropertyDispatch propertydispatch = PropertyDispatch.property(ageProperty)
                    .generate(
                            p_388091_ -> {
                                int i = ageToVisualStageMapping[p_388091_];
                                ResourceLocation resourcelocation = int2objectmap.computeIfAbsent(
                                        i, p_387534_ -> this.createSuffixedVariant(cropBlock, "_stage" + i, ModelTemplates.CROP.extend().renderType(CUTOUT).build(), TextureMapping::crop)
                                );
                                return Variant.variant().with(VariantProperties.MODEL, resourcelocation);
                            }
                    );
            this.registerSimpleFlatItemModel(cropBlock.asItem());
            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(cropBlock).with(propertydispatch));
        }
    }

    protected static ModelTemplate copy(ModelTemplate template, ResourceLocation renderType) {
        return template.extend().renderType(renderType).build();
    }

}
