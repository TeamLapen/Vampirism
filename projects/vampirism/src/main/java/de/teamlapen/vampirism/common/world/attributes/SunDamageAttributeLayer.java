package de.teamlapen.vampirism.common.world.attributes;

import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.world.attachments.LevelFog;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.attribute.EnvironmentAttributeLayer;
import net.minecraft.world.attribute.SpatialAttributeInterpolator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class SunDamageAttributeLayer implements EnvironmentAttributeLayer.Positional<Boolean> {

    private final Level level;
    private final LevelFog fog;

    public SunDamageAttributeLayer(Level level) {
        this.level = level;
        this.fog = LevelFog.get(level);
    }

    @Override
    public Boolean applyPositional(Boolean baseValue, Vec3 pos, @Nullable SpatialAttributeInterpolator biomeInterpolator) {
        if (!baseValue) {
            return false;
        }
        BlockPos containing = BlockPos.containing(pos);
        return level.precipitationAt(containing) == Biome.Precipitation.NONE && !fog.isInsideArtificialVampireFogArea(containing) && canBlockSeeSun(containing);
    }

    private boolean canBlockSeeSun(BlockPos pos) {
        // first air block above the highest non-air block of this column
        int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ());
        if (pos.getY() >= surfaceY) {
            return level.canSeeSky(pos);
        } else {
            BlockPos.MutableBlockPos blockpos = new BlockPos.MutableBlockPos(pos.getX(), surfaceY, pos.getZ());
            if (!level.canSeeSky(blockpos)) {
                return false;
            } else {
                int maxLiquidBlocks = ModConfig.balance().vpSundamageWaterblocks.get();
                int liquidBlocks = 0;
                for (blockpos.move(Direction.DOWN); blockpos.getY() > pos.getY(); blockpos.move(Direction.DOWN)) {
                    BlockState state = level.getBlockState(blockpos);
                    if (state.liquid()) { // if fluid than it propagates the light until `vpSundamageWaterBlocks`
                        liquidBlocks++;
                        if (liquidBlocks >= maxLiquidBlocks) {
                            return false;
                        }
                    } else if (state.canOcclude() && (state.isFaceSturdy(level, pos, Direction.DOWN) || state.isFaceSturdy(level, pos, Direction.UP))) { //solid block blocks the light (fence is solid too?)
                        return false;
                    } else if (state.getLightDampening() > 0) { //if not solid, but propagates no light
                        return false;
                    }
                }
                return true;
            }
        }
    }

}
