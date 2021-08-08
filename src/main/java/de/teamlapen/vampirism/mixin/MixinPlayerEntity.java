package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.player.IVampirismPlayer;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class MixinPlayerEntity extends LivingEntity implements IVampirismPlayer {

    private final VampirismPlayerAttributes vampirismPlayerAttributes = new VampirismPlayerAttributes();

    @Deprecated
    private MixinPlayerEntity(EntityType<? extends LivingEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public VampirismPlayerAttributes getVampAtts() {
        return vampirismPlayerAttributes;
    }
}
