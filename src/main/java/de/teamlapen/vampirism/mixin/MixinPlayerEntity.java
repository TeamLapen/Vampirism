package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.player.IVampirismPlayer;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

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
}
