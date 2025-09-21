package de.teamlapen.vampirism.common.items;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.effects.SanguinareMobEffect;
import de.teamlapen.vampirism.common.inventory.RevertBackMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SanguinareInjectionItem extends InjectionItem {

    public SanguinareInjectionItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean handleInjection(Level level, BlockPos pos, Player player, IFactionPlayerHandler handler, @Nullable Holder<? extends IPlayableFaction<?>> currentFaction) {
        if (IFaction.is(ModFactions.VAMPIRE, currentFaction)) {
            player.displayClientMessage(Component.translatable("text.vampirism.already_vampire"), false);
            return false;
        }
        if (ModFactions.HUNTER.match(currentFaction)) {
            if (!level.isClientSide) {
                player.openMenu(new SimpleMenuProvider((containerId, playerInventory, player1) -> new RevertBackMenu(containerId, playerInventory, ContainerLevelAccess.create(level, pos)), Component.empty()));
            }
            return false;
        }
        if (currentFaction == null) {
            if (handler.canJoin(ModFactions.VAMPIRE)) {
                if (ModConfig.SERVER.disableFangInfection.get()) {
                    player.displayClientMessage(Component.translatable("text.vampirism.deactivated_by_serveradmin"), true);
                } else {
                    SanguinareMobEffect.addRandom(player, true, true);
                    player.addEffect(new MobEffectInstance(ModEffects.POISON, 60));
                    return true;
                }
            }
        }
        return false;
    }
}
