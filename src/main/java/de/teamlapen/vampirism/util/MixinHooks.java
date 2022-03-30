package de.teamlapen.vampirism.util;

import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

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

    public static void addSingleInstanceStructure(List<ResourceLocation> structures) {
        onlyOneStructure.addAll(structures.stream().map(MixinHooks::singleJigsawString).toList());
    }


    public static boolean checkStructures(List<? super PoolElementStructurePiece> pieces, StructurePoolElement jigsawPiece) {
        if (!onlyOneStructure.contains(jigsawPiece.toString())) return false;
        return pieces.stream().anyMatch(structurePiece -> onlyOneStructure.stream().anyMatch(string -> ((PoolElementStructurePiece) structurePiece).getElement().toString().equals(string)));
    }

    private static String singleJigsawString(ResourceLocation resourceLocation) {
        return "Single[Left[" + resourceLocation.toString() + "]]";
    }

}
