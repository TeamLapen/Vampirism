package de.teamlapen.vampirism.proxy;


import de.teamlapen.lib.HelperLib;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.inventory.container.TaskContainer;
import de.teamlapen.vampirism.network.CActionBindingPacket;
import de.teamlapen.vampirism.network.CAppearancePacket;
import de.teamlapen.vampirism.network.CTaskActionPacket;
import de.teamlapen.vampirism.player.skills.SkillTree;
import de.teamlapen.vampirism.player.skills.SkillTreeManager;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
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
    public void handleActionBindingPacket(CActionBindingPacket msg, PlayerEntity playerEntity) {
        FactionPlayerHandler.getOpt(playerEntity).ifPresent(factionPlayerHandler -> factionPlayerHandler.setBoundAction(msg.actionBindingId, msg.action, false, false));
    }

    @Override
    public void handleAppearancePacket(PlayerEntity player, CAppearancePacket msg) {
        Entity entity = player.level.getEntity(msg.entityId);
        if (entity instanceof PlayerEntity) {
            VampirePlayer.getOpt(player).ifPresent(vampire -> {
                vampire.setSkinData(msg.data);
            });
        } else if (entity instanceof MinionEntity<?>) {
            ((MinionEntity<?>) entity).getMinionData().ifPresent(minionData -> minionData.handleMinionAppearanceConfig(msg.name, msg.data));
            HelperLib.sync((MinionEntity<?>) entity);
        }
    }

    @Override
    public void handleTaskActionPacket(CTaskActionPacket msg, PlayerEntity playerEntity) {
        FactionPlayerHandler.getOpt(playerEntity).ifPresent(factionPlayerHandler -> factionPlayerHandler.getCurrentFactionPlayer().ifPresent(factionPlayer -> {
            switch (msg.action) {
                case COMPLETE:
                    factionPlayer.getTaskManager().completeTask(msg.entityId, msg.task);
                    break;
                case ACCEPT:
                    factionPlayer.getTaskManager().acceptTask(msg.entityId, msg.task);
                    break;
                default:
                    factionPlayer.getTaskManager().abortTask(msg.entityId, msg.task, msg.action == TaskContainer.TaskAction.REMOVE);
                    break;
            }
        }));
    }

    @Override
    public void onInitStep(Step step, ParallelDispatchEvent event) {
    }
}
