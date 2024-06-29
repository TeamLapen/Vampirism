package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.minion.IMinionData;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.api.entity.player.ISkillPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public abstract class DefaultMinionTask<T extends IMinionTask.IMinionTaskDesc<Q>, Q extends IMinionData> implements IMinionTask<T, Q> {

    private Component name;
    private final @Nullable Holder<ISkill<?>> requiredSkill;

    public DefaultMinionTask() {
        this( null);
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
    public @NotNull Component getName() {
        if (name == null) {
            name = Component.translatable(Util.makeDescriptionId("minion_task", RegUtil.id(this)));
        }
        return name;
    }

    protected void triggerAdvancements(Player player) {
        if (player instanceof ServerPlayer) {
            ModAdvancements.TRIGGER_MINION_ACTION.get().trigger(((ServerPlayer) player), this);
        }
    }

    public boolean isRequiredSkillUnlocked(@NotNull Holder<? extends IPlayableFaction<?>> faction, @Nullable ILordPlayer player) {
        return this.requiredSkill == null || player == null || faction.value().getPlayerCapability(player.getPlayer()) instanceof ISkillPlayer<?> s && s.getSkillHandler().isSkillEnabled(this.requiredSkill);
    }
}
