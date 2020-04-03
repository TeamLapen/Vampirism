package de.teamlapen.vampirism.proxy;


import de.teamlapen.lib.HelperLib;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.network.AppearancePacket;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.TaskFinishedPacket;
import de.teamlapen.vampirism.player.skills.SkillTree;
import de.teamlapen.vampirism.player.skills.SkillTreeManager;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;

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
    public void onInitStep(Step step, ModLifecycleEvent event) {
    }

    @Override
    public void handleAppearancePacket(PlayerEntity player, AppearancePacket msg) {
        Entity entity = player.world.getEntityByID(msg.entityId);
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
    public void handleTaskFinishedPacket(TaskFinishedPacket msg, PlayerEntity playerEntity) {
        FactionPlayerHandler.getOpt(playerEntity).ifPresent(factionPlayerHandler -> factionPlayerHandler.getCurrentFactionPlayer().ifPresent(factionPlayer -> {
            factionPlayer.getTaskManager().addCompletedTask(msg.task);
            factionPlayer.getTaskManager().removeRequirements(msg.task);
            factionPlayer.getTaskManager().applyRewards(msg.task);
        }));
    }
}
