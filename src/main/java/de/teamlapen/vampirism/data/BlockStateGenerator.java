package de.teamlapen.vampirism.data;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blocks.*;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.WoodButtonBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Arrays;


public class BlockStateGenerator extends BlockStateProvider {


    public BlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, REFERENCE.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        //models
        models().withExistingParent("fire_side_alt0", modLoc("block/fire_side_alt")).texture("particle", mcLoc("block/fire_0")).texture("fire", mcLoc("block/fire_0"));
        models().withExistingParent("fire_side_alt1", modLoc("block/fire_side_alt")).texture("particle", mcLoc("block/fire_1")).texture("fire", mcLoc("block/fire_1"));
        models().withExistingParent("fire_side0", modLoc("block/fire_side")).texture("particle", mcLoc("block/fire_0")).texture("fire", mcLoc("block/fire_0"));
        models().withExistingParent("fire_side1", modLoc("block/fire_side")).texture("particle", mcLoc("block/fire_1")).texture("fire", mcLoc("block/fire_1"));
        models().withExistingParent("fire_floor0", modLoc("block/fire_floor")).texture("particle", mcLoc("block/fire_0")).texture("fire", mcLoc("block/fire_0"));
        models().withExistingParent("fire_floor1", modLoc("block/fire_floor")).texture("particle", mcLoc("block/fire_1")).texture("fire", mcLoc("block/fire_1"));
        ModelFile dark_spruce_sapling = models().cross("dark_spruce_sapling", modLoc("block/dark_spruce_sapling"));
        ModelFile cursed_spruce_sapling = models().cross("cursed_spruce_sapling", modLoc("block/cursed_spruce_sapling"));

        //default blocks
        horizontalBlock(ModBlocks.garlic_beacon_normal, models().withExistingParent("garlic_beacon_normal", modLoc("block/garlic_beacon")));
        horizontalBlock(ModBlocks.garlic_beacon_weak, models().withExistingParent("garlic_beacon_weak", modLoc("block/garlic_beacon")));
        horizontalBlock(ModBlocks.garlic_beacon_improved, models().withExistingParent("garlic_beacon_improved", modLoc("block/garlic_beacon")));
        horizontalBlock(ModBlocks.church_altar, models().getExistingFile(modLoc("block/church_altar")));
        horizontalBlock(ModBlocks.blood_grinder, models().getExistingFile(modLoc("block/blood_grinder")));

        simpleBlock(ModBlocks.castle_block_dark_brick);
        simpleBlock(ModBlocks.castle_block_dark_brick_bloody);
        simpleBlock(ModBlocks.castle_block_dark_stone);
        simpleBlock(ModBlocks.castle_block_normal_brick);
        simpleBlock(ModBlocks.castle_block_purple_brick);
        simpleBlock(ModBlocks.cursed_earth);
        simpleBlock(ModBlocks.sunscreen_beacon, models().withExistingParent("vampirism:block/sunscreen_beacon", "minecraft:block/beacon").texture("beacon", "vampirism:block/cursed_earth"));
        BlockModelBuilder builder1 = models().getBuilder("vampirism:block/empty").texture("particle", "minecraft:block/spruce_planks");
        CoffinBlock.COFFIN_BLOCKS.values().forEach(coffin -> getVariantBuilder(coffin).forAllStates(state -> ConfiguredModel.builder().modelFile(builder1).build()));
        simpleBlock(ModBlocks.vampire_orchid, models().cross("vampire_orchid", modLoc("block/vampire_orchid")));
        simpleBlock(ModBlocks.totem_top, models().getExistingFile(modLoc("block/totem_top")));
        simpleBlock(ModBlocks.totem_top_crafted, models().getExistingFile(modLoc("block/totem_top_crafted")));
        simpleBlock(ModBlocks.totem_top_vampirism_hunter, models().withExistingParent("totem_top_vampirism_hunter", modLoc("block/totem_top")));
        simpleBlock(ModBlocks.totem_top_vampirism_vampire, models().withExistingParent("totem_top_vampirism_vampire", modLoc("block/totem_top")));
        simpleBlock(ModBlocks.totem_top_vampirism_hunter_crafted, models().withExistingParent("totem_top_vampirism_hunter_crafted", modLoc("block/totem_top_crafted")));
        simpleBlock(ModBlocks.totem_top_vampirism_vampire_crafted, models().withExistingParent("totem_top_vampirism_vampire_crafted", modLoc("block/totem_top_crafted")));
        simpleBlock(ModBlocks.totem_base, models().getExistingFile(modLoc("block/totem_base")));
        simpleBlock(ModBlocks.altar_infusion, models().getExistingFile(modLoc("block/altar_infusion")));
        simpleBlock(ModBlocks.altar_inspiration, models().getExistingFile(modLoc("block/altar_inspiration/altar_inspiration")));
        simpleBlock(ModBlocks.altar_tip, models().getExistingFile(modLoc("block/altar_tip")));
        simpleBlock(ModBlocks.blood_container, models().getExistingFile(modLoc("block/blood_container/blood_container")));
        simpleBlock(ModBlocks.blood_pedestal, models().getExistingFile(modLoc("block/blood_pedestal")));
        simpleBlock(ModBlocks.potion_table, models().getExistingFile(modLoc("block/potion_table")));
        simpleBlock(ModBlocks.fire_place, models().getExistingFile(modLoc("block/fire_place")));
        simpleBlock(ModBlocks.potted_vampire_orchid, models().withExistingParent("vampirism:block/potted_vampire_orchid", "minecraft:block/flower_pot_cross").texture("plant", "vampirism:block/vampire_orchid"));
        simpleBlock(ModBlocks.dark_spruce_leaves, models().getExistingFile(mcLoc("block/oak_leaves")));
        simpleBlock(ModBlocks.dark_spruce_sapling, dark_spruce_sapling);
        simpleBlock(ModBlocks.cursed_spruce_sapling, cursed_spruce_sapling);


