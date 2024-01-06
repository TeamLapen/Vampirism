package de.teamlapen.vampirism.world.gen;


import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;


public class ModTreeGrower {

    public static final TreeGrower DARK_SPRUCE = new TreeGrower("dark_spruce", Optional.empty(), Optional.of(VampirismFeatures.DARK_SPRUCE_TREE), Optional.empty());
    public static final TreeGrower CURSED_SPRUCE = new TreeGrower("cursed_spruce", Optional.empty(), Optional.of(VampirismFeatures.CURSED_SPRUCE_TREE), Optional.empty());
}
