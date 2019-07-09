package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.ItemThrowableEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Splash version of the holy water bottle
 *
 * @author maxanier
 */
public class HolyWaterSplashBottleItem extends HolyWaterBottleItem implements ItemThrowableEntity.IVampirismThrowableItem {

    public final static String regName = "holy_water_splash_bottle";

    public HolyWaterSplashBottleItem(TIER tier) {
        super(regName + "_" + tier.getName(), tier, new Properties().group(VampirismMod.creativeTab));
        setTranslation_key(regName);
    }

    @Override
    public void onImpact(ItemThrowableEntity entity, ItemStack stack, RayTraceResult result, boolean remote) {

        TIER tier = getVampirismTier();
        if (!remote) {


            AxisAlignedBB axisalignedbb = entity.getBoundingBox().grow(4.0D, 2.0D, 4.0D);
            List<LivingEntity> list1 = entity.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, axisalignedbb);


            if (!list1.isEmpty()) {
                for (LivingEntity entitylivingbase : list1) {
                    DamageHandler.affectEntityHolyWaterSplash(entitylivingbase, getStrength(tier), entity.getDistanceSq(entitylivingbase), result.getType() == RayTraceResult.Type.ENTITY);
                }
            }

            entity.getEntityWorld().playEvent(2002, new BlockPos(entity), PotionUtils.getPotionColor(Potions.MUNDANE));
        }

    }


    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);


        worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isRemote) {
            ItemThrowableEntity entityThrowable = new ItemThrowableEntity(worldIn, playerIn);
            ItemStack throwStack = stack.copy();
            throwStack.setCount(1);
            entityThrowable.setItem(throwStack);
            entityThrowable.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, -20.0F, 0.5F, 1.0F);
            worldIn.addEntity(entityThrowable);
        }

        if (!playerIn.abilities.isCreativeMode) {
            stack.shrink(1);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);

    }

}
