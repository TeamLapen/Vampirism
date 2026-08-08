package de.teamlapen.vampirism.client.models.items;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.core.ModRenderPipelines;
import de.teamlapen.vampirism.common.world.items.ShatteredArmorItem;
import de.teamlapen.vampirism.misc.extension.client.IItemStackRenderState;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;

/**
 * Renders the armor piece stored in the stack and repeats its geometry with {@link ModRenderPipelines#itemCrumbling()}
 * so that the item has the crack texture that is masked to the shape of the armor model.
 */
public class ShatteredArmorModel implements ItemModel {

    private final ItemModel fallback;

    public ShatteredArmorModel(ItemModel fallback) {
        this.fallback = fallback;
    }

    @Override
    public void update(ItemStackRenderState output, ItemStack item, ItemModelResolver resolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        output.appendModelIdentityElement(this);
        ItemStack armor = ShatteredArmorItem.contained(item);
        if (armor.isEmpty()) {
            this.fallback.update(output, item, resolver, displayContext, level, owner, seed);
            return;
        }

        IItemStackRenderState state = (IItemStackRenderState) output;
        int armorLayer = state.vampirism$activeLayerCount();
        resolver.appendItemLayers(output, armor, displayContext, level, owner, seed);
        int overlayLayer = state.vampirism$activeLayerCount();
        if (!hasOnlyQuadLayers(state.vampirism$layers(), armorLayer, overlayLayer)) return;

        resolver.appendItemLayers(output, armor, displayContext, level, owner, seed);
        ItemStackRenderState.LayerRenderState[] layers = state.vampirism$layers();
        for (int i = overlayLayer; i < state.vampirism$activeLayerCount(); i++) {
            layers[i].setFoilType(ItemStackRenderState.FoilType.NONE);
            layers[i].prepareQuadList().replaceAll(ShatteredArmorModel::crumblingQuad);
        }
    }

    /**
     * Not all layers have quads (at least the special renderer), so we need to check if there are any before trying to
     * render, so such models are left like they are just in case.
     */
    private static boolean hasOnlyQuadLayers(ItemStackRenderState.LayerRenderState[] layers, int from, int to) {
        for (int i = from; i < to; i++) {
            if (layers[i].prepareQuadList().isEmpty()) {
                return false;
            }
        }
        
        return to > from;
    }

    /**
     * Turns one face of the armor model into the matching face of the crack overlay. The four corners are kept exactly
     * where they were, so the copy sits right on top of the original and the depth test can mask it to the pixels the
     * armor actually drew.
     */
    private static BakedQuad crumblingQuad(BakedQuad quad) {
        Direction direction = quad.direction();
        BakedQuad.MaterialInfo material = quad.materialInfo();
        return new BakedQuad(
                quad.position0(), quad.position1(), quad.position2(), quad.position3(),
                crumblingUV(quad.position0(), direction), crumblingUV(quad.position1(), direction), crumblingUV(quad.position2(), direction), crumblingUV(quad.position3(), direction),
                direction,
                new BakedQuad.MaterialInfo(material.sprite(), material.layer(), ModRenderPipelines.itemCrumbling(), -1, material.shade(), 0, material.ambientOcclusion())
        );
    }

    /**
     * The crack texture is standalone, outside the atlas where the item texture is located, so the coordinates the quad
     * came with would point at a random sliver of it. They are rebuilt from the corner's position instead, which runs
     * from 0 to 1 across the same box the item texture covers, so the cracks end up spread over the item exactly.
     * <p>
     * A texture coordinate only has two axes, so the one the face points along is left out: the front and back of a flat
     * item vary in x and y, the extruded top and bottom edges in x and z, and the side edges in z and y.
     */
    private static long crumblingUV(Vector3fc position, Direction direction) {
        return switch (direction.getAxis()) {
            case X -> pack(position.z(), position.y());
            case Y -> pack(position.x(), position.z());
            case Z -> pack(position.x(), position.y());
        };
    }

    /**
     * Packs one corner into the single long a quad stores its coordinates in. The vertical axis is flipped on the way
     * because a texture counts its rows downwards from the top while the model counts upwards (for some reason), and
     * the clamp stops a model that reaches outside the usual box from sampling past the edge of the crack texture.
     */
    private static long pack(float horizontal, float vertical) {
        return UVPair.pack(Mth.clamp(horizontal, 0f, 1f), Mth.clamp(1f - vertical, 0f, 1f));
    }

    public record Unbaked() implements ItemModel.Unbaked {

        public static final Identifier ID = VIdentifier.mod("shattered_armor");
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public MapCodec<Unbaked> type() {
            return CODEC;
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext context, Matrix4fc transformation) {
            return new ShatteredArmorModel(context.missingItemModel(transformation));
        }
    }
}