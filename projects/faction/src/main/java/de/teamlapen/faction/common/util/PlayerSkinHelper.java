package de.teamlapen.faction.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerSkinHelper {

    public static void getPlayerRenderInfo(@Nullable String name, Consumer<Optional<PlayerSkinRenderCache.RenderInfo>> callback) {
        callback.accept(Optional.empty());
        if (name == null) return;
        resolve(ResolvableProfile.createUnresolved(name), callback);
    }

    public static void getPlayerRenderInfo(@Nullable UUID name, Consumer<Optional<PlayerSkinRenderCache.RenderInfo>> callback) {
        callback.accept(Optional.empty());
        if (name == null) return;
        resolve(ResolvableProfile.createUnresolved(name), callback);
    }

    public static void resolve(ResolvableProfile profile, Consumer<Optional<PlayerSkinRenderCache.RenderInfo>> callback) {
        Minecraft.getInstance().playerSkinRenderCache().lookup(profile).thenAccept(callback);
    }
}
