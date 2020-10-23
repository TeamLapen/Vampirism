package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.tileentity.TotemTileEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.ResourceLocation;


public class RestrictSunVampireGoal<T extends CreatureEntity & IVampire> extends Goal {
    private final T vampire;
    private boolean cache = false;


    public RestrictSunVampireGoal(T creature) {
        this.vampire = creature;
    }

    public void resetTask() {
        ((GroundPathNavigator) this.vampire.getNavigator()).setAvoidSun(false);
    }

    @Override
    public boolean shouldExecute() {
        if (vampire.ticksExisted % 10 == 3) {
            ResourceLocation biome = Helper.getBiomeId(vampire);
            cache = VampirismAPI.sundamageRegistry().getSundamageInDim(vampire.getEntityWorld().getDimensionKey()) && VampirismAPI.sundamageRegistry().getSundamageInBiome(biome) && !TotemTileEntity.isInsideVampireAreaCached(vampire.getEntityWorld().getDimensionKey(), vampire.getPosition());
        }
        return cache && vampire.getEntityWorld().isDaytime() && !vampire.isIgnoringSundamage();
    }

    public void startExecuting() {
        ((GroundPathNavigator) this.vampire.getNavigator()).setAvoidSun(true);
    }
}
