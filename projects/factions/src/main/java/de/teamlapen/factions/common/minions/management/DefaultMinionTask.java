package de.teamlapen.factions.common.minions.management;

import de.teamlapen.factions.api.entities.minion.IMinionData;
import de.teamlapen.factions.api.entities.minion.IMinionEntity;
import de.teamlapen.factions.api.entities.minion.IMinionTask;
import de.teamlapen.factions.api.entities.player.ILordPlayer;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.api.skills.ISkill;
import de.teamlapen.factions.api.skills.ISkillPlayer;
import de.teamlapen.factions.common.core.FactionAdvancements;
import de.teamlapen.factions.common.util.RegUtil;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public abstract class DefaultMinionTask<T extends IMinionTask.IMinionTaskDesc<Q>, Q extends IMinionData> implements IMinionTask<T, Q> {

    private final @Nullable Holder<ISkill<?>> requiredSkill;
    @Nullable
    private String descriptionId;

    public DefaultMinionTask() {
        this(null);
    }

    public DefaultMinionTask(@Nullable Holder<ISkill<?>> requiredSkill) {
        this.requiredSkill = requiredSkill;
    }

    @Nullable
    @Override
    public T activateTask(@Nullable Player lord, @Nullable IMinionEntity minion, Q data) {
        triggerAdvancements(lord);
        return null;
    }

    @Override
    public String getDescriptionId() {
        if (this.descriptionId != null) {
            this.descriptionId = Util.makeDescriptionId("minion_task", RegUtil.id(this));
        }
        return this.descriptionId;
    }

    protected void triggerAdvancements(Player player) {
        if (player instanceof ServerPlayer) {
            FactionAdvancements.TRIGGER_MINION_ACTION.get().trigger(((ServerPlayer) player), this);
        }
    }

    public boolean isRequiredSkillUnlocked(@NotNull Holder<? extends IPlayableFaction<?>> faction, @Nullable ILordPlayer player) {
        return this.requiredSkill == null || player == null || faction.value().getPlayerCapability(player.getPlayer()) instanceof ISkillPlayer<?> s && s.getSkillHandler().isSkillEnabled(this.requiredSkill);
    }
}
