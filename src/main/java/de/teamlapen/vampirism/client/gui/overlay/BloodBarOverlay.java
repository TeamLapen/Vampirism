package de.teamlapen.vampirism.client.gui.overlay;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.modcompat.IMCHandler;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class BloodBarOverlay extends GuiComponent implements IGuiOverlay {
    private final ResourceLocation icons = new ResourceLocation(REFERENCE.MODID + ":textures/gui/icons.png");
    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public void render(ForgeGui gui, PoseStack stack, float partialTicks, int width, int height) {
        if (this.mc.player != null && Helper.isVampire(this.mc.player) && !IMCHandler.requestedToDisableBloodbar) {
            if (this.mc.gameMode.hasExperience() && this.mc.player.isAlive()) {
                VampirePlayer.getOpt(this.mc.player).map(VampirePlayer::getBloodStats).ifPresent(stats -> {
                            GlStateManager._enableBlend();

                            RenderSystem.setShaderTexture(0, icons);
                            int left = this.mc.getWindow().getGuiScaledWidth() / 2 + 91;
                            int top = this.mc.getWindow().getGuiScaledHeight() - ((ForgeGui) this.mc.gui).rightHeight;
                            ((ForgeGui) this.mc.gui).rightHeight += 10;
                            int blood = stats.getBloodLevel();
                            int maxBlood = stats.getMaxBlood();
                            int blood2 = blood - 20;
                            int maxBlood2 = maxBlood - 20;
                            for (int i = 0; i < 10; ++i) {
                                int idx = i * 2 + 1;
                                int x = left - i * 8 - 9;

                                // Draw Background
                                blit(stack, x, top, 0, idx <= maxBlood2 ? 9 : 0, 9, 9);

                                if (idx < blood) {
                                    blit(stack, x, top, 9, idx < blood2 ? 9 : 0, 9, 9);
                                    if (idx == blood2) {
                                        blit(stack, x, top, 18, 9, 9, 9);
                                    }
                                } else if (idx == blood) {
                                    blit(stack, x, top, 18, 0, 9, 9);
                                }
                            }
                            RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
                            GlStateManager._disableBlend();
                        }
                );
            }
        }
    }
}
