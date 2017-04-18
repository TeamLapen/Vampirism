package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.ItemStackUtil;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;


public class VampirismItemBloodFood extends ItemFood {
    protected final int bloodAmount;
    protected final float saturation;
    private final String regName;

    public VampirismItemBloodFood(String regName, int amount, float saturation) {
        super(0, false);
        this.regName = regName;
        this.bloodAmount = amount;
        this.saturation = saturation;
        setCreativeTab(VampirismMod.creativeTab);
        setRegistryName(REFERENCE.MODID, regName);
        this.setUnlocalizedName(REFERENCE.MODID + "." + regName);
    }

    public String getLocalizedName() {
        return UtilLib.translate(getUnlocalizedName() + ".name");
    }

    /**
     * @return The name this item is registered with in the GameRegistry
     */
    public String getRegisteredName() {
        return regName;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        ItemStackUtil.decr(stack);
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityLiving;
            VampirePlayer.get(player).drinkBlood(bloodAmount, saturation);
            worldIn.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);

            this.onFoodEaten(stack, worldIn, player);
        }
        return stack;
    }


}
