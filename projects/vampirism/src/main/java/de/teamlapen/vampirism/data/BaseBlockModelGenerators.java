package de.teamlapen.vampirism.data;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.Identifier;
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

public abstract class BaseBlockModelGenerators extends BlockModelGenerators {

    private static final Identifier CUTOUT = Identifier.withDefaultNamespace("cutout");

    public static final PropertyDispatch<VariantMutator> HORIZONTAL_ROTATION = ROTATION_HORIZONTAL_FACING;

    public BaseBlockModelGenerators(Consumer<BlockModelDefinitionGenerator> blockStateGenerator, ItemModelOutput itemModelOutput, BiConsumer<Identifier, ModelInstance> modelOutput) {
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

    protected void createDefaultBlockItem(Block block, Identifier model) {
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
     * copy from parent with changed cutout render type
     */
    @Override
    public void createCrossBlock(@NotNull Block block, PlantType plantType, @NotNull TextureMapping textureMapping) {
        MultiVariant multivariant = plainVariant(plantType.getCross().extend().renderType(CUTOUT).build().create(block, textureMapping, this.modelOutput));
        this.blockStateOutput.accept(createSimpleBlock(block, multivariant));
    }

    /**
     * copy from parent with changed cutout render type
     */
    @Override
    public @NotNull MultiVariant createFloorFireModels(@NotNull Block block) {
        ModelTemplate copy = copy(ModelTemplates.FIRE_FLOOR, CUTOUT);
        return variants(
                plainModel(copy.create(ModelLocationUtils.getModelLocation(block, "_floor0"), TextureMapping.fire0(block), this.modelOutput)),
                plainModel(copy.create(ModelLocationUtils.getModelLocation(block, "_floor1"), TextureMapping.fire1(block), this.modelOutput))
        );
    }

    @Override
    public @NotNull MultiVariant createSideFireModels(@NotNull Block block) {
        var fireSide = copy(ModelTemplates.FIRE_SIDE, CUTOUT);
        var fireSideAlt = copy(ModelTemplates.FIRE_SIDE_ALT, CUTOUT);

        return variants(
                plainModel(fireSide.create(ModelLocationUtils.getModelLocation(block, "_side0"), TextureMapping.fire0(block), this.modelOutput)),
                plainModel(fireSide.create(ModelLocationUtils.getModelLocation(block, "_side1"), TextureMapping.fire1(block), this.modelOutput)),
                plainModel(fireSideAlt.create(ModelLocationUtils.getModelLocation(block, "_side_alt0"), TextureMapping.fire0(block), this.modelOutput)),
                plainModel(fireSideAlt.create(ModelLocationUtils.getModelLocation(block, "_side_alt1"), TextureMapping.fire1(block), this.modelOutput))
        );
    }

    @Override
    public @NotNull MultiVariant createTopFireModels(@NotNull Block block) {
        var fireUp = copy(ModelTemplates.FIRE_UP, CUTOUT);
        var fireUpAlt = copy(ModelTemplates.FIRE_UP_ALT, CUTOUT);

        return variants(
                plainModel(fireUp.create(ModelLocationUtils.getModelLocation(block, "_up0"), TextureMapping.fire0(block), this.modelOutput)),
                plainModel(fireUp.create(ModelLocationUtils.getModelLocation(block, "_up1"), TextureMapping.fire1(block), this.modelOutput)),
                plainModel(fireUpAlt.create(ModelLocationUtils.getModelLocation(block, "_up_alt0"), TextureMapping.fire0(block), this.modelOutput)),
                plainModel(fireUpAlt.create(ModelLocationUtils.getModelLocation(block, "_up_alt1"), TextureMapping.fire1(block), this.modelOutput))
        );
    }

    @Override
    public void createPlant(@NotNull Block block, @NotNull Block pottedBlock, @NotNull PlantType plantType) {
        this.createCrossBlock(block, plantType);
        TextureMapping texturemapping = plantType.getPlantTextureMapping(block);
        MultiVariant multivariant = plainVariant(plantType.getCrossPot().extend().renderType(CUTOUT).build().create(pottedBlock, texturemapping, this.modelOutput));
        this.blockStateOutput.accept(createSimpleBlock(pottedBlock, multivariant));
    }

    @Override
    public void createCropBlock(@NotNull Block cropBlock, Property<Integer> ageProperty, int... ageToVisualStageMapping) {
        this.registerSimpleFlatItemModel(cropBlock.asItem());
        if (ageProperty.getPossibleValues().size() != ageToVisualStageMapping.length) {
            throw new IllegalArgumentException();
        } else {
            Int2ObjectMap<Identifier> int2objectmap = new Int2ObjectOpenHashMap<>();
            this.blockStateOutput.accept(MultiVariantGenerator.dispatch(cropBlock)
                    .with(PropertyDispatch.initial(ageProperty).generate(p_408977_ -> {
                        int i = ageToVisualStageMapping[p_408977_];
                        return plainVariant(int2objectmap.computeIfAbsent(i, p_387308_ -> this.createSuffixedVariant(cropBlock, "_stage" + p_387308_, ModelTemplates.CROP.extend().renderType(CUTOUT).build(), TextureMapping::crop)));
                    })));
        }
    }

    protected static ModelTemplate copy(ModelTemplate template, Identifier renderType) {
        return template.extend().renderType(renderType).build();
    }

}
