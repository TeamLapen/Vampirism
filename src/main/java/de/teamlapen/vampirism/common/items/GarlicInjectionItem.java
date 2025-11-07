package de.teamlapen.vampirism.common.items;

import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.client.VampirismModClient;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModFactions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class GarlicInjectionItem extends InjectionItem {

    public GarlicInjectionItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean handleInjection(Level level, BlockPos pos, Player player, IFactionPlayerHandler handler, @Nullable Holder<? extends IPlayableFaction<?>> currentFaction) {
        if (handler.canJoin(ModFactions.HUNTER)) {
            if (level.isClientSide()) {
                VampirismModClient.getInstance().getOverlay().makeRenderFullColor(4, 30, 0xBBBBBBFF);
            } else {
                handler.joinFaction(ModFactions.HUNTER);
                player.addEffect(new MobEffectInstance(ModEffects.POISON, 200, 1));
            }
            return true;
        } else if (currentFaction != null) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable("text.vampirism.med_chair_other_faction", currentFaction.value().getName()));
            }
        }
        return false;
    }
}
