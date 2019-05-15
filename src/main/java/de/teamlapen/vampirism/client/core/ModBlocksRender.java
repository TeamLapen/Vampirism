package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.client.render.tiles.AltarInfusionTESR;
import de.teamlapen.vampirism.client.render.tiles.CoffinTESR;
import de.teamlapen.vampirism.client.render.tiles.PedestalTESR;
import de.teamlapen.vampirism.client.render.tiles.TotemTESR;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.tileentity.*;

import net.minecraft.client.Minecraft;
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

    static void registerColors() {
        Minecraft.getInstance().getBlockColors().register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 1) {
                return 0x9966FF;
            }
            return 0x8855FF;
        }, ModBlocks.alchemical_fire);
        Minecraft.getInstance().getBlockColors().register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 255) {
                TileEntity tile = (worldIn == null || pos == null) ? null : worldIn.getTileEntity(pos);
                if (tile != null && tile instanceof TileAlchemicalCauldron) {
                    return ((TileAlchemicalCauldron) tile).getLiquidColorClient();
                }
            }
            return 0xFFFFFF;
        }, ModBlocks.alchemical_cauldron);
        Minecraft.getInstance().getBlockColors().register((state, worldIn, pos, tintIndex) -> {
            if (tintIndex == 255) {
                TileEntity tile = (worldIn == null || pos == null) ? null : worldIn.getTileEntity(pos);
                if (tile instanceof TileTotem) {
                    IPlayableFaction f = ((TileTotem) tile).getControllingFaction();
                    if (f != null) return f.getColor();
                }
            }
            return 0xFFFFFF;
        }, ModBlocks.totem_top);
    }

    private static void registerTileRenderer() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileCoffin.class, new CoffinTESR());
        ClientRegistry.bindTileEntitySpecialRenderer(TileAltarInfusion.class, new AltarInfusionTESR());
        ClientRegistry.bindTileEntitySpecialRenderer(TilePedestal.class, new PedestalTESR());
        ClientRegistry.bindTileEntitySpecialRenderer(TileTotem.class, new TotemTESR());
    }


}