        stairsBlock(ModBlocks.castle_stairs_dark_stone, modLoc("block/castle_block_dark_stone"));
        stairsBlock(ModBlocks.castle_stairs_dark_brick, modLoc("block/castle_block_dark_brick"));
        stairsBlock(ModBlocks.castle_stairs_purple_brick, modLoc("block/castle_block_purple_brick"));

        slabBlock(ModBlocks.castle_slab_dark_brick, modLoc("block/castle_block_dark_brick"), modLoc("block/castle_block_dark_brick"));
        slabBlock(ModBlocks.castle_slab_dark_stone, modLoc("block/castle_block_dark_stone"), modLoc("block/castle_block_dark_stone"));
        slabBlock(ModBlocks.castle_slab_purple_brick, modLoc("block/castle_block_purple_brick"), modLoc("block/castle_block_purple_brick"));


        //variants

        getVariantBuilder(ModBlocks.garlic)
                .partialState().with(GarlicBlock.AGE, 0).modelForState().modelFile(models().getExistingFile(modLoc("block/garlic_stage_0"))).addModel()
                .partialState().with(GarlicBlock.AGE, 1).modelForState().modelFile(models().getExistingFile(modLoc("block/garlic_stage_0"))).addModel()
                .partialState().with(GarlicBlock.AGE, 2).modelForState().modelFile(models().getExistingFile(modLoc("block/garlic_stage_1"))).addModel()
                .partialState().with(GarlicBlock.AGE, 3).modelForState().modelFile(models().getExistingFile(modLoc("block/garlic_stage_1"))).addModel()
                .partialState().with(GarlicBlock.AGE, 4).modelForState().modelFile(models().getExistingFile(modLoc("block/garlic_stage_2"))).addModel()
                .partialState().with(GarlicBlock.AGE, 5).modelForState().modelFile(models().getExistingFile(modLoc("block/garlic_stage_2"))).addModel()
                .partialState().with(GarlicBlock.AGE, 6).modelForState().modelFile(models().getExistingFile(modLoc("block/garlic_stage_3"))).addModel()
                .partialState().with(GarlicBlock.AGE, 7).modelForState().modelFile(models().getExistingFile(modLoc("block/garlic_stage_3"))).addModel();

