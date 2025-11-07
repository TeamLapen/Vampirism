package de.teamlapen.vampirism.data.provider.models;

import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quadrant;
import de.teamlapen.lib.data.BaseBlockModelGenerators;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.renderer.items.BloodContainerRenderer;
import de.teamlapen.vampirism.client.renderer.items.MotherTrophyRenderer;
import de.teamlapen.vampirism.common.blocks.*;
import de.teamlapen.vampirism.common.blocks.candle.CandleHolderBlock;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.util.ColorListsUtil;
import de.teamlapen.vampirism.data.ModBlockFamilies;
import net.minecraft.client.color.item.GrassColorSource;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static de.teamlapen.vampirism.api.util.VResourceLocation.mod;
import static de.teamlapen.vampirism.api.util.VResourceLocation.modString;
import static net.minecraft.client.data.models.model.ModelLocationUtils.decorateBlockModelLocation;
import static net.minecraft.client.data.models.model.ModelLocationUtils.getModelLocation;

public class ModBlockModelGenerators extends BaseBlockModelGenerators {

    public ModBlockModelGenerators(net.minecraft.client.data.models.BlockModelGenerators generators) {
        super(generators.blockStateOutput, generators.itemModelOutput, generators.modelOutput);
    }

    @Override
    public void run() {
        createFamilies(ModBlockFamilies.getFamilies());

        createGarlicDiffuser();
        createCandleHolders();
        createVampireSoulLantern();
        createBloodGrinder();
        createBloodSieve();
        createCursedBark();
        createHunterTable();
        createCoffin();
        createAlchemyTable();
        createWeaponTable();
        createNonTemplateBlocks();
        createTrivialBlocks();
        createMotherTrophy();
        createAltarPillar();
        createTent();
        createMedChair();
        createTotem();
        createAlchemicalCauldron();
        createCursedGrassBlock();
        createAlchemicalFire();
        createPlants();
        createWood();
        createCursedEarthPath();
        createInfuser();

        createTintedLeaves(ModBlocks.DARK_SPRUCE_LEAVES.get(), TexturedModel.LEAVES, -1);

        var sunscreenModel = ModModelTemplates.BEACON_MODEL.create(ModBlocks.SUNSCREEN_BEACON.get(), new TextureMapping().put(ModTextureSlots.BEACON, mod("block/cursed_earth")), this.modelOutput);
        this.blockStateOutput.accept(createSimpleBlock(ModBlocks.SUNSCREEN_BEACON.get(), plainVariant(sunscreenModel)));
        createDefaultBlockItem(ModBlocks.SUNSCREEN_BEACON.get(), sunscreenModel);

        ResourceLocation vampireBeaconModel = ModModelTemplates.BEACON_MODEL.create(ModBlocks.VAMPIRE_BEACON.get(), new TextureMapping().put(ModTextureSlots.BEACON, mod("block/vampire_beacon")), this.modelOutput);
        this.blockStateOutput.accept(createSimpleBlock(ModBlocks.VAMPIRE_BEACON.get(), plainVariant(vampireBeaconModel)));
        createDefaultBlockItem(ModBlocks.VAMPIRE_BEACON.get(), vampireBeaconModel);

        ResourceLocation infestedDarkStoneModel = ModModelTemplates.CUBE_ALL.create(ModBlocks.INFESTED_DARK_STONE.get(), new TextureMapping().put(TextureSlot.ALL, mod("block/dark_stone")), this.modelOutput);
        this.blockStateOutput.accept(createSimpleBlock(ModBlocks.INFESTED_DARK_STONE.get(), plainVariant(infestedDarkStoneModel)));
        createDefaultBlockItem(ModBlocks.INFESTED_DARK_STONE.get(), VResourceLocation.mod("block/infested_dark_stone"));

        ResourceLocation batCageModel = mod("block/bat_cage/block");
        this.blockStateOutput.accept(createSimpleBlock(ModBlocks.BAT_CAGE.get(), plainVariant(batCageModel)));
        createDefaultBlockItem(ModBlocks.BAT_CAGE.get(), batCageModel);

        ResourceLocation bloodContainerModel = mod("block/blood_container/blood_container");
        this.blockStateOutput.accept(createSimpleBlock(ModBlocks.BLOOD_CONTAINER.get(), plainVariant(bloodContainerModel)));
        this.itemModelOutput.accept(ModBlocks.BLOOD_CONTAINER.asItem(), ItemModelUtils.composite(ItemModelUtils.plainModel(bloodContainerModel), ItemModelUtils.specialModel(bloodContainerModel, new BloodContainerRenderer.Unbaked())));

        ResourceLocation inspirationModel = mod("block/altar_inspiration/altar_inspiration");
        this.blockStateOutput.accept(createSimpleBlock(ModBlocks.ALTAR_INSPIRATION.get(), plainVariant(inspirationModel)));
        createDefaultBlockItem(ModBlocks.ALTAR_INSPIRATION.get(), inspirationModel);

        createNonTemplateModelBlock(ModBlocks.BLOOD.get());
    }

