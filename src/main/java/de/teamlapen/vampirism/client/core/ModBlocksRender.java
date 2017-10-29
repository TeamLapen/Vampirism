package de.teamlapen.vampirism.client.core;

import de.teamlapen.lib.lib.util.InventoryRenderHelper;
import de.teamlapen.vampirism.blocks.*;
import de.teamlapen.vampirism.client.render.tiles.AltarInfusionTESR;
import de.teamlapen.vampirism.client.render.tiles.CoffinTESR;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.tileentity.TileAlchemicalCauldron;
import de.teamlapen.vampirism.tileentity.TileAltarInfusion;
import de.teamlapen.vampirism.tileentity.TileCoffin;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Handles all block render registration including TileEntities
 */
@SideOnly(Side.CLIENT)
public class ModBlocksRender {


    public static void register() {
        registerRenderer();
        registerTileRenderer();
    }

    static void registerColors() {
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 1) {
                return 0x9966FF;
            }
            return 0x8855FF;
        }, ModBlocks.alchemical_fire);
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 255) {
                TileEntity tile = (worldIn == null || pos == null) ? null : worldIn.getTileEntity(pos);
                if (tile != null && tile instanceof TileAlchemicalCauldron) {
                    return ((TileAlchemicalCauldron) tile).getLiquidColorClient();
                }
            }
            return 0xFFFFFF;
        }, ModBlocks.alchemical_cauldron);
    }

    private static void registerRenderer() {
        InventoryRenderHelper renderHelper = new InventoryRenderHelper(REFERENCE.MODID);
        renderHelper.registerRenderAllMeta(Item.getItemFromBlock(ModBlocks.castle_block), BlockCastleBlock.EnumType.values());
        renderHelper.registerRenderAllMeta(Item.getItemFromBlock(ModBlocks.altar_pillar), BlockAltarPillar.EnumPillarType.values());
        renderHelper.registerRender(ModBlocks.altar_tip);
        renderHelper.registerRender(ModBlocks.altar_infusion);
        renderHelper.registerRender(ModBlocks.cursed_earth);
        renderHelper.registerRender(ModBlocks.blood_container);
        renderHelper.registerRender(ModBlocks.altar_inspiration);
        renderHelper.registerRender(ModBlocks.fire_place);
        renderHelper.registerRender(ModBlocks.blood_potion_table);
        renderHelper.registerRender(ModBlocks.sunscreen_beacon);
        renderHelper.registerRender(ModBlocks.castle_stairs_dark);
        renderHelper.registerRender(ModBlocks.castle_stairs_dark_stone);
        renderHelper.registerRender(ModBlocks.castle_stairs_purple);
        renderHelper.registerRenderAllMeta(Item.getItemFromBlock(ModBlocks.castle_slab), BlockCastleSlab.EnumType.values());
        renderHelper.registerRenderAllMeta(Item.getItemFromBlock(ModBlocks.hunter_table), EnumFacing.HORIZONTALS);
        renderHelper.registerRenderAllMeta(Item.getItemFromBlock(ModBlocks.vampirism_flower), VampirismFlower.EnumFlowerType.values());
        renderHelper.registerRenderAllMeta(Item.getItemFromBlock(ModBlocks.church_altar), EnumFacing.HORIZONTALS);
        renderHelper.registerRender(Item.getItemFromBlock(ModBlocks.weapon_table), "inventory");
        renderHelper.registerRender(ModBlocks.alchemical_cauldron);


        for (EnumFacing f : EnumFacing.HORIZONTALS) {
            for (BlockGarlicBeacon.Type t : BlockGarlicBeacon.Type.values()) {
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.garlic_beacon), f.getHorizontalIndex() | t.getId() << 2, new ModelResourceLocation(new ResourceLocation(REFERENCE.MODID, BlockGarlicBeacon.regName), "facing=" + f.getName() + ",type=" + t.getName()));
            }
        }

        ModelLoader.setCustomStateMapper(ModBlocks.weapon_table, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(new ResourceLocation(REFERENCE.MODID, BlockWeaponTable.regName), "normal");
            }
        });
        ModelBakery.registerItemVariants(Item.getItemFromBlock(ModBlocks.block_blood_fluid));
        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(ModBlocks.block_blood_fluid), stack -> new ModelResourceLocation(new ResourceLocation(REFERENCE.MODID, "fluids"), "blood"));
        ModelLoader.setCustomStateMapper(ModBlocks.block_blood_fluid, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(new ResourceLocation(REFERENCE.MODID, "fluids"), "blood");
            }
        });
        ModelLoader.setCustomStateMapper(ModBlocks.block_coffin, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(new ResourceLocation(REFERENCE.MODID, "block_coffin"), "normal");
            }
        });
        ModelLoader.setCustomStateMapper(ModBlocks.tent_main, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(Block.REGISTRY.getNameForObject(ModBlocks.tent), this.getPropertyString(state.getProperties()));

            }
        });
        ModelLoader.setCustomStateMapper(ModBlocks.alchemical_fire, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(new ResourceLocation(REFERENCE.MODID, "alchemical_fire"), "normal");
            }
        });


    }

    private static void registerTileRenderer() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileCoffin.class, new CoffinTESR());
        ClientRegistry.bindTileEntitySpecialRenderer(TileAltarInfusion.class, new AltarInfusionTESR());
    }


}
