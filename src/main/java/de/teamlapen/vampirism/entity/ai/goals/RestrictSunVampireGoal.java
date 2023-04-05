package de.teamlapen.vampirism.entity.ai.goals;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;


public class RestrictSunVampireGoal<T extends PathfinderMob & IVampire> extends Goal {
    private final T vampire;
    private boolean cache = false;


    public RestrictSunVampireGoal(T creature) {
        this.vampire = creature;
    }

    @Override
    public boolean canUse() {
        if (vampire.tickCount % 10 == 3) {
            cache = VampirismAPI.sundamageRegistry().hasSunDamage(vampire.level, vampire.blockPosition()) && !Helper.isEntityInArtificalVampireFogArea(vampire);
        }
        return cache && vampire.getCommandSenderWorld().isDay() && !vampire.isIgnoringSundamage();
    }

    public void start() {
        ((GroundPathNavigation) this.vampire.getNavigation()).setAvoidSun(true);
    }

    public void stop() {
        ((GroundPathNavigation) this.vampire.getNavigation()).setAvoidSun(false);
    }
}
