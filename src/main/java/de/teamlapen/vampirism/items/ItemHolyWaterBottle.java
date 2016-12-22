package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.EntityThrowableItem;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.player.vampire.actions.VampireActions;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
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
                    boolean vampire = Helper.isVampire(entitylivingbase);
                    if (entitylivingbase.canBeHitWithPotion() && (vampire || EnumCreatureAttribute.UNDEAD.equals(entitylivingbase.getCreatureAttribute()))) {
                        double dist = entity.getDistanceSqToEntity(entitylivingbase);

                        if (dist < 16.0D) {
                            double affect = 1.0D - Math.sqrt(dist) / 4.0D;

                            if (entitylivingbase == result.entityHit) {
                                affect = 1.0D;
                            }
                            if (!vampire) {
                                affect *= 0.5D;
                            }


                            int amount = (int) (affect * (Balance.general.HOLY_WATER_SPLASH_DAMAGE * (tier == TIER.NORMAL ? 1 : tier == TIER.ENHANCED ? Balance.general.HOLY_WATER_TIER_DAMAGE_INC : (Balance.general.HOLY_WATER_TIER_DAMAGE_INC * Balance.general.HOLY_WATER_TIER_DAMAGE_INC))) + 0.5D);
                            entitylivingbase.attackEntityFrom(VReference.HOLY_WATER, amount);
                        }
                    }
                    if (vampire && entitylivingbase instanceof EntityPlayer) {
                        IActionHandler<IVampirePlayer> actionHandler = VampirePlayer.get((EntityPlayer) entitylivingbase).getActionHandler();
                        if (actionHandler.isActionActive(VampireActions.disguiseAction)) {
                            actionHandler.toggleAction(VampireActions.disguiseAction);
                        }
                        if (actionHandler.isActionActive(VampireActions.invisibilityAction)) {
                            actionHandler.toggleAction(VampireActions.invisibilityAction);
                        }
                    }
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
