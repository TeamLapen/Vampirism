package de.teamlapen.factions.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public class Util {

    public static float horizontalDistance(BlockPos pos1, BlockPos pos2) {
        int i = pos2.getX() - pos1.getX();
        int j = pos2.getZ() - pos1.getZ();
        return Mth.sqrt((float) (i * i + j * j));
    }
}
