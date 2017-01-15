package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.EntityThrowableItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.List;

/**
 * HolyWaterBottle
 * Exists in different tiers and as splash versions.
 */
public class ItemHolyWaterBottle extends VampirismItem implements IItemWithTier, EntityThrowableItem.IVampirismThrowableItem {

    private static final String regName = "holy_water_bottle";

    /**
     * Registers the splash recipes for the given holywater bottle tier stack.
     * Should only be used once and after the item has been registed
     *
     * @param item The item
     * @param tier The tier
     */
    @Deprecated
    public static void registerSplashRecipes(ItemHolyWaterBottle item, TIER tier) {
        ItemStack base = item.setTier(new ItemStack(item), tier);
        ItemStack splash = item.setSplash(base.copy(), true);
        GameRegistry.addShapelessRecipe(splash.copy(), base, Items.GUNPOWDER);
        splash.stackSize++;
        GameRegistry.addShapelessRecipe(splash.copy(), base, base, Items.GUNPOWDER);
        splash.stackSize++;
        GameRegistry.addShapelessRecipe(splash.copy(), base, base, base, Items.GUNPOWDER);
        splash.stackSize++;
        GameRegistry.addShapelessRecipe(splash.copy(), base, base, base, base, Items.GUNPOWDER);
        splash.stackSize++;
        GameRegistry.addShapelessRecipe(splash.copy(), base, base, base, base, base, Items.GUNPOWDER);
    }

    public ItemHolyWaterBottle() {
        super(regName);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        TIER t = getTier(stack);
        if (t != TIER.NORMAL) {
            tooltip.add(TextFormatting.AQUA + UtilLib.translate("text.vampirism.itemTier." + t.name().toLowerCase()));
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
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (TIER t : TIER.values()) {
            subItems.add(setTier(new ItemStack(itemIn), t));
            subItems.add(setSplash(setTier(new ItemStack(itemIn), t), true));

        }
    }

    @Override
    public TIER getTier(ItemStack stack) {
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
        if (tag.hasKey("splash")) {
            return tag.getBoolean("splash");
        }
        return false;
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
            List<EntityLivingBase> list1 = entity.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);


            if (!list1.isEmpty()) {
                for (EntityLivingBase entitylivingbase : list1) {
                    DamageHandler.affectEntityHolyWaterSplash(entitylivingbase, getStrength(tier), entity.getDistanceSqToEntity(entitylivingbase), result.entityHit != null);
                }
            }

            entity.worldObj.playEvent(2002, new BlockPos(entity), PotionType.getID(PotionTypes.MUNDANE));
        }

    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (isSplash(itemStackIn)) {
            if (!playerIn.capabilities.isCreativeMode) {
                --itemStackIn.stackSize;
            }

            worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

            if (!worldIn.isRemote) {
                EntityThrowableItem entityThrowable = new EntityThrowableItem(worldIn, playerIn);
                entityThrowable.setItem(itemStackIn);
                entityThrowable.setHeadingFromThrower(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, -20.0F, 0.5F, 1.0F);
                worldIn.spawnEntityInWorld(entityThrowable);
            }

            playerIn.addStat(StatList.getObjectUseStats(this));
            return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
        }
        return new ActionResult<>(EnumActionResult.PASS, itemStackIn);
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

    @Override
    public ItemStack setTier(ItemStack stack, TIER tier) {
        NBTTagCompound tag = UtilLib.checkNBT(stack);
        tag.setString("tier", tier.name());
        return stack;
    }

}
