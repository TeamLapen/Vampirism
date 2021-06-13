package de.teamlapen.vampirism.util;

import com.google.common.collect.Lists;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;

import java.util.List;


@SuppressWarnings("unused")
public class ASMHooks {
    /**
     * Used by mixins as a fast check if reduced blood vision is enabled
     */
    public static boolean enforcingGlowing_bloodVision = false;


    /**
     * JigsawPieces in this list only will be generated once per village
     * <p>
     * holds {@link net.minecraft.world.gen.feature.jigsaw.JigsawPiece#toString()}'s
     */
    private static final List<String> onlyOneStructure = Lists.newArrayList();

    public static void addSingleInstanceStructure(List<String> structures) {
        onlyOneStructure.addAll(structures);
    }

    public static boolean checkStructures(List<? super AbstractVillagePiece> pieces, JigsawPiece jigsawPiece) {
        if (!onlyOneStructure.contains(jigsawPiece.toString())) return false;
        return pieces.stream().anyMatch(structurePiece -> onlyOneStructure.stream().anyMatch(string -> ((AbstractVillagePiece) structurePiece).getJigsawPiece().toString().equals(string)));
    }

}