        getVariantBuilder(ModBlocks.altar_pillar)
                .partialState().with(AltarPillarBlock.TYPE_PROPERTY, AltarPillarBlock.EnumPillarType.NONE).modelForState().modelFile(models().getExistingFile(modLoc("block/altar_pillar"))).addModel()
                .partialState().with(AltarPillarBlock.TYPE_PROPERTY, AltarPillarBlock.EnumPillarType.BONE).modelForState().modelFile(models().withExistingParent("altar_pillar_filled_bone", modLoc("block/altar_pillar_filled")).texture("filler", mcLoc("block/bone_block_side"))).addModel()
                .partialState().with(AltarPillarBlock.TYPE_PROPERTY, AltarPillarBlock.EnumPillarType.GOLD).modelForState().modelFile(models().withExistingParent("altar_pillar_filled_gold", modLoc("block/altar_pillar_filled")).texture("filler", mcLoc("block/gold_block"))).addModel()
                .partialState().with(AltarPillarBlock.TYPE_PROPERTY, AltarPillarBlock.EnumPillarType.STONE).modelForState().modelFile(models().withExistingParent("altar_pillar_filled_stone_bricks", modLoc("block/altar_pillar_filled")).texture("filler", mcLoc("block/stone_bricks"))).addModel()
                .partialState().with(AltarPillarBlock.TYPE_PROPERTY, AltarPillarBlock.EnumPillarType.IRON).modelForState().modelFile(models().withExistingParent("altar_pillar_filled_iron", modLoc("block/altar_pillar_filled")).texture("filler", mcLoc("block/iron_block"))).addModel();

        ModelFile sieve = models().getExistingFile(modLoc("block/blood_sieve"));
        ModelFile activeSieve = models().getBuilder("active_blood_sieve").parent(sieve).texture("filter", modLoc("block/blood_sieve_filter_active"));
        getVariantBuilder(ModBlocks.blood_sieve)
                .partialState().with(SieveBlock.PROPERTY_ACTIVE, true).modelForState().modelFile(activeSieve).addModel()
                .partialState().with(SieveBlock.PROPERTY_ACTIVE, false).modelForState().modelFile(sieve).addModel();

        getVariantBuilder(ModBlocks.med_chair).forAllStates(blockState -> ConfiguredModel.builder().modelFile(models().getExistingFile(modLoc(blockState.getValue(MedChairBlock.PART) == MedChairBlock.EnumPart.TOP ? "block/medchairhead" : "block/medchairbase"))).rotationY(((int) blockState.getValue(MedChairBlock.FACING).toYRot() + 180) % 360).build());

        //multiparts

        getMultipartBuilder(ModBlocks.alchemical_fire)
                .part().modelFile(models().getExistingFile(modLoc("block/fire_floor0"))).nextModel().modelFile(models().getExistingFile(modLoc("block/fire_floor1"))).addModel().end()
                .part().modelFile(models().getExistingFile(modLoc("block/fire_side0"))).nextModel().modelFile(models().getExistingFile(modLoc("block/fire_side1"))).nextModel().modelFile(models().getExistingFile(modLoc("block/fire_side_alt0"))).nextModel().modelFile(models().getExistingFile(modLoc("block/fire_side_alt1"))).addModel().end()
                .part().modelFile(models().getExistingFile(modLoc("block/fire_side0"))).rotationY(90).nextModel().modelFile(models().getExistingFile(modLoc("block/fire_side1"))).rotationY(90).nextModel().modelFile(models().getExistingFile(modLoc("block/fire_side_alt0"))).rotationY(90).nextModel().modelFile(models().getExistingFile(modLoc("block/fire_side_alt1"))).rotationY(90).addModel().end()
                .part().modelFile(models().getExistingFile(modLoc("block/fire_side0"))).rotationY(180).nextModel().modelFile(models().getExistingFile(modLoc("block/fire_side1"))).rotationY(180).nextModel().modelFile(models().getExistingFile(modLoc("block/fire_side_alt0"))).rotationY(180).nextModel().modelFile(models().getExistingFile(modLoc("block/fire_side_alt1"))).rotationY(180).addModel().end()
                .part().modelFile(models().getExistingFile(modLoc("block/fire_side0"))).rotationY(270).nextModel().modelFile(models().getExistingFile(modLoc("block/fire_side1"))).rotationY(270).nextModel().modelFile(models().getExistingFile(modLoc("block/fire_side_alt0"))).rotationY(270).nextModel().modelFile(models().getExistingFile(modLoc("block/fire_side_alt1"))).rotationY(270).addModel().end();

