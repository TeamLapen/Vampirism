package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class MinionDamageSource extends EntityDamageSource {

    @NotNull
    protected final MinionEntity<?> minionEntity;
    @Nullable
    protected final Player playerEntity;

    public MinionDamageSource(@NotNull MinionEntity<?> minion) {
        super("mob", minion);
        this.minionEntity = minion;
        this.playerEntity = minion.getLordOpt().map(ILordPlayer::getPlayer).orElse(null);
    }

    @Override
    @Nullable
    public Entity getDirectEntity() {
        return this.minionEntity;
    }

    @Override
    @Nullable
    public Entity getEntity() {
        return this.playerEntity;
    }

    @NotNull
    @Override
    public Component getLocalizedDeathMessage(@NotNull LivingEntity entityLivingBaseIn) {
        Component minionName = this.entity.getDisplayName();
        ItemStack itemstack = minionEntity.getMainHandItem();
        String s = "death.attack." + this.msgId;
        String s1 = s + ".item";
        MutableComponent msg = !itemstack.isEmpty() && itemstack.hasCustomHoverName() ? Component.translatable(s1, entityLivingBaseIn.getDisplayName(), minionName, itemstack.getDisplayName()) : Component.translatable(s, entityLivingBaseIn.getDisplayName(), minionName);
        if (playerEntity != null) {
            msg.append(Component.literal(" ")).append(Component.translatable("death.minion.on_behalf", playerEntity.getDisplayName()));
        }
        return msg;
    }
}
