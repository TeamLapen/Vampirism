package de.teamlapen.vampirism.mixin;

import net.minecraft.client.gui.ClientBossInfo;
import net.minecraft.client.gui.overlay.BossOverlayGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@Mixin(BossOverlayGui.class)
public interface BossOverlayGuiAccessor {

    @Accessor(value = "mapBossInfos")
    Map<UUID, ClientBossInfo> getMapBossInfos();
}
