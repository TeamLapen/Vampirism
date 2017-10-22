package de.teamlapen.vampirism.world.gen.structure;

import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponentTemplate;
import net.minecraft.world.gen.structure.template.PlacementSettings;

import java.util.Random;

public abstract class VampirismComponentFeaturePiece extends StructureComponentTemplate {

    private static final PlacementSettings DEFAULT_PLACEMENT_SETTINGS = new PlacementSettings().setIgnoreEntities(true).setReplacedBlock(Blocks.AIR);
    private final TemplateManager.Structure structure;
    private final PlacementSettings placementSettings;

    protected VampirismComponentFeaturePiece(TemplateManager.Structure structure, PlacementSettings placementSettings) {
        this.structure = structure;
        this.placementSettings = placementSettings;
    }


    protected VampirismComponentFeaturePiece(TemplateManager.Structure structure) {
        this.structure = structure;
        this.placementSettings = DEFAULT_PLACEMENT_SETTINGS;
    }

    public static void registerFeaturePieces() {
        MapGenStructureIO.registerStructureComponent(VampirismComponentFeaturePiece.VampireHouse1.class, "Vampirism_VHS1");

    }

    @Override
    public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
        return super.addComponentParts(worldIn, randomIn, structureBoundingBoxIn);
    }

    @Override
    protected void handleDataMarker(String function, BlockPos pos, World worldIn, Random rand, StructureBoundingBox sbb) {

    }


    public void setup(BlockPos pos) {
        super.setup(TemplateManager.get(structure), pos, placementSettings);
    }

    public static class VampireHouse1 extends VampirismComponentFeaturePiece {


        public VampireHouse1() {
            super(TemplateManager.Structure.HOUSE1);
        }


        @Override
        public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn) {
            VampirismMod.log.t("Addings to %s", structureBoundingBoxIn);
            return super.addComponentParts(worldIn, randomIn, structureBoundingBoxIn);
        }
    }
}
