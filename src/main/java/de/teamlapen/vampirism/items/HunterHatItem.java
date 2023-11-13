package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.client.model.armor.HunterHatModel;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Simple headwear that look like a hunter head
 */
public class HunterHatItem extends VampirismHunterArmorItem {
    private final int type;

    public HunterHatItem(int type) {
        super(ArmorMaterials.IRON, Type.HELMET, new Properties());
        this.type = type;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "vampirism:textures/models/armor/" + RegUtil.id(this).getPath() + ".png";
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull Model getGenericArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                return (type == 0 ? HunterHatModel.getAdjustedInstance0(original) : HunterHatModel.getAdjustedInstance1(original));
            }
        });
    }

    private String descriptionId;

    @Override
    @NotNull
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = super.getOrCreateDescriptionId().replaceAll("_0|_1", "");
        }

        return this.descriptionId;
    }

    @Override
    public void onArmorTick(ItemStack stack, Level world, Player player) {
        if (stack.hasCustomHoverName() && "10000000".equals(stack.getHoverName().getString()) && VampirismAPI.settings().isSettingTrue("vampirism:10000000d")) {
            UtilLib.spawnParticlesAroundEntity(player, ParticleTypes.ELECTRIC_SPARK, 0.5, 4);
            if (player.tickCount % 16 == 4) {
                player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 30, 0));
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 2));
            }
        }
    }
}
