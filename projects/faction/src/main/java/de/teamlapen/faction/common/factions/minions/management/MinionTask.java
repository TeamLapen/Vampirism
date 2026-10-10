package de.teamlapen.faction.common.factions.minions.management;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillHandler;
import de.teamlapen.faction.api.tags.FactionTags;
import de.teamlapen.faction.api.world.entities.minion.IMinionData;
import de.teamlapen.faction.api.world.entities.minion.IMinionEntity;
import de.teamlapen.faction.api.world.entities.minion.MinionStat;
import de.teamlapen.faction.api.world.entities.minion.tasks.IMinionTask;
import de.teamlapen.faction.api.world.entities.minion.tasks.MinionTaskProperties;
import de.teamlapen.faction.common.core.FactionAdvancements;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.core.ModRegistries;
import de.teamlapen.faction.common.factions.minions.MinionData;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class MinionTask<TState extends IMinionTask.IMinionTaskState> implements IMinionTask<TState> {

    private final String nameId;
    private final Codec<TState> stateCodec;
    @Nullable
    private final TState emptyState;
    private final Holder.Reference<IMinionTask<?>> builtInRegistryHolder;

    public MinionTask(MinionTaskProperties properties, @NotNull TState emptyState) {
        this.nameId = properties.effectiveNameId();
        this.stateCodec = MapCodec.unitCodec(emptyState);
        this.emptyState = emptyState;
        this.builtInRegistryHolder = ModRegistries.MINION_TASKS.createIntrusiveHolder(this);
    }

    protected MinionTask(MinionTaskProperties properties, Codec<TState> stateCodec) {
        this.nameId = properties.effectiveNameId();
        this.stateCodec = stateCodec;
        this.builtInRegistryHolder = ModRegistries.MINION_TASKS.createIntrusiveHolder(this);
        this.emptyState = null;
    }

    public DataComponentMap components() {
        return this.builtInRegistryHolder.components();
    }

    @Override
    public String getNameId() {
        return this.nameId;
    }

    @Override
    public Component getName() {
        return this.components().getOrDefault(FactionDataComponents.SKILL_NAME, Component.empty());
    }

    @Override
    public TagKey<IFaction<?>> allowedFactions() {
        return components().getOrDefault(FactionDataComponents.MINION_TASK_FACTIONS, FactionTags.ALL_FACTIONS);
    }

    @Override
    public boolean isAvailable(ILordPlayer player) {
        if (!IFaction.is(player.getFaction(), allowedFactions())) {
            return false;
        }
        Holder<? extends ISkill<?>> holder = components().get(FactionDataComponents.MINION_TASK_SKILL_REQUIREMENT);
        if (holder != null && !ISkillHandler.isSkillEnabled(player.asEntity(), holder)) {
            return false;
        }
        return true;
    }

    @NotNull
    @Override
    public ActivateResult<TState> activateTask(@Nullable Player lord, @Nullable IMinionEntity minion, IMinionData data) {
        return new ActivateResult<>(this.emptyState);
    }

    @Override
    public void deactivateTask(TState desc) {

    }

    @Override
    public void tickActive(TState desc, Supplier<Optional<IMinionEntity>> minionGetter, IMinionData minionData) {
        this.tickBackground(desc, minionData);
    }

    @Override
    public void tickBackground(TState desc, IMinionData minionData) {

    }

    @Nullable
    @Override
    public Codec<TState> stateCodec() {
        return this.stateCodec;
    }

    protected void triggerAdvancements(Player player) {
        if (player instanceof ServerPlayer) {
            FactionAdvancements.TRIGGER_MINION_ACTION.get().trigger(((ServerPlayer) player), this);
        }
    }

}
