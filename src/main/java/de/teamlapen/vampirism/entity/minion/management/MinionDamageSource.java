package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class MinionDamageSource extends EntityDamageSource {

    @Nonnull
    protected final MinionEntity<?> minionEntity;
    @Nullable
    protected final Player playerEntity;

    public MinionDamageSource(@Nonnull MinionEntity<?> minion) {
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

    @Override
    public Component getLocalizedDeathMessage(LivingEntity entityLivingBaseIn) {
        Component minionName = this.entity.getDisplayName();
        ItemStack itemstack = minionEntity.getMainHandItem();
        String s = "death.attack." + this.msgId;
        String s1 = s + ".item";
        MutableComponent msg = !itemstack.isEmpty() && itemstack.hasCustomHoverName() ? new TranslatableComponent(s1, entityLivingBaseIn.getDisplayName(), minionName, itemstack.getDisplayName()) : new TranslatableComponent(s, entityLivingBaseIn.getDisplayName(), minionName);
        if (playerEntity != null) {
            msg.append(new TextComponent(" ")).append(new TranslatableComponent("death.minion.on_behalf", playerEntity.getDisplayName()));
        }
        return msg;
    }
}
