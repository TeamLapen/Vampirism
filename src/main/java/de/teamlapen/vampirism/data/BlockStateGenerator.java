package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blocks.*;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Arrays;


public class BlockStateGenerator extends BlockStateProvider {


    public BlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, REFERENCE.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ResourceLocation cutout = new ResourceLocation("cutout");
        ResourceLocation cutout_mipped = new ResourceLocation("cutout_mipped");
        ResourceLocation translucent = new ResourceLocation("translucent");
        //models
        models().getBuilder("coffin").texture("particle", mcLoc("block/spruce_planks")).renderType(cutout);
        ModelFile bloody_spruce_sapling = models().cross("bloody_spruce_sapling", modLoc("block/bloody_spruce_sapling")).renderType(cutout);
        ModelFile vampire_spruce_sapling = models().cross("vampire_spruce_sapling", modLoc("block/vampire_spruce_sapling")).renderType(cutout);
        ModelFile bloody_spruce_log = models().cubeColumn("bloody_spruce_log", modLoc("block/bloody_spruce_log"), modLoc("block/bloody_spruce_log_top"));

        //default blocks
        horizontalBlock(ModBlocks.GARLIC_DIFFUSER_NORMAL.get(), models().withExistingParent("garlic_diffuser_normal", modLoc("block/garlic_diffuser")).renderType(cutout));
        horizontalBlock(ModBlocks.GARLIC_DIFFUSER_WEAK.get(), models().withExistingParent("garlic_diffuser_weak", modLoc("block/garlic_diffuser")).renderType(cutout));
        horizontalBlock(ModBlocks.GARLIC_DIFFUSER_IMPROVED.get(), models().withExistingParent("garlic_diffuser_improved", modLoc("block/garlic_diffuser")).renderType(cutout));
        horizontalBlock(ModBlocks.ALTAR_CLEANSING.get(), models().getExistingFile(modLoc("block/altar_cleansing")));
        horizontalBlock(ModBlocks.BLOOD_GRINDER.get(), models().getExistingFile(modLoc("block/blood_grinder")));

        simpleBlock(ModBlocks.CASTLE_BLOCK_DARK_BRICK.get());
        simpleBlock(ModBlocks.CASTLE_BLOCK_DARK_BRICK_BLOODY.get());
        simpleBlock(ModBlocks.CASTLE_BLOCK_DARK_STONE.get());
        simpleBlock(ModBlocks.CASTLE_BLOCK_NORMAL_BRICK.get());
        simpleBlock(ModBlocks.CASTLE_BLOCK_PURPLE_BRICK.get());
        simpleBlock(ModBlocks.CURSED_EARTH.get());
        simpleBlock(ModBlocks.SUNSCREEN_BEACON.get(), models().withExistingParent("vampirism:block/sunscreen_beacon", "minecraft:block/beacon").texture("beacon", "vampirism:block/cursed_earth").renderType(cutout));
        simpleBlock(ModBlocks.COFFIN.get(), models().getExistingFile(modLoc("block/coffin")));
        simpleBlock(ModBlocks.VAMPIRE_ORCHID.get(), models().cross("vampire_orchid", modLoc("block/vampire_orchid")).renderType(cutout));
        simpleBlock(ModBlocks.TOTEM_TOP.get(), models().getExistingFile(modLoc("block/totem_top")));
        simpleBlock(ModBlocks.TOTEM_TOP_CRAFTED.get(), models().getExistingFile(modLoc("block/totem_top_crafted")));
        simpleBlock(ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER.get(), models().withExistingParent("totem_top_vampirism_hunter", modLoc("block/totem_top")));
        simpleBlock(ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE.get(), models().withExistingParent("totem_top_vampirism_vampire", modLoc("block/totem_top")));
        simpleBlock(ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER_CRAFTED.get(), models().withExistingParent("totem_top_vampirism_hunter_crafted", modLoc("block/totem_top_crafted")));
        simpleBlock(ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE_CRAFTED.get(), models().withExistingParent("totem_top_vampirism_vampire_crafted", modLoc("block/totem_top_crafted")));
        simpleBlock(ModBlocks.TOTEM_BASE.get(), models().getExistingFile(modLoc("block/totem_base")));
        simpleBlock(ModBlocks.ALTAR_INFUSION.get(), models().getExistingFile(modLoc("block/altar_infusion")));
        simpleBlock(ModBlocks.ALTAR_INSPIRATION.get(), models().getExistingFile(modLoc("block/altar_inspiration/altar_inspiration")));
        simpleBlock(ModBlocks.ALTAR_TIP.get(), models().getExistingFile(modLoc("block/altar_tip")));
        simpleBlock(ModBlocks.BLOOD_CONTAINER.get(), models().getExistingFile(modLoc("block/blood_container/blood_container")));
        simpleBlock(ModBlocks.BLOOD_PEDESTAL.get(), models().getExistingFile(modLoc("block/blood_pedestal")));
        simpleBlock(ModBlocks.POTION_TABLE.get(), models().getExistingFile(modLoc("block/potion_table")));
        simpleBlock(ModBlocks.FIRE_PLACE.get(), models().getExistingFile(modLoc("block/fire_place")));
        simpleBlock(ModBlocks.POTTED_VAMPIRE_ORCHID.get(), models().withExistingParent("vampirism:block/potted_vampire_orchid", "minecraft:block/flower_pot_cross").texture("plant", "vampirism:block/vampire_orchid").renderType(cutout));
        simpleBlock(ModBlocks.VAMPIRE_SPRUCE_LEAVES.get(), models().getExistingFile(mcLoc("block/oak_leaves")));
        simpleBlock(ModBlocks.BLOODY_SPRUCE_LEAVES.get(), models().getExistingFile(mcLoc("block/oak_leaves")));
        simpleBlock(ModBlocks.BLOODY_SPRUCE_SAPLING.get(), bloody_spruce_sapling);
        simpleBlock(ModBlocks.VAMPIRE_SPRUCE_SAPLING.get(), vampire_spruce_sapling);


