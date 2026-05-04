package de.teamlapen.vampirism.client.gui.overlay;

import de.teamlapen.faction.client.gui.overlay.BaseOverlay;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.network.packets.client.ClientboundDraculaEventPacket;
import de.teamlapen.vampirism.common.world.entity.dracula.DraculaEvent;
import de.teamlapen.vampirism.common.world.entity.dracula.FightStage;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

public class DraculaEventOverlay extends BaseOverlay {

    private static final Identifier BACKGROUND = VIdentifier.mod("textures/gui/overlay/dracula_event.png");
    private static final Identifier TEXTURE_STAGE = VIdentifier.mod("textures/gui/overlay/dracula_event_overlay_1.png");
    private static final Identifier TEXTURE_LOCKED = VIdentifier.mod("textures/gui/overlay/dracula_event_overlay_locked.png");

    @Nullable
    private DraculaEvent event;

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        if (this.event != null && this.event.getStage() != FightStage.NONE) {
            guiGraphics.nextStratum();
            Matrix3x2fStack pose = guiGraphics.pose();
            pose.pushMatrix();
            pose.scale(0.7f);
            int xPos = (int) ((guiGraphics.guiWidth() / 0.7f / 2f - 360/2f) );
            int yPos = 3;
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, xPos,yPos,0,0, 360,34,360,34);

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE_STAGE, xPos + 45,yPos + 12,45,12, (int) (294 * this.event.getPercentage()),10,360,34);

            if (this.event.isInVulnerable()) {
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE_LOCKED, xPos + 45,yPos + 12,45,12, 294,10,360,34);
            }
            pose.popMatrix();
        }
    }

    public void handle(ClientboundDraculaEventPacket packet) {
        switch (packet.operation()) {
            case ClientboundDraculaEventPacket.AddOperation addOperation -> {
                this.event = DraculaEvent.fromOperation(addOperation);
            }
            case ClientboundDraculaEventPacket.RemoveOperation removeOperation -> {
                if (event != null && event.id().equals(removeOperation.id())) {
                    this.event = null;
                }
            }
            case ClientboundDraculaEventPacket.UpdateOperation updateOperation -> {
                if (event != null && event.id().equals(updateOperation.id())) {
                    event.setPercentage(updateOperation.percentage());
                    event.setStage(updateOperation.stage());
                    event.setInVulnerable(updateOperation.vulnerable());
                }
            }
            case ClientboundDraculaEventPacket.UpdateProgressOperation updateProgressOperation -> {
                if (event != null && event.id().equals(updateProgressOperation.id())) {
                    event.setPercentage(updateProgressOperation.percentage());
                }
            }
            case ClientboundDraculaEventPacket.UpdateStageOperation updateStageOperation -> {
                if (event != null && event.id().equals(updateStageOperation.id())) {
                    event.setStage(updateStageOperation.stage());
                }
            }
            case ClientboundDraculaEventPacket.UpdateVulnerableOperation updateVulnerableOperation -> {
                if (event != null && event.id().equals(updateVulnerableOperation.id())) {
                    event.setInVulnerable(updateVulnerableOperation.vulnerable());
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + packet.operation());
        }
    }


}
