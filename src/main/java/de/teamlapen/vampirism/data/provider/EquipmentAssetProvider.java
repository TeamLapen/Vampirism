package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.equipment.EquipmentAsset;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static de.teamlapen.vampirism.core.ModArmorMaterials.Asset.*;

public class EquipmentAssetProvider extends net.minecraft.client.data.models.EquipmentAssetProvider {

    public EquipmentAssetProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void registerModels(@NotNull BiConsumer<ResourceKey<EquipmentAsset>, EquipmentClientInfo> output) {
        Stream.of(HUNTER_COAT_NORMAL, HUNTER_COAT_ENHANCED, HUNTER_COAT_ULTIMATE, SWIFTNESS_NORMAL, SWIFTNESS_ENHANCED, SWIFTNESS_ULTIMATE).forEach(asset -> {
            output.accept(asset, createDefaultArmor(asset));
        });

        Stream.of(VAMPIRE_CLOTH_BOOTS, VAMPIRE_CLOTH_LEGS, VAMPIRE_CLOTH_HAT, VAMPIRE_CLOTH_CROWN, HUNTER_HAT_TALL, HUNTER_HAT_BROAD).forEach(asset -> {
            output.accept(asset, createCustomOnly(asset));
        });

        for (Map.Entry<DyeColor, ResourceKey<EquipmentAsset>> entry : VAMPIRE_CLOAKS.entrySet()) {
            output.accept(entry.getValue(), EquipmentClientInfo.builder().addMainHumanoidLayer(VResourceLocation.mod("cloak/" + entry.getKey().getName()), false).build());
        }
    }

    protected EquipmentClientInfo createDefaultArmor(ResourceKey<EquipmentAsset> asset) {
        return EquipmentClientInfo.builder().addHumanoidLayers(asset.location()).build();
    }

    protected EquipmentClientInfo createCustomOnly(ResourceKey<EquipmentAsset> asset) {
        return EquipmentClientInfo.builder().addMainHumanoidLayer(asset.location(), false).build();
    }
}
