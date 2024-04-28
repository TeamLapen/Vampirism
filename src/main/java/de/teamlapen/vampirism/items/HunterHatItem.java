package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.client.extensions.ItemExtensions;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Simple headwear that look like a hunter head
 */
public class HunterHatItem extends HunterArmorItem {
    private final HatType type;

    public HunterHatItem(HatType type) {
        super(ArmorMaterials.IRON, Type.HELMET, new Properties());
        this.type = type;
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(ItemExtensions.HUNTER_HAT);
    }

    private String descriptionId;

    public HatType getHateType() {
        return type;
    }

    @Override
    @NotNull
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = super.getOrCreateDescriptionId().replaceAll("_0|_1", "");
        }

        return this.descriptionId;
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if (pSlotId >= 36 && pSlotId <= 39 && pEntity instanceof Player living) {
            if (pStack.has(DataComponents.CUSTOM_NAME) && "10000000".equals(pStack.getHoverName().getString()) && VampirismAPI.settings().isSettingTrue("vampirism:10000000d")) {
                UtilLib.spawnParticlesAroundEntity(living, ParticleTypes.ELECTRIC_SPARK, 0.5, 4);
                if (living.tickCount % 16 == 4) {
                    living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 30, 0));
                    living.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 2));
                }
            }
        }
    }

    public enum HatType {
        TYPE_1, TYPE_2
    }
}
