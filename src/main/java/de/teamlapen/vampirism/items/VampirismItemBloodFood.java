package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;


public class VampirismItemBloodFood extends ItemFood {
    protected final int bloodAmount;
    protected final float saturation;
    private final String regName;

    public VampirismItemBloodFood(String regName, int amount, float saturation, Properties props) {
        super(0, 0, false, props);
        this.regName = regName;
        this.bloodAmount = amount;
        this.saturation = saturation;
        setRegistryName(REFERENCE.MODID, regName);
    }

    /**
     * @return The name this item is registered with in the GameRegistry
     */
    public String getRegisteredName() {
        return regName;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        stack.shrink(1);
        if (entityLiving instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entityLiving;
            VampirePlayer.get(player).drinkBlood(bloodAmount, saturation);
            worldIn.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);

            this.onFoodEaten(stack, worldIn, player);
        }
        return stack;
    }


}