        stairsBlock(ModBlocks.CASTLE_STAIRS_DARK_STONE.get(), modLoc("block/castle_block_dark_stone"));
        stairsBlock(ModBlocks.CASTLE_STAIRS_DARK_BRICK.get(), modLoc("block/castle_block_dark_brick"));
        stairsBlock(ModBlocks.CASTLE_STAIRS_PURPLE_BRICK.get(), modLoc("block/castle_block_purple_brick"));

        slabBlock(ModBlocks.CASTLE_SLAB_DARK_BRICK.get(), modLoc("block/castle_block_dark_brick"), modLoc("block/castle_block_dark_brick"));
        slabBlock(ModBlocks.CASTLE_SLAB_DARK_STONE.get(), modLoc("block/castle_block_dark_stone"), modLoc("block/castle_block_dark_stone"));
        slabBlock(ModBlocks.CASTLE_SLAB_PURPLE_BRICK.get(), modLoc("block/castle_block_purple_brick"), modLoc("block/castle_block_purple_brick"));


        //variants

        getVariantBuilder(ModBlocks.BLOODY_SPRUCE_LOG.get())
                .partialState().with(BlockStateProperties.AXIS, Direction.Axis.Y).modelForState().modelFile(bloody_spruce_log).addModel()
                .partialState().with(BlockStateProperties.AXIS, Direction.Axis.Z).modelForState().rotationX(90).modelFile(bloody_spruce_log).addModel()
                .partialState().with(BlockStateProperties.AXIS, Direction.Axis.X).modelForState().rotationX(90).rotationY(90).modelFile(bloody_spruce_log).addModel();

        getVariantBuilder(ModBlocks.GARLIC.get())
                .partialState().with(GarlicBlock.AGE, 0).modelForState().modelFile(models().getExistingFile(modLoc("block/garlic_stage_0"))).addModel()
                .partialState().with(GarlicBlock.AGE, 1).modelForState().modelFile(models().getExistingFile(modLoc("block/garlic_stage_0"))).addModel()
                .partialState().with(GarlicBlock.AGE, 2).modelForState().modelFile(models().getExistingFile(modLoc("block/garlic_stage_1"))).addModel()
                .partialState().with(GarlicBlock.AGE, 3).modelForState().modelFile(models().getExistingFile(modLoc("block/garlic_stage_1"))).addModel()
                .partialState().with(GarlicBlock.AGE, 4).modelForState().modelFile(models().getExistingFile(modLoc("block/garlic_stage_2"))).addModel()
                .partialState().with(GarlicBlock.AGE, 5).modelForState().modelFile(models().getExistingFile(modLoc("block/garlic_stage_2"))).addModel()
                .partialState().with(GarlicBlock.AGE, 6).modelForState().modelFile(models().getExistingFile(modLoc("block/garlic_stage_3"))).addModel()
                .partialState().with(GarlicBlock.AGE, 7).modelForState().modelFile(models().getExistingFile(modLoc("block/garlic_stage_3"))).addModel();

