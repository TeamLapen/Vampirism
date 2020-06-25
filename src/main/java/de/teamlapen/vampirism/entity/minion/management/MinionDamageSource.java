package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;


public class MinionDamageSource extends IndirectEntityDamageSource {

    protected final MinionEntity<?> minionEntity;
    @Nullable
    protected final PlayerEntity playerEntity;

    public MinionDamageSource(MinionEntity<?> minion) {
        super("mob", minion, minion.getLordOpt().map(ILordPlayer::getPlayer).orElse(null));
        this.minionEntity = minion;
        this.playerEntity = (PlayerEntity) getTrueSource();
    }

    @Override
    public ITextComponent getDeathMessage(LivingEntity entityLivingBaseIn) {
        ITextComponent minionName = this.damageSourceEntity.getDisplayName();
        ItemStack itemstack = minionEntity.getHeldItemMainhand();
        String s = "death.attack." + this.damageType;
        String s1 = s + ".item";
        ITextComponent msg = !itemstack.isEmpty() && itemstack.hasDisplayName() ? new TranslationTextComponent(s1, entityLivingBaseIn.getDisplayName(), minionName, itemstack.getTextComponent()) : new TranslationTextComponent(s, entityLivingBaseIn.getDisplayName(), minionName);
        if (playerEntity != null) {
            msg.appendSibling(new StringTextComponent(" ")).appendSibling(new TranslationTextComponent("death.minion.on_behalf", playerEntity.getDisplayName()));
        }
        return msg;
    }
}
