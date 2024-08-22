package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.vampire.DrinkBloodContext;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VampirismItemBloodFoodItem extends Item {

    private final FoodProperties vampireFood;

    public VampirismItemBloodFoodItem(Properties properties, FoodProperties vampireFood) {
        super(properties);
        this.vampireFood = vampireFood;
    }

    @NotNull
    @Override
    public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull LivingEntity entityLiving) {
        if (entityLiving instanceof Player player) {
            //Don't shrink stack before retrieving food
            VampirePlayer.get(player).drinkBlood(vampireFood.nutrition(), vampireFood.saturation(), new DrinkBloodContext(stack));
        }
        if (entityLiving instanceof IVampire) {
            ((IVampire) entityLiving).drinkBlood(vampireFood.nutrition(), vampireFood.saturation(), new DrinkBloodContext(stack));
            stack.shrink(1);
        } else {
            entityLiving.eat(worldIn, stack); //Shrinks stack and applies human food effects
        }
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
        if (!Helper.isVampire(entityLiving)) {
            entityLiving.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20 * 20));
        }
        return stack;
    }

    @Override
    public @Nullable FoodProperties getFoodProperties(@NotNull ItemStack stack, @Nullable LivingEntity entity) {
        if (entity == null) {
            entity = VampirismMod.proxy.getClientPlayer();
        }

        return Helper.isVampire(entity) ? vampireFood : super.getFoodProperties(stack, entity);
    }
}
