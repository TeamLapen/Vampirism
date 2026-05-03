package de.teamlapen.vampirism.common.world.attributes;

import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.attachments.LevelFog;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.attribute.EnvironmentAttributeLayer;
import net.minecraft.world.attribute.SpatialAttributeInterpolator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
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
        BlockPos containing = BlockPos.containing(pos);
        return baseValue && level.precipitationAt(containing) == Biome.Precipitation.NONE && (fog.isInsideArtificialVampireFogArea(containing) || canBlockSeeSun(containing));
    }

    private boolean canBlockSeeSun(BlockPos pos) {
        if (pos.getY() >= level.getSeaLevel()) {
            return level.canSeeSky(pos);
        } else {
            BlockPos blockpos = new BlockPos(pos.getX(), level.getSeaLevel(), pos.getZ());
            if (!level.canSeeSky(blockpos)) {
                return false;
            } else {
                int liquidBlocks = 0;
                for (blockpos = blockpos.below(); blockpos.getY() > pos.getY(); blockpos = blockpos.below()) {
                    BlockState state = level.getBlockState(blockpos);
                    if (state.liquid()) { // if fluid than it propagates the light until `vpSundamageWaterBlocks`
                        liquidBlocks++;
                        if (liquidBlocks >= ModConfig.balance().vpSundamageWaterblocks.get()) {
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
