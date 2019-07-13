package de.teamlapen.vampirism.world.gen.structure;

import com.mojang.datafixers.Dynamic;

import de.teamlapen.vampirism.core.ModWorld;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.function.Function;

public class HunterCampStructure extends ScatteredStructure<NoFeatureConfig> {
    public HunterCampStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51449_1_) {
        super(p_i51449_1_);
    }

    @Override
    protected int getSeedModifier() {
        return 0;//TODO 1.14 edit
    }

    @Override
    public IStartFactory getStartFactory() {
        return Start::new;
    }

    @Override
    public String getStructureName() {
        return ModWorld.StructureName.Hunter_Camp.name();
    }

    @Override
    public int getSize() {
        return 3;//TODO 1.14 modify
    }

    public static class Start extends StructureStart {
        public Start(Structure<?> p_i49949_1_, int p_i49949_2_, int p_i49949_3_, Biome p_i49949_4_, MutableBoundingBox p_i49949_5_, int p_i49949_6_, long p_i49949_7_) {
            super(p_i49949_1_, p_i49949_2_, p_i49949_3_, p_i49949_4_, p_i49949_5_, p_i49949_6_, p_i49949_7_);
        }

        public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
            FortressPieces.Start fortresspieces$start = new FortressPieces.Start(this.rand, (chunkX << 4) + 2, (chunkZ << 4) + 2);
            this.components.add(fortresspieces$start);
            fortresspieces$start.buildComponent(fortresspieces$start, this.components, this.rand);
            List<StructurePiece> list = fortresspieces$start.pendingChildren;

            while (!list.isEmpty()) {
                int i = this.rand.nextInt(list.size());
                StructurePiece structurepiece = list.remove(i);
                structurepiece.buildComponent(fortresspieces$start, this.components, this.rand);
            }

            this.recalculateStructureSize();
            this.func_214626_a(this.rand, 48, 70);
        }
    }
}
