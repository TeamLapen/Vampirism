package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.entity.player.IVampirismPlayer;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.util.MixinHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Player.class)
public abstract class MixinPlayerEntity extends MixinLivingEntity implements IVampirismPlayer {

    @Unique
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

    @Override
    protected MobType vampirism$getMobType() {
        if (getVampAtts().vampireLevel > 0 && VampirismConfig.SERVER.vampiresAreUndeadType.get()) {
            return MobType.UNDEAD;
        }
        return null;
    }
}
