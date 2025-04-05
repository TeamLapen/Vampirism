package de.teamlapen.vampirism.entity.ai.goals;

import net.minecraft.world.entity.Mob;

public class OpenGateGoal extends GateInteractGoal {
    private final boolean closeGate;
    private int forgetTime;

    public OpenGateGoal(Mob mob, boolean closeGate) {
        super(mob);
        this.mob = mob;
        this.closeGate = closeGate;
    }

    @Override
    public boolean canContinueToUse() {
        return closeGate && forgetTime > 0 && super.canContinueToUse();
    }

    @Override
    public void start() {
        forgetTime = 20;
        setOpen(true);
    }

    @Override
    public void stop() {
        setOpen(false);
    }

    @Override
    public void tick() {
        forgetTime--;
        super.tick();
    }
}
