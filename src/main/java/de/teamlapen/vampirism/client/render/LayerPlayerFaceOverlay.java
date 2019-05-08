package de.teamlapen.vampirism.client.render;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import de.teamlapen.vampirism.util.IPlayerFace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Renders an overlay over the entities face
 *
 * @param <T> Has to be the same as Q
 */
@SideOnly(Side.CLIENT)
public class LayerPlayerFaceOverlay<T extends EntityCreature & IPlayerFace> implements LayerRenderer<T> {

    private final RenderBiped<T> renderBiped;
    private static final Map<UUID, NetworkPlayerInfo> playerInfoMap = new HashMap<>();

    public LayerPlayerFaceOverlay(RenderBiped<T> renderBiped) {
        this.renderBiped = renderBiped;
    }

    private static NetworkPlayerInfo getPlayerInfo(GameProfile p) {
        NetworkPlayerInfo i = playerInfoMap.get(p.getId());
        if (i == null) {
            i = new NetworkPlayerInfo(p);
            playerInfoMap.put(p.getId(), i);
        }
        return i;
    }

    @Override
    public void doRenderLayer(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        ResourceLocation loc = DefaultPlayerSkin.getDefaultSkinLegacy();
        GameProfile prof = entitylivingbaseIn.getPlayerFaceProfile();
        if (prof != null) {
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Minecraft.getMinecraft().getSkinManager().loadSkinFromCache(prof);
            if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                loc = Minecraft.getMinecraft().getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            }

        }

            renderBiped.bindTexture(loc);
            GlStateManager.pushMatrix();
            if (entitylivingbaseIn.isSneaking()) {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }
        GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);

        ((ModelBiped) this.renderBiped.getMainModel()).bipedHead.render(scale);
            GlStateManager.popMatrix();


    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}
