package de.teamlapen.vampirism.world.gen.structure;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;

import java.util.List;
import java.util.Random;

public class ScattedFeatureWrapper extends MapGenScatteredFeature {
    private final MapGenScatteredFeature vanilla;


    private final MapGenVampirismFeatures vampirism;


    public ScattedFeatureWrapper(MapGenScatteredFeature vanilla, MapGenVampirismFeatures vampirism) {
        this.vanilla = vanilla;
        this.vampirism = vampirism;
    }

    @Override
    public synchronized boolean generateStructure(World worldIn, Random randomIn, ChunkPos chunkCoord) {
        return vanilla.generateStructure(worldIn, randomIn, chunkCoord) || vampirism.generateStructure(worldIn, randomIn, chunkCoord);
    }

    @Override
    public void generate(World worldIn, int x, int z, ChunkPrimer primer) {
        vanilla.generate(worldIn, x, z, primer);
        vampirism.generate(worldIn, x, z, primer);
    }

    @Override
    public boolean isInsideStructure(BlockPos pos) {
        return vanilla.isInsideStructure(pos) || vampirism.isInsideStructure(pos);
    }

    @Override
    public List<Biome.SpawnListEntry> getMonsters() {
        return vanilla.getMonsters();
    }

    @Override
    public boolean isSwampHut(BlockPos pos) {
        return vanilla.isSwampHut(pos);
    }
}
