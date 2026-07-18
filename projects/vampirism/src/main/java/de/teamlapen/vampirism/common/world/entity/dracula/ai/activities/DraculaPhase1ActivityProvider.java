package de.teamlapen.vampirism.common.world.entity.dracula.ai.activities;

import de.teamlapen.vampirism.common.core.ModActivities;
import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.world.entity.ai.activities.ActivityBuilder;
import de.teamlapen.vampirism.common.world.entity.ai.system.AiActivityProvider;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors.SummonProtectorsBehavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class DraculaPhase1ActivityProvider extends AiActivityProvider<Dracula> {

    public DraculaPhase1ActivityProvider() {
        super(ModActivities.DRACULA_PHASE_1);
    }

    public void createActivity(ActivityBuilder<Dracula> builder) {
        builder.requires(ModMemoryTypes.DRACULA_PHASE_1, MemoryStatus.VALUE_PRESENT)
                .add(DraculaIdleActivityProvider.buildLook())
                .add(DraculaIdleActivityProvider.buildMovement(0.3f))
        ;

        var actions = builder.useActions();

        actions.addAction(ModActivities.DRACULA_SUMMON_PROTECTOR, SummonProtectorsBehavior::configure);
    }
}
