package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.mixin.BossOverlayGuiAccessor;
import de.teamlapen.vampirism.network.UpdateMultiBossInfoPacket;
import de.teamlapen.vampirism.world.DummyBossInfo;
import de.teamlapen.vampirism.world.MultiBossInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;
import java.util.stream.Collectors;

public class CustomBossInfoOverlay extends AbstractGui {
    private static final ResourceLocation GUI_BARS_TEXTURES = new ResourceLocation("textures/gui/bars.png");
    private final Minecraft client;
    private final Map<UUID, MultiBossInfo> bossInfoMap = new LinkedHashMap<>();

    public CustomBossInfoOverlay() {
        this.client = Minecraft.getInstance();
    }

    @SubscribeEvent
    public void onRenderOverlayBoss(RenderGameOverlayEvent.Post event){
        if (event.getType() == RenderGameOverlayEvent.ElementType.BOSSHEALTH){
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableBlend();
            render(event.getMatrixStack());
            RenderSystem.disableBlend();
        }
    }

    public void render(MatrixStack stack) {
        int i = Minecraft.getInstance().getMainWindow().getScaledWidth();
        int j = 12+ ((BossOverlayGuiAccessor) this.client.ingameGUI.getBossOverlay()).getMapBossInfos().size() *(10 + this.client.fontRenderer.FONT_HEIGHT);
        for (MultiBossInfo value : bossInfoMap.values()) {
            int k = i / 2 - 91;
            net.minecraftforge.client.event.RenderGameOverlayEvent.BossInfo event =
                    net.minecraftforge.client.ForgeHooksClient.bossBarRenderPre(stack, this.client.getMainWindow(), new DummyBossInfo(value.getUniqueId(), value.getName()), k, j, 10 + this.client.fontRenderer.FONT_HEIGHT);
            if (!event.isCanceled()) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getInstance().getTextureManager().bindTexture(GUI_BARS_TEXTURES);
            this.render(stack, k, j, value);
            ITextComponent itextcomponent = value.getName();
            int l = this.client.fontRenderer.getStringPropertyWidth(itextcomponent);
            int i1 = i / 2 - l / 2;
            int j1 = j - 9;
            this.client.fontRenderer.func_243246_a(stack, itextcomponent, (float) i1, (float) j1, 16777215);
        }
            j += event.getIncrement() ;
            net.minecraftforge.client.ForgeHooksClient.bossBarRenderPost(stack, this.client.getMainWindow());

            if (j >= this.client.getMainWindow().getScaledHeight() / 3) {
                break;
            }
        }
    }

    private void render(MatrixStack stack, int k, int j, MultiBossInfo value) {
        int textureStart = 0;
        List<MultiBossInfo.Entry> s = value.getEntries().values().stream().sorted(Comparator.comparingInt(MultiBossInfo.Entry::getOrdinal)).collect(Collectors.toList());
        for (int i = 0; i < s.size(); i++) {
            MultiBossInfo.Entry entry = s.get(i);
            int width = (int) (entry.getPercentage() * 182);
            if (i == s.size()-1) {
                if (textureStart + width < 182){
                    width = 182 - textureStart;
                }
            }
            this.blit(stack, k + textureStart, j, textureStart, entry.getColor().ordinal() * 5 * 2 +5, width, 5);
            textureStart+=width;
        }
    }

    public void read(UpdateMultiBossInfoPacket packet) {
        if (packet.getOperation() == SUpdateBossInfoPacket.Operation.ADD) {
            this.bossInfoMap.put(packet.getUniqueId(), new MultiBossInfo(packet));
        } else if (packet.getOperation() == SUpdateBossInfoPacket.Operation.REMOVE) {
            this.bossInfoMap.remove(packet.getUniqueId());
        }else {
            this.bossInfoMap.get(packet.getUniqueId()).updateFromPackage(packet);
        }
    }
}