        ModelFile cauldronLiquid = models().getExistingFile(modLoc("block/alchemy_cauldron_liquid"));
        ModelFile cauldronLiquidBoiling = models().getBuilder("cauldron_boiling").parent(cauldronLiquid).texture("liquid", modLoc("block/blank_liquid_boiling"));
        getMultipartBuilder(ModBlocks.alchemical_cauldron)
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
        Arrays.stream(new TentBlock[]{ModBlocks.tent, ModBlocks.tent_main}).forEach(t -> getMultipartBuilder(t)
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

        getMultipartBuilder(ModBlocks.weapon_table)
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

        getMultipartBuilder(ModBlocks.hunter_table)
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
        simpleBlock(ModBlocks.chandelier, models().getExistingFile(modLoc("block/chandelier")));
        horizontalBlock(ModBlocks.candelabra, models().getExistingFile(modLoc("block/candelabra")));
        horizontalBlock(ModBlocks.candelabra_wall, models().getExistingFile(modLoc("block/candelabra_wall")));
        horizontalBlock(ModBlocks.cross, models().getExistingFile(modLoc("block/cross")));
        horizontalBlock(ModBlocks.tombstone1, models().getExistingFile(modLoc("block/tombstone1")));
        horizontalBlock(ModBlocks.tombstone2, models().getExistingFile(modLoc("block/tombstone2")));
        horizontalBlock(ModBlocks.tombstone3, models().getExistingFile(modLoc("block/tombstone3")));
        horizontalBlock(ModBlocks.grave_cage, models().getExistingFile(modLoc("block/grave_cage")));

        getMultipartBuilder(ModBlocks.cursed_grass)
                .part().modelFile(models().cubeBottomTop(ModBlocks.cursed_grass.getRegistryName().toString(),modLoc("block/cursed_grass_side"), modLoc("block/cursed_earth"),modLoc("block/cursed_grass_top"))).addModel().end()
                .part().modelFile(models().cubeBottomTop(ModBlocks.cursed_grass.getRegistryName().toString()+"_snowy",modLoc("block/cursed_grass_side_snowy"), modLoc("block/cursed_earth"),modLoc("block/cursed_grass_top"))).addModel().condition(BlockStateProperties.SNOWY, true).end();

        simpleBlock(ModBlocks.cursed_roots, models().cross("cursed_roots", modLoc("block/cursed_roots")));
        simpleBlock(ModBlocks.potted_cursed_roots, models().withExistingParent("vampirism:block/potted_cursed_roots", "minecraft:block/flower_pot_cross").texture("plant", "vampirism:block/cursed_roots"));

        trapdoorBlock(ModBlocks.dark_spruce_trapdoor, new ResourceLocation(REFERENCE.MODID, "block/dark_spruce_trapdoor"), true);
        trapdoorBlock(ModBlocks.cursed_spruce_trapdoor, new ResourceLocation(REFERENCE.MODID, "block/cursed_spruce_trapdoor"), true);

        doorBlock(ModBlocks.dark_spruce_door, new ResourceLocation(REFERENCE.MODID, "block/dark_spruce_door_bottom"),  new ResourceLocation(REFERENCE.MODID, "block/dark_spruce_door_top"));
        doorBlock(ModBlocks.cursed_spruce_door, new ResourceLocation(REFERENCE.MODID, "block/cursed_spruce_door_bottom"),  new ResourceLocation(REFERENCE.MODID, "block/cursed_spruce_door_top"));

        createWoodStates();
        createCursedBark();

        horizontalBlock(ModBlocks.vampire_rack, models().getExistingFile(modLoc("block/vampire_rack")));
        horizontalBlock(ModBlocks.throne, models().getExistingFile(modLoc("block/throne")));
    }

