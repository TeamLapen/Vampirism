package de.teamlapen.vampirism.util;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


public class PlayerSkinHelper {

    private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(0, 5, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<>());


    /**
     * Update the game profile in the background
     * Tries to return a game profile with fully loaded texture profile properties.
     * Otherwise it just returns the input profile.
     *
     * @param input    GameProfile containing at least the players name
     * @param callback Will be called with the populated profile. Might be called outside the main thread
     */
    public static void updateGameProfileAsync(final GameProfile input, Consumer<GameProfile> callback) {
        if (input != null) {
            if (input.isComplete() && input.getProperties().containsKey("textures")) {
                callback.accept(input);
            } else if (SkullTileEntity.profileCache != null && SkullTileEntity.sessionService != null) {
                THREAD_POOL.submit(() -> {
                    GameProfile gameprofile = input.getId() == null ? SkullTileEntity.profileCache.getGameProfileForUsername(input.getName()) : SkullTileEntity.profileCache.getProfileByUUID(input.getId()); //This might create race conditions with other game profile updates. Maybe this has to be moved to the main thread

                    if (gameprofile == null) {
                        gameprofile = input;
                    } else {
                        Property property = (Property) Iterables.getFirst(gameprofile.getProperties().get("textures"), (Object) null);

                        if (property == null) {
                            gameprofile = SkullTileEntity.sessionService.fillProfileProperties(gameprofile, true);
                        }
                    }
                    callback.accept(gameprofile);
                });
            } else {
                callback.accept(input);
            }
        } else {
            callback.accept(input);
        }

    }

    @OnlyIn(Dist.CLIENT)
    public static void obtainPlayerSkinPropertiesAsync(final GameProfile input, Consumer<Pair<ResourceLocation, Boolean>> callback) {
        updateGameProfileAsync(input, p -> {
            ResourceLocation loc;
            boolean alex;
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Minecraft.getInstance().getSkinManager().loadSkinFromCache(p);
            if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                MinecraftProfileTexture t = map.get(MinecraftProfileTexture.Type.SKIN);
                loc = Minecraft.getInstance().getSkinManager().loadSkin(t, MinecraftProfileTexture.Type.SKIN);
                alex = "slim".equals(t.getMetadata("model"));
            } else {
                loc = DefaultPlayerSkin.getDefaultSkin(p.getId());
                alex = "slim".equals(DefaultPlayerSkin.getSkinType(p.getId()));
            }
            callback.accept(Pair.of(loc, alex));
        });
    }
}