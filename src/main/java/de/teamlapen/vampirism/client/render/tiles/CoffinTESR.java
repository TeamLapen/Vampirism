package de.teamlapen.vampirism.client.render.tiles;

import com.mojang.blaze3d.platform.GlStateManager;

import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.client.model.CoffinModel;
import de.teamlapen.vampirism.tileentity.CoffinTileEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Render the coffin with it's different colors and the lid opening animation
 */
@OnlyIn(Dist.CLIENT)
public class CoffinTESR extends VampirismTESR<CoffinTileEntity> {
    private Logger LOGGER = LogManager.getLogger();

    private final int maxLidPos = 61;
    private final CoffinModel model;
    private final ResourceLocation[] textures = new ResourceLocation[DyeColor.values().length];

    public CoffinTESR() {
        this.model = new CoffinModel();
        for (DyeColor e : DyeColor.values()) {
            textures[e.getId()] = new ResourceLocation(REFERENCE.MODID, "textures/block/coffin/coffin_" + e.getName() + ".png");
        }
    }


    @Override
    public void render(CoffinTileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
        CoffinTileEntity tile = te;
        if (!isHeadSafe(te.getWorld(), te.getPos())) return;

        // Calculate lid position
        boolean occupied = CoffinBlock.isOccupied(te.getWorld(), te.getPos());
        if (!occupied && tile.lidPos > 0)
            tile.lidPos--;
        else if (occupied && tile.lidPos < maxLidPos)
            tile.lidPos++;
        // Logger.i("RendererCoffin", String.format("Rendering at x=%s, yDisplay=%s, z=%s, occupied=%s, lidpos=%s", te.xCoord, te.yCoord, te.zCoord, occupied, tile.lidPos));
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        int color = Math.min(tile.color, 15);
        bindTexture(textures[color]);
        GlStateManager.pushMatrix();
        adjustRotatePivotViaState(te);
        GlStateManager.rotatef(180F, 0.0F, 0.0F, 1.0F);
        model.rotateLid(calcLidAngle(tile.lidPos));
        model.render(0.0625F);
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    private float calcLidAngle(int pos) {
        if (pos == maxLidPos)
            return 0.0F;
        else if (pos == 0)
            return (float) (0.75F * Math.PI);
        return (float) (-Math.pow(1.02, pos) + 1 + 0.75 * Math.PI);
    }

    /**
     * Checks if the coffin part at the given pos is the head of the coffin. Any exception is caught and false is returned
     */
    private boolean isHeadSafe(World world, BlockPos pos) {
        try {
            return CoffinBlock.isHead(world, pos);
        } catch (IllegalArgumentException e) {
            LOGGER.error("CoffinTESR", "Failed to check coffin head at %s caused by wrong blockstate. Block at that pos: %s", pos, world.getBlockState(pos));
        } catch (Exception e) {
            LOGGER.error("CoffinTESR", e, "Failed to check coffin head at %s.", pos);
        }
        return false;
    }
}
