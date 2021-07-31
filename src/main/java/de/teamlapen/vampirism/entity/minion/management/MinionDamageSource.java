package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class MinionDamageSource extends EntityDamageSource {

    @Nonnull
    protected final MinionEntity<?> minionEntity;
    @Nullable
    protected final PlayerEntity playerEntity;

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
    public ITextComponent getLocalizedDeathMessage(LivingEntity entityLivingBaseIn) {
        ITextComponent minionName = this.entity.getDisplayName();
        ItemStack itemstack = minionEntity.getMainHandItem();
        String s = "death.attack." + this.msgId;
        String s1 = s + ".item";
        IFormattableTextComponent msg = !itemstack.isEmpty() && itemstack.hasCustomHoverName() ? new TranslationTextComponent(s1, entityLivingBaseIn.getDisplayName(), minionName, itemstack.getDisplayName()) : new TranslationTextComponent(s, entityLivingBaseIn.getDisplayName(), minionName);
        if (playerEntity != null) {
            msg.append(new StringTextComponent(" ")).append(new TranslationTextComponent("death.minion.on_behalf", playerEntity.getDisplayName()));
        }
        return msg;
    }
}