    private void createWoodStates() {
        simpleBlock(ModBlocks.dark_spruce_planks);
        simpleBlock(ModBlocks.cursed_spruce_planks);

        stairsBlock(ModBlocks.dark_spruce_stairs, blockTexture(ModBlocks.dark_spruce_planks));
        stairsBlock(ModBlocks.cursed_spruce_stairs, blockTexture(ModBlocks.cursed_spruce_planks));
        slabBlock(ModBlocks.dark_spruce_slab, blockTexture(ModBlocks.dark_spruce_planks), blockTexture(ModBlocks.dark_spruce_planks));
        slabBlock(ModBlocks.cursed_spruce_slab, blockTexture(ModBlocks.cursed_spruce_planks), blockTexture(ModBlocks.cursed_spruce_planks));

        fenceBlock(ModBlocks.dark_spruce_fence, blockTexture(ModBlocks.dark_spruce_planks));
        fenceBlock(ModBlocks.cursed_spruce_fence, blockTexture(ModBlocks.cursed_spruce_planks));
        models().withExistingParent(ModBlocks.dark_spruce_fence.getRegistryName().getPath() + "_inventory", new ResourceLocation("block/fence_inventory")).texture("texture", "block/" + ModBlocks.dark_spruce_planks.getRegistryName().getPath());
        models().withExistingParent(ModBlocks.cursed_spruce_fence.getRegistryName().getPath() + "_inventory", new ResourceLocation("block/fence_inventory")).texture("texture", "block/" + ModBlocks.cursed_spruce_planks.getRegistryName().getPath());
        fenceGateBlock(ModBlocks.dark_spruce_fence_gate, blockTexture(ModBlocks.dark_spruce_planks));
        fenceGateBlock(ModBlocks.cursed_spruce_fence_gate,blockTexture(ModBlocks.cursed_spruce_planks));

        logBlock(ModBlocks.dark_spruce_log);
        logBlock(ModBlocks.cursed_spruce_log);
        axisBlock(ModBlocks.dark_spruce_wood,blockTexture(ModBlocks.dark_spruce_log),blockTexture(ModBlocks.dark_spruce_log));
        axisBlock(ModBlocks.cursed_spruce_wood,blockTexture(ModBlocks.cursed_spruce_log),blockTexture(ModBlocks.cursed_spruce_log));
        logBlock(ModBlocks.stripped_dark_spruce_log);
        logBlock(ModBlocks.stripped_cursed_spruce_log);
        axisBlock(ModBlocks.stripped_dark_spruce_wood,blockTexture(ModBlocks.stripped_dark_spruce_log),blockTexture(ModBlocks.stripped_dark_spruce_log));
        axisBlock(ModBlocks.stripped_cursed_spruce_wood,blockTexture(ModBlocks.stripped_cursed_spruce_log),blockTexture(ModBlocks.stripped_cursed_spruce_log));

        button(ModBlocks.dark_spruce_button, blockTexture(ModBlocks.dark_spruce_planks));
        button(ModBlocks.cursed_spruce_button, blockTexture(ModBlocks.cursed_spruce_planks));

        pressurePlate(ModBlocks.dark_spruce_pressure_place, blockTexture(ModBlocks.dark_spruce_planks));
        pressurePlate(ModBlocks.cursed_spruce_pressure_place, blockTexture(ModBlocks.cursed_spruce_planks));

        simpleBlock(ModBlocks.dark_spruce_wall_sign, models().getBuilder("vampirism:dark_spruce_wall_sign").texture("particle", "vampirism:block/cursed_spruce_planks"));
        simpleBlock(ModBlocks.cursed_spruce_wall_sign, models().getBuilder("vampirism:cursed_spruce_wall_sign").texture("particle", "vampirism:block/cursed_spruce_planks"));
        simpleBlock(ModBlocks.dark_spruce_sign, models().getBuilder("vampirism:dark_spruce_sign").texture("particle", "vampirism:block/cursed_spruce_planks"));
        simpleBlock(ModBlocks.cursed_spruce_sign, models().getBuilder("vampirism:cursed_spruce_sign").texture("particle", "vampirism:block/cursed_spruce_planks"));
    }

