package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionPlayerHandler;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.world.effects.SanguinareMobEffect;
import de.teamlapen.vampirism.common.world.inventory.RevertBackMenu;
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
    public boolean handleInjection(Level level, BlockPos pos, Player player, IFactionPlayerHandler handler, Holder<? extends IPlayableFaction<?>> currentFaction) {
        if (IFaction.is(ModFactions.VAMPIRE, currentFaction)) {
            player.sendOverlayMessage(Component.translatable("message.vampirism.already_vampire"));
            return false;
        }
        if (ModFactions.HUNTER.match(currentFaction)) {
            if (!level.isClientSide()) {
                player.openMenu(new SimpleMenuProvider((containerId, playerInventory, player1) -> new RevertBackMenu(containerId, playerInventory, ContainerLevelAccess.create(level, pos)), Component.empty()));
            }
            return false;
        }
        if (IFaction.isNeutral(currentFaction)) {
            if (handler.canJoin(ModFactions.VAMPIRE)) {
                if (!ModConfig.server().fangInfection.get()) {
                    player.sendOverlayMessage(Component.translatable("message.vampirism.infection_disabled_server"));
                } else {
                    SanguinareMobEffect.addRandom(player, true, true);
                    player.addEffect(new MobEffectInstance(ModEffects.TOXICANT, 60));
                    return true;
                }
            }
        }
        return false;
    }
}
