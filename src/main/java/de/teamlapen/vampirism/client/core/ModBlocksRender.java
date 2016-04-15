package de.teamlapen.vampirism.client.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.lib.lib.util.InventoryRenderHelper;
import de.teamlapen.vampirism.blocks.BlockAltarPillar;
import de.teamlapen.vampirism.blocks.BlockCastleBlock;
import de.teamlapen.vampirism.blocks.VampirismFlower;
import de.teamlapen.vampirism.client.render.tiles.AltarInfusionTESR;
import de.teamlapen.vampirism.client.render.tiles.CoffinTESR;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.tileentity.TileAltarInfusion;
import de.teamlapen.vampirism.tileentity.TileCoffin;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLStateEvent;

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
        }

    }

    private static void registerRenderer() {
        InventoryRenderHelper renderHelper = new InventoryRenderHelper(REFERENCE.MODID);
        renderHelper.registerRenderAllMeta(Item.getItemFromBlock(ModBlocks.castleBlock), BlockCastleBlock.types);
        renderHelper.registerRenderAllMeta(Item.getItemFromBlock(ModBlocks.altarPillar), BlockAltarPillar.EnumPillarType.values());
        renderHelper.registerRender(ModBlocks.altarTip);
        renderHelper.registerRender(ModBlocks.altarInfusion);
        renderHelper.registerRender(ModBlocks.cursedEarth);
        renderHelper.registerRender(ModBlocks.bloodContainer);
        renderHelper.registerRender(ModBlocks.altarInspiration);
        renderHelper.registerRenderAllMeta(Item.getItemFromBlock(ModBlocks.hunterTable), EnumFacing.HORIZONTALS);
        renderHelper.registerRenderAllMeta(Item.getItemFromBlock(ModBlocks.vampirismFlower), VampirismFlower.EnumFlowerType.values());
        renderHelper.registerRenderAllMeta(Item.getItemFromBlock(ModBlocks.churchAltar), EnumFacing.HORIZONTALS);
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

    }

    private static void registerTileRenderer() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileCoffin.class, new CoffinTESR());
        ClientRegistry.bindTileEntitySpecialRenderer(TileAltarInfusion.class, new AltarInfusionTESR());
    }


}
