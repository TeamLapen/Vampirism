package de.teamlapen.lib.lib.data;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
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
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class BlockModelGenerators extends net.minecraft.client.data.models.BlockModelGenerators {
    protected static final ResourceLocation CUTOUT = ResourceLocation.withDefaultNamespace("cutout");
    protected static final ModelTemplate CROP = copy(ModelTemplates.CROP);
    protected static final ModelTemplate FIRE_FLOOR = copy(ModelTemplates.FIRE_FLOOR);
    protected static final ModelTemplate FIRE_SIDE = copy(ModelTemplates.FIRE_SIDE);
    protected static final ModelTemplate FIRE_SIDE_ALT = copy(ModelTemplates.FIRE_SIDE_ALT);
    protected static final ModelTemplate FIRE_UP = copy(ModelTemplates.FIRE_UP);
    protected static final ModelTemplate FIRE_UP_ALT = copy(ModelTemplates.FIRE_UP_ALT);

    protected static ModelTemplate copy(ModelTemplate template) {
        return copy(template, CUTOUT);
    }

    protected static ModelTemplate copy(ModelTemplate template, ResourceLocation renderType) {
        return template.extend().renderType(renderType).build();
    }

    public BlockModelGenerators(net.minecraft.client.data.models.BlockModelGenerators generators) {
        this(generators.blockStateOutput, generators.itemModelOutput, generators.modelOutput);
    }

    public BlockModelGenerators(Consumer<BlockStateGenerator> blockStateGenerator, ItemModelOutput itemModelOutput, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
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

    @Override
    public void createCrossBlock(@NotNull Block block, PlantType type, @NotNull TextureMapping textureMapping) {
        ResourceLocation resourcelocation = type.getCross().extend().renderType(ResourceLocation.withDefaultNamespace("cutout")).build().create(block, textureMapping, this.modelOutput);
        this.blockStateOutput.accept(createSimpleBlock(block, resourcelocation));
    }

    public void createCropBlock(@SuppressWarnings("NullableProblems") Block cropBlock, Property<Integer> ageProperty, int... ageToVisualStageMapping) {
        if (ageProperty.getPossibleValues().size() != ageToVisualStageMapping.length) {
            throw new IllegalArgumentException();
        } else {
            Int2ObjectMap<ResourceLocation> int2objectmap = new Int2ObjectOpenHashMap<>();
            PropertyDispatch propertydispatch = PropertyDispatch.property(ageProperty)
                    .generate(
                            p_388091_ -> {
                                int i = ageToVisualStageMapping[p_388091_];
                                ResourceLocation resourcelocation = int2objectmap.computeIfAbsent(
                                        i, p_387534_ -> this.createSuffixedVariant(cropBlock, "_stage" + i, CROP, TextureMapping::crop)
                                );
                                return Variant.variant().with(VariantProperties.MODEL, resourcelocation);
                            }
                    );
            this.registerSimpleFlatItemModel(cropBlock.asItem());
            this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(cropBlock).with(propertydispatch));
        }
    }


    @Override
    public @NotNull List<ResourceLocation> createFloorFireModels(@NotNull Block fireBlock) {
        ResourceLocation resourcelocation = FIRE_FLOOR
                .create(ModelLocationUtils.getModelLocation(fireBlock, "_floor0"), TextureMapping.fire0(fireBlock), this.modelOutput);
        ResourceLocation resourcelocation1 = FIRE_FLOOR
                .create(ModelLocationUtils.getModelLocation(fireBlock, "_floor1"), TextureMapping.fire1(fireBlock), this.modelOutput);
        return ImmutableList.of(resourcelocation, resourcelocation1);
    }

    @Override
    public @NotNull List<ResourceLocation> createSideFireModels(@NotNull Block fireBlock) {
        ResourceLocation resourcelocation = FIRE_SIDE
                .create(ModelLocationUtils.getModelLocation(fireBlock, "_side0"), TextureMapping.fire0(fireBlock), this.modelOutput);
        ResourceLocation resourcelocation1 = FIRE_SIDE
                .create(ModelLocationUtils.getModelLocation(fireBlock, "_side1"), TextureMapping.fire1(fireBlock), this.modelOutput);
        ResourceLocation resourcelocation2 = FIRE_SIDE_ALT
                .create(ModelLocationUtils.getModelLocation(fireBlock, "_side_alt0"), TextureMapping.fire0(fireBlock), this.modelOutput);
        ResourceLocation resourcelocation3 = FIRE_SIDE_ALT
                .create(ModelLocationUtils.getModelLocation(fireBlock, "_side_alt1"), TextureMapping.fire1(fireBlock), this.modelOutput);
        return ImmutableList.of(resourcelocation, resourcelocation1, resourcelocation2, resourcelocation3);
    }

    @Override
    public @NotNull List<ResourceLocation> createTopFireModels(@NotNull Block fireBlock) {
        ResourceLocation resourcelocation = FIRE_UP
                .create(ModelLocationUtils.getModelLocation(fireBlock, "_up0"), TextureMapping.fire0(fireBlock), this.modelOutput);
        ResourceLocation resourcelocation1 = FIRE_UP
                .create(ModelLocationUtils.getModelLocation(fireBlock, "_up1"), TextureMapping.fire1(fireBlock), this.modelOutput);
        ResourceLocation resourcelocation2 = FIRE_UP_ALT
                .create(ModelLocationUtils.getModelLocation(fireBlock, "_up_alt0"), TextureMapping.fire0(fireBlock), this.modelOutput);
        ResourceLocation resourcelocation3 = FIRE_UP_ALT
                .create(ModelLocationUtils.getModelLocation(fireBlock, "_up_alt1"), TextureMapping.fire1(fireBlock), this.modelOutput);
        return ImmutableList.of(resourcelocation, resourcelocation1, resourcelocation2, resourcelocation3);
    }
}
