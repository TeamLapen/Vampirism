package de.teamlapen.vampirism.client.renderer;

import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.client.renderer.entity.layers.ConvertedVampireEntityLayer;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import de.teamlapen.vampirism.mixin.client.LivingEntityRendererAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class VampirismClientEntityRegistry extends VampirismEntityRegistry {

    private static final Logger LOGGER = LogManager.getLogger();

    public VampirismClientEntityRegistry(BiFunction<IConvertingHandler.IDefaultHelper, @Nullable ResourceLocation, IConvertingHandler<?>> creator) {
        super(creator);
    }

    public void applyDataConvertibleOverlays(Map<EntityType<? extends PathfinderMob>, ResourceLocation> entries) {
        this.convertibleOverlay.clear();
        this.convertibleOverlay.putAll(this.convertibleOverlayAPI);
        this.convertibleOverlay.putAll(entries);
        syncOverlays();
    }

    public <I extends LivingEntity, U extends EntityModel<I>> void syncOverlays() {
        this.convertibleIdOverlay.clear();
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        this.convertibleIdOverlay.putAll(this.convertibleOverlay.entrySet().stream().filter(s -> {
            var texture = textureManager.getTexture(s.getValue());
            // call twice in case of missing texture
            texture = textureManager.getTexture(s.getValue());
            return texture != MissingTextureAtlasSprite.getTexture();
        }).collect(Collectors.toMap(x -> BuiltInRegistries.ENTITY_TYPE.getKey(x.getKey()).toString(), Map.Entry::getValue)));

        for (EntityType<? extends PathfinderMob> type: getConvertibleOverlay().keySet()) {
            LivingEntityRenderer<I, U> render = (LivingEntityRenderer<I, U>) Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(type);
            if (render == null) {
                LOGGER.error("Did not find renderer for {}", type);
                continue;
            }
            if(((LivingEntityRendererAccessor) render).getLayers().stream().noneMatch(s -> s instanceof ConvertedVampireEntityLayer<?,?>)) {
                render.addLayer(new ConvertedVampireEntityLayer<>(render, true));
            }
        }
    }
}
