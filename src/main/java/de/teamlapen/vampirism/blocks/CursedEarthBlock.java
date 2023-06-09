package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.blocks.HolyWaterEffectConsumer;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.HolyWaterBottleItem;
import de.teamlapen.vampirism.items.HolyWaterSplashBottleItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.IPlantable;
import org.jetbrains.annotations.NotNull;

public class CursedEarthBlock extends VampirismBlock implements HolyWaterEffectConsumer {

    public CursedEarthBlock() {
        super(Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).strength(0.5f, 2.0f).sound(SoundType.GRAVEL));

    }

    @Override
    public boolean canSustainPlant(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull Direction direction, @NotNull IPlantable plantable) {
        return plantable instanceof BushBlock || plantable.getPlantType(world, pos).equals(VReference.VAMPIRE_PLANT_TYPE);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand handIn, @NotNull BlockHitResult hit) {
        ItemStack heldItemStack = player.getItemInHand(handIn);
        Item heldItem = heldItemStack.getItem();
        if (heldItem instanceof HolyWaterBottleItem && !(heldItem instanceof HolyWaterSplashBottleItem)) {
            int uses = heldItem == ModItems.HOLY_WATER_BOTTLE_ULTIMATE.get() ? 100 : (heldItem == ModItems.HOLY_WATER_BOTTLE_ENHANCED.get() ? 50 : 25);
            if (!player.getAbilities().instabuild && player.getRandom().nextInt(uses) == 0) {
                heldItemStack.setCount(heldItemStack.getCount() - 1);
            }
            worldIn.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
            return InteractionResult.SUCCESS;
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public void onHolyWaterEffect(Level level, BlockState state, BlockPos pos, ItemStack holyWaterStack, IItemWithTier.TIER tier) {
        level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
    }
}