        getVariantBuilder(ModBlocks.ALTAR_PILLAR.get())
                .partialState().with(AltarPillarBlock.TYPE_PROPERTY, AltarPillarBlock.EnumPillarType.NONE).modelForState().modelFile(models().getExistingFile(modLoc("block/altar_pillar"))).addModel()
                .partialState().with(AltarPillarBlock.TYPE_PROPERTY, AltarPillarBlock.EnumPillarType.BONE).modelForState().modelFile(models().withExistingParent("altar_pillar_filled_bone", modLoc("block/altar_pillar_filled")).texture("filler", mcLoc("block/bone_block_side"))).addModel()
                .partialState().with(AltarPillarBlock.TYPE_PROPERTY, AltarPillarBlock.EnumPillarType.GOLD).modelForState().modelFile(models().withExistingParent("altar_pillar_filled_gold", modLoc("block/altar_pillar_filled")).texture("filler", mcLoc("block/gold_block"))).addModel()
                .partialState().with(AltarPillarBlock.TYPE_PROPERTY, AltarPillarBlock.EnumPillarType.STONE).modelForState().modelFile(models().withExistingParent("altar_pillar_filled_stone_bricks", modLoc("block/altar_pillar_filled")).texture("filler", mcLoc("block/stone_bricks"))).addModel()
                .partialState().with(AltarPillarBlock.TYPE_PROPERTY, AltarPillarBlock.EnumPillarType.IRON).modelForState().modelFile(models().withExistingParent("altar_pillar_filled_iron", modLoc("block/altar_pillar_filled")).texture("filler", mcLoc("block/iron_block"))).addModel();

        ModelFile sieve = models().getExistingFile(modLoc("block/blood_sieve"));
        ModelFile activeSieve = models().getBuilder("active_blood_sieve").parent(sieve).texture("filter", modLoc("block/blood_sieve_filter_active"));
        getVariantBuilder(ModBlocks.BLOOD_SIEVE.get())
                .partialState().with(SieveBlock.PROPERTY_ACTIVE, true).modelForState().modelFile(activeSieve).addModel()
                .partialState().with(SieveBlock.PROPERTY_ACTIVE, false).modelForState().modelFile(sieve).addModel();

        getVariantBuilder(ModBlocks.MED_CHAIR.get()).forAllStates(blockState -> ConfiguredModel.builder().modelFile(models().getExistingFile(modLoc(blockState.getValue(MedChairBlock.PART) == MedChairBlock.EnumPart.TOP ? "block/medchairhead" : "block/medchairbase"))).rotationY(((int) blockState.getValue(MedChairBlock.FACING).toYRot() + 180) % 360).build());

