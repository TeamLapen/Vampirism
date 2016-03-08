package de.teamlapen.vampirism.biome;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.blocks.VampirismFlower;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.entity.EntityBlindingBat;
import de.teamlapen.vampirism.entity.EntityGhost;
import de.teamlapen.vampirism.entity.vampire.EntityBasicVampire;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenTrees;

public class BiomeGenVampireForest extends BiomeGenBase {
    public final static String name = "vampireForest";

    public BiomeGenVampireForest(int id) {
        super(id);
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
        this.spawnableMonsterList.add(new SpawnListEntry(EntityGhost.class, 2, 1, 1));
        this.spawnableMonsterList.add(new SpawnListEntry(EntityBasicVampire.class, 6, 1, 3));
        this.spawnableCaveCreatureList.add(new SpawnListEntry(EntityBlindingBat.class, 8, 2, 4));
        //TODO this.spawnableCreatureList.add(new SpawnListEntry(EntityDummyBittenEntity));

        this.topBlock = ModBlocks.cursedEarth.getDefaultState();
        this.fillerBlock = ModBlocks.cursedEarth.getDefaultState();
        this.theBiomeDecorator.treesPerChunk = 5;
        this.theBiomeDecorator.grassPerChunk = 4;
        this.theBiomeDecorator.deadBushPerChunk = 3;
        this.worldGeneratorTrees = new WorldGenTrees(false, 4, Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.SPRUCE), Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK), false);

        this.waterColorMultiplier = 0xEE2505;
        VampirismAPI.sundamageRegistry().addNoSundamageBiome(id);
        setBiomeName(name);
        setHeight(new BiomeGenBase.Height(0.1F, 0.025F));
        setColor(0xCC00CC);
    }

    @Override
    public void addDefaultFlowers() {
        addFlower(ModBlocks.vampirismFlower.getDefaultState().withProperty(VampirismFlower.TYPE, VampirismFlower.EnumFlowerType.ORCHID), 10);
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
}
