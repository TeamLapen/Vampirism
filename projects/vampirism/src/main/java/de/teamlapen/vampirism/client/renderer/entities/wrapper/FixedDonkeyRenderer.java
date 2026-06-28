package de.teamlapen.vampirism.client.renderer.entities.wrapper;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse;

public class FixedDonkeyRenderer<T extends AbstractChestedHorse> extends net.minecraft.client.renderer.entity.DonkeyRenderer<T> {
    public FixedDonkeyRenderer(EntityRendererProvider.Context context) {
        super(context, EquipmentClientInfo.LayerType.DONKEY_SADDLE, ModelLayers.DONKEY_SADDLE, FixedDonkeyRenderer.Type.DONKEY, FixedDonkeyRenderer.Type.DONKEY_BABY);
    }
}
