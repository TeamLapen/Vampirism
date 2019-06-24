package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.EntityThrowableItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
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
public class ItemHolyWaterSplashBottle extends ItemHolyWaterBottle implements EntityThrowableItem.IVampirismThrowableItem {

    public final static String regName = "holy_water_splash_bottle";

    public ItemHolyWaterSplashBottle(TIER tier) {
        super(regName + "_" + tier.getName(), tier, new Properties());
        setTranslation_key(regName);
    }

    @Override
    public void onImpact(EntityThrowableItem entity, ItemStack stack, RayTraceResult result, boolean remote) {

        TIER tier = getVampirismTier();
        if (!remote) {


            AxisAlignedBB axisalignedbb = entity.getBoundingBox().grow(4.0D, 2.0D, 4.0D);
            List<EntityLivingBase> list1 = entity.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);


            if (!list1.isEmpty()) {
                for (EntityLivingBase entitylivingbase : list1) {
                    DamageHandler.affectEntityHolyWaterSplash(entitylivingbase, getStrength(tier), entity.getDistanceSq(entitylivingbase), result.entity != null);
                }
            }

            entity.getEntityWorld().playEvent(2002, new BlockPos(entity), PotionUtils.getPotionColor(PotionTypes.MUNDANE));
        }

    }


    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);


        worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isRemote) {
            EntityThrowableItem entityThrowable = new EntityThrowableItem(worldIn, playerIn);
            ItemStack throwStack = stack.copy();
            throwStack.setCount(1);
            entityThrowable.setItem(throwStack);
            entityThrowable.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, -20.0F, 0.5F, 1.0F);
            worldIn.spawnEntity(entityThrowable);
        }

        if (!playerIn.abilities.isCreativeMode) {
            stack.shrink(1);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);

    }

}
