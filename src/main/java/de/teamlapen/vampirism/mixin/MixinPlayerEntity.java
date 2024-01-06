package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.api.items.IVampirismCrossbow;
import de.teamlapen.vampirism.entity.player.IVampirismPlayer;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.util.MixinHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(Player.class)
public abstract class MixinPlayerEntity extends LivingEntity implements IVampirismPlayer {

    private final VampirismPlayerAttributes vampirismPlayerAttributes = new VampirismPlayerAttributes();

    private MixinPlayerEntity(@NotNull EntityType<? extends LivingEntity> type, @NotNull Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public VampirismPlayerAttributes getVampAtts() {
        return vampirismPlayerAttributes;
    }

    @ModifyVariable(method = "attack(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "STORE", ordinal = 0), ordinal = 1)
    public float vampireSlayerEnchantment(float damage, Entity target) {
        return damage + MixinHooks.calculateVampireSlayerEnchantments(target, this.getMainHandItem());
    }

    @Redirect(method = "getProjectile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ProjectileWeaponItem;getSupportedHeldProjectiles()Ljava/util/function/Predicate;"))
    private Predicate<ItemStack> getSupportedHeldProjectilesWithItemStack(ProjectileWeaponItem instance, ItemStack stack) {
        if (instance instanceof IVampirismCrossbow crossbow) {
            return crossbow.getSupportedProjectiles(stack);
        }
        return instance.getSupportedHeldProjectiles();
    }

    @Redirect(method = "getProjectile", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ProjectileWeaponItem;getAllSupportedProjectiles()Ljava/util/function/Predicate;"))
    private Predicate<ItemStack> getAllSupportedProjectilesWithItemStack(ProjectileWeaponItem instance, ItemStack stack) {
        if (instance instanceof IVampirismCrossbow crossbow) {
            return crossbow.getSupportedProjectiles(stack);
        }
        return instance.getSupportedHeldProjectiles();
    }
}
