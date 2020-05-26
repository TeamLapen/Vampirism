package de.teamlapen.vampirism.client.render;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.vampirism.client.model.MinionModel;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.util.IPlayerOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.function.Predicate;

/**
 * Overlays the body with a player skin
 */
@OnlyIn(Dist.CLIENT)
public class LayerPlayerBodyOverlay<T extends MinionEntity & IPlayerOverlay, M extends MinionModel<T>> extends LayerRenderer<T, M> {

    private final BipedRenderer<T, M> renderBiped;
    private final Predicate<T> renderPredicate;

    public LayerPlayerBodyOverlay(BipedRenderer<T, M> renderBiped, Predicate<T> renderPredicate) {
        super(renderBiped);
        this.renderBiped = renderBiped;
        this.renderPredicate = renderPredicate;
    }

    @Override
    public void render(T entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float scale) {
        if (!renderPredicate.test(entityIn)) return;
        ResourceLocation loc = DefaultPlayerSkin.getDefaultSkinLegacy();
        GameProfile prof = entityIn.getOverlayPlayerProfile();
        if (prof != null) {
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Minecraft.getInstance().getSkinManager().loadSkinFromCache(prof);
            if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                loc = Minecraft.getInstance().getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            }

        } else {
            return;
        }

        renderBiped.bindTexture(loc);
        GlStateManager.pushMatrix();

        GlStateManager.setProfile(GlStateManager.Profile.PLAYER_SKIN);

        this.renderBiped.getEntityModel().renderBody(entityIn, scale);


        GlStateManager.unsetProfile(GlStateManager.Profile.PLAYER_SKIN);

        GlStateManager.popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}
