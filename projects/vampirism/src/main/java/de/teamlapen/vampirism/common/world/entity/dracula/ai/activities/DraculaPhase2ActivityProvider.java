package de.teamlapen.vampirism.common.world.entity.dracula.ai.activities;

import de.teamlapen.vampirism.common.core.ModActivities;
import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.world.entity.ai.activities.ActivityBuilder;
import de.teamlapen.vampirism.common.world.entity.ai.system.AiActivityProvider;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors.*;
import de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors.flyingneedle.FlyingNeedleAttack;
import de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors.flyingsword.FlyingSwordAttack;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class DraculaPhase2ActivityProvider extends AiActivityProvider<Dracula> {

    public DraculaPhase2ActivityProvider() {
        super(ModActivities.DRACULA_PHASE_2);
    }

    @Override
    protected void createActivity(ActivityBuilder<Dracula> builder) {
        builder.requires(ModMemoryTypes.DRACULA_PHASE_2, MemoryStatus.VALUE_PRESENT)
                .add(DraculaIdleActivityProvider.buildLook())
                .add(KeepDistanceBehavior.build(0.6f, 14))
                .add(NearbyKnockbackBehavior.build(4, 1.2))
                .add(SurroundedEscapeBehavior.build(4.0, 60, 2))
        ;

        var actionBuilder = builder.useActions();
        actionBuilder.addAction(ModActivities.DRACULA_SUMMON_BATS, SummonVampireBats::configure);
        actionBuilder.addAction(ModActivities.DRACULA_FLYING_SWORD, FlyingSwordAttack::configure);
        actionBuilder.addAction(ModActivities.DRACULA_FLYING_NEEDLE, FlyingNeedleAttack::configure);
        actionBuilder.addAction(ModActivities.DRACULA_BACKSTAB, BackstabBehavior::configure);
        actionBuilder.addAction(ModActivities.DRACULA_BAT_SWARM_DASH, BatSwarmDashBehavior::configure);
    }
}
