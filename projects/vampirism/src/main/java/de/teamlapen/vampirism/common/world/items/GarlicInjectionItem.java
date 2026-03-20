package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.faction.api.factions.IFactionPlayerHandler;
import de.teamlapen.faction.api.factions.IPlayableFaction;
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
                VampirismModClient.services().fullScreenOverlay().start(level, 4, 30, 0xBBBBBBFF);
            } else {
                handler.joinFaction(ModFactions.HUNTER);
                player.addEffect(new MobEffectInstance(ModEffects.POISON, 200, 1));
            }
            return true;
        } else if (currentFaction != null) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable("message.vampirism.injection_chair.already_non_hunter", currentFaction.value().getName()));
            }
        }
        return false;
    }
}
