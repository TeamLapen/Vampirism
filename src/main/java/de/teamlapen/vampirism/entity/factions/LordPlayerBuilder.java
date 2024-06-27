package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.api.entity.factions.ILordPlayerBuilder;
import de.teamlapen.vampirism.api.entity.factions.ILordPlayerEntry;
import de.teamlapen.vampirism.api.entity.factions.ILordTitleProvider;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class LordPlayerBuilder<T extends IFactionPlayer<T>> implements ILordPlayerBuilder<T> {

    protected int maxLevel = 0;
    protected ILordTitleProvider lordTitleFunction = (LordTitleProvider) (a, b) -> Component.literal("Lord " + a);

    @Override
    public @NotNull LordPlayerBuilder<T> lordLevel(int level) {
        this.maxLevel = level;
        return this;
    }

    @Override
    public ILordPlayerBuilder<T> lordTitle(@NotNull ILordTitleProvider lordTitleFunction) {
        this.lordTitleFunction = lordTitleFunction;
        return this;
    }

    @Override
    public ILordPlayerEntry build() {
        return new LordPlayerEntry(maxLevel, lordTitleFunction);
    }

    public interface LordTitleProvider extends ILordTitleProvider {

        @Override
        default Component getShort(int level, IPlayableFaction.TitleGender titleGender) {
            return getLordTitle(level, titleGender);
        }
    }
}