    protected void createCursedEarthPath() {
        ResourceLocation pathModel = ModModelTemplates.DIRT_PATH.create(ModBlocks.CURSED_EARTH_PATH.get(), new TextureMapping().put(TextureSlot.PARTICLE, mod("block/cursed_earth")).put(TextureSlot.BOTTOM, mod("block/cursed_earth")).put(TextureSlot.SIDE, mod("block/cursed_earth_path_side")).put(TextureSlot.TOP, mod("block/cursed_earth_path_top")), this.modelOutput);
        Variant variant = plainModel(pathModel);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(Blocks.DIRT_PATH, createRotatedVariants(variant)));
        createDefaultBlockItem(ModBlocks.CURSED_EARTH_PATH.get(), pathModel);
    }

    protected void createPlants() {
        createCropBlock(ModBlocks.GARLIC.get(), BlockStateProperties.AGE_7, 0, 0, 1, 1, 2, 2, 2, 3);
        createPlantWithDefaultItem(ModBlocks.VAMPIRE_ORCHID.get(), ModBlocks.POTTED_VAMPIRE_ORCHID.get(), PlantType.NOT_TINTED);
        createPlantWithDefaultItem(ModBlocks.CURSED_SPRUCE_SAPLING.get(), ModBlocks.POTTED_CURSED_SPRUCE_SAPLING.get(), PlantType.NOT_TINTED);
        createPlantWithDefaultItem(ModBlocks.DARK_SPRUCE_SAPLING.get(), ModBlocks.POTTED_DARK_SPRUCE_SAPLING.get(),PlantType.NOT_TINTED);
        createPlantWithDefaultItem(ModBlocks.CURSED_ROOTS.get(), ModBlocks.POTTED_CURSED_ROOTS.get(), PlantType.NOT_TINTED);
        createCrossBlock(ModBlocks.CURSED_HANGING_ROOTS.get(), PlantType.NOT_TINTED);
        this.itemModelOutput.accept(ModBlocks.CURSED_HANGING_ROOTS.asItem(), ItemModelUtils.plainModel(createFlatItemModelWithBlockTexture(ModBlocks.CURSED_HANGING_ROOTS.asItem(), ModBlocks.CURSED_HANGING_ROOTS.get())));
    }

    protected void createWood() {
        this.woodProvider(ModBlocks.DARK_SPRUCE_LOG.get()).logWithHorizontal(ModBlocks.DARK_SPRUCE_LOG.get()).wood(ModBlocks.DARK_SPRUCE_WOOD.get());
        this.woodProvider(ModBlocks.STRIPPED_DARK_SPRUCE_LOG.get()).logWithHorizontal(ModBlocks.STRIPPED_DARK_SPRUCE_LOG.get()).wood(ModBlocks.STRIPPED_DARK_SPRUCE_WOOD.get());
        this.woodProvider(ModBlocks.CURSED_SPRUCE_LOG.get()).logWithHorizontal(ModBlocks.CURSED_SPRUCE_LOG.get()).wood(ModBlocks.CURSED_SPRUCE_WOOD.get());
        this.woodProvider(ModBlocks.STRIPPED_CURSED_SPRUCE_LOG.get()).logWithHorizontal(ModBlocks.STRIPPED_CURSED_SPRUCE_LOG.get()).wood(ModBlocks.STRIPPED_CURSED_SPRUCE_WOOD.get());
        createDefaultBlockItem(ModBlocks.DARK_SPRUCE_LOG.get());
        createDefaultBlockItem(ModBlocks.DARK_SPRUCE_WOOD.get());
        createDefaultBlockItem(ModBlocks.STRIPPED_DARK_SPRUCE_LOG.get());
        createDefaultBlockItem(ModBlocks.STRIPPED_DARK_SPRUCE_WOOD.get());
        createDefaultBlockItem(ModBlocks.CURSED_SPRUCE_LOG.get());
        createDefaultBlockItem(ModBlocks.CURSED_SPRUCE_WOOD.get());
        createDefaultBlockItem(ModBlocks.STRIPPED_CURSED_SPRUCE_LOG.get());
        createDefaultBlockItem(ModBlocks.STRIPPED_CURSED_SPRUCE_WOOD.get());

        this.createHangingSign(ModBlocks.DARK_SPRUCE_LOG.get(), ModBlocks.DARK_SPRUCE_HANGING_SIGN.get(), ModBlocks.DARK_SPRUCE_WALL_HANGING_SIGN.get());
        this.createHangingSign(ModBlocks.CURSED_SPRUCE_LOG.get(), ModBlocks.CURSED_SPRUCE_HANGING_SIGN.get(), ModBlocks.CURSED_SPRUCE_WALL_HANGING_SIGN.get());
    }

    protected void createGarlicDiffuser() {
        ResourceLocation normalModel = ModModelTemplates.GARLIC_DIFFUSER.create(ModBlocks.GARLIC_DIFFUSER_NORMAL.get(), new TextureMapping().put(ModTextureSlots.GARLIC, mod("block/garlic_diffuser_inside")), this.modelOutput);
        this.blockStateOutput.accept(createSimpleBlock(ModBlocks.GARLIC_DIFFUSER_NORMAL.get(), plainVariant(normalModel)));
        createDefaultBlockItem(ModBlocks.GARLIC_DIFFUSER_NORMAL.get(), normalModel);
        ResourceLocation weakModel = ModModelTemplates.GARLIC_DIFFUSER.create(ModBlocks.GARLIC_DIFFUSER_WEAK.get(), new TextureMapping().put(ModTextureSlots.GARLIC, mod("block/garlic_diffuser_inside")), this.modelOutput);
        this.blockStateOutput.accept(createSimpleBlock(ModBlocks.GARLIC_DIFFUSER_WEAK.get(), plainVariant(weakModel)));
        createDefaultBlockItem(ModBlocks.GARLIC_DIFFUSER_WEAK.get(), weakModel);
        ResourceLocation improvedModel = ModModelTemplates.GARLIC_DIFFUSER.create(ModBlocks.GARLIC_DIFFUSER_IMPROVED.get(), new TextureMapping().put(ModTextureSlots.GARLIC, mod("block/garlic_diffuser_inside_improved")), this.modelOutput);
        this.blockStateOutput.accept(createSimpleBlock(ModBlocks.GARLIC_DIFFUSER_IMPROVED.get(), plainVariant(improvedModel)));
        createDefaultBlockItem(ModBlocks.GARLIC_DIFFUSER_IMPROVED.get(), improvedModel);
    }

    protected void createAltarPillar() {
        ResourceLocation model = decorateBlockModelLocation(modString("altar_pillar"));
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.ALTAR_PILLAR.get(), plainVariant(VResourceLocation.mod("altar_pillar_filled")))
                .with(PropertyDispatch.modify(AltarPillarBlock.PILLAR_TYPE)
                        .select(AltarPillarBlock.EnumPillarType.STONE, x -> x.withModel(ModModelTemplates.ALTAR_PILLAR_FILLED.createWithSuffix(ModBlocks.ALTAR_PILLAR.get(), "_stone", new TextureMapping().put(ModTextureSlots.FILLER, VResourceLocation.mc("block/stone_bricks")), this.modelOutput)))
                        .select(AltarPillarBlock.EnumPillarType.IRON, x -> x.withModel(ModModelTemplates.ALTAR_PILLAR_FILLED.createWithSuffix(ModBlocks.ALTAR_PILLAR.get(), "_iron", new TextureMapping().put(ModTextureSlots.FILLER, VResourceLocation.mc("block/iron_block")), this.modelOutput)))
                        .select(AltarPillarBlock.EnumPillarType.GOLD, x -> x.withModel(ModModelTemplates.ALTAR_PILLAR_FILLED.createWithSuffix(ModBlocks.ALTAR_PILLAR.get(), "_gold", new TextureMapping().put(ModTextureSlots.FILLER, VResourceLocation.mc("block/gold_block")), this.modelOutput)))
                        .select(AltarPillarBlock.EnumPillarType.BONE, x -> x.withModel(ModModelTemplates.ALTAR_PILLAR_FILLED.createWithSuffix(ModBlocks.ALTAR_PILLAR.get(), "_bone", new TextureMapping().put(ModTextureSlots.FILLER, VResourceLocation.mc("block/bone_block_side")), this.modelOutput)))
                ));
        createDefaultBlockItem(ModBlocks.ALTAR_PILLAR.get(), model);
    }

    protected void createAlchemicalCauldron() {
        var cauldron = mod("block/alchemy_cauldron");
        var normal = VResourceLocation.mod("block/alchemy_cauldron_liquid");
        var boiling = ModModelTemplates.ALCHEMICAL_CAULDRON.createWithSuffix(ModBlocks.ALCHEMICAL_CAULDRON.get(), "_boiling", new TextureMapping().put(ModTextureSlots.LIQUID, mod("block/blank_liquid_boiling")), this.modelOutput);
        this.blockStateOutput.accept(MultiPartGenerator.multiPart(ModBlocks.ALCHEMICAL_CAULDRON.get())
                .with(plainVariant(cauldron))
                .with(condition(AlchemicalCauldronBlock.LIT, true), plainVariant(VResourceLocation.mod("block/alchemy_cauldron_fire")))
                .with(condition(AlchemicalCauldronBlock.LIQUID, AlchemicalCauldronBlock.LiquidState.FILLED), plainVariant(normal))
                .with(condition(AlchemicalCauldronBlock.LIQUID, AlchemicalCauldronBlock.LiquidState.BOILING), plainVariant(boiling))
        );
        createDefaultBlockItem(ModBlocks.ALCHEMICAL_CAULDRON.get(), cauldron);
    }

    protected void createTotem() {
        createNonTemplateModelBlock(ModBlocks.TOTEM_BASE.get());
        createDefaultBlockItem(ModBlocks.TOTEM_BASE.get());
        this.blockStateOutput.accept(createSimpleBlock(ModBlocks.TOTEM_TOP.get(), plainVariant(ModModelTemplates.TOTEM.getDefaultModelLocation(ModBlocks.TOTEM_TOP.get()))));
        createDefaultBlockItem(ModBlocks.TOTEM_TOP.get(), ModModelTemplates.TOTEM.getDefaultModelLocation(ModBlocks.TOTEM_TOP.get()));
        this.blockStateOutput.accept(createSimpleBlock(ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER.get(), plainVariant(ModModelTemplates.TOTEM.create(ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER.get(), new TextureMapping().put(ModTextureSlots.OUTER, VResourceLocation.mc("block/glowstone")), this.modelOutput))));
        this.blockStateOutput.accept(createSimpleBlock(ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE.get(), plainVariant(ModModelTemplates.TOTEM.create(ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE.get(), new TextureMapping().put(ModTextureSlots.OUTER, VResourceLocation.mc("block/glowstone")), this.modelOutput))));
        ResourceLocation totemCraftedModel = ModModelTemplates.TOTEM.create(ModBlocks.TOTEM_TOP_CRAFTED.get(), new TextureMapping().put(ModTextureSlots.OUTER, VResourceLocation.mc("block/obsidian")), this.modelOutput);
        this.blockStateOutput.accept(createSimpleBlock(ModBlocks.TOTEM_TOP_CRAFTED.get(), plainVariant(totemCraftedModel)));
        createDefaultBlockItem(ModBlocks.TOTEM_TOP_CRAFTED.get(), totemCraftedModel);
        this.blockStateOutput.accept(createSimpleBlock(ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER_CRAFTED.get(), plainVariant(ModModelTemplates.TOTEM.create(ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER_CRAFTED.get(), new TextureMapping().put(ModTextureSlots.OUTER, VResourceLocation.mc("block/obsidian")), this.modelOutput))));
        this.blockStateOutput.accept(createSimpleBlock(ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE_CRAFTED.get(), plainVariant(ModModelTemplates.TOTEM.create(ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE_CRAFTED.get(), new TextureMapping().put(ModTextureSlots.OUTER, VResourceLocation.mc("block/obsidian")), this.modelOutput))));
    }

    protected void createCandleHolders() {
        createEmptyCandleHolder(ModBlocks.CANDLE_STICK.get());
        createDefaultBlockItem(ModBlocks.CANDLE_STICK.get());
        createEmptyCandleHolder(ModBlocks.WALL_CANDLE_STICK.get());

        for (int i = 1; i < ColorListsUtil.STANDING_AND_WALL_CANDLE_STICKS.size(); i++) {
            Pair<CandleHolderBlock, CandleHolderBlock> pair = ColorListsUtil.STANDING_AND_WALL_CANDLE_STICKS.get(i);
            createCandleStick(pair.getFirst(), pair.getSecond(), pair.getFirst().getCandle().get());
            createDefaultBlockItem(pair.getFirst());
        }

        createEmptyCandleHolder(ModBlocks.CANDELABRA.get());
        createDefaultBlockItem(ModBlocks.CANDELABRA.get());
        createEmptyCandleHolder(ModBlocks.WALL_CANDELABRA.get());

        for (int i = 1; i < ColorListsUtil.STANDING_AND_WALL_CANDELABRAS.size(); i++) {
            Pair<CandleHolderBlock, CandleHolderBlock> pair = ColorListsUtil.STANDING_AND_WALL_CANDELABRAS.get(i);
            createCandelabra(pair.getFirst(), pair.getSecond(), pair.getFirst().getCandle().get());
            createDefaultBlockItem(pair.getFirst());
        }

        createNonTemplateModelBlock(ModBlocks.CHANDELIER.get());
        createDefaultBlockItem(ModBlocks.CHANDELIER.get());

        for (int i = 1; i < ColorListsUtil.HANGING_CHANDELIERS.size(); i++) {
            CandleHolderBlock block = ColorListsUtil.HANGING_CHANDELIERS.get(i);
            createChandelier(block, block.getCandle().get());
            createDefaultBlockItem(block);
        }
    }

    private void createCandleStick(CandleHolderBlock standingBlock, CandleHolderBlock wallBlock, Item candle) {
        createFilledCandleHolder(standingBlock, candle, ModModelTemplates.CANDLE_STICK_FILLED);
        createFilledCandleHolder(wallBlock, candle, ModModelTemplates.WALL_CANDLE_STICK_FILLED);
    }

    private void createCandelabra(CandleHolderBlock standingBlock, CandleHolderBlock wallBlock, Item candle) {
        createFilledCandleHolder(standingBlock, candle, ModModelTemplates.CANDELABRA_FILLED);
        createFilledCandleHolder(wallBlock, candle, ModModelTemplates.WALL_CANDELABRA_FILLED);
    }

    protected void createEmptyCandleHolder(CandleHolderBlock block) {
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, plainVariant(getModelLocation(block)))
                .with(HORIZONTAL_ROTATION));
    }

    protected void createFilledCandleHolder(CandleHolderBlock block, Item candle, ModelTemplate modelTemplate) {
        ResourceLocation candleTexture = BuiltInRegistries.ITEM.getKey(candle).withPrefix("block/");
        ResourceLocation model = modelTemplate.create(block, new TextureMapping().put(ModTextureSlots.CANDLE, candleTexture), this.modelOutput);
        ResourceLocation litModel = modelTemplate.createWithSuffix(block, "_lit", new TextureMapping().put(ModTextureSlots.CANDLE, candleTexture.withSuffix("_lit")), this.modelOutput);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, plainVariant(model))
                .with(HORIZONTAL_ROTATION)
                .with(PropertyDispatch.modify(AbstractCandleBlock.LIT)
                        .select(true, x -> x.withModel(litModel))));
    }

    private void createChandelier(CandleHolderBlock block, Item candle) {
        ResourceLocation candleTexture = BuiltInRegistries.ITEM.getKey(candle).withPrefix("block/");
        ResourceLocation model = ModModelTemplates.CHANDELIER_FILLED.create(block, new TextureMapping().put(ModTextureSlots.CANDLE, candleTexture), this.modelOutput);
        ResourceLocation litModel = ModModelTemplates.CHANDELIER_FILLED.createWithSuffix(block, "_lit", new TextureMapping().put(ModTextureSlots.CANDLE, candleTexture.withSuffix("_lit")), this.modelOutput);
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, plainVariant(model))
                .with(HORIZONTAL_ROTATION)
                .with(PropertyDispatch.modify(AbstractCandleBlock.LIT)
                        .select(true, x -> x.withModel(litModel)))
        );
    }

    public void createVampireSoulLantern() {
        VampireSoulLanternBlock block = ModBlocks.VAMPIRE_SOUL_LANTERN.get();

        this.registerSimpleFlatItemModel(block.asItem());

        MultiPartGenerator multiPartGenerator = MultiPartGenerator.multiPart(block);

        ConditionBuilder standing = condition(VampireSoulLanternBlock.HANGING, false);
        ConditionBuilder hanging = condition(VampireSoulLanternBlock.HANGING, true);

        MultiVariant modeVariant = plainVariant(getModelLocation(block));
        MultiVariant hangingVariant = plainVariant(getModelLocation(block, "_hanging"));

        multiPartGenerator.with(standing.term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH), modeVariant);
        multiPartGenerator.with(standing.term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST), modeVariant.with(Y_ROT_90));
        multiPartGenerator.with(standing.term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), modeVariant.with(Y_ROT_180));
        multiPartGenerator.with(standing.term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST), modeVariant.with(Y_ROT_270));
        multiPartGenerator.with(hanging.term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH), hangingVariant);
        multiPartGenerator.with(hanging.term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST), hangingVariant.with(Y_ROT_90));
        multiPartGenerator.with(hanging.term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), hangingVariant.with(Y_ROT_180));
        multiPartGenerator.with(hanging.term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST), hangingVariant.with(Y_ROT_270));

        this.blockStateOutput.accept(multiPartGenerator);
    }

    protected void createBloodGrinder() {
        BloodGrinderBlock block = ModBlocks.BLOOD_GRINDER.get();

        ResourceLocation fullModelLoc = getModelLocation(block);
        var fullModel = plainVariant(fullModelLoc);
        var bottomModelEmpty = plainVariant(getModelLocation(block, "_bottom_empty"));
        var bottomModel = plainVariant(getModelLocation(block, "_bottom"));
        var baseModel = plainVariant(getModelLocation(block, "_base"));
        var wheelModel = plainVariant(getModelLocation(block, "_wheel"));
        var grindingWheelModel = plainVariant(getModelLocation(block, "_wheel_grinding"));

        this.createDefaultBlockItem(block, fullModelLoc);

        MultiPartGenerator multiPartGenerator = MultiPartGenerator.multiPart(block);

        ConditionBuilder hasFilter = condition(BloodGrinderBlock.HAS_FILTER, true);
        ConditionBuilder noFilter = condition(BloodGrinderBlock.HAS_FILTER, false);
        ConditionBuilder grinding = condition(BloodGrinderBlock.GRINDING, true);
        ConditionBuilder notGrinding = condition(BloodGrinderBlock.GRINDING, false);

        multiPartGenerator.with(baseModel);
        multiPartGenerator.with(noFilter.term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH), bottomModelEmpty);
        multiPartGenerator.with(noFilter.term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST), bottomModelEmpty.with(Y_ROT_90));
        multiPartGenerator.with(noFilter.term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), bottomModelEmpty.with(Y_ROT_180));
        multiPartGenerator.with(noFilter.term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST), bottomModelEmpty.with(Y_ROT_270));
        multiPartGenerator.with(hasFilter.term(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH), bottomModel);
        multiPartGenerator.with(hasFilter.term(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST), bottomModel.with(Y_ROT_90));
        multiPartGenerator.with(hasFilter.term(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH), bottomModel.with(Y_ROT_180));
        multiPartGenerator.with(hasFilter.term(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST), bottomModel.with(Y_ROT_270));

        multiPartGenerator.with(notGrinding, wheelModel);
        multiPartGenerator.with(grinding, grindingWheelModel);

        this.blockStateOutput.accept(multiPartGenerator);
    }

    protected void createBloodSieve() {
        BloodSieveBlock block = ModBlocks.BLOOD_SIEVE.get();

        ResourceLocation model = getModelLocation(block);
        ResourceLocation modelEmpty = getModelLocation(block, "_empty");

        this.createDefaultBlockItem(block, model);


        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, plainVariant(model))
                .with(HORIZONTAL_ROTATION)
                .with(PropertyDispatch.modify(BloodSieveBlock.HAS_FILTER)
                        .select(false, x -> x.withModel(modelEmpty))));
    }

    protected void createMotherTrophy() {
        createNonTemplateModelBlock(ModBlocks.MOTHER_TROPHY.get());
        ResourceLocation modelLocation = getModelLocation(ModBlocks.MOTHER_TROPHY.get());
        this.itemModelOutput.accept(ModBlocks.MOTHER_TROPHY.asItem(), ItemModelUtils.composite(ItemModelUtils.plainModel(modelLocation), ItemModelUtils.specialModel(modelLocation, new MotherTrophyRenderer.Unbaked())));
    }

    protected void createNonTemplateBlocks() {
        Stream.of(
                ModBlocks.ALTAR_CLEANSING,
                ModBlocks.ALTAR_INFUSION,
                ModBlocks.ALTAR_TIP,
                ModBlocks.BLOOD_PEDESTAL,
                ModBlocks.POTION_TABLE,
                ModBlocks.FIRE_PLACE,
                ModBlocks.CROSS,
                ModBlocks.TOMBSTONE1,
                ModBlocks.TOMBSTONE2,
                ModBlocks.TOMBSTONE3,
                ModBlocks.GRAVE_CAGE,
                ModBlocks.VAMPIRE_RACK,
                ModBlocks.THRONE,
                ModBlocks.FOG_DIFFUSER
        ).map(DeferredHolder::get).forEach(this::createNonTemplateBlockWithItem);
    }

    protected void createMedChair() {
        this.blockStateOutput.accept(MultiVariantGenerator.dispatch(ModBlocks.MED_CHAIR.get())
                .with(PropertyDispatch.initial(MedChairBlock.PART)
                        .select(MedChairBlock.EnumPart.BOTTOM, plainVariant(VResourceLocation.mod("block/medchairbase")))
                        .select(MedChairBlock.EnumPart.TOP, plainVariant(VResourceLocation.mod("block/medchairhead")))
                )
                .with(PropertyDispatch.modify(MedChairBlock.FACING)
                        .select(Direction.NORTH, NOP)
                        .select(Direction.EAST, Y_ROT_90)
                        .select(Direction.SOUTH, Y_ROT_180)
                        .select(Direction.WEST, Y_ROT_270)
                )
        );
    }

    protected void createTrivialBlocks() {
        Stream.of(
                ModBlocks.CURSED_EARTH,
                ModBlocks.BLOOD_INFUSED_IRON_BLOCK,
                ModBlocks.BLOOD_INFUSED_ENHANCED_IRON_BLOCK,
                ModBlocks.REMAINS,
                ModBlocks.INCAPACITATED_VULNERABLE_REMAINS,
                ModBlocks.VULNERABLE_REMAINS,
                ModBlocks.ACTIVE_VULNERABLE_REMAINS,
                ModBlocks.MOTHER,
                ModBlocks.BLOODY_DARK_STONE_BRICKS
        ).map(DeferredHolder::get).forEach(this::createTrivialBlockWithItem);
    }

    protected void createCursedBark() {
        var side1 = plainVariant(mod("block/cursed_bark_side"));
        var side2 = plainVariant(mod("block/cursed_bark_side_2"));
        this.blockStateOutput.accept(MultiPartGenerator.multiPart(ModBlocks.DIRECT_CURSED_BARK.get())
                .with(condition(DirectCursedBarkBlock.EAST_TYPE, DirectCursedBarkBlock.Type.VERTICAL), side1.with(Y_ROT_90))
                .with(condition(DirectCursedBarkBlock.NORTH_TYPE, DirectCursedBarkBlock.Type.VERTICAL), side1.with(NOP))
                .with(condition(DirectCursedBarkBlock.WEST_TYPE, DirectCursedBarkBlock.Type.VERTICAL), side1.with(Y_ROT_270))
                .with(condition(DirectCursedBarkBlock.SOUTH_TYPE, DirectCursedBarkBlock.Type.VERTICAL), side1.with(Y_ROT_180))
                .with(condition(DirectCursedBarkBlock.UP_TYPE, DirectCursedBarkBlock.Type.VERTICAL), side1.with(Y_ROT_270))
                .with(condition(DirectCursedBarkBlock.DOWN_TYPE, DirectCursedBarkBlock.Type.VERTICAL), side1.with(Y_ROT_90))

                .with(condition(DirectCursedBarkBlock.EAST_TYPE, DirectCursedBarkBlock.Type.HORIZONTAL), side2.with(Y_ROT_90))
                .with(condition(DirectCursedBarkBlock.NORTH_TYPE, DirectCursedBarkBlock.Type.HORIZONTAL), side2.with(Y_ROT_180))
                .with(condition(DirectCursedBarkBlock.WEST_TYPE, DirectCursedBarkBlock.Type.HORIZONTAL), side2.with(Y_ROT_270))
                .with(condition(DirectCursedBarkBlock.SOUTH_TYPE, DirectCursedBarkBlock.Type.HORIZONTAL), side2.with(Y_ROT_180))
                .with(condition(DirectCursedBarkBlock.UP_TYPE, DirectCursedBarkBlock.Type.HORIZONTAL), side2.with(Y_ROT_270))
                .with(condition(DirectCursedBarkBlock.DOWN_TYPE, DirectCursedBarkBlock.Type.HORIZONTAL), side2.with(Y_ROT_90))
        );
        createNonTemplateModelBlock(ModBlocks.DIAGONAL_CURSED_BARK.get());
    }

    protected void createHunterTable() {
        var hunterTable = mod("block/hunter_table/hunter_table");
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.HUNTER_TABLE.get());


        withHorizontalRotation(generator, null, plainVariant(hunterTable));
        withHorizontalRotation(generator, condition(HunterTableBlock.WEAPON_TABLE, true), plainVariant(mod("block/hunter_table/hunter_table_hammer")));
        withHorizontalRotation(generator, condition(HunterTableBlock.ALCHEMICAL_CAULDRON, true), plainVariant(mod("block/hunter_table/hunter_table_garlic")));
        withHorizontalRotation(generator, condition(HunterTableBlock.POTION_TABLE, true), plainVariant(mod("block/hunter_table/hunter_table_bottle")));
        this.blockStateOutput.accept(generator);
        createDefaultBlockItem(ModBlocks.HUNTER_TABLE.get(), hunterTable);
    }

    protected void createAlchemicalFire() {
        ConditionBuilder conditionbuilder = condition()
                .term(BlockStateProperties.NORTH, false)
                .term(BlockStateProperties.EAST, false)
                .term(BlockStateProperties.SOUTH, false)
                .term(BlockStateProperties.WEST, false)
                .term(BlockStateProperties.UP, false);
        var model1 = this.createFloorFireModels(ModBlocks.ALCHEMICAL_FIRE.get());
        var model2 = this.createSideFireModels(ModBlocks.ALCHEMICAL_FIRE.get());
        var model3 = this.createTopFireModels(ModBlocks.ALCHEMICAL_FIRE.get());
        this.blockStateOutput
                .accept(
                        MultiPartGenerator.multiPart(ModBlocks.ALCHEMICAL_FIRE.get())
                                .with(conditionbuilder, model1)
                                .with(or(condition().term(BlockStateProperties.NORTH, true), conditionbuilder), model2)
                                .with(or(condition().term(BlockStateProperties.EAST, true), conditionbuilder), model2.with(Y_ROT_90))
                                .with(or(condition().term(BlockStateProperties.SOUTH, true), conditionbuilder), model2.with(Y_ROT_180))
                                .with(or(condition().term(BlockStateProperties.WEST, true), conditionbuilder), model2.with(Y_ROT_270))
                                .with(condition().term(BlockStateProperties.UP, true), model3)
                );
    }

    protected void createCoffin() {
        Stream.of(ModBlocks.COFFIN_WHITE, ModBlocks.COFFIN_ORANGE, ModBlocks.COFFIN_MAGENTA, ModBlocks.COFFIN_LIGHT_BLUE, ModBlocks.COFFIN_YELLOW, ModBlocks.COFFIN_LIME, ModBlocks.COFFIN_PINK, ModBlocks.COFFIN_GRAY, ModBlocks.COFFIN_LIGHT_GRAY, ModBlocks.COFFIN_CYAN, ModBlocks.COFFIN_PURPLE, ModBlocks.COFFIN_BLUE, ModBlocks.COFFIN_BROWN, ModBlocks.COFFIN_GREEN, ModBlocks.COFFIN_RED, ModBlocks.COFFIN_BLACK).map(DeferredHolder::get).forEach(block -> {
            ModModelTemplates.COFFIN.create(VResourceLocation.mod("block/coffin/coffin_" + block.getColor().getName()), new TextureMapping().put(ModTextureSlots.TEXTURE0, mod("block/coffin/coffin_" + block.getColor().getName())), this.modelOutput);
            var coffin = ModModelTemplates.COFFIN_BOTTOM.create(VResourceLocation.mod("block/coffin/coffin_bottom_" + block.getColor().getName()), new TextureMapping().put(ModTextureSlots.TEXTURE0, mod("block/coffin/coffin_" + block.getColor().getName())), this.modelOutput);
            ModModelTemplates.COFFIN_TOP.create(VResourceLocation.mod("block/coffin/coffin_top_" + block.getColor().getName()), new TextureMapping().put(ModTextureSlots.TEXTURE0, mod("block/coffin/coffin_" + block.getColor().getName())), this.modelOutput);
            ResourceLocation model = decorateBlockModelLocation(modString("coffin_empty"));
            this.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, plainVariant(model)));
            this.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(coffin));
        });
    }

    protected void createAlchemyTable() {
        ResourceLocation model = mod("block/alchemy_table/alchemy_table");
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.ALCHEMY_TABLE.get());
        withHorizontalRotation(generator, null, plainVariant(model));
        withHorizontalRotation(generator, condition(AlchemyTableBlock.HAS_BOTTLE_INPUT_0, true), plainVariant(mod("block/alchemy_table/alchemy_table_input_0")));
        withHorizontalRotation(generator, condition(AlchemyTableBlock.HAS_BOTTLE_INPUT_1, true), plainVariant(mod("block/alchemy_table/alchemy_table_input_1")));
        withHorizontalRotation(generator, condition(AlchemyTableBlock.HAS_BOTTLE_OUTPUT_0, true), plainVariant(mod("block/alchemy_table/alchemy_table_output_0")));
        withHorizontalRotation(generator, condition(AlchemyTableBlock.HAS_BOTTLE_OUTPUT_1, true), plainVariant(mod("block/alchemy_table/alchemy_table_output_1")));
        this.blockStateOutput.accept(generator);
        createDefaultBlockItem(ModBlocks.ALCHEMY_TABLE.get(), model);
    }

    protected void createWeaponTable() {
        ResourceLocation model = mod("block/weapon_table/weapon_table");
        MultiPartGenerator generator = MultiPartGenerator.multiPart(ModBlocks.WEAPON_TABLE.get());
        withHorizontalRotation(generator, null, plainVariant(model));
        withHorizontalRotation(generator, condition().term(WeaponTableBlock.LAVA, 1), plainVariant(mod("block/weapon_table/weapon_table_lava1")));
        withHorizontalRotation(generator, condition().term(WeaponTableBlock.LAVA, 2), plainVariant(mod("block/weapon_table/weapon_table_lava2")));
        withHorizontalRotation(generator, condition().term(WeaponTableBlock.LAVA, 3), plainVariant(mod("block/weapon_table/weapon_table_lava3")));
        withHorizontalRotation(generator, condition().term(WeaponTableBlock.LAVA, 4), plainVariant(mod("block/weapon_table/weapon_table_lava4")));
        withHorizontalRotation(generator, condition().term(WeaponTableBlock.LAVA, 5), plainVariant(mod("block/weapon_table/weapon_table_lava5")));
        this.blockStateOutput.accept(generator);
        createDefaultBlockItem(ModBlocks.WEAPON_TABLE.get(), model);
    }

    protected void createTent() {

        Stream.of(ModBlocks.TENT, ModBlocks.TENT_MAIN).map(DeferredHolder::get).forEach(block -> {
            MultiPartGenerator generator = MultiPartGenerator.multiPart(block);
            withHorizontalRotation(generator, condition().term(TentBlock.POSITION, 0), plainVariant(ModModelTemplates.TENT.create(mod("block/tent_br"), new TextureMapping().put(ModTextureSlots.FLOOR, mod("block/tent/floor_br")), this.modelOutput)));
            withHorizontalRotation(generator, condition().term(TentBlock.POSITION, 1), plainVariant(ModModelTemplates.TENT.create(mod("block/tent_bl"), new TextureMapping().put(ModTextureSlots.FLOOR, mod("block/tent/floor_bl")), this.modelOutput)));
            withHorizontalRotation(generator, condition().term(TentBlock.POSITION, 2), plainVariant(ModModelTemplates.TENT.create(mod("block/tent_tl"), new TextureMapping().put(ModTextureSlots.FLOOR, mod("block/tent/floor_tl")), this.modelOutput)));
            withHorizontalRotation(generator, condition().term(TentBlock.POSITION, 3), plainVariant(ModModelTemplates.TENT.create(mod("block/tent_tr"), new TextureMapping().put(ModTextureSlots.FLOOR, mod("block/tent/floor_tr")), this.modelOutput)));
            withHorizontalRotation(generator, condition().term(TentBlock.POSITION, 2), plainVariant(VResourceLocation.mod("block/tentback")), 2);
            withHorizontalRotation(generator, condition().term(TentBlock.POSITION, 3), plainVariant(VResourceLocation.mod("block/tentback_flipped")));
            this.blockStateOutput.accept(generator);
        });
    }

    protected void withHorizontalRotation(MultiPartGenerator generator, @Nullable ConditionBuilder condition, MultiVariant model) {
        this.withHorizontalRotation(generator, condition, model, 0);
    }

    protected void withHorizontalRotation(MultiPartGenerator generator, @Nullable ConditionBuilder condition, MultiVariant model, int rotation) {
        Function<ConditionBuilder, Condition> and = cond -> condition == null ? cond.build() : and(cond, condition);
        List<Quadrant> list = Arrays.stream(Quadrant.values()).toList();
        generator
                .with(and.apply(condition(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)), model.with(x -> x.withYRot(list.get((list.indexOf(Quadrant.R0) + rotation) % list.size()))))
                .with(and.apply(condition(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST)), model.with(x -> x.withYRot(list.get((list.indexOf(Quadrant.R90) + rotation) % list.size()))))
                .with(and.apply(condition(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)), model.with(x -> x.withYRot(list.get((list.indexOf(Quadrant.R180) + rotation) % list.size()))))
                .with(and.apply(condition(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST)), model.with(x -> x.withYRot(list.get((list.indexOf(Quadrant.R270) + rotation) % list.size()))));
    }

    protected void createCursedGrassBlock() {
        ResourceLocation resourceLocation = TextureMapping.getBlockTexture(ModBlocks.CURSED_EARTH.get());
        TextureMapping textureMapping = new TextureMapping()
                .put(TextureSlot.BOTTOM, resourceLocation)
                .copyForced(TextureSlot.BOTTOM, TextureSlot.PARTICLE)
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(ModBlocks.CURSED_GRASS.get(), "_top"))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(ModBlocks.CURSED_GRASS.get(), "_side"));
        var model = ModModelTemplates.CUBE_BOTTOM_TOP.create(ModBlocks.CURSED_GRASS.get(), textureMapping ,this.modelOutput);
        TextureMapping snowTextureMapping = new TextureMapping()
                .put(TextureSlot.BOTTOM, resourceLocation)
                .copyForced(TextureSlot.BOTTOM, TextureSlot.PARTICLE)
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(ModBlocks.CURSED_GRASS.get(), "_top"))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(ModBlocks.CURSED_GRASS.get(), "_side_snowy"));
        MultiVariant multivariant = plainVariant(ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(ModBlocks.CURSED_GRASS.get(), "_snow", snowTextureMapping, this.modelOutput));
        ResourceLocation resourcelocation1 = ModelLocationUtils.getModelLocation(ModBlocks.CURSED_GRASS.get());
        this.createGrassLikeBlock(ModBlocks.CURSED_GRASS.get(), createRotatedVariants(plainModel(resourcelocation1)), multivariant);
        this.registerSimpleTintedItemModel(ModBlocks.CURSED_GRASS.get(), getModelLocation(ModBlocks.CURSED_GRASS.get()), new GrassColorSource());
    }

    protected void createInfuser() {
        this.blockStateOutput.accept(MultiPartGenerator.multiPart(ModBlocks.INFUSER.get())
                .with(plainVariant(VResourceLocation.mod("block/blood_infuser/infuser")))
                .with(condition(BloodInfuserBlock.IS_ACTIVE, true), plainVariant(VResourceLocation.mod("block/blood_infuser/infuser_blood"))));
        this.createDefaultBlockItem(ModBlocks.INFUSER.get(), VResourceLocation.mod("block/blood_infuser/infuser"));
    }
}
