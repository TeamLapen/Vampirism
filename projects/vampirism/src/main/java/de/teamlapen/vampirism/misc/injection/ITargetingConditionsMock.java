package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.ITargetingConditions;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public interface ITargetingConditionsMock extends ITargetingConditions {
    @Override
    default TargetingConditions.Selector getSelector() {
        return null;
    }
}
