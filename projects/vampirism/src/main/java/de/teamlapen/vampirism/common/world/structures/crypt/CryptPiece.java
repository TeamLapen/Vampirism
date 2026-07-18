package de.teamlapen.vampirism.common.world.structures.crypt;

import de.teamlapen.vampirism.common.core.ModStructures;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.neoforged.neoforge.common.world.PieceBeardifierModifier;

public class CryptPiece extends PoolElementStructurePiece implements PieceBeardifierModifier {

    private static final String KEY_TERRAIN_ADJUSTMENT = "terrain_adjustment";

    private final TerrainAdjustment terrainAdjustment;

    public CryptPiece(StructureTemplateManager structureTemplateManager, PoolElementStructurePiece piece, LiquidSettings liquidSettings, TerrainAdjustment terrainAdjustment) {
        super(structureTemplateManager, piece.getElement(), piece.getPosition(), piece.getGroundLevelDelta(), piece.getRotation(), piece.getBoundingBox(), liquidSettings);
        this.terrainAdjustment = terrainAdjustment;
    }

    public CryptPiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(context, tag);
        this.terrainAdjustment = tag.read(KEY_TERRAIN_ADJUSTMENT, TerrainAdjustment.CODEC).orElse(TerrainAdjustment.NONE);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.store(KEY_TERRAIN_ADJUSTMENT, TerrainAdjustment.CODEC, terrainAdjustment);
    }

    @Override
    public BoundingBox getBeardifierBox() {
        return getBoundingBox();
    }

    @Override
    public TerrainAdjustment getTerrainAdjustment() {
        return terrainAdjustment;
    }

    @Override
    public StructurePieceType getType() {
        return ModStructures.CRYPT_PIECE.get();
    }
}
