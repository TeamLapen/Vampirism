package de.teamlapen.vampirism.proxy;


import de.teamlapen.lib.HelperLib;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.inventory.container.TaskContainer;
import de.teamlapen.vampirism.network.ActionBindingPacket;
import de.teamlapen.vampirism.network.AppearancePacket;
import de.teamlapen.vampirism.network.TaskActionPacket;
import de.teamlapen.vampirism.player.skills.SkillTree;
import de.teamlapen.vampirism.player.skills.SkillTreeManager;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;

/**
 * Abstract proxy base for both client and server.
 * Try to keep this quite empty and move larger code parts into dedicated classes.
 *
 * @author Maxanier
 */
public abstract class CommonProxy implements IProxy {

    @Override
    public SkillTree getSkillTree(boolean client) {
        return SkillTreeManager.getInstance().getSkillTree();
    }

    @Override
    public void handleActionBindingPacket(ActionBindingPacket msg, Player playerEntity) {
        FactionPlayerHandler.getOpt(playerEntity).ifPresent(factionPlayerHandler -> factionPlayerHandler.setBoundAction(msg.actionBindingId(), msg.action(), false, false));
    }

    @Override
    public void handleAppearancePacket(Player player, AppearancePacket msg) {
        Entity entity = player.level.getEntity(msg.entityId);
        if (entity instanceof Player) {
            VampirePlayer.getOpt(player).ifPresent(vampire -> vampire.setSkinData(msg.data));
        } else if (entity instanceof MinionEntity<?>) {
            ((MinionEntity<?>) entity).getMinionData().ifPresent(minionData -> minionData.handleMinionAppearanceConfig(msg.name, msg.data));
            HelperLib.sync((MinionEntity<?>) entity);
        }
    }

    @Override
    public void handleTaskActionPacket(TaskActionPacket msg, Player playerEntity) {
        FactionPlayerHandler.getOpt(playerEntity).ifPresent(factionPlayerHandler -> factionPlayerHandler.getCurrentFactionPlayer().ifPresent(factionPlayer -> {
            switch (msg.action()) {
                case COMPLETE -> factionPlayer.getTaskManager().completeTask(msg.entityId(), msg.task());
                case ACCEPT -> factionPlayer.getTaskManager().acceptTask(msg.entityId(), msg.task());
                default -> factionPlayer.getTaskManager().abortTask(msg.entityId(), msg.task(), msg.action() == TaskContainer.TaskAction.REMOVE);
            }
        }));
    }

    @Override
    public void onInitStep(Step step, ParallelDispatchEvent event) {
    }
}
