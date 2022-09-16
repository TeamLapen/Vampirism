package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.model.armor.HunterHatModel;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Simple headwear that look like a hunter head
 */
public class HunterHatItem extends VampirismHunterArmor {
    private final int type;

    public HunterHatItem(int type) {
        super(ArmorMaterial.IRON, EquipmentSlotType.HEAD, new Properties().tab(VampirismMod.creativeTab));
        this.type = type;
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public BipedModel getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, BipedModel _default) {
        return type == 0 ? HunterHatModel.hat0 : HunterHatModel.hat1;
    }


    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return "vampirism:textures/entity/hunter_extra.png";
    }


    @Override
    protected int getDamageReduction(int slot, ItemStack stack) {
        return 2;
    }

    private String descriptionId;
    @Override
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = super.getOrCreateDescriptionId().replaceAll("_\\d", "");
        }

        return this.descriptionId;
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        if(stack.hasCustomHoverName() && "10000000".equals(stack.getHoverName().getString())){
            UtilLib.spawnParticlesAroundEntity(player, ParticleTypes.FIREWORK, 0.5, 4);
            if(player.tickCount % 16 == 4) {
                player.addEffect(new EffectInstance(Effects.LEVITATION,30,0));
                player.addEffect(new EffectInstance(Effects.SLOW_FALLING, 100, 2));
            }
        }
    }
}
