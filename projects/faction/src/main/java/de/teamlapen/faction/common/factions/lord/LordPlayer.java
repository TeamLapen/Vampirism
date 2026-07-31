package de.teamlapen.faction.common.factions.lord;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.level.FactionUpdate;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.factions.lord.LordTitles;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.core.FactionAdvancements;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.factions.FactionExtension;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.factions.minions.MinionWorldData;
import de.teamlapen.faction.common.factions.minions.PlayerMinionController;
import de.teamlapen.faction.common.tags.FactionTaskTags;
import de.teamlapen.faction.common.util.AttachmentSynchronization;
import de.teamlapen.sync.AttachmentSync;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jetbrains.annotations.Nullable;

public class LordPlayer extends FactionExtension implements ILordPlayer {

    private IPlayableFaction.TitleGender titleGender = IPlayableFaction.TitleGender.UNKNOWN;
    private int currentLordLevel = 0;

    public LordPlayer(Player player) {
        super(player);
    }

    @Override
    public IPlayableFaction.TitleGender titleGender() {
        return this.titleGender;
    }

    @Override
    public int getLordLevel() {
        return this.currentLordLevel;
    }

    @Override
    public int getMaxLordLevel() {
        return getFaction().value().getHighestLordLevel();
    }

    @Override
    public int getMaxMinions() {
        return getLordLevel() * FactionConfig.server().minionPerLordLevel.get();
    }

    @Override
    public @Nullable Component getLordTitle() {
        return getFaction().components().getOrDefault(FactionDataComponents.LORD_TITLES, LordTitles.EMPTY).get(getLordLevel(), titleGender());
    }

    @Override
    public @Nullable Component getLordTitleShort() {
        return getFaction().components().getOrDefault(FactionDataComponents.LORD_TITLES, LordTitles.EMPTY).getShort(getLordLevel(), titleGender());
    }

    @Override
    public AttachmentType<?> getType() {
        return de.teamlapen.faction.common.core.FactionAttachments.LORD_PLAYER.get();
    }

    @Override
    public boolean setTitleGender(boolean female) {
        var gender = female ? IPlayableFaction.TitleGender.FEMALE : IPlayableFaction.TitleGender.MALE;
        return this.setTitleGender(gender);
    }

    @Override
    public boolean setTitleGender(IPlayableFaction.TitleGender female) {
        this.titleGender = female;
        player.refreshDisplayName();
        sync();
        return true;
    }

    @Override
    public void setLevel(FactionUpdate change) {
        int oldLordLevel = this.currentLordLevel;

        if (change.getLevel() != getFaction().value().getHighestReachableLevel()) {
            this.currentLordLevel = 0;
        } else if (change.hasLordLevelChange()) {
            this.currentLordLevel = Math.clamp(change.getLordLevel(), 0, getMaxLordLevel());
        }

        if (this.currentLordLevel < oldLordLevel) {
            resetLordTasks();
        }

        MinionWorldData.getData(this.player.level()).ifPresent(data -> {
            PlayerMinionController c = data.getController(this.player.getUUID());
            if (c != null) {
                c.setMaxMinions(this.getFaction(), getMaxMinions());
            }
        });

        if (player instanceof ServerPlayer serverPlayer) {
            FactionAdvancements.TRIGGER_FACTION.get().trigger(serverPlayer, change.getFaction(), this.currentLordLevel);
        }
        sync();
    }

    /**
     * Reset all lord tasks that should be available again for the player's current lord level.
     */
    @Override
    public void resetLordTasks() {
        getTaskManager().ifPresent(manager -> {
            this.player.level().registryAccess().lookupOrThrow(FactionRegistries.Keys.TASK).getTagOrEmpty(FactionTaskTags.AWARDS_LORD_LEVEL).forEach(holder -> {
                holder.unwrapKey().ifPresent(manager::resetUniqueTask);
            });
        });
    }

    @Override
    protected void registerProperties() {
        super.registerProperties();
        registerProperty(FIdentifier.mod("lord_level")).simple(0, () -> this.currentLordLevel, l -> this.currentLordLevel = l);
        registerProperty(FIdentifier.mod("title_gender")).simple(IPlayableFaction.TitleGender.CODEC).defaultValue(IPlayableFaction.TitleGender.UNKNOWN).provider(() -> this.titleGender).commonLoader(l -> this.titleGender = l, Enum::compareTo).register();
    }

    @Override
    public void updateMinionAttributes(boolean increasedStats) {
        MinionWorldData.getData(this.player.level()).ifPresent(a -> {
            a.getOrCreateController(this).forEach((data, minion) -> {
                data.setIncreasedStats(increasedStats);
                minion.ifPresent(x -> {
                    x.updateAttributes();
                    x.sync();
                });
            });
        });
    }

    @Override
    public void onLeaveFaction(Player player) {
        this.currentLordLevel = 0;
        resetLordTasks();
        MinionWorldData.getData(this.player.level()).ifPresent(data -> data.removeController(this.player.getUUID()));
    }

    public static class AttachmentOptions extends AttachmentSynchronization.PlayerOptions<LordPlayer> {
        @Override
        protected LordPlayer create(Player player) {
            return new LordPlayer(player);
        }
    }
}
