package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.blocks.TotemTopBlock;
import de.teamlapen.vampirism.client.render.tiles.AltarInfusionTESR;
import de.teamlapen.vampirism.client.render.tiles.CoffinTESR;
import de.teamlapen.vampirism.client.render.tiles.PedestalTESR;
import de.teamlapen.vampirism.client.render.tiles.TotemTESR;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.tileentity.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * Handles all block render registration including TileEntities
 */
@OnlyIn(Dist.CLIENT)
public class ModBlocksRender {


    public static void register() {
        registerTileRenderer();
    }

    public static void registerColors() {
        BlockColors colors = Minecraft.getInstance().getBlockColors();
        colors.register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 1) {
                return 0x9966FF;
            }
            return 0x8855FF;
        }, ModBlocks.alchemical_fire);
        colors.register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 255) {
                TileEntity tile = (worldIn == null || pos == null) ? null : worldIn.getTileEntity(pos);
                if (tile instanceof AlchemicalCauldronTileEntity) {
                    return ((AlchemicalCauldronTileEntity) tile).getLiquidColorClient();
                }
            }
            return 0xFFFFFF;
        }, ModBlocks.alchemical_cauldron);
        colors.register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 255) {
                TileEntity tile = (worldIn == null || pos == null) ? null : worldIn.getTileEntity(pos);
                if (tile instanceof TotemTileEntity) {
                    IFaction f = ((TotemTileEntity) tile).getControllingFaction();
                    if (f != null) return f.getColor().getRGB();
                }
            }
            return 0xFFFFFF;
        }, TotemTopBlock.getBlocks().toArray(new TotemTopBlock[0]));
        colors.register((state, worldIn, pos, tintIndex) -> {
            return 0x1E1F1F;
        }, ModBlocks.vampire_spruce_leaves);
        colors.register((state, worldIn, pos, tintIndex) -> {
            return 0x2e0606;
        }, ModBlocks.bloody_spruce_leaves);
    }

    private static void registerTileRenderer() {
        ClientRegistry.bindTileEntitySpecialRenderer(CoffinTileEntity.class, new CoffinTESR());
        ClientRegistry.bindTileEntitySpecialRenderer(AltarInfusionTileEntity.class, new AltarInfusionTESR());
        ClientRegistry.bindTileEntitySpecialRenderer(PedestalTileEntity.class, new PedestalTESR());
        ClientRegistry.bindTileEntitySpecialRenderer(TotemTileEntity.class, new TotemTESR());
    }


}
