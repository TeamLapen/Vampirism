package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.ITargetingConditions;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

@Deprecated
public interface ITargetingConditionsVampirismMock extends ITargetingConditions {
    @Override
    default TargetingConditions.Selector getSelector() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
