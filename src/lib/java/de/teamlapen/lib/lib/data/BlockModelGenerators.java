package de.teamlapen.lib.lib.data;

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
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class BlockModelGenerators extends net.minecraft.client.data.models.BlockModelGenerators {
    private static final ModelTemplate CROP = copy(net.minecraft.client.data.models.model.ModelTemplates.CROP, ResourceLocation.withDefaultNamespace("cutout"));

    private static ModelTemplate copy(ModelTemplate template, ResourceLocation renderType) {
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
}
