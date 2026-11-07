package de.teamlapen.vampirism.common.world.blockentity;

import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.world.dimensions.velmorra.VelmorraDimension;
import de.teamlapen.vampirism.common.world.portal.VelmorraPortalShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class VelmorraPortalBlockEntity extends BlockEntity {

    private static final String KEY_VELMORRA_ID = "velmorra_id";
    private static final int CHECK_INTERVAL = 20;

    /**
     * The Velmorra instance this portal was opened for. See {@link VelmorraDimension#getInstanceId(net.minecraft.server.MinecraftServer)}.
     * Null for portals from before the id was tracked.
     */
    private @Nullable UUID velmorraId;

    public VelmorraPortalBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.VELMORRA_PORTAL.get(), pos, blockState);
    }

    public void setVelmorraId(UUID velmorraId) {
        this.velmorraId = velmorraId;
        setChanged();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, VelmorraPortalBlockEntity blockEntity) {
        if (level instanceof ServerLevel serverLevel && (level.getGameTime() + pos.asLong()) % CHECK_INTERVAL == 0) {
            blockEntity.checkVelmorra(serverLevel);
        }
    }

    /**
     * Removes the portal if Velmorra no longer exists or was recreated since the portal was opened
     */
    private void checkVelmorra(ServerLevel level) {
        UUID current = VelmorraDimension.getInstanceId(level.getServer());
        if (current == null) {
            VelmorraPortalShape.removePortalBlocks(level, this.worldPosition, 4);
        } else if (this.velmorraId == null) {
            // portal from before the id was tracked, assume it belongs to the existing Velmorra
            setVelmorraId(current);
        } else if (!this.velmorraId.equals(current)) {
            VelmorraPortalShape.removePortalBlocks(level, this.worldPosition, 4);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.velmorraId = input.read(KEY_VELMORRA_ID, UUIDUtil.CODEC).orElse(null);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.storeNullable(KEY_VELMORRA_ID, UUIDUtil.CODEC, this.velmorraId);
    }
}
