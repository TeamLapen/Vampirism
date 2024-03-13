package de.teamlapen.vampirism.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.teamlapen.vampirism.api.items.ICrossbow;
import de.teamlapen.vampirism.api.items.IVampirismCrossbow;
import de.teamlapen.vampirism.entity.player.IVampirismPlayer;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.util.MixinHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;

@Mixin(Player.class)
public abstract class MixinPlayerEntity extends LivingEntity implements IVampirismPlayer {

    @Unique
    private final VampirismPlayerAttributes vampirismPlayerAttributes = new VampirismPlayerAttributes();

    private MixinPlayerEntity(@NotNull EntityType<? extends LivingEntity> type, @NotNull Level worldIn) {
        super(type, worldIn);
    }

    @Unique
    @Override
    public VampirismPlayerAttributes getVampAtts() {
        return vampirismPlayerAttributes;
    }

    @ModifyExpressionValue(method = "attack(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getDamageBonus(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/MobType;)F"))
    private float addVampireSlayerDamageBonus(float damageBonus, Entity target) {
        return damageBonus + MixinHooks.calculateVampireSlayerEnchantments(target, this.getMainHandItem());
    }

    @ModifyExpressionValue(method = "getProjectile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ProjectileWeaponItem;getSupportedHeldProjectiles()Ljava/util/function/Predicate;"))
    private Predicate<ItemStack> getSupport(Predicate<ItemStack> original, ItemStack shootable) {
        if (shootable.getItem() instanceof ICrossbow crossbow) {
            return crossbow.getSupportedProjectiles(shootable);
        }
        return original;
    }

    @ModifyExpressionValue(method = "getProjectile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ProjectileWeaponItem;getAllSupportedProjectiles()Ljava/util/function/Predicate;"))
    private Predicate<ItemStack> getAllSupport(Predicate<ItemStack> original, ItemStack shootable) {
        if (shootable.getItem() instanceof ICrossbow crossbow) {
            return crossbow.getSupportedProjectiles(shootable);
        }
        return original;
    }
}