        //multiparts
        BlockModelBuilder fire_side_alt0 = models().withExistingParent("fire_side_alt0", modLoc("block/fire_side_alt")).renderType(cutout).texture("particle", mcLoc("block/fire_0")).texture("fire", mcLoc("block/fire_0"));
        BlockModelBuilder fire_side_alt1 = models().withExistingParent("fire_side_alt1", modLoc("block/fire_side_alt")).renderType(cutout).texture("particle", mcLoc("block/fire_1")).texture("fire", mcLoc("block/fire_1"));
        BlockModelBuilder fire_side0 = models().withExistingParent("fire_side0", modLoc("block/fire_side")).renderType(cutout).texture("particle", mcLoc("block/fire_0")).texture("fire", mcLoc("block/fire_0"));
        BlockModelBuilder fire_side1 =models().withExistingParent("fire_side1", modLoc("block/fire_side")).renderType(cutout).texture("particle", mcLoc("block/fire_1")).texture("fire", mcLoc("block/fire_1"));
        BlockModelBuilder fire_floor0 = models().withExistingParent("fire_floor0", modLoc("block/fire_floor")).renderType(cutout).texture("particle", mcLoc("block/fire_0")).texture("fire", mcLoc("block/fire_0"));
        BlockModelBuilder fire_floor1 = models().withExistingParent("fire_floor1", modLoc("block/fire_floor")).renderType(cutout).texture("particle", mcLoc("block/fire_1")).texture("fire", mcLoc("block/fire_1"));
        getMultipartBuilder(ModBlocks.ALCHEMICAL_FIRE.get())
                .part().modelFile(fire_floor0).nextModel().modelFile(fire_floor1).addModel().end()
                .part().modelFile(fire_side0).nextModel().modelFile(fire_side1).nextModel().modelFile(fire_side_alt0).nextModel().modelFile(fire_side_alt1).addModel().end()
                .part().modelFile(fire_side0).rotationY(90).nextModel().modelFile(fire_side1).rotationY(90).nextModel().modelFile(fire_side_alt0).rotationY(90).nextModel().modelFile(fire_side_alt1).rotationY(90).addModel().end()
                .part().modelFile(fire_side0).rotationY(180).nextModel().modelFile(fire_side1).rotationY(180).nextModel().modelFile(fire_side_alt0).rotationY(180).nextModel().modelFile(fire_side_alt1).rotationY(180).addModel().end()
                .part().modelFile(fire_side0).rotationY(270).nextModel().modelFile(fire_side1).rotationY(270).nextModel().modelFile(fire_side_alt0).rotationY(270).nextModel().modelFile(fire_side_alt1).rotationY(270).addModel().end();

        ModelFile cauldronLiquid = models().getExistingFile(modLoc("block/alchemy_cauldron_liquid"));
        ModelFile cauldronLiquidBoiling = models().getBuilder("cauldron_boiling").parent(cauldronLiquid).texture("liquid", modLoc("block/blank_liquid_boiling"));
        getMultipartBuilder(ModBlocks.ALCHEMICAL_CAULDRON.get())
                .part().modelFile(models().getExistingFile(modLoc("block/alchemy_cauldron"))).addModel().end()
                .part().modelFile(models().getExistingFile(modLoc("block/alchemy_cauldron_fire"))).addModel().condition(AlchemicalCauldronBlock.LIT, true).end()
                .part().modelFile(cauldronLiquid).addModel().condition(AlchemicalCauldronBlock.LIQUID, 1).end()
                .part().modelFile(cauldronLiquidBoiling).addModel().condition(AlchemicalCauldronBlock.LIQUID, 2).end();

        ModelFile tentModel = models().getExistingFile(modLoc("block/tent"));
        ModelFile tentBackLeft = models().getExistingFile(modLoc("block/tentback"));
        ModelFile tentBackRight = models().getExistingFile(modLoc("block/tentback_flipped"));

