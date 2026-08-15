package de.teamlapen.vampirism.common.util;

import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


public class PlayerSkinHelper  extends de.teamlapen.faction.common.util.PlayerSkinHelper {

    @SubscribeEvent
    public void onLoadLevel(LevelEvent.Load event) {
    }

}