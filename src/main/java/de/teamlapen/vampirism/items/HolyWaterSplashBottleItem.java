package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.ThrowableItemEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Splash version of the holy water bottle
 *
 * @author maxanier
 */
public class HolyWaterSplashBottleItem extends HolyWaterBottleItem implements ThrowableItemEntity.IVampirismThrowableItem {

    public HolyWaterSplashBottleItem(TIER tier) {
        super(tier, new Properties().tab(VampirismMod.creativeTab));
    }

    @Override
    public void onImpact(ThrowableItemEntity entity, ItemStack stack, RayTraceResult result, boolean remote) {
        TIER tier = getVampirismTier();
        if (!remote) {
            AxisAlignedBB axisalignedbb = entity.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
            List<LivingEntity> list1 = entity.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
            @Nullable Entity thrower = entity.getOwner();

            if (!list1.isEmpty()) {
                for (LivingEntity entitylivingbase : list1) {
                    DamageHandler.affectEntityHolyWaterSplash(entitylivingbase, getStrength(tier), entity.distanceToSqr(entitylivingbase), result.getType() == RayTraceResult.Type.ENTITY, thrower instanceof LivingEntity ? (LivingEntity) thrower : null);
                }
            }

            entity.getCommandSenderWorld().levelEvent(2002, entity.blockPosition(), PotionUtils.getColor(Potions.MUNDANE));
        }

    }


    @Nonnull
    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);


        worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isClientSide) {
            ThrowableItemEntity entityThrowable = new ThrowableItemEntity(worldIn, playerIn);
            ItemStack throwStack = stack.copy();
            throwStack.setCount(1);
            entityThrowable.setItem(throwStack);
            entityThrowable.shootFromRotation(playerIn, playerIn.xRot, playerIn.yRot, -20.0F, 0.5F, 1.0F);
            worldIn.addFreshEntity(entityThrowable);
        }

        if (!playerIn.abilities.instabuild) {
            stack.shrink(1);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);

    }

}
