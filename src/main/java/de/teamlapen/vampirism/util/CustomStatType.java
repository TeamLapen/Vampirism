package de.teamlapen.vampirism.util;

import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.StatType;
import org.jetbrains.annotations.NotNull;

public class CustomStatType<T> extends StatType<T> {

    private final StatFormatter defaultFormatter;

    public CustomStatType(Registry<T> pRegistry, Component pDisplayName, StatFormatter defaultFormatter) {
        super(pRegistry, pDisplayName);
        this.defaultFormatter = defaultFormatter;
    }

    @Override
    public @NotNull Stat<T> get(@NotNull T pValue) {
        return this.get(pValue, this.defaultFormatter);
    }
}
