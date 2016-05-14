package de.teamlapen.vampirism.api.entity.hunter;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.world.villages.VampirismVillage;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * Interface for basic
 */
public interface IBasicHunter extends IHunterMob, IAdjustableLevel {
    boolean isLookingForHome();

    void makeCampHunter(AxisAlignedBB box);

    void makeNormalHunter();

    void makeVillageHunter(VampirismVillage village);
}
