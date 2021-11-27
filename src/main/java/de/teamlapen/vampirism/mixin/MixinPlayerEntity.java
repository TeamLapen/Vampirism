package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.player.IVampirismPlayer;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.util.MixinHooks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity implements IVampirismPlayer {

    private final VampirismPlayerAttributes vampirismPlayerAttributes = new VampirismPlayerAttributes();

    @Deprecated
    private MixinPlayerEntity(EntityType<? extends LivingEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public VampirismPlayerAttributes getVampAtts() {
        return vampirismPlayerAttributes;
    }

    @ModifyVariable(method = "attack(Lnet/minecraft/entity/Entity;)V", at = @At(value = "STORE", ordinal = 0), ordinal = 1)
    public float vampireSlayerEnchantment(float damage, Entity target) {
        return damage + MixinHooks.calculateVampireSlayerEnchantments(target, this.getMainHandItem());
    }
}
