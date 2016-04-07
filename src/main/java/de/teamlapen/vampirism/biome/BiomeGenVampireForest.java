package de.teamlapen.vampirism.biome;

import de.teamlapen.vampirism.blocks.VampirismFlower;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.entity.EntityBlindingBat;
import de.teamlapen.vampirism.entity.EntityGhost;
import de.teamlapen.vampirism.entity.vampire.EntityBasicVampire;
import de.teamlapen.vampirism.entity.vampire.EntityDummyBittenAnimal;
import de.teamlapen.vampirism.entity.vampire.EntityVampireBaron;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenTrees;

import java.util.Random;

public class BiomeGenVampireForest extends BiomeGenBase {
    public final static String name = "vampireForest";
    protected WorldGenTrees worldGenTrees;

    public BiomeGenVampireForest() {
        super(new BiomeProperties(name).setWaterColor(0xEE2505).setBaseHeight(0.1F).setHeightVariation(0.025F));
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
        this.spawnableMonsterList.add(new SpawnListEntry(EntityGhost.class, 3, 1, 1));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityBasicVampire.class, 7, 1, 3));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityVampireBaron.class, 2, 1, 1));
        this.spawnableCaveCreatureList.add(new SpawnListEntry(EntityBlindingBat.class, 8, 2, 4));
        this.spawnableCreatureList.add(new SpawnListEntry(EntityDummyBittenAnimal.class, 15, 3, 6));

        this.topBlock = ModBlocks.cursedEarth.getDefaultState();
        this.fillerBlock = ModBlocks.cursedEarth.getDefaultState();
        this.theBiomeDecorator.treesPerChunk = 5;
        this.theBiomeDecorator.grassPerChunk = 4;
        this.theBiomeDecorator.deadBushPerChunk = 3;
        this.worldGenTrees = new WorldGenTrees(false, 4, Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE), Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK), false);

    }

    @Override
    public void addDefaultFlowers() {
        addFlower(ModBlocks.vampirismFlower.getDefaultState().withProperty(VampirismFlower.TYPE, VampirismFlower.EnumFlowerType.ORCHID), 10);
    }

    @Override
    public WorldGenAbstractTree genBigTreeChance(Random rand) {
        return worldGenTrees;
    }

    @Override
    public int getFoliageColorAtPos(BlockPos pos) {
        return 0x1E1F1F;
    }

    @Override
    public int getGrassColorAtPos(BlockPos pos) {
        // 0x7A317A; dark purple
        return 0x1E1F1F;
    }

    @Override
    public int getSkyColorByTemp(float p_76731_1_) {
        return 0xA33641;
    }

    @Override
    public int getWaterColorMultiplier() {
        return super.getWaterColorMultiplier();
    }
}
