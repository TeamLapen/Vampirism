package de.teamlapen.vampirism.client.render.tiles;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.tileentity.CoffinTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nonnull;

/**
 * Render the coffin with it's different colors and the lid opening animation
 */
@OnlyIn(Dist.CLIENT)
public class CoffinTESR extends VampirismTESR<CoffinTileEntity> {
    private static final Marker COFFIN = new MarkerManager.Log4jMarker("COFFIN");
    private final Logger LOGGER = LogManager.getLogger();

    public CoffinTESR(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(@Nonnull CoffinTileEntity tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
        this.renderBlock(tile, partialTicks, matrixStack, iRenderTypeBuffer, i, i1);
    }

    public void renderBlock(CoffinTileEntity tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
        assert tile.getLevel() != null;
        BlockState state = tile.getBlockState();
        Direction direction = state.getValue(HorizontalBlock.FACING);

        if (!isHeadSafe(tile.getLevel(), tile.getBlockPos())) return;

        matrixStack.pushPose();
        boolean vertical = state.getValue(CoffinBlock.VERTICAL);
        switch (direction){
            case EAST:
                if (vertical) {
                    matrixStack.mulPose(new Quaternion(new Vector3f(0, 0, 1), 90, true));
                    matrixStack.translate(0, -1, 0);
                }
                matrixStack.mulPose(new Quaternion(new Vector3f(0,1,0), 90, true));
                matrixStack.translate(-1,0,-1);
                break;
            case WEST:
                if (vertical) {
                    matrixStack.mulPose(new Quaternion(new Vector3f(0, 0, 1), -90, true));
                    matrixStack.translate(-1, 0, 0);
                }
                matrixStack.mulPose(new Quaternion(new Vector3f(0,1,0), -90, true));
                matrixStack.translate(0,0,-2);
                break;
            case SOUTH:
                if (vertical) {
                    matrixStack.mulPose(new Quaternion(new Vector3f(1, 0, 0), -90, true));
                    matrixStack.translate(0, -1, 0);
                }
                matrixStack.translate(0,0,-1);
                break;
            case NORTH:
                if (vertical) {
                    matrixStack.mulPose(new Quaternion(new Vector3f(1, 0, 0), 90, true));
                    matrixStack.translate(0, 0, -1);
                }
                matrixStack.mulPose(new Quaternion(new Vector3f(0,1,0), 180, true));
                matrixStack.translate(-1,0,-2);
                break;
        }

        IBakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(new ResourceLocation(REFERENCE.MODID, "block/coffin/coffin_bottom_" + tile.color.getName()));
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(matrixStack.last(), iRenderTypeBuffer.getBuffer(RenderTypeLookup.getRenderType(state,false)), state, baseModel, 1,1,1,i, i1, EmptyModelData.INSTANCE);

        matrixStack.pushPose();
        if (vertical) {
            matrixStack.mulPose(new Quaternion(new Vector3f(0,0,1), 80 * tile.lidPos, true));
            matrixStack.translate(0,-0.5 * tile.lidPos,0);
        } else {
            matrixStack.mulPose(new Quaternion(new Vector3f(0, 1, 0), -35 * tile.lidPos, true));
            matrixStack.translate(0, 0, -0.5 * tile.lidPos);
        }

        IBakedModel lidModel = Minecraft.getInstance().getModelManager().getModel( new ResourceLocation(REFERENCE.MODID, "block/coffin/coffin_top_" + tile.color.getName()));
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(matrixStack.last(), iRenderTypeBuffer.getBuffer(RenderTypeLookup.getRenderType(state,false)), state, lidModel, 1,1,1,i, i1, EmptyModelData.INSTANCE);
        matrixStack.popPose();
        matrixStack.popPose();
    }

    /**
     * Checks if the coffin part at the given pos is the head of the coffin. Any exception is caught and false is returned
     */
    private boolean isHeadSafe(World world, BlockPos pos) {
        try {
            return CoffinBlock.isHead(world, pos);
        } catch (IllegalArgumentException e) {
            LOGGER.error(COFFIN, "Failed to check coffin head at {} caused by wrong blockstate. Block at that pos: {}", pos, world.getBlockState(pos));
        } catch (Exception e) {
            LOGGER.error(COFFIN, "Failed to check coffin head at "+pos+".", e);
        }
        return false;
    }
}
