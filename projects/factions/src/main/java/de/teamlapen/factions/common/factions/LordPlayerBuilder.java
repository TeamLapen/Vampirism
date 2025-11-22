package de.teamlapen.factions.common.factions;

import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.api.factions.lord.ILordPlayerBuilder;
import de.teamlapen.factions.api.factions.lord.ILordPlayerEntry;
import de.teamlapen.factions.api.factions.lord.ILordTitleProvider;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LordPlayerBuilder<T extends IFactionPlayer<T>> implements ILordPlayerBuilder<@NotNull T> {

    protected int maxLevel = 0;
    protected ILordTitleProvider lordTitleFunction = (LordTitleProvider) (a, b) -> Component.literal("Lord " + a);

    @Override
    public LordPlayerBuilder<T> lordLevel(int level) {
        this.maxLevel = level;
        return this;
    }

    @Override
    public ILordPlayerBuilder<@NotNull T> lordTitle(ILordTitleProvider lordTitleFunction) {
        this.lordTitleFunction = lordTitleFunction;
        return this;
    }

    @Override
    public ILordPlayerEntry build() {
        return new LordPlayerEntry(maxLevel, lordTitleFunction);
    }

    public interface LordTitleProvider extends ILordTitleProvider {

        @Nullable
        @Override
        default Component getShort(int level, IPlayableFaction.TitleGender titleGender) {
            return getLordTitle(level, titleGender);
        }
    }
}