        ModelFile tentTR = models().getBuilder("tent_tr").parent(tentModel).texture("floor", modLoc("block/tent/floor_tr"));
        ModelFile tentTL = models().getBuilder("tent_tl").parent(tentModel).texture("floor", modLoc("block/tent/floor_tl"));
        ModelFile tentBL = models().getBuilder("tent_bl").parent(tentModel).texture("floor", modLoc("block/tent/floor_bl"));
        ModelFile tentBR = models().getBuilder("tent_br").parent(tentModel).texture("floor", modLoc("block/tent/floor_br"));
        Arrays.stream(new TentBlock[]{ModBlocks.TENT.get(), ModBlocks.TENT_MAIN.get()}).forEach(t -> getMultipartBuilder(t)
                .part().modelFile(tentBR).rotationY(0).addModel().condition(TentBlock.FACING, Direction.NORTH).condition(TentBlock.POSITION, 0).end()
                .part().modelFile(tentBR).rotationY(90).addModel().condition(TentBlock.FACING, Direction.EAST).condition(TentBlock.POSITION, 0).end()
                .part().modelFile(tentBR).rotationY(180).addModel().condition(TentBlock.FACING, Direction.SOUTH).condition(TentBlock.POSITION, 0).end()
                .part().modelFile(tentBR).rotationY(270).addModel().condition(TentBlock.FACING, Direction.WEST).condition(TentBlock.POSITION, 0).end()
                .part().modelFile(tentBL).rotationY(0).addModel().condition(TentBlock.FACING, Direction.NORTH).condition(TentBlock.POSITION, 1).end()
                .part().modelFile(tentBL).rotationY(90).addModel().condition(TentBlock.FACING, Direction.EAST).condition(TentBlock.POSITION, 1).end()
                .part().modelFile(tentBL).rotationY(180).addModel().condition(TentBlock.FACING, Direction.SOUTH).condition(TentBlock.POSITION, 1).end()
                .part().modelFile(tentBL).rotationY(270).addModel().condition(TentBlock.FACING, Direction.WEST).condition(TentBlock.POSITION, 1).end()
                .part().modelFile(tentTL).rotationY(0).addModel().condition(TentBlock.FACING, Direction.NORTH).condition(TentBlock.POSITION, 2).end()
                .part().modelFile(tentTL).rotationY(90).addModel().condition(TentBlock.FACING, Direction.EAST).condition(TentBlock.POSITION, 2).end()
                .part().modelFile(tentTL).rotationY(180).addModel().condition(TentBlock.FACING, Direction.SOUTH).condition(TentBlock.POSITION, 2).end()
                .part().modelFile(tentTL).rotationY(270).addModel().condition(TentBlock.FACING, Direction.WEST).condition(TentBlock.POSITION, 2).end()
                .part().modelFile(tentTR).rotationY(0).addModel().condition(TentBlock.FACING, Direction.NORTH).condition(TentBlock.POSITION, 3).end()
                .part().modelFile(tentTR).rotationY(90).addModel().condition(TentBlock.FACING, Direction.EAST).condition(TentBlock.POSITION, 3).end()
                .part().modelFile(tentTR).rotationY(180).addModel().condition(TentBlock.FACING, Direction.SOUTH).condition(TentBlock.POSITION, 3).end()
                .part().modelFile(tentTR).rotationY(270).addModel().condition(TentBlock.FACING, Direction.WEST).condition(TentBlock.POSITION, 3).end()
                .part().modelFile(tentBackLeft).rotationY(180).addModel().condition(TentBlock.FACING, Direction.NORTH).condition(TentBlock.POSITION, 2).end()
                .part().modelFile(tentBackLeft).rotationY(270).addModel().condition(TentBlock.FACING, Direction.EAST).condition(TentBlock.POSITION, 2).end()
                .part().modelFile(tentBackLeft).rotationY(0).addModel().condition(TentBlock.FACING, Direction.SOUTH).condition(TentBlock.POSITION, 2).end()
                .part().modelFile(tentBackLeft).rotationY(90).addModel().condition(TentBlock.FACING, Direction.WEST).condition(TentBlock.POSITION, 2).end()
                .part().modelFile(tentBackRight).rotationY(0).addModel().condition(TentBlock.FACING, Direction.NORTH).condition(TentBlock.POSITION, 3).end()
                .part().modelFile(tentBackRight).rotationY(90).addModel().condition(TentBlock.FACING, Direction.EAST).condition(TentBlock.POSITION, 3).end()
                .part().modelFile(tentBackRight).rotationY(180).addModel().condition(TentBlock.FACING, Direction.SOUTH).condition(TentBlock.POSITION, 3).end()
                .part().modelFile(tentBackRight).rotationY(270).addModel().condition(TentBlock.FACING, Direction.WEST).condition(TentBlock.POSITION, 3).end());

        ModelFile weaponTable = models().getExistingFile(modLoc("block/weapon_table/weapon_table"));
        ModelFile weaponTableL1 = models().getExistingFile(modLoc("block/weapon_table/weapon_table_lava1"));
        ModelFile weaponTableL2 = models().getExistingFile(modLoc("block/weapon_table/weapon_table_lava2"));
        ModelFile weaponTableL3 = models().getExistingFile(modLoc("block/weapon_table/weapon_table_lava3"));
        ModelFile weaponTableL4 = models().getExistingFile(modLoc("block/weapon_table/weapon_table_lava4"));
        ModelFile weaponTableL5 = models().getExistingFile(modLoc("block/weapon_table/weapon_table_lava5"));

