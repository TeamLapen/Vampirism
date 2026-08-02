package de.teamlapen.faction.common.factions.minions.management;

import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.world.entities.minion.IMinionData;
import de.teamlapen.faction.api.world.entities.minion.IMinionEntity;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import de.teamlapen.faction.common.core.FactionAdvancements;
import de.teamlapen.faction.common.util.RegUtil;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Optional;


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
    public T activateTask(@Nullable Player lord, @Nullable IMinionEntity minion, @NonNull Q data) {
        triggerAdvancements(lord);
        return null;
    }

    @Override
    public @NotNull String getDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("minion_task", RegUtil.id(this));
        }
        return this.descriptionId;
    }

    protected void triggerAdvancements(Player player) {
        if (player instanceof ServerPlayer) {
            FactionAdvancements.TRIGGER_MINION_ACTION.get().trigger(((ServerPlayer) player), this);
        }
    }

    public boolean isRequiredSkillUnlocked(Optional<? extends ISkillPlayer<?>> player) {
        return this.requiredSkill == null || player.map(x -> x.getSkillHandler().isSkillEnabled(this.requiredSkill)).orElse(false);
    }
}
