package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.ThrowableItemEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

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
    public void onImpact(ThrowableItemEntity entity, ItemStack stack, HitResult result, boolean remote) {
        TIER tier = getVampirismTier();
        if (!remote) {
            AABB axisalignedbb = entity.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
            List<LivingEntity> list1 = entity.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
            @Nullable Entity thrower = entity.getOwner();

            if (!list1.isEmpty()) {
                for (LivingEntity entitylivingbase : list1) {
                    DamageHandler.affectEntityHolyWaterSplash(entitylivingbase, getStrength(tier), entity.distanceToSqr(entitylivingbase), result.getType() == HitResult.Type.ENTITY, thrower instanceof LivingEntity ? (LivingEntity) thrower : null);
                }
            }

            entity.getCommandSenderWorld().levelEvent(2002, entity.blockPosition(), PotionUtils.getColor(Potions.MUNDANE));
        }

    }


    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);


        worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (playerIn.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isClientSide) {
            ThrowableItemEntity entityThrowable = new ThrowableItemEntity(worldIn, playerIn);
            ItemStack throwStack = stack.copy();
            throwStack.setCount(1);
            entityThrowable.setItem(throwStack);
            entityThrowable.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), -20.0F, 0.5F, 1.0F);
            worldIn.addFreshEntity(entityThrowable);
        }

        if (!playerIn.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);

    }

}
