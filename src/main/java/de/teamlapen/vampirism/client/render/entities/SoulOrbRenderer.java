package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.entity.SoulOrbEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;


@OnlyIn(Dist.CLIENT)
public class SoulOrbRenderer extends ThrownItemRenderer<SoulOrbEntity> {

    public SoulOrbRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context);
    }

    @Override
    public boolean shouldRender(@NotNull SoulOrbEntity livingEntity, @NotNull Frustum camera, double camX, double camY, double camZ) { //shouldRender
        boolean flag = true;
        if (Minecraft.getInstance().player != null) {
            flag = !livingEntity.isInvisibleTo(Minecraft.getInstance().player);
        }
        return flag && super.shouldRender(livingEntity, camera, camX, camY, camZ);
    }


}
