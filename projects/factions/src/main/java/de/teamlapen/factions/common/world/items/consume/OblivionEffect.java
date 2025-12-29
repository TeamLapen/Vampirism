package de.teamlapen.factions.common.world.items.consume;

import com.mojang.serialization.MapCodec;
import de.teamlapen.factions.common.core.FactionItems;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.factions.common.factions.minions.MinionEntity;
import de.teamlapen.factions.common.world.items.OblivionPotionItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;

public class OblivionEffect implements ConsumeEffect {

    private static final OblivionEffect INSTANCE = new OblivionEffect();
    public static final MapCodec<OblivionEffect> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, OblivionEffect> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends ConsumeEffect> getType() {
        return FactionItems.OBLIVION.get();
    }

    @Override
    public boolean apply(Level level, ItemStack itemstack, LivingEntity livingEntity) {
        if (livingEntity instanceof Player player) {
            FactionPlayerHandler.get(player).getCurrentSkillPlayer().ifPresent(OblivionPotionItem::applyEffect);
        }
        if (livingEntity instanceof MinionEntity<?> minion) {
            minion.getMinionData().ifPresent(d -> d.upgradeStat(-1, minion));
        }
        return true;
    }
}
