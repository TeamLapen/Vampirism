package de.teamlapen.vampirism.common.util;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.settings.Supporter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;


public class PlayerSkinHelper {

    @SubscribeEvent
    public void onLoadLevel(LevelEvent.Load event) {
        loadPlayerSkins();
    }
    public static void loadPlayerSkins() {
        PlayerSkinRenderCache playerSkinRenderCache = Minecraft.getInstance().playerSkinRenderCache();
        List<CompletableFuture<Optional<PlayerSkinRenderCache.RenderInfo>>> list = VampirismMod.services().supporterManager().getSupporter().map(Supporter::texture).map(ResolvableProfile::createUnresolved).map(playerSkinRenderCache::lookup).toList();
        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).thenAccept(v -> list.forEach(CompletableFuture::join));
    }


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

    public static void resolve(@NotNull ResolvableProfile profile, Consumer<Optional<PlayerSkinRenderCache.RenderInfo>> callback) {
        Minecraft.getInstance().playerSkinRenderCache().lookup(profile).thenAccept(callback);
    }

}