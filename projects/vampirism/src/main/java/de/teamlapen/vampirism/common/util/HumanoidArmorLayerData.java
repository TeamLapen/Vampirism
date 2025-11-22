package de.teamlapen.vampirism.common.util;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.jetbrains.annotations.ApiStatus;

public class HumanoidArmorLayerData {

    private static HumanoidRenderState renderState;

    public static HumanoidRenderState getRenderState() {
        return renderState;
    }

    @ApiStatus.Internal
    public static void setRenderState(HumanoidRenderState renderState) {
        HumanoidArmorLayerData.renderState = renderState;
    }

    @ApiStatus.Internal
    public static void reset() {
        renderState = null;
    }
}
