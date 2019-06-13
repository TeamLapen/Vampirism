package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.tileentity.TileTotem;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.world.biome.Biome;


public class VampireAIRestrictSun<T extends EntityCreature & IVampire> extends EntityAIBase {
    private final T vampire;
    private boolean cache = false;


    public VampireAIRestrictSun(T creature) {
        this.vampire = creature;
    }

    public void resetTask() {
        ((PathNavigateGround) this.vampire.getNavigator()).setAvoidSun(false);
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
        ((PathNavigateGround) this.vampire.getNavigator()).setAvoidSun(true);
    }
}
