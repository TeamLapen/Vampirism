package de.teamlapen.faction.common.factions.lord;

import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.LevelingChange;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.factions.lord.LordTitles;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.factions.minions.MinionWorldData;
import de.teamlapen.sync.AttachmentSync;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LordPlayer extends AttachmentSync implements ILordPlayer {

    private final Player player;

    private IPlayableFaction.TitleGender titleGender = IPlayableFaction.TitleGender.UNKNOWN;
    private int currentLordLevel = 0;

    public LordPlayer(Player player) {
        this.player = player;
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
        return 0;
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
    public Holder<? extends IPlayableFaction<?>> getFaction() {
        return null;
    }

    @Override
    public Player asEntity() {
        return this.player;
    }

    @Override
    public AttachmentType<?> getType() {
        return null;
    }

    public boolean setTitleGender(boolean female) {
        var gender = female ? IPlayableFaction.TitleGender.FEMALE : IPlayableFaction.TitleGender.MALE;
        return this.setTitleGender(gender);
    }

    public boolean setTitleGender(IPlayableFaction.TitleGender female) {
        this.titleGender = female;
        player.refreshDisplayName();
        sync();
        return true;
    }

    @Override
    public void setLevel(LevelingChange change) {
        if (change.getNewLevel() != getFaction().value().getHighestReachableLevel()) {
            this.currentLordLevel = 0;
            return;
        }
        if (change.hasLordLevelChange()) {
            this.currentLordLevel = change.getNewLordLevel();
        }
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
    }
}
