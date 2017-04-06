package de.teamlapen.vampirism.client.core;

import de.teamlapen.lib.lib.util.IInitListener;
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
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLStateEvent;

import javax.annotation.Nullable;

/**
 * Handles all block render registration including TileEntities
 */
public class ModBlocksRender {


    public static void onInitStep(IInitListener.Step step, FMLStateEvent event) {
        switch (step) {
            case PRE_INIT:
                registerRenderer();
                registerTileRenderer();
                break;
            case POST_INIT:
                registerColors();
                break;

        }

    }

    private static void registerColors() {
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new IBlockColor() {
            @Override
            public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
                if (tintIndex == 1) {
                    return 0x9966FF;
                }
                return 0x8855FF;
            }
        }, ModBlocks.alchemicalFire);
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new IBlockColor() {
            @Override
            public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
                if (tintIndex == 255) {
                    TileEntity tile = (worldIn == null || pos == null) ? null : worldIn.getTileEntity(pos);
                    if (tile != null && tile instanceof TileAlchemicalCauldron) {
                        return ((TileAlchemicalCauldron) tile).getLiquidColorClient();
                    }
                }
                return 0xFFFFFF;
            }
        }, ModBlocks.alchemicalCauldron);
    }

    private static void registerRenderer() {
        InventoryRenderHelper renderHelper = new InventoryRenderHelper(REFERENCE.MODID);
        renderHelper.registerRenderAllMeta(Item.getItemFromBlock(ModBlocks.castleBlock), BlockCastleBlock.EnumType.values());
        renderHelper.registerRenderAllMeta(Item.getItemFromBlock(ModBlocks.altarPillar), BlockAltarPillar.EnumPillarType.values());
        renderHelper.registerRender(ModBlocks.altarTip);
        renderHelper.registerRender(ModBlocks.altarInfusion);
        renderHelper.registerRender(ModBlocks.cursedEarth);
        renderHelper.registerRender(ModBlocks.bloodContainer);
        renderHelper.registerRender(ModBlocks.altarInspiration);
        renderHelper.registerRender(ModBlocks.firePlace);
        renderHelper.registerRender(ModBlocks.bloodPotionTable);
        renderHelper.registerRender(ModBlocks.sunscreenBeacon);
        renderHelper.registerRenderAllMeta(Item.getItemFromBlock(ModBlocks.hunterTable), EnumFacing.HORIZONTALS);
        renderHelper.registerRenderAllMeta(Item.getItemFromBlock(ModBlocks.vampirismFlower), VampirismFlower.EnumFlowerType.values());
        renderHelper.registerRenderAllMeta(Item.getItemFromBlock(ModBlocks.churchAltar), EnumFacing.HORIZONTALS);
        renderHelper.registerRender(Item.getItemFromBlock(ModBlocks.weaponTable), "inventory");
        renderHelper.registerRender(ModBlocks.alchemicalCauldron);

        for (EnumFacing f : EnumFacing.HORIZONTALS) {
            for (BlockGarlicBeacon.Type t : BlockGarlicBeacon.Type.values()) {
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(ModBlocks.garlicBeacon), f.getHorizontalIndex() | t.getId() << 2, new ModelResourceLocation(new ResourceLocation(REFERENCE.MODID, BlockGarlicBeacon.regName), "facing=" + f.getName() + ",type=" + t.getName()));
            }
        }

        ModelLoader.setCustomStateMapper(ModBlocks.weaponTable, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(new ResourceLocation(REFERENCE.MODID, BlockWeaponTable.regName), "normal");
            }
        });
        ModelBakery.registerItemVariants(Item.getItemFromBlock(ModBlocks.fluidBlood));
        ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(ModBlocks.fluidBlood), new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return new ModelResourceLocation(new ResourceLocation(REFERENCE.MODID, "fluids"), "blood");
            }
        });
        ModelLoader.setCustomStateMapper(ModBlocks.fluidBlood, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(new ResourceLocation(REFERENCE.MODID, "fluids"), "blood");
            }
        });
        ModelLoader.setCustomStateMapper(ModBlocks.coffin, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(new ResourceLocation(REFERENCE.MODID, "blockCoffin"), "normal");
            }
        });
        ModelLoader.setCustomStateMapper(ModBlocks.tentMain, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(Block.REGISTRY.getNameForObject(ModBlocks.tent), this.getPropertyString(state.getProperties()));

            }
        });
        ModelLoader.setCustomStateMapper(ModBlocks.alchemicalFire, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return new ModelResourceLocation(new ResourceLocation(REFERENCE.MODID, "alchemicalFire"), "normal");
            }
        });


    }

    private static void registerTileRenderer() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileCoffin.class, new CoffinTESR());
        ClientRegistry.bindTileEntitySpecialRenderer(TileAltarInfusion.class, new AltarInfusionTESR());
    }


}
