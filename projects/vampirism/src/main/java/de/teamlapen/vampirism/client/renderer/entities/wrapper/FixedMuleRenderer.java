package de.teamlapen.vampirism.client.renderer.entities.wrapper;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.DonkeyRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse;

public class FixedMuleRenderer<T extends AbstractChestedHorse> extends DonkeyRenderer<T> {
    public FixedMuleRenderer(EntityRendererProvider.Context context) {
        super(context, EquipmentClientInfo.LayerType.MULE_SADDLE, ModelLayers.MULE_SADDLE, DonkeyRenderer.Type.MULE, DonkeyRenderer.Type.MULE_BABY);
    }
}
