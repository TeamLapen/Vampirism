package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;


public class VampirismItemBloodFood extends VampirismItem {

    private final Food vampireFood;

    public VampirismItemBloodFood(String regName, Food vampireFood, Food humanFood) {
        super(regName, new Properties().group(VampirismMod.creativeTab).food(humanFood));
        this.vampireFood = vampireFood;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if (entityLiving instanceof PlayerEntity) {
            assert stack.getItem().getFood() != null;//Don't shrink stack before retrieving food
            PlayerEntity player = (PlayerEntity) entityLiving;
            VampirePlayer.getOpt(player).ifPresent(v -> v.drinkBlood(vampireFood.getHealing(), vampireFood.getSaturation()));
        }
        if (entityLiving instanceof IVampire) {
            ((IVampire) entityLiving).drinkBlood(vampireFood.getHealing(), vampireFood.getSaturation());
            stack.shrink(1);
        } else {
            entityLiving.onFoodEaten(worldIn, stack); //Shrinks stack and applies human food effects
        }
        worldIn.playSound(null, entityLiving.getPosX(), entityLiving.getPosY(), entityLiving.getPosZ(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
        if (!Helper.isVampire(entityLiving)) {
            entityLiving.addPotionEffect(new EffectInstance(Effects.NAUSEA, 20 * 20));
        }
        return stack;
    }


}
