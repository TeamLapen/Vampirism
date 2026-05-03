package de.teamlapen.vampirism.common.world.blocks;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.world.blocks.HolyWaterEffectConsumer;
import de.teamlapen.vampirism.api.world.items.IItemWithTier;
import de.teamlapen.vampirism.common.core.ModBiomes;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.world.features.VampirismFeatures;
import de.teamlapen.vampirism.common.world.items.HolyWaterBottleItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CursedGrass extends SpreadingSnowyBlock implements BonemealableBlock, HolyWaterEffectConsumer {
    public static final MapCodec<CursedGrass> CODEC = simpleCodec(CursedGrass::new);

    public CursedGrass(Properties properties) {
        super(properties, ModBlocks.CURSED_EARTH.getKey());
    }

    @Override
    protected MapCodec<? extends SpreadingSnowyBlock> codec() {
        return CODEC;
    }

    @Override
    public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos soilPosition, Direction facing, BlockState plant) {
        return super.canSustainPlant(state, level, soilPosition, facing, plant);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pPos, BlockState pState) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    /**
     * copied from {@link net.minecraft.world.level.block.GrassBlock#performBonemeal(ServerLevel, RandomSource, BlockPos, BlockState)}
     * and add a random flower
     * and use vampire forest as flower source
     */
    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockPos above = pos.above();
        BlockState grass = Blocks.SHORT_GRASS.defaultBlockState();
        Optional<Holder.Reference<PlacedFeature>> grassFeature = level.registryAccess()
                .lookupOrThrow(Registries.PLACED_FEATURE)
                .get(VegetationPlacements.GRASS_BONEMEAL);

        label48:
        for (int j = 0; j < 128; j++) {
            BlockPos testPos = above;

            for (int i = 0; i < j / 16; i++) {
                testPos = testPos.offset(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                if (!level.getBlockState(testPos.below()).is(this) || level.getBlockState(testPos).isCollisionShapeFullBlock(level, testPos)) {
                    continue label48;
                }
            }

            BlockState testState = level.getBlockState(testPos);
            if (testState.is(grass.getBlock()) && random.nextInt(10) == 0) {
                BonemealableBlock bonemealableBlock = (BonemealableBlock)grass.getBlock();
                if (bonemealableBlock.isValidBonemealTarget(level, testPos, testState)) {
                    bonemealableBlock.performBonemeal(level, random, testPos, testState);
                }
            }

            if (testState.isAir() && !level.isOutsideBuildHeight(testPos)) {
                if (random.nextInt(8) == 0) {
                    final BlockPos finalPos = testPos;
                    List<ConfiguredFeature<?, ?>> features = new ArrayList<>(level.registryAccess().lookup(Registries.BIOME).flatMap(x -> x.get(ModBiomes.VAMPIRE_FOREST).map(Holder.Reference::value)).orElseGet(() -> level.getBiome(finalPos).value()).getGenerationSettings().getBoneMealFeatures());
                    ConfiguredFeature<?, ?> cursedRoots= level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).getValue(VampirismFeatures.CURSED_ROOT);
                    if (cursedRoots != null) features.add(cursedRoots);
                    if (!features.isEmpty()) {
                        ConfiguredFeature<?, ?> placementFeature = Util.getRandom(features, random);
                        placementFeature.place(level, level.getChunkSource().getGenerator(), random, testPos);
                    }
                } else if (grassFeature.isPresent()) {
                    grassFeature.get().value().place(level, level.getChunkSource().getGenerator(), random, testPos);
                }
            }
        }
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        return HolyWaterBottleItem.onHolyWaterUsedOnBlock(stack, player, () -> level.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState()));
    }

    @Override
    public void onHolyWaterEffect(Level level, BlockState state, BlockPos pos, ItemStack holyWaterStack, IItemWithTier.Tier tier) {
        level.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState());
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility toolAction, boolean simulate) {
        return toolAction == ItemAbilities.SHOVEL_FLATTEN ? ModBlocks.CURSED_EARTH_PATH.get().defaultBlockState() : super.getToolModifiedState(state, context, toolAction, simulate);
    }
}
