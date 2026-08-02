package de.teamlapen.faction.api.factions.lord;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.util.ModStreamCodecs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public record LordTitles(Table<Integer, IPlayableFaction.TitleGender, Component> titles, Table<Integer, IPlayableFaction.TitleGender, Component> titleShort) {

    public static final StreamCodec<RegistryFriendlyByteBuf, LordTitles> STREAM_CODEC = StreamCodec.composite(
            ModStreamCodecs.table(ByteBufCodecs.VAR_INT, IPlayableFaction.TitleGender.STREAM_CODEC, ComponentSerialization.STREAM_CODEC), LordTitles::titles,
            ModStreamCodecs.table(ByteBufCodecs.VAR_INT, IPlayableFaction.TitleGender.STREAM_CODEC, ComponentSerialization.STREAM_CODEC), LordTitles::titleShort,
            LordTitles::new
    );
    public static final LordTitles EMPTY = new LordTitles(ImmutableTable.of(), ImmutableTable.of());

    @Nullable
    public Component get(int level, IPlayableFaction.TitleGender gender) {
        return this.titles.get(level, gender);
    }

    @Nullable
    public Component getShort(int level, IPlayableFaction.TitleGender gender) {
        return this.titleShort.get(level, gender);
    }

    public static LordTitles provideDefault(Identifier id, int maxLevel) {
        return provide(maxLevel, new ILordTitleProvider() {
            @Override
            public Component getLordTitle(int level, IPlayableFaction.TitleGender titleGender) {
                var genderText = switch (titleGender) {
                    case FEMALE -> "female";
                    default -> "male";
                };
                return Component.translatable(id.toLanguageKey("lord_title", "%s.%s".formatted(genderText, level)));
            }

            @Override
            public Component getShort(int level, IPlayableFaction.TitleGender titleGender) {
                var genderText = switch (titleGender) {
                    case FEMALE -> "female";
                    default -> "male";
                };
                return Component.translatable(id.toLanguageKey("lord_title", "%s.%s.short".formatted(genderText, level)));
            }
        });
    }
    public static LordTitles provide(int maxLevel, ILordTitleProvider provider) {
        var builder = ImmutableTable.<Integer, IPlayableFaction.TitleGender, Component>builder();
        var shortBuilder = ImmutableTable.<Integer, IPlayableFaction.TitleGender, Component>builder();
        for (IPlayableFaction.TitleGender value : IPlayableFaction.TitleGender.values()) {
            for (int i = 1; i <= maxLevel; i++) {
                Component lordTitle = provider.getLordTitle(i, value);
                if (lordTitle != null) {
                    builder.put(i, value, lordTitle);
                }
                Component shortLordTitle = provider.getShort(i, value);
                if (shortLordTitle != null) {
                    shortBuilder.put(i, value, shortLordTitle);
                }
            }
        }

        return new LordTitles(builder.build(), shortBuilder.build());
    }
}
