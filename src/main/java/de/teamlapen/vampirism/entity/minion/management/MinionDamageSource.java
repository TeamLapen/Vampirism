package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class MinionDamageSource extends DamageSource {

    @NotNull
    protected final MinionEntity<?> minionEntity;
    @Nullable
    protected final Player playerEntity;

    public MinionDamageSource(Holder<DamageType> damageType, @NotNull MinionEntity<?> minion) {
        super(damageType, minion, minion.getLordOpt().map(ILordPlayer::getPlayer).orElse(null));
        this.minionEntity = minion;
        this.playerEntity = (Player) getEntity();
    }

    @NotNull
    @Override
    public Component getLocalizedDeathMessage(@NotNull LivingEntity entityLivingBaseIn) {
        Component minionName = this.minionEntity.getDisplayName();
        ItemStack itemstack = minionEntity.getMainHandItem();
        String s = "death.attack." + this.type().msgId();
        String s1 = s + ".item";
        MutableComponent msg = !itemstack.isEmpty() && itemstack.get(DataComponents.CUSTOM_NAME) != null ? Component.translatable(s1, entityLivingBaseIn.getDisplayName(), minionName, itemstack.getDisplayName()) : Component.translatable(s, entityLivingBaseIn.getDisplayName(), minionName);
        if (playerEntity != null) {
            msg.append(Component.literal(" ")).append(Component.translatable("death.minion.on_behalf", playerEntity.getDisplayName()));
        }
        return msg;
    }
}
