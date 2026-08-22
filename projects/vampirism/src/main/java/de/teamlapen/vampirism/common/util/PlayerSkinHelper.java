package de.teamlapen.vampirism.common.util;

import com.google.common.collect.ImmutableMap;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.util.supporter.Supporter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.core.ClientAsset;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientResourceLoadFinishedEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.jetbrains.annotations.UnmodifiableView;

import javax.annotation.concurrent.Immutable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


public class PlayerSkinHelper {

    private final Map<String, PlayerSkin> skins = new HashMap<>();
    private final Map<String, PlayerSkin> skinsReadonly = Collections.unmodifiableMap(skins);
    private final Map<String, PlayerSkin> remoteSkins = new HashMap<>();

    /**
     * Load required resources when resources changes
     */
    @SubscribeEvent
    public void onResourcesLoad(ClientResourceLoadFinishedEvent event) {
        var skins = loadBuiltInSkins();
        checkMissingSkins(skins);
        this.skins.clear();
        this.skins.putAll(skins);
    }

    /**
     * Reload missing textures after supporters are loaded
     */
    @SubscribeEvent
    public void onLevelLoad(LevelEvent.Load event) {
        checkMissingSkins(skins);
    }

    @UnmodifiableView
    public Map<String, PlayerSkin> getSkins() {
        return this.skinsReadonly;
    }


    private Map<String, PlayerSkin> loadBuiltInSkins() {
        return Minecraft.getInstance().getResourceManager().listResources("textures/entity/advanced", s -> s.getPath().endsWith(".png")).keySet().stream()
                .filter(x -> x.getNamespace().equals(REFERENCE.MODID))
                .map(texturePath -> {
                    String[] pathSegments = texturePath.getPath().split("/");
                    String fileName = pathSegments[pathSegments.length - 1];
                    PlayerModelType b = fileName.endsWith("_slim.png") ? PlayerModelType.SLIM : PlayerModelType.WIDE;
                    String id = fileName.substring(0, fileName.length() - (fileName.endsWith("_slim.png") ? 9 : 4));
                    return new PlayerSkin(new ClientAsset.ResourceTexture(VIdentifier.mc(id), texturePath), null, null, b, false);
                })
                .collect(Collectors.toMap(x -> x.body().id().getPath(), x -> x));
    }

    private void checkMissingSkins(Map<String, PlayerSkin> skins) {
        this.skins.forEach((name, _) -> remoteSkins.remove(name));
        List<String> list = VampirismMod.services().supporterManager().getSupporter().map(Supporter::player).filter(player -> !skins.containsKey(player)).toList();
        if (list.isEmpty()) {
            return;
        }
        loadPlayerSkins(skins, list);
    }

    private void loadPlayerSkins(Map<String, PlayerSkin> skins, List<String> ids) {
        PlayerSkinRenderCache playerSkinRenderCache = Minecraft.getInstance().playerSkinRenderCache();
        var list = ids.stream().filter(x -> !remoteSkins.containsKey(x)).map(ResolvableProfile::createUnresolved).map(playerSkinRenderCache::lookup).toList();
        CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).thenAccept(_ -> list.forEach(x -> x.join().ifPresent(y -> remoteSkins.put(y.gameProfile().name(), y.playerSkin())))).thenAccept(_ -> skins.putAll(remoteSkins));
    }
}