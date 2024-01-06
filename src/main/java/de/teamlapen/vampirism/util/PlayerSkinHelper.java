package de.teamlapen.vampirism.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;


public class PlayerSkinHelper {

    public static void obtainPlayerSkinPropertiesAsync(final GameProfile input, @NotNull Consumer<Pair<ResourceLocation, PlayerModelType>> callback) {
        Minecraft.getInstance().getSkinManager().getOrLoad(input).thenAccept(skin -> {
            callback.accept(Pair.of(skin.texture(), fromVanilla(skin.model())));
        });
    }

    public static PlayerSkin.Model toVanilla(PlayerModelType type) {
        return switch (type) {
            case WIDE -> PlayerSkin.Model.WIDE;
            case SLIM -> PlayerSkin.Model.SLIM;
        };
    }

    public static PlayerModelType fromVanilla(PlayerSkin.Model type) {
        return switch (type) {
            case WIDE -> PlayerModelType.WIDE;
            case SLIM -> PlayerModelType.SLIM;
        };
    }
}