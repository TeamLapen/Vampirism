package de.teamlapen.vampirism.misc.extension;

import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public interface ITargetingConditions {

    TargetingConditions.Selector getSelector();

}