        getMultipartBuilder(ModBlocks.WEAPON_TABLE.get())
                .part().modelFile(weaponTable).rotationY(0).addModel().condition(WeaponTableBlock.FACING, Direction.NORTH).end()
                .part().modelFile(weaponTable).rotationY(90).addModel().condition(WeaponTableBlock.FACING, Direction.EAST).end()
                .part().modelFile(weaponTable).rotationY(180).addModel().condition(WeaponTableBlock.FACING, Direction.SOUTH).end()
                .part().modelFile(weaponTable).rotationY(270).addModel().condition(WeaponTableBlock.FACING, Direction.WEST).end()
                .part().modelFile(weaponTableL1).rotationY(0).addModel().condition(WeaponTableBlock.FACING, Direction.NORTH).condition(WeaponTableBlock.LAVA, 1).end()
                .part().modelFile(weaponTableL1).rotationY(90).addModel().condition(WeaponTableBlock.FACING, Direction.EAST).condition(WeaponTableBlock.LAVA, 1).end()
                .part().modelFile(weaponTableL1).rotationY(180).addModel().condition(WeaponTableBlock.FACING, Direction.SOUTH).condition(WeaponTableBlock.LAVA, 1).end()
                .part().modelFile(weaponTableL1).rotationY(270).addModel().condition(WeaponTableBlock.FACING, Direction.WEST).condition(WeaponTableBlock.LAVA, 1).end()
                .part().modelFile(weaponTableL2).rotationY(0).addModel().condition(WeaponTableBlock.FACING, Direction.NORTH).condition(WeaponTableBlock.LAVA, 2).end()
                .part().modelFile(weaponTableL2).rotationY(90).addModel().condition(WeaponTableBlock.FACING, Direction.EAST).condition(WeaponTableBlock.LAVA, 2).end()
                .part().modelFile(weaponTableL2).rotationY(180).addModel().condition(WeaponTableBlock.FACING, Direction.SOUTH).condition(WeaponTableBlock.LAVA, 2).end()
                .part().modelFile(weaponTableL2).rotationY(270).addModel().condition(WeaponTableBlock.FACING, Direction.WEST).condition(WeaponTableBlock.LAVA, 2).end()
                .part().modelFile(weaponTableL3).rotationY(0).addModel().condition(WeaponTableBlock.FACING, Direction.NORTH).condition(WeaponTableBlock.LAVA, 3).end()
                .part().modelFile(weaponTableL3).rotationY(90).addModel().condition(WeaponTableBlock.FACING, Direction.EAST).condition(WeaponTableBlock.LAVA, 3).end()
                .part().modelFile(weaponTableL3).rotationY(180).addModel().condition(WeaponTableBlock.FACING, Direction.SOUTH).condition(WeaponTableBlock.LAVA, 3).end()
                .part().modelFile(weaponTableL3).rotationY(270).addModel().condition(WeaponTableBlock.FACING, Direction.WEST).condition(WeaponTableBlock.LAVA, 3).end()
                .part().modelFile(weaponTableL4).rotationY(0).addModel().condition(WeaponTableBlock.FACING, Direction.NORTH).condition(WeaponTableBlock.LAVA, 4).end()
                .part().modelFile(weaponTableL4).rotationY(90).addModel().condition(WeaponTableBlock.FACING, Direction.EAST).condition(WeaponTableBlock.LAVA, 4).end()
                .part().modelFile(weaponTableL4).rotationY(180).addModel().condition(WeaponTableBlock.FACING, Direction.SOUTH).condition(WeaponTableBlock.LAVA, 4).end()
                .part().modelFile(weaponTableL4).rotationY(270).addModel().condition(WeaponTableBlock.FACING, Direction.WEST).condition(WeaponTableBlock.LAVA, 4).end()
                .part().modelFile(weaponTableL5).rotationY(0).addModel().condition(WeaponTableBlock.FACING, Direction.NORTH).condition(WeaponTableBlock.LAVA, 5).end()
                .part().modelFile(weaponTableL5).rotationY(90).addModel().condition(WeaponTableBlock.FACING, Direction.EAST).condition(WeaponTableBlock.LAVA, 5).end()
                .part().modelFile(weaponTableL5).rotationY(180).addModel().condition(WeaponTableBlock.FACING, Direction.SOUTH).condition(WeaponTableBlock.LAVA, 5).end()
                .part().modelFile(weaponTableL5).rotationY(270).addModel().condition(WeaponTableBlock.FACING, Direction.WEST).condition(WeaponTableBlock.LAVA, 5).end();