    private void createCursedBark() {
        ModelFile side = models().getExistingFile(new ResourceLocation(REFERENCE.MODID, "cursed_bark_side"));
        ModelFile side2 = models().getExistingFile(new ResourceLocation(REFERENCE.MODID, "cursed_bark_side_2"));
        VariantBlockStateBuilder bark = getVariantBuilder(ModBlocks.cursed_bark)
                .partialState().with(CursedBarkBlock.FACING, Direction.NORTH).with(CursedBarkBlock.FACING2, Direction.NORTH).with(CursedBarkBlock.AXIS, Direction.Axis.Y).modelForState().modelFile(side).addModel()
                .partialState().with(CursedBarkBlock.FACING, Direction.WEST).with(CursedBarkBlock.FACING2, Direction.WEST).with(CursedBarkBlock.AXIS, Direction.Axis.Y).modelForState().modelFile(side).rotationY(270).addModel()
                .partialState().with(CursedBarkBlock.FACING, Direction.SOUTH).with(CursedBarkBlock.FACING2, Direction.SOUTH).with(CursedBarkBlock.AXIS, Direction.Axis.Y).modelForState().modelFile(side).rotationY(180).addModel()
                .partialState().with(CursedBarkBlock.FACING, Direction.EAST).with(CursedBarkBlock.FACING2, Direction.EAST).with(CursedBarkBlock.AXIS, Direction.Axis.Y).modelForState().modelFile(side).rotationY(90).addModel()
                .partialState().with(CursedBarkBlock.FACING, Direction.UP).with(CursedBarkBlock.FACING2, Direction.UP).with(CursedBarkBlock.AXIS, Direction.Axis.Y).modelForState().modelFile(side).rotationX(270).addModel()
                .partialState().with(CursedBarkBlock.FACING, Direction.DOWN).with(CursedBarkBlock.FACING2, Direction.DOWN).with(CursedBarkBlock.AXIS, Direction.Axis.Y).modelForState().modelFile(side).rotationX(90).addModel()

                .partialState().with(CursedBarkBlock.FACING, Direction.NORTH).with(CursedBarkBlock.FACING2, Direction.NORTH).with(CursedBarkBlock.AXIS, Direction.Axis.X).modelForState().modelFile(side2).rotationX(180).rotationY(180).addModel()
                .partialState().with(CursedBarkBlock.FACING, Direction.WEST).with(CursedBarkBlock.FACING2, Direction.WEST).with(CursedBarkBlock.AXIS, Direction.Axis.X).modelForState().modelFile(side2).rotationY(270).rotationX(90).addModel()
                .partialState().with(CursedBarkBlock.FACING, Direction.SOUTH).with(CursedBarkBlock.FACING2, Direction.SOUTH).with(CursedBarkBlock.AXIS, Direction.Axis.X).modelForState().modelFile(side2).rotationY(180).rotationX(0).addModel()
                .partialState().with(CursedBarkBlock.FACING, Direction.EAST).with(CursedBarkBlock.FACING2, Direction.EAST).with(CursedBarkBlock.AXIS, Direction.Axis.X).modelForState().modelFile(side2).rotationY(90).rotationX(90).addModel()
                .partialState().with(CursedBarkBlock.FACING, Direction.UP).with(CursedBarkBlock.FACING2, Direction.UP).with(CursedBarkBlock.AXIS, Direction.Axis.X).modelForState().modelFile(side2).rotationX(270).rotationY(180).addModel()
                .partialState().with(CursedBarkBlock.FACING, Direction.DOWN).with(CursedBarkBlock.FACING2, Direction.DOWN).with(CursedBarkBlock.AXIS, Direction.Axis.X).modelForState().modelFile(side2).rotationX(90).rotationY(180).addModel()

                .partialState().with(CursedBarkBlock.FACING, Direction.NORTH).with(CursedBarkBlock.FACING2, Direction.NORTH).with(CursedBarkBlock.AXIS, Direction.Axis.Z).modelForState().modelFile(side2).rotationX(90).addModel()
                .partialState().with(CursedBarkBlock.FACING, Direction.WEST).with(CursedBarkBlock.FACING2, Direction.WEST).with(CursedBarkBlock.AXIS, Direction.Axis.Z).modelForState().modelFile(side2).rotationY(90).rotationX(180).addModel()
                .partialState().with(CursedBarkBlock.FACING, Direction.SOUTH).with(CursedBarkBlock.FACING2, Direction.SOUTH).with(CursedBarkBlock.AXIS, Direction.Axis.Z).modelForState().modelFile(side2).rotationY(180).rotationX(90).addModel()
                .partialState().with(CursedBarkBlock.FACING, Direction.EAST).with(CursedBarkBlock.FACING2, Direction.EAST).with(CursedBarkBlock.AXIS, Direction.Axis.Z).modelForState().modelFile(side2).rotationY(90).addModel()
                .partialState().with(CursedBarkBlock.FACING, Direction.UP).with(CursedBarkBlock.FACING2, Direction.UP).with(CursedBarkBlock.AXIS, Direction.Axis.Z).modelForState().modelFile(side2).rotationX(270).rotationY(90).addModel()
                .partialState().with(CursedBarkBlock.FACING, Direction.DOWN).with(CursedBarkBlock.FACING2, Direction.DOWN).with(CursedBarkBlock.AXIS, Direction.Axis.Z).modelForState().modelFile(side2).rotationX(90).rotationY(90).addModel();
        ModelFile empty = models().getBuilder(ModBlocks.cursed_bark.getRegistryName().toString() + "_empty");
        for (Direction direction : Direction.values()) {
            for (Direction direction1 : Direction.values()) {
                if (direction == direction1) continue;
                bark.partialState().with(CursedBarkBlock.FACING, direction).with(CursedBarkBlock.FACING2, direction1).modelForState().modelFile(empty).addModel();
            }
        }
    }

