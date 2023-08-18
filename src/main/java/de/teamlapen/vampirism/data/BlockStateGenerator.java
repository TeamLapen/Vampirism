package de.teamlapen.vampirism.data;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blocks.*;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Consumer;


public class BlockStateGenerator extends BlockStateProvider {//TODO 1.20 move to de.teamlapen.vampirism.data.provider

    public BlockStateGenerator(@NotNull PackOutput packOutput, @NotNull ExistingFileHelper exFileHelper) {
        super(packOutput, REFERENCE.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ResourceLocation cutout = new ResourceLocation("cutout");
        ResourceLocation cutout_mipped = new ResourceLocation("cutout_mipped");
        ResourceLocation translucent = new ResourceLocation("translucent");
        //models
        ModelFile dark_spruce_sapling = models().cross("dark_spruce_sapling", modLoc("block/dark_spruce_sapling")).renderType(cutout);
        ModelFile cursed_spruce_sapling = models().cross("cursed_spruce_sapling", modLoc("block/cursed_spruce_sapling")).renderType(cutout);

        //default blocks
        horizontalBlock(ModBlocks.GARLIC_DIFFUSER_NORMAL.get(), models().withExistingParent("garlic_diffuser_normal", modLoc("block/garlic_diffuser")).renderType(cutout));
        horizontalBlock(ModBlocks.GARLIC_DIFFUSER_WEAK.get(), models().withExistingParent("garlic_diffuser_weak", modLoc("block/garlic_diffuser")).renderType(cutout));
        horizontalBlock(ModBlocks.GARLIC_DIFFUSER_IMPROVED.get(), models().withExistingParent("garlic_diffuser_improved", modLoc("block/garlic_diffuser")).texture("garlic", "vampirism:block/garlic_diffuser_inside_improved").renderType(cutout));
        horizontalBlock(ModBlocks.ALTAR_CLEANSING.get(), models().getExistingFile(modLoc("block/altar_cleansing")));
        horizontalBlock(ModBlocks.BLOOD_GRINDER.get(), models().getExistingFile(modLoc("block/blood_grinder")));

        simpleBlock(ModBlocks.CASTLE_BLOCK_DARK_BRICK.get());
        simpleBlock(ModBlocks.CASTLE_BLOCK_DARK_BRICK_BLOODY.get());
        simpleBlock(ModBlocks.CASTLE_BLOCK_DARK_STONE.get());
        simpleBlock(ModBlocks.CASTLE_BLOCK_NORMAL_BRICK.get());
        simpleBlock(ModBlocks.CASTLE_BLOCK_PURPLE_BRICK.get());
        simpleBlock(ModBlocks.CURSED_EARTH.get());
        simpleBlock(ModBlocks.SUNSCREEN_BEACON.get(), models().withExistingParent("vampirism:block/sunscreen_beacon", "minecraft:block/beacon").texture("beacon", "vampirism:block/cursed_earth").renderType(cutout));
        BlockModelBuilder builder1 = models().getBuilder("vampirism:block/empty").texture("particle", "minecraft:block/spruce_planks");
        CoffinBlock.COFFIN_BLOCKS.values().forEach(coffin -> getVariantBuilder(coffin).forAllStates(state -> ConfiguredModel.builder().modelFile(builder1).build()));
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
        simpleBlock(ModBlocks.DARK_SPRUCE_LEAVES.get(), models().getExistingFile(mcLoc("block/oak_leaves")));
        simpleBlock(ModBlocks.DARK_SPRUCE_SAPLING.get(), dark_spruce_sapling);
        simpleBlock(ModBlocks.CURSED_SPRUCE_SAPLING.get(), cursed_spruce_sapling);


        stairsBlock(ModBlocks.CASTLE_STAIRS_DARK_STONE.get(), modLoc("block/castle_block_dark_stone"));
        stairsBlock(ModBlocks.CASTLE_STAIRS_DARK_BRICK.get(), modLoc("block/castle_block_dark_brick"));
        stairsBlock(ModBlocks.CASTLE_STAIRS_PURPLE_BRICK.get(), modLoc("block/castle_block_purple_brick"));

        slabBlock(ModBlocks.CASTLE_SLAB_DARK_BRICK.get(), modLoc("block/castle_block_dark_brick"), modLoc("block/castle_block_dark_brick"));
        slabBlock(ModBlocks.CASTLE_SLAB_DARK_STONE.get(), modLoc("block/castle_block_dark_stone"), modLoc("block/castle_block_dark_stone"));
        slabBlock(ModBlocks.CASTLE_SLAB_PURPLE_BRICK.get(), modLoc("block/castle_block_purple_brick"), modLoc("block/castle_block_purple_brick"));


        //variants

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

        getVariantBuilder(ModBlocks.MED_CHAIR.get()).forAllStates(blockState -> {
            if (blockState.getValue(MedChairBlock.PART) == MedChairBlock.EnumPart.BOTTOM) {
                return ConfiguredModel.builder().modelFile(models().getExistingFile(modLoc("block/medchairbase"))).rotationY(((int) blockState.getValue(MedChairBlock.FACING).toYRot() + 180) % 360).build();
            } else {
                return ConfiguredModel.builder().modelFile(models().getExistingFile(modLoc("block/medchairhead"))).rotationY(((int) blockState.getValue(MedChairBlock.FACING).toYRot() + 180) % 360).build();
            }
        });

        //multiparts
        BlockModelBuilder fire_side_alt0 = models().withExistingParent("fire_side_alt0", modLoc("block/fire_side_alt")).renderType(cutout).texture("particle", mcLoc("block/fire_0")).texture("fire", mcLoc("block/fire_0"));
        BlockModelBuilder fire_side_alt1 = models().withExistingParent("fire_side_alt1", modLoc("block/fire_side_alt")).renderType(cutout).texture("particle", mcLoc("block/fire_1")).texture("fire", mcLoc("block/fire_1"));
        BlockModelBuilder fire_side0 = models().withExistingParent("fire_side0", modLoc("block/fire_side")).renderType(cutout).texture("particle", mcLoc("block/fire_0")).texture("fire", mcLoc("block/fire_0"));
        BlockModelBuilder fire_side1 = models().withExistingParent("fire_side1", modLoc("block/fire_side")).renderType(cutout).texture("particle", mcLoc("block/fire_1")).texture("fire", mcLoc("block/fire_1"));
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

        getMultipartBuilder(ModBlocks.CURSED_GRASS.get())
                .part().modelFile(models().cubeBottomTop("vampirism:cursed_grass", modLoc("block/cursed_grass_side"), modLoc("block/cursed_earth"), modLoc("block/cursed_grass_top"))).addModel().end()
                .part().modelFile(models().cubeBottomTop("vampirism:cursed_grass_snowy", modLoc("block/cursed_grass_side_snowy"), modLoc("block/cursed_earth"), modLoc("block/cursed_grass_top"))).addModel().condition(BlockStateProperties.SNOWY, true).end();

        simpleBlock(ModBlocks.CURSED_ROOTS.get(), models().cross("cursed_roots", modLoc("block/cursed_roots")).renderType(cutout));
        simpleBlock(ModBlocks.POTTED_CURSED_ROOTS.get(), models().withExistingParent("vampirism:block/potted_cursed_roots", "minecraft:block/flower_pot_cross").texture("plant", "vampirism:block/cursed_roots"));

        trapdoorBlock(ModBlocks.DARK_SPRUCE_TRAPDOOR.get(), new ResourceLocation(REFERENCE.MODID, "block/dark_spruce_trapdoor"), true);
        trapdoorBlock(ModBlocks.CURSED_SPRUCE_TRAPDOOR.get(), new ResourceLocation(REFERENCE.MODID, "block/cursed_spruce_trapdoor"), true);

        doorBlock(ModBlocks.DARK_SPRUCE_DOOR.get(), new ResourceLocation(REFERENCE.MODID, "block/dark_spruce_door_bottom"), new ResourceLocation(REFERENCE.MODID, "block/dark_spruce_door_top"));
        doorBlock(ModBlocks.CURSED_SPRUCE_DOOR.get(), new ResourceLocation(REFERENCE.MODID, "block/cursed_spruce_door_bottom"), new ResourceLocation(REFERENCE.MODID, "block/cursed_spruce_door_top"));

        createWoodStates();
        createCursedBark();

        horizontalBlock(ModBlocks.VAMPIRE_RACK.get(), models().getExistingFile(modLoc("block/vampire_rack")));
        horizontalBlock(ModBlocks.THRONE.get(), models().getExistingFile(modLoc("block/throne")));

        for (DyeColor dye : DyeColor.values()) {
            models().withExistingParent(REFERENCE.MODID + ":block/coffin/coffin_" + dye.getName(), "vampirism:block/coffin").texture("0", "vampirism:block/coffin/coffin_" + dye.getName());
            models().withExistingParent(REFERENCE.MODID + ":block/coffin/coffin_bottom_" + dye.getName(), "vampirism:block/coffin_bottom").texture("0", "vampirism:block/coffin/coffin_" + dye.getName());
            models().withExistingParent(REFERENCE.MODID + ":block/coffin/coffin_top_" + dye.getName(), "vampirism:block/coffin_top").texture("0", "vampirism:block/coffin/coffin_" + dye.getName());
        }

        MultiPartBlockStateBuilder alchemy_table = getMultipartBuilder(ModBlocks.ALCHEMY_TABLE.get());
        applyHorizontalModel(alchemy_table, models().getExistingFile(modLoc("block/alchemy_table/alchemy_table")));
        applyHorizontalModel(alchemy_table, models().getExistingFile(modLoc("block/alchemy_table/alchemy_table_input_0")), partBuilder -> partBuilder.condition(AlchemyTableBlock.HAS_BOTTLE_INPUT_0, true));
        applyHorizontalModel(alchemy_table, models().getExistingFile(modLoc("block/alchemy_table/alchemy_table_input_1")), partBuilder -> partBuilder.condition(AlchemyTableBlock.HAS_BOTTLE_INPUT_1, true));
        applyHorizontalModel(alchemy_table, models().getExistingFile(modLoc("block/alchemy_table/alchemy_table_output_0")), partBuilder -> partBuilder.condition(AlchemyTableBlock.HAS_BOTTLE_OUTPUT_0, true));
        applyHorizontalModel(alchemy_table, models().getExistingFile(modLoc("block/alchemy_table/alchemy_table_output_1")), partBuilder -> partBuilder.condition(AlchemyTableBlock.HAS_BOTTLE_OUTPUT_1, true));

        var cursedEarthPath = models().getBuilder(ModBlocks.CURSED_EARTH_PATH.getId().getPath())
                .parent(models().getExistingFile(mcLoc("block/block")))
                .texture("particle", modLoc("block/cursed_earth_path_side"))
                .texture("side", modLoc("block/cursed_earth_path_side"))
                .texture("top", modLoc("block/cursed_earth_path_top"))
                .texture("bottom", modLoc("block/cursed_earth_path_top"))
                .element()
                .from(0,0,0).to(16,15,16)
                .allFaces((direction, builder) -> {
                    switch (direction) {
                        case UP -> builder.uvs(0, 0, 16, 16).texture("#top").end();
                        case DOWN -> builder.uvs(0, 0, 16, 16).texture("#bottom").end();
                        default -> builder.uvs(0, 1, 16, 16).texture("#side").end();
                    }
                })
                .end()
                .renderType(cutout);
        simpleBlock(ModBlocks.CURSED_EARTH_PATH.get(), cursedEarthPath);
    }

    private void createWoodStates() {
        simpleBlock(ModBlocks.DARK_SPRUCE_PLANKS.get());
        simpleBlock(ModBlocks.CURSED_SPRUCE_PLANKS.get());

        stairsBlock(ModBlocks.DARK_SPRUCE_STAIRS.get(), blockTexture(ModBlocks.DARK_SPRUCE_PLANKS.get()));
        stairsBlock(ModBlocks.CURSED_SPRUCE_STAIRS.get(), blockTexture(ModBlocks.CURSED_SPRUCE_PLANKS.get()));
        slabBlock(ModBlocks.DARK_SPRUCE_SLAB.get(), blockTexture(ModBlocks.DARK_SPRUCE_PLANKS.get()), blockTexture(ModBlocks.DARK_SPRUCE_PLANKS.get()));
        slabBlock(ModBlocks.CURSED_SPRUCE_SLAB.get(), blockTexture(ModBlocks.CURSED_SPRUCE_PLANKS.get()), blockTexture(ModBlocks.CURSED_SPRUCE_PLANKS.get()));

        fenceBlock(ModBlocks.DARK_SPRUCE_FENCE.get(), blockTexture(ModBlocks.DARK_SPRUCE_PLANKS.get()));
        fenceBlock(ModBlocks.CURSED_SPRUCE_FENCE.get(), blockTexture(ModBlocks.CURSED_SPRUCE_PLANKS.get()));
        models().withExistingParent("dark_spruce_fence_inventory", new ResourceLocation("block/fence_inventory")).texture("texture", "block/dark_spruce_planks");
        models().withExistingParent("cursed_spruce_fence_inventory", new ResourceLocation("block/fence_inventory")).texture("texture", "block/cursed_spruce_planks");
        fenceGateBlock(ModBlocks.DARK_SPRUCE_FENCE_GATE.get(), blockTexture(ModBlocks.DARK_SPRUCE_PLANKS.get()));
        fenceGateBlock(ModBlocks.CURSED_SPRUCE_FENCE_GATE.get(), blockTexture(ModBlocks.CURSED_SPRUCE_PLANKS.get()));

        logBlock(ModBlocks.DARK_SPRUCE_LOG.get());
        logBlock(ModBlocks.CURSED_SPRUCE_LOG.get());
        axisBlock(ModBlocks.CURSED_SPRUCE_LOG_CURED.get(), blockTexture(ModBlocks.CURSED_SPRUCE_LOG.get()), UtilLib.amend(blockTexture(ModBlocks.CURSED_SPRUCE_LOG.get()), "_top"));
        axisBlock(ModBlocks.DARK_SPRUCE_WOOD.get(), blockTexture(ModBlocks.DARK_SPRUCE_LOG.get()), blockTexture(ModBlocks.DARK_SPRUCE_LOG.get()));
        axisBlock(ModBlocks.CURSED_SPRUCE_WOOD.get(), blockTexture(ModBlocks.CURSED_SPRUCE_LOG.get()), blockTexture(ModBlocks.CURSED_SPRUCE_LOG.get()));
        axisBlock(ModBlocks.CURSED_SPRUCE_WOOD_CURED.get(), blockTexture(ModBlocks.CURSED_SPRUCE_LOG.get()), blockTexture(ModBlocks.CURSED_SPRUCE_LOG.get()));
        logBlock(ModBlocks.STRIPPED_DARK_SPRUCE_LOG.get());
        logBlock(ModBlocks.STRIPPED_CURSED_SPRUCE_LOG.get());
        axisBlock(ModBlocks.STRIPPED_DARK_SPRUCE_WOOD.get(), blockTexture(ModBlocks.STRIPPED_DARK_SPRUCE_LOG.get()), blockTexture(ModBlocks.STRIPPED_DARK_SPRUCE_LOG.get()));
        axisBlock(ModBlocks.STRIPPED_CURSED_SPRUCE_WOOD.get(), blockTexture(ModBlocks.STRIPPED_CURSED_SPRUCE_LOG.get()), blockTexture(ModBlocks.STRIPPED_CURSED_SPRUCE_LOG.get()));

        button(ModBlocks.DARK_SPRUCE_BUTTON.get(), blockTexture(ModBlocks.DARK_SPRUCE_PLANKS.get()));
        button(ModBlocks.CURSED_SPRUCE_BUTTON.get(), blockTexture(ModBlocks.CURSED_SPRUCE_PLANKS.get()));

        pressurePlate(ModBlocks.DARK_SPRUCE_PRESSURE_PLACE.get(), blockTexture(ModBlocks.DARK_SPRUCE_PLANKS.get()));
        pressurePlate(ModBlocks.CURSED_SPRUCE_PRESSURE_PLACE.get(), blockTexture(ModBlocks.CURSED_SPRUCE_PLANKS.get()));

        simpleBlock(ModBlocks.DARK_SPRUCE_WALL_SIGN.get(), models().getBuilder("vampirism:dark_spruce_wall_sign").texture("particle", "vampirism:block/dark_spruce_planks"));
        simpleBlock(ModBlocks.CURSED_SPRUCE_WALL_SIGN.get(), models().getBuilder("vampirism:cursed_spruce_wall_sign").texture("particle", "vampirism:block/cursed_spruce_planks"));
        simpleBlock(ModBlocks.DARK_SPRUCE_SIGN.get(), models().getBuilder("vampirism:dark_spruce_sign").texture("particle", "vampirism:block/dark_spruce_planks"));
        simpleBlock(ModBlocks.CURSED_SPRUCE_SIGN.get(), models().getBuilder("vampirism:cursed_spruce_sign").texture("particle", "vampirism:block/cursed_spruce_planks"));

        simpleBlock(ModBlocks.DARK_SPRUCE_HANGING_SIGN.get(), models().getBuilder("vampirism:dark_spruce_hanging_sign").texture("particle", "vampirism:block/dark_spruce_planks"));
        simpleBlock(ModBlocks.DARK_SPRUCE_WALL_HANGING_SIGN.get(), models().getBuilder("vampirism:dark_spruce_wall_hanging_sign").texture("particle", "vampirism:block/dark_spruce_planks"));
        simpleBlock(ModBlocks.CURSED_SPRUCE_HANGING_SIGN.get(), models().getBuilder("vampirism:cursed_spruce_hanging_sign").texture("particle", "vampirism:block/cursed_spruce_planks"));
        simpleBlock(ModBlocks.CURSED_SPRUCE_WALL_HANGING_SIGN.get(), models().getBuilder("vampirism:cursed_spruce_wall_hanging_sign").texture("particle", "vampirism:block/cursed_spruce_planks"));
    }

    private void createCursedBark() {
        ModelFile side = models().getExistingFile(new ResourceLocation(REFERENCE.MODID, "cursed_bark_side"));
        ModelFile side2 = models().getExistingFile(new ResourceLocation(REFERENCE.MODID, "cursed_bark_side_2"));
        MultiPartBlockStateBuilder builder = getMultipartBuilder(ModBlocks.DIRECT_CURSED_BARK.get())
                .part().modelFile(side).rotationY(90).addModel().condition(DirectCursedBarkBlock.EAST_TYPE, DirectCursedBarkBlock.Type.VERTICAL).end()
                .part().modelFile(side).addModel().condition(DirectCursedBarkBlock.NORTH_TYPE, DirectCursedBarkBlock.Type.VERTICAL).end()
                .part().modelFile(side).rotationY(270).addModel().condition(DirectCursedBarkBlock.WEST_TYPE, DirectCursedBarkBlock.Type.VERTICAL).end()
                .part().modelFile(side).rotationY(180).addModel().condition(DirectCursedBarkBlock.SOUTH_TYPE, DirectCursedBarkBlock.Type.VERTICAL).end()
                .part().modelFile(side).rotationX(270).addModel().condition(DirectCursedBarkBlock.UP_TYPE, DirectCursedBarkBlock.Type.VERTICAL).end()
                .part().modelFile(side).rotationX(90).addModel().condition(DirectCursedBarkBlock.DOWN_TYPE, DirectCursedBarkBlock.Type.VERTICAL).end()

                .part().modelFile(side2).rotationY(90).addModel().condition(DirectCursedBarkBlock.EAST_TYPE, DirectCursedBarkBlock.Type.HORIZONTAL).end()
                .part().modelFile(side2).rotationX(180).rotationY(180).addModel().condition(DirectCursedBarkBlock.NORTH_TYPE, DirectCursedBarkBlock.Type.HORIZONTAL).end()
                .part().modelFile(side2).rotationY(270).addModel().condition(DirectCursedBarkBlock.WEST_TYPE, DirectCursedBarkBlock.Type.HORIZONTAL).end()
                .part().modelFile(side2).rotationY(180).addModel().condition(DirectCursedBarkBlock.SOUTH_TYPE, DirectCursedBarkBlock.Type.HORIZONTAL).end()
                .part().modelFile(side2).rotationX(270).addModel().condition(DirectCursedBarkBlock.UP_TYPE, DirectCursedBarkBlock.Type.HORIZONTAL).end()
                .part().modelFile(side2).rotationX(90).rotationY(180).addModel().condition(DirectCursedBarkBlock.DOWN_TYPE, DirectCursedBarkBlock.Type.HORIZONTAL).end()
                ;
        simpleBlock(ModBlocks.DIAGONAL_CURSED_BARK.get(), models().getBuilder("vampirism:cursed_bark_empty"));
    }

    private void button(Block block, @NotNull ResourceLocation texture) {
        ResourceLocation id = RegUtil.id(block);
        ModelFile button = models().withExistingParent("block/" + id.getPath(), new ResourceLocation("block/button")).texture("texture", texture.getPath());
        ModelFile button_pressed = models().withExistingParent("block/" + id.getPath() + "_pressed", new ResourceLocation("block/button_pressed")).texture("texture", texture.getPath());
        ModelFile button_inventory = models().withExistingParent("block/" + id.getPath() + "_inventory", new ResourceLocation("block/button_inventory")).texture("texture", texture.getPath());
        getVariantBuilder(block)
                .partialState().with(ButtonBlock.FACE, AttachFace.CEILING).with(ButtonBlock.FACING, Direction.EAST).with(ButtonBlock.POWERED, false).modelForState().modelFile(button).rotationY(270).rotationX(180).addModel()
                .partialState().with(ButtonBlock.FACE, AttachFace.CEILING).with(ButtonBlock.FACING, Direction.EAST).with(ButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).rotationY(270).rotationX(180).addModel()
                .partialState().with(ButtonBlock.FACE, AttachFace.CEILING).with(ButtonBlock.FACING, Direction.NORTH).with(ButtonBlock.POWERED, false).modelForState().modelFile(button).rotationY(180).rotationX(180).addModel()
                .partialState().with(ButtonBlock.FACE, AttachFace.CEILING).with(ButtonBlock.FACING, Direction.NORTH).with(ButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).rotationY(180).rotationX(180).addModel()
                .partialState().with(ButtonBlock.FACE, AttachFace.CEILING).with(ButtonBlock.FACING, Direction.SOUTH).with(ButtonBlock.POWERED, false).modelForState().modelFile(button).rotationX(180).addModel()
                .partialState().with(ButtonBlock.FACE, AttachFace.CEILING).with(ButtonBlock.FACING, Direction.SOUTH).with(ButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).rotationX(180).addModel()
                .partialState().with(ButtonBlock.FACE, AttachFace.CEILING).with(ButtonBlock.FACING, Direction.WEST).with(ButtonBlock.POWERED, false).modelForState().modelFile(button).rotationY(90).rotationX(180).addModel()
                .partialState().with(ButtonBlock.FACE, AttachFace.CEILING).with(ButtonBlock.FACING, Direction.WEST).with(ButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).rotationX(90).rotationX(180).addModel()

                .partialState().with(ButtonBlock.FACE, AttachFace.FLOOR).with(ButtonBlock.FACING, Direction.EAST).with(ButtonBlock.POWERED, false).modelForState().modelFile(button).rotationY(90).addModel()
                .partialState().with(ButtonBlock.FACE, AttachFace.FLOOR).with(ButtonBlock.FACING, Direction.EAST).with(ButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).rotationY(90).addModel()
                .partialState().with(ButtonBlock.FACE, AttachFace.FLOOR).with(ButtonBlock.FACING, Direction.NORTH).with(ButtonBlock.POWERED, false).modelForState().modelFile(button).addModel()
                .partialState().with(ButtonBlock.FACE, AttachFace.FLOOR).with(ButtonBlock.FACING, Direction.NORTH).with(ButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).addModel()
                .partialState().with(ButtonBlock.FACE, AttachFace.FLOOR).with(ButtonBlock.FACING, Direction.SOUTH).with(ButtonBlock.POWERED, false).modelForState().modelFile(button).rotationY(180).addModel()
                .partialState().with(ButtonBlock.FACE, AttachFace.FLOOR).with(ButtonBlock.FACING, Direction.SOUTH).with(ButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).rotationY(180).addModel()
                .partialState().with(ButtonBlock.FACE, AttachFace.FLOOR).with(ButtonBlock.FACING, Direction.WEST).with(ButtonBlock.POWERED, false).modelForState().modelFile(button).rotationY(270).addModel()
                .partialState().with(ButtonBlock.FACE, AttachFace.FLOOR).with(ButtonBlock.FACING, Direction.WEST).with(ButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).rotationX(270).addModel()

                .partialState().with(ButtonBlock.FACE, AttachFace.WALL).with(ButtonBlock.FACING, Direction.EAST).with(ButtonBlock.POWERED, false).modelForState().modelFile(button).rotationY(90).rotationX(90).uvLock(true).addModel()
                .partialState().with(ButtonBlock.FACE, AttachFace.WALL).with(ButtonBlock.FACING, Direction.EAST).with(ButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).rotationY(90).rotationX(90).uvLock(true).addModel()
                .partialState().with(ButtonBlock.FACE, AttachFace.WALL).with(ButtonBlock.FACING, Direction.NORTH).with(ButtonBlock.POWERED, false).modelForState().modelFile(button).rotationX(90).uvLock(true).addModel()
                .partialState().with(ButtonBlock.FACE, AttachFace.WALL).with(ButtonBlock.FACING, Direction.NORTH).with(ButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).rotationX(90).uvLock(true).addModel()
                .partialState().with(ButtonBlock.FACE, AttachFace.WALL).with(ButtonBlock.FACING, Direction.SOUTH).with(ButtonBlock.POWERED, false).modelForState().modelFile(button).rotationY(180).rotationX(90).uvLock(true).addModel()
                .partialState().with(ButtonBlock.FACE, AttachFace.WALL).with(ButtonBlock.FACING, Direction.SOUTH).with(ButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).rotationY(180).rotationX(90).uvLock(true).addModel()
                .partialState().with(ButtonBlock.FACE, AttachFace.WALL).with(ButtonBlock.FACING, Direction.WEST).with(ButtonBlock.POWERED, false).modelForState().modelFile(button).rotationY(270).rotationX(90).uvLock(true).addModel()
                .partialState().with(ButtonBlock.FACE, AttachFace.WALL).with(ButtonBlock.FACING, Direction.WEST).with(ButtonBlock.POWERED, true).modelForState().modelFile(button_pressed).rotationY(270).rotationX(90).uvLock(true).addModel()
        ;
    }

    private void pressurePlate(Block block, @NotNull ResourceLocation texture) {
        ResourceLocation id = RegUtil.id(block);
        ModelFile pressure_plate = models().withExistingParent("block/" + id.getPath(), new ResourceLocation("block/pressure_plate_up")).texture("texture", texture.getPath());
        ModelFile pressure_plate_down = models().withExistingParent("block/" + id.getPath() + "_down", new ResourceLocation("block/pressure_plate_down")).texture("texture", texture.getPath());

        getVariantBuilder(block)
                .partialState().with(PressurePlateBlock.POWERED, false).modelForState().modelFile(pressure_plate).addModel()
                .partialState().with(PressurePlateBlock.POWERED, true).modelForState().modelFile(pressure_plate_down).addModel();
    }

    private @NotNull MultiPartBlockStateBuilder applyHorizontalModel(@NotNull MultiPartBlockStateBuilder builder, ModelFile file) {
        return applyHorizontalModel(builder, file, partBuilder -> {
        });
    }

    private @NotNull MultiPartBlockStateBuilder applyHorizontalModel(@NotNull MultiPartBlockStateBuilder builder, ModelFile file, @NotNull Consumer<MultiPartBlockStateBuilder.PartBuilder> conditions) {
        MultiPartBlockStateBuilder.PartBuilder partBuilder = builder.part().modelFile(file).rotationY(0).addModel().condition(HunterTableBlock.FACING, Direction.NORTH);
        conditions.accept(partBuilder);
        partBuilder.end();
        partBuilder = builder.part().modelFile(file).rotationY(90).addModel().condition(HunterTableBlock.FACING, Direction.EAST);
        conditions.accept(partBuilder);
        partBuilder.end();
        partBuilder = builder.part().modelFile(file).rotationY(180).addModel().condition(HunterTableBlock.FACING, Direction.SOUTH);
        conditions.accept(partBuilder);
        partBuilder.end();
        partBuilder = builder.part().modelFile(file).rotationY(270).addModel().condition(HunterTableBlock.FACING, Direction.WEST);
        conditions.accept(partBuilder);
        partBuilder.end();

        return builder;
    }


    private @NotNull MultiPartBlockStateBuilder getHorizontalMultiPartBlockStateBuilder(Block block, ModelFile file, @NotNull Consumer<MultiPartBlockStateBuilder.PartBuilder> conditions) {
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block);
        MultiPartBlockStateBuilder.PartBuilder partBuilder = builder.part().modelFile(file).rotationY(0).addModel().condition(HunterTableBlock.FACING, Direction.NORTH);
        conditions.accept(partBuilder);
        partBuilder.end();
        partBuilder = builder.part().modelFile(file).rotationY(90).addModel().condition(HunterTableBlock.FACING, Direction.EAST);
        conditions.accept(partBuilder);
        partBuilder.end();
        partBuilder = builder.part().modelFile(file).rotationY(180).addModel().condition(HunterTableBlock.FACING, Direction.SOUTH);
        conditions.accept(partBuilder);
        partBuilder.end();
        partBuilder = builder.part().modelFile(file).rotationY(270).addModel().condition(HunterTableBlock.FACING, Direction.WEST);
        conditions.accept(partBuilder);
        partBuilder.end();

        return builder;
    }

}
