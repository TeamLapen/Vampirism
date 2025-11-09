package de.teamlapen.vampirism.misc.extension;

import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;

public interface IProcessorRule {
    RuleTest getInputPredicate();

    RuleBlockEntityModifier getBlockEntityModifier();

    RuleTest getLocPredicate();
}
