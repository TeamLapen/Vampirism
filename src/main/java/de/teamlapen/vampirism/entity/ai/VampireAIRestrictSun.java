package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.tileentity.TileTotem;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.world.biome.Biome;


public class VampireAIRestrictSun<T extends CreatureEntity & IVampire> extends Goal {
    private final T vampire;
    private boolean cache = false;


    public VampireAIRestrictSun(T creature) {
        this.vampire = creature;
    }

    public void resetTask() {
        ((GroundPathNavigator) this.vampire.getNavigator()).setAvoidSun(false);
    }

    @Override
    public boolean shouldExecute() {
        if (vampire.ticksExisted % 10 == 3) {
            Biome biome = vampire.getEntityWorld().getBiome(vampire.getPosition());
            cache = VampirismAPI.sundamageRegistry().getSundamageInDim(vampire.getEntityWorld().dimension.getType()) && VampirismAPI.sundamageRegistry().getSundamageInBiome(biome) && !TileTotem.isInsideVampireAreaCached(vampire.getEntityWorld().getDimension(), vampire.getPosition());
        }
        return cache && vampire.getEntityWorld().isDaytime() && !vampire.isIgnoringSundamage();
    }

    public void startExecuting() {
        ((GroundPathNavigator) this.vampire.getNavigator()).setAvoidSun(true);
    }
}
