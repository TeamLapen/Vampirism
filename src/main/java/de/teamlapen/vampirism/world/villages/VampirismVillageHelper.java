package de.teamlapen.vampirism.world.villages;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.world.World;

import javax.annotation.Nullable;


public class VampirismVillageHelper {

    /**
     * Finds the nearest village, but only the given coordinates are withing it's bounding box plus the given the distance.
     */
    public static @Nullable
    VampirismVillage getNearestVillage(World w, BlockPos pos, int r) {
        Village v = w.villageCollection.getNearestVillage(pos, r);
        if (v != null) {
            return VampirismVillage.get(v);
        }
        return null;
    }

    /**
     * @return The nearest village the entity is in or next to.
     */
    public static @Nullable
    VampirismVillage getNearestVillage(Entity e) {
        return getNearestVillage(e.getEntityWorld(), e.getPosition(), 10);
    }

    /**
     * Tick all Vampirism Villages
     */
    public static void tick(World w) {
        if (w.villageCollection != null) { //Shouldn't be null, but https://github.com/TeamLapen/Vampirism/issues/372
            for (Village v : w.villageCollection.getVillageList()) {
                VampirismVillage.get(v).tick(w.getTotalWorldTime());
            }
        }
    }

}