        ModelFile hunterTable = models().getExistingFile(modLoc("block/hunter_table/hunter_table"));
        ModelFile hunterTableBottle = models().getExistingFile(modLoc("block/hunter_table/hunter_table_bottle"));
        ModelFile hunterTableGarlic = models().getExistingFile(modLoc("block/hunter_table/hunter_table_garlic"));
        ModelFile hunterTableHammer = models().getExistingFile(modLoc("block/hunter_table/hunter_table_hammer"));

        getMultipartBuilder(ModBlocks.HUNTER_TABLE.get())
                .part().modelFile(hunterTable).rotationY(0).addModel().condition(HunterTableBlock.FACING, Direction.NORTH).end()
                .part().modelFile(hunterTable).rotationY(90).addModel().condition(HunterTableBlock.FACING, Direction.EAST).end()
                .part().modelFile(hunterTable).rotationY(180).addModel().condition(HunterTableBlock.FACING, Direction.SOUTH).end()
                .part().modelFile(hunterTable).rotationY(270).addModel().condition(HunterTableBlock.FACING, Direction.WEST).end()
                .part().modelFile(hunterTableBottle).rotationY(0).addModel().condition(HunterTableBlock.FACING, Direction.NORTH).condition(HunterTableBlock.VARIANT, HunterTableBlock.TABLE_VARIANT.POTION, HunterTableBlock.TABLE_VARIANT.POTION_CAULDRON, HunterTableBlock.TABLE_VARIANT.WEAPON_POTION, HunterTableBlock.TABLE_VARIANT.COMPLETE).end()
                .part().modelFile(hunterTableBottle).rotationY(90).addModel().condition(HunterTableBlock.FACING, Direction.EAST).condition(HunterTableBlock.VARIANT, HunterTableBlock.TABLE_VARIANT.POTION, HunterTableBlock.TABLE_VARIANT.POTION_CAULDRON, HunterTableBlock.TABLE_VARIANT.WEAPON_POTION, HunterTableBlock.TABLE_VARIANT.COMPLETE).end()
                .part().modelFile(hunterTableBottle).rotationY(180).addModel().condition(HunterTableBlock.FACING, Direction.SOUTH).condition(HunterTableBlock.VARIANT, HunterTableBlock.TABLE_VARIANT.POTION, HunterTableBlock.TABLE_VARIANT.POTION_CAULDRON, HunterTableBlock.TABLE_VARIANT.WEAPON_POTION, HunterTableBlock.TABLE_VARIANT.COMPLETE).end()
                .part().modelFile(hunterTableBottle).rotationY(270).addModel().condition(HunterTableBlock.FACING, Direction.WEST).condition(HunterTableBlock.VARIANT, HunterTableBlock.TABLE_VARIANT.POTION, HunterTableBlock.TABLE_VARIANT.POTION_CAULDRON, HunterTableBlock.TABLE_VARIANT.WEAPON_POTION, HunterTableBlock.TABLE_VARIANT.COMPLETE).end()
                .part().modelFile(hunterTableGarlic).rotationY(0).addModel().condition(HunterTableBlock.FACING, Direction.NORTH).condition(HunterTableBlock.VARIANT, HunterTableBlock.TABLE_VARIANT.CAULDRON, HunterTableBlock.TABLE_VARIANT.POTION_CAULDRON, HunterTableBlock.TABLE_VARIANT.WEAPON_CAULDRON, HunterTableBlock.TABLE_VARIANT.COMPLETE).end()
                .part().modelFile(hunterTableGarlic).rotationY(90).addModel().condition(HunterTableBlock.FACING, Direction.EAST).condition(HunterTableBlock.VARIANT, HunterTableBlock.TABLE_VARIANT.CAULDRON, HunterTableBlock.TABLE_VARIANT.POTION_CAULDRON, HunterTableBlock.TABLE_VARIANT.WEAPON_CAULDRON, HunterTableBlock.TABLE_VARIANT.COMPLETE).end()
                .part().modelFile(hunterTableGarlic).rotationY(180).addModel().condition(HunterTableBlock.FACING, Direction.SOUTH).condition(HunterTableBlock.VARIANT, HunterTableBlock.TABLE_VARIANT.CAULDRON, HunterTableBlock.TABLE_VARIANT.POTION_CAULDRON, HunterTableBlock.TABLE_VARIANT.WEAPON_CAULDRON, HunterTableBlock.TABLE_VARIANT.COMPLETE).end()
                .part().modelFile(hunterTableGarlic).rotationY(270).addModel().condition(HunterTableBlock.FACING, Direction.WEST).condition(HunterTableBlock.VARIANT, HunterTableBlock.TABLE_VARIANT.CAULDRON, HunterTableBlock.TABLE_VARIANT.POTION_CAULDRON, HunterTableBlock.TABLE_VARIANT.WEAPON_CAULDRON, HunterTableBlock.TABLE_VARIANT.COMPLETE).end()
                .part().modelFile(hunterTableHammer).rotationY(0).addModel().condition(HunterTableBlock.FACING, Direction.NORTH).condition(HunterTableBlock.VARIANT, HunterTableBlock.TABLE_VARIANT.WEAPON, HunterTableBlock.TABLE_VARIANT.WEAPON_CAULDRON, HunterTableBlock.TABLE_VARIANT.WEAPON_POTION, HunterTableBlock.TABLE_VARIANT.COMPLETE).end()
                .part().modelFile(hunterTableHammer).rotationY(90).addModel().condition(HunterTableBlock.FACING, Direction.EAST).condition(HunterTableBlock.VARIANT, HunterTableBlock.TABLE_VARIANT.WEAPON, HunterTableBlock.TABLE_VARIANT.WEAPON_CAULDRON, HunterTableBlock.TABLE_VARIANT.WEAPON_POTION, HunterTableBlock.TABLE_VARIANT.COMPLETE).end()
                .part().modelFile(hunterTableHammer).rotationY(180).addModel().condition(HunterTableBlock.FACING, Direction.SOUTH).condition(HunterTableBlock.VARIANT, HunterTableBlock.TABLE_VARIANT.WEAPON, HunterTableBlock.TABLE_VARIANT.WEAPON_CAULDRON, HunterTableBlock.TABLE_VARIANT.WEAPON_POTION, HunterTableBlock.TABLE_VARIANT.COMPLETE).end()
                .part().modelFile(hunterTableHammer).rotationY(270).addModel().condition(HunterTableBlock.FACING, Direction.WEST).condition(HunterTableBlock.VARIANT, HunterTableBlock.TABLE_VARIANT.WEAPON, HunterTableBlock.TABLE_VARIANT.WEAPON_CAULDRON, HunterTableBlock.TABLE_VARIANT.WEAPON_POTION, HunterTableBlock.TABLE_VARIANT.COMPLETE).end();
        simpleBlock(ModBlocks.CHANDELIER.get(), models().getExistingFile(modLoc("block/chandelier")));
        horizontalBlock(ModBlocks.CANDELABRA.get(), models().getExistingFile(modLoc("block/candelabra")));
        horizontalBlock(ModBlocks.CANDELABRA_WALL.get(), models().getExistingFile(modLoc("block/candelabra_wall")));
        horizontalBlock(ModBlocks.CROSS.get(), models().getExistingFile(modLoc("block/cross")));
        horizontalBlock(ModBlocks.TOMBSTONE1.get(), models().getExistingFile(modLoc("block/tombstone1")));
        horizontalBlock(ModBlocks.TOMBSTONE2.get(), models().getExistingFile(modLoc("block/tombstone2")));
        horizontalBlock(ModBlocks.TOMBSTONE3.get(), models().getExistingFile(modLoc("block/tombstone3")));
        horizontalBlock(ModBlocks.GRAVE_CAGE.get(), models().getExistingFile(modLoc("block/grave_cage")));

        getVariantBuilder(ModBlocks.CURSED_GRASS_BLOCK.get()).partialState().with(BlockStateProperties.SNOWY, false).modelForState().modelFile(models().getExistingFile(modLoc("block/cursed_grass_block"))).addModel().partialState().with(BlockStateProperties.SNOWY, true).modelForState().modelFile(models().getExistingFile(modLoc("block/cursed_grass_block_snowy"))).addModel();
    }
}
