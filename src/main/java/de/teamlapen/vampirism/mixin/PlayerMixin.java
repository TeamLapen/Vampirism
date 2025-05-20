package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.entity.player.IVampirismPlayer;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements IVampirismPlayer {

    @Unique
    private final VampirismPlayerAttributes vampirism$vampirismPlayerAttributes = new VampirismPlayerAttributes();

    private PlayerMixin(@NotNull EntityType<? extends LivingEntity> type, @NotNull Level worldIn) {
        super(type, worldIn);
    }

    @Unique
    @Override
    public VampirismPlayerAttributes vampirism$getVampAtts() {
        return vampirism$vampirismPlayerAttributes;
    }
}
