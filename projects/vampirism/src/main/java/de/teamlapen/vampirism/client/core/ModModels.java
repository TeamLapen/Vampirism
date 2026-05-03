package de.teamlapen.vampirism.client.core;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

public class ModModels {

    @Unmodifiable
    public static Map<DyeColor, StandaloneModelKey<BlockStateModelPart>> COFFIN_BOTTOM_KEYS = createCoffinBottomKeys();

    @Unmodifiable
    public static StandaloneModelKey<BlockStateModelPart> COFFIN_TOP_KEY = new StandaloneModelKey<>(new CoffinTopModelKey());

    private static Map<DyeColor, StandaloneModelKey<BlockStateModelPart>> createCoffinBottomKeys() {
        Map<DyeColor, StandaloneModelKey<BlockStateModelPart>> map = new java.util.HashMap<>(Map.ofEntries());

        for (DyeColor color : DyeColor.values()) {
            map.put(color, new StandaloneModelKey<>(new CoffinBottomModelKey(color)));
        }

        return map;
    }

    public record CoffinBottomModelKey(DyeColor dye) implements ModelDebugName {

        @Override
        public @NotNull String debugName() {
            return "coffin_bottom_" + dye.getName();
        }
    }

    public record CoffinTopModelKey() implements ModelDebugName {

        @Override
        public @NotNull String debugName() {
            return "coffin_top";
        }
    }

    public record BloodSphereModelKey() implements ModelDebugName {

        @Override
        public @NotNull String debugName() {
            return "blood_sphere";
        }
    }
}
