package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
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

    @Override
    public boolean canUse() {
        if (vampire.tickCount % 10 == 3) {
            ResourceLocation biome = Helper.getBiomeId(vampire);
            cache = VampirismAPI.sundamageRegistry().getSundamageInDim(vampire.getCommandSenderWorld().dimension()) && VampirismAPI.sundamageRegistry().getSundamageInBiome(biome) && !Helper.isEntityInArtificalVampireFogArea(vampire);
        }
        return cache && vampire.getCommandSenderWorld().isDay() && !vampire.isIgnoringSundamage();
    }

    public void start() {
        ((GroundPathNavigator) this.vampire.getNavigation()).setAvoidSun(true);
    }

    public void stop() {
        ((GroundPathNavigator) this.vampire.getNavigation()).setAvoidSun(false);
    }
}