    private void button(Block block, ResourceLocation texture) {
        ModelFile button = models().withExistingParent("block/" + block.getRegistryName().getPath(), new ResourceLocation("block/button")).texture("texture", texture.getPath());
        ModelFile button_pressed = models().withExistingParent("block/" + block.getRegistryName().getPath() + "_pressed", new ResourceLocation("block/button_pressed")).texture("texture", texture.getPath());
        ModelFile button_inventory  = models().withExistingParent("block/" + block.getRegistryName().getPath() + "_inventory", new ResourceLocation("block/button_inventory")).texture("texture", texture.getPath());
        getVariantBuilder(block)
                .partialState().with(WoodButtonBlock.FACE, AttachFace.CEILING).with(WoodButtonBlock.FACING, Direction.EAST).with(WoodButtonBlock.POWERED, false).modelForState().modelFile(button).rotationY(270).rotationX(180).addModel()
                .partialState().with(WoodButtonBlock.FACE, AttachFace.CEILING).with(WoodButtonBlock.FACING, Direction.EAST).with(WoodButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).rotationY(270).rotationX(180).addModel()
                .partialState().with(WoodButtonBlock.FACE, AttachFace.CEILING).with(WoodButtonBlock.FACING, Direction.NORTH).with(WoodButtonBlock.POWERED, false).modelForState().modelFile(button).rotationY(180).rotationX(180).addModel()
                .partialState().with(WoodButtonBlock.FACE, AttachFace.CEILING).with(WoodButtonBlock.FACING, Direction.NORTH).with(WoodButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).rotationY(180).rotationX(180).addModel()
                .partialState().with(WoodButtonBlock.FACE, AttachFace.CEILING).with(WoodButtonBlock.FACING, Direction.SOUTH).with(WoodButtonBlock.POWERED, false).modelForState().modelFile(button).rotationX(180).addModel()
                .partialState().with(WoodButtonBlock.FACE, AttachFace.CEILING).with(WoodButtonBlock.FACING, Direction.SOUTH).with(WoodButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).rotationX(180).addModel()
                .partialState().with(WoodButtonBlock.FACE, AttachFace.CEILING).with(WoodButtonBlock.FACING, Direction.WEST).with(WoodButtonBlock.POWERED, false).modelForState().modelFile(button).rotationY(90).rotationX(180).addModel()
                .partialState().with(WoodButtonBlock.FACE, AttachFace.CEILING).with(WoodButtonBlock.FACING, Direction.WEST).with(WoodButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).rotationX(90).rotationX(180).addModel()

                .partialState().with(WoodButtonBlock.FACE, AttachFace.FLOOR).with(WoodButtonBlock.FACING, Direction.EAST).with(WoodButtonBlock.POWERED, false).modelForState().modelFile(button).rotationY(90).addModel()
                .partialState().with(WoodButtonBlock.FACE, AttachFace.FLOOR).with(WoodButtonBlock.FACING, Direction.EAST).with(WoodButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).rotationY(90).addModel()
                .partialState().with(WoodButtonBlock.FACE, AttachFace.FLOOR).with(WoodButtonBlock.FACING, Direction.NORTH).with(WoodButtonBlock.POWERED, false).modelForState().modelFile(button).addModel()
                .partialState().with(WoodButtonBlock.FACE, AttachFace.FLOOR).with(WoodButtonBlock.FACING, Direction.NORTH).with(WoodButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).addModel()
                .partialState().with(WoodButtonBlock.FACE, AttachFace.FLOOR).with(WoodButtonBlock.FACING, Direction.SOUTH).with(WoodButtonBlock.POWERED, false).modelForState().modelFile(button).rotationY(180).addModel()
                .partialState().with(WoodButtonBlock.FACE, AttachFace.FLOOR).with(WoodButtonBlock.FACING, Direction.SOUTH).with(WoodButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).rotationY(180).addModel()
                .partialState().with(WoodButtonBlock.FACE, AttachFace.FLOOR).with(WoodButtonBlock.FACING, Direction.WEST).with(WoodButtonBlock.POWERED, false).modelForState().modelFile(button).rotationY(270).addModel()
                .partialState().with(WoodButtonBlock.FACE, AttachFace.FLOOR).with(WoodButtonBlock.FACING, Direction.WEST).with(WoodButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).rotationX(270).addModel()

                .partialState().with(WoodButtonBlock.FACE, AttachFace.WALL).with(WoodButtonBlock.FACING, Direction.EAST).with(WoodButtonBlock.POWERED, false).modelForState().modelFile(button).rotationY(90).rotationX(90).uvLock(true).addModel()
                .partialState().with(WoodButtonBlock.FACE, AttachFace.WALL).with(WoodButtonBlock.FACING, Direction.EAST).with(WoodButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).rotationY(90).rotationX(90).uvLock(true).addModel()
                .partialState().with(WoodButtonBlock.FACE, AttachFace.WALL).with(WoodButtonBlock.FACING, Direction.NORTH).with(WoodButtonBlock.POWERED, false).modelForState().modelFile(button).rotationX(90).uvLock(true).addModel()
                .partialState().with(WoodButtonBlock.FACE, AttachFace.WALL).with(WoodButtonBlock.FACING, Direction.NORTH).with(WoodButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).rotationX(90).uvLock(true).addModel()
                .partialState().with(WoodButtonBlock.FACE, AttachFace.WALL).with(WoodButtonBlock.FACING, Direction.SOUTH).with(WoodButtonBlock.POWERED, false).modelForState().modelFile(button).rotationY(180).rotationX(90).uvLock(true).addModel()
                .partialState().with(WoodButtonBlock.FACE, AttachFace.WALL).with(WoodButtonBlock.FACING, Direction.SOUTH).with(WoodButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).rotationY(180).rotationX(90).uvLock(true).addModel()
                .partialState().with(WoodButtonBlock.FACE, AttachFace.WALL).with(WoodButtonBlock.FACING, Direction.WEST).with(WoodButtonBlock.POWERED, false).modelForState().modelFile(button).rotationY(270).rotationX(90).uvLock(true).addModel()
                .partialState().with(WoodButtonBlock.FACE, AttachFace.WALL).with(WoodButtonBlock.FACING, Direction.WEST).with(WoodButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).rotationY(270).rotationX(90).uvLock(true).addModel()
        ;
    }

    private void pressurePlate(Block block, ResourceLocation texture) {
        ModelFile pressure_plate = models().withExistingParent("block/" + block.getRegistryName().getPath(), new ResourceLocation("block/pressure_plate_up")).texture("texture", texture.getPath());
        ModelFile pressure_plate_down = models().withExistingParent("block/" + block.getRegistryName().getPath() + "_down", new ResourceLocation("block/pressure_plate_down")).texture("texture", texture.getPath());

        getVariantBuilder(block)
                .partialState().with(PressurePlateBlock.POWERED, false).modelForState().modelFile(pressure_plate).addModel()
                .partialState().with(PressurePlateBlock.POWERED, true).modelForState().modelFile(pressure_plate_down).addModel();
    }

}
