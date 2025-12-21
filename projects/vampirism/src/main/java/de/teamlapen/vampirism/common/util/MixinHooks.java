package de.teamlapen.vampirism.common.util;

import com.google.common.collect.Lists;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MixinHooks {
    /**
     * JigsawPieces in this list only will be generated once per village
     * <p>
     * holds {@link StructurePoolElement#toString()}'s
     */
    private static final List<String> onlyOneStructure = Lists.newArrayList();
    /**
     * Used by mixins as a fast check if reduced blood vision is enabled
     */
    public static boolean enforcingGlowing_bloodVision = false;

    public static float armorLayerPartialTicks;

    public static void addSingleInstanceStructure(@NotNull List<Identifier> structures) {
        onlyOneStructure.addAll(structures.stream().map(MixinHooks::singleJigsawString).toList());
    }

    public static void replaceSingleInstanceStructure(@NotNull List<Identifier> structures) {
        onlyOneStructure.clear();
        onlyOneStructure.addAll(structures.stream().map(MixinHooks::singleJigsawString).toList());
    }


    public static boolean checkStructures(@NotNull List<? super PoolElementStructurePiece> pieces, @NotNull StructurePoolElement jigsawPiece) {
        if (!onlyOneStructure.contains(jigsawPiece.toString())) return false;
        return pieces.stream().anyMatch(structurePiece -> structurePiece instanceof PoolElementStructurePiece elem && equals(elem.getElement(), jigsawPiece));
    }

    private static boolean equals(StructurePoolElement first, StructurePoolElement second) {
        if (first == second) return true;
        if (first.getClass() != second.getClass()) return false;
        return first.toString().equals(second.toString());
    }

    private static @NotNull String singleJigsawString(@NotNull Identifier resourceLocation) {
        return "Single[Left[" + resourceLocation + "]]";
    }

}
