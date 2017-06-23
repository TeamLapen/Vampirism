package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.ItemStackUtil;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.EntityThrowableItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * HolyWaterBottle
 * Exists in different tiers and as splash versions.
 */
public class ItemHolyWaterBottle extends VampirismItem implements IItemWithTier, EntityThrowableItem.IVampirismThrowableItem {

    private static final String regName = "holy_water_bottle";

    //TODO CRAFTING
//    /**
//     * Registers the splash recipes for the given holywater bottle tier stack.
//     * Should only be used once and after the item has been registed
//     *
//     * @param item The item
//     * @param tier The tier
//     */
//    @Deprecated
//    public static void registerSplashRecipes(ItemHolyWaterBottle item, TIER tier) {
//        ItemStack base = item.setTier(new ItemStack(item), tier);
//        ItemStack splash = item.setSplash(base.copy(), true);
//        GameRegistry.addShapelessRecipe(splash.copy(), base, Items.GUNPOWDER);
//        ItemStackUtil.grow(splash, 1);
//        GameRegistry.addShapelessRecipe(splash.copy(), base, base, Items.GUNPOWDER);
//        ItemStackUtil.grow(splash, 1);
//        GameRegistry.addShapelessRecipe(splash.copy(), base, base, base, Items.GUNPOWDER);
//        ItemStackUtil.grow(splash, 1);
//        GameRegistry.addShapelessRecipe(splash.copy(), base, base, base, base, Items.GUNPOWDER);
//        ItemStackUtil.grow(splash, 1);
//        GameRegistry.addShapelessRecipe(splash.copy(), base, base, base, base, base, Items.GUNPOWDER);
//    }

    public ItemHolyWaterBottle() {
        super(regName);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        TIER t = getTier(stack);
        if (t != TIER.NORMAL) {
            tooltip.add(TextFormatting.AQUA + UtilLib.translate("text.vampirism.item_tier." + t.name().toLowerCase()));
        }
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return isSplash(stack) ? 1 : 64;
    }

    /**
     * @param tier
     * @return A stack of this item with the given tier
     */
    public ItemStack getStack(TIER tier) {
        return setTier(new ItemStack(this), tier);
    }

    /**
     * Converts the tier of this bottle into the strength of the applied holy water
     *
     * @param tier
     * @return
     */
    public EnumStrength getStrength(TIER tier) {
        switch (tier) {
            case NORMAL:
                return EnumStrength.WEAK;
            case ENHANCED:
                return EnumStrength.MEDIUM;
            case ULTIMATE:
                return EnumStrength.STRONG;
        }
        return EnumStrength.NONE;
    }

    @Override
    public void getSubItems(@Nonnull Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        for (TIER t : TIER.values()) {
            subItems.add(setTier(new ItemStack(itemIn), t));
            subItems.add(setSplash(setTier(new ItemStack(itemIn), t), true));

        }
    }

    @Override
    public TIER getTier(@Nonnull ItemStack stack) {
        NBTTagCompound tag = UtilLib.checkNBT(stack);
        if (tag.hasKey("tier")) {
            try {
                return TIER.valueOf(tag.getString("tier"));
            } catch (IllegalArgumentException e) {
                VampirismMod.log.e("HolyWaterBottle", e, "Unknown item tier %s", tag.getString("tier"));
            }

        }
        return TIER.NORMAL;
    }

    @Nonnull
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        String unloc = super.getUnlocalizedName(stack);
        return isSplash(stack) ? unloc + ".splash" : unloc;
    }

    /**
     * @return If the bottle is a splash bottle
     */
    public boolean isSplash(ItemStack stack) {
        NBTTagCompound tag = UtilLib.checkNBT(stack);
        return tag.hasKey("splash") && tag.getBoolean("splash");
    }

    @Override
    public void onImpact(EntityThrowableItem entity, ItemStack stack, RayTraceResult result, boolean remote) {
        if (!isSplash(stack)) {
            VampirismMod.log.w("HolyWaterBottle", "Threw non splash bottle");
            return;
        }
        TIER tier = getTier(stack);
        if (!remote) {


            AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand(4.0D, 2.0D, 4.0D);
            List<EntityLivingBase> list1 = entity.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);


            if (!list1.isEmpty()) {
                for (EntityLivingBase entitylivingbase : list1) {
                    DamageHandler.affectEntityHolyWaterSplash(entitylivingbase, getStrength(tier), entity.getDistanceSqToEntity(entitylivingbase), result.entityHit != null);
                }
            }

            entity.getEntityWorld().playEvent(2002, new BlockPos(entity), PotionUtils.getPotionColor(PotionTypes.MUNDANE));
        }

    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (isSplash(stack)) {
            if (!playerIn.capabilities.isCreativeMode) {
                ItemStackUtil.decr(stack);
            }

            worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

            if (!worldIn.isRemote) {
                EntityThrowableItem entityThrowable = new EntityThrowableItem(worldIn, playerIn);
                entityThrowable.setItem(stack);
                entityThrowable.setHeadingFromThrower(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, -20.0F, 0.5F, 1.0F);
                worldIn.spawnEntity(entityThrowable);
            }

            playerIn.addStat(StatList.getObjectUseStats(this));
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    /**
     * Marks the stack as splash bottle
     *
     * @return The same stack
     */
    public ItemStack setSplash(ItemStack stack, boolean value) {
        NBTTagCompound tag = UtilLib.checkNBT(stack);
        tag.setBoolean("splash", value);
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack setTier(@Nonnull ItemStack stack, TIER tier) {
        NBTTagCompound tag = UtilLib.checkNBT(stack);
        tag.setString("tier", tier.name());
        return stack;
    }

}
