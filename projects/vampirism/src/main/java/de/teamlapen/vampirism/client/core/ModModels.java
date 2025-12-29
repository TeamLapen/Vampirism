package de.teamlapen.vampirism.client.core;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public class ModModels {

    @Unmodifiable
    public static Table<CoffinType, DyeColor, StandaloneModelKey<BlockStateModel>> COFFIN_KEYS = createCoffinKeys();

    private static Table<CoffinType, DyeColor, StandaloneModelKey<BlockStateModel>> createCoffinKeys() {
        var builder = ImmutableTable.<CoffinType, DyeColor, StandaloneModelKey<BlockStateModel>>builder();

        for (CoffinType type : CoffinType.values()) {
            for (DyeColor color : DyeColor.values()) {
                builder.put(type, color, new StandaloneModelKey<>(new CoffinModelKey(color, type)));
            }
        }

        return builder.build();
    }

    public enum CoffinType {
        BOTTOM("bottom", "_bottom"),
        TOP("top", "_top"),
        MAIN("main", "");

        private final String name;
        private final String modelSuffix;

        CoffinType(String name, String modelSuffix) {
            this.name = name;
            this.modelSuffix = modelSuffix;
        }

        public @NotNull String getName() {
            return name;
        }

        public @NotNull String getModelSuffix() {
            return modelSuffix;
        }
    }

    public record CoffinModelKey(DyeColor dye, CoffinType type) implements ModelDebugName {

        @Override
        public @NotNull String debugName() {
            return "coffin_" + dye.getName() + "_" + type;
        }
    }
}
