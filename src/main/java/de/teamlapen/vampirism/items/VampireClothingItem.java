package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.client.model.armor.*;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.VampirismArmorMaterials;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class VampireClothingItem extends ArmorItem implements IFactionExclusiveItem {


    public VampireClothingItem(EquipmentSlotType slotType) {
        super(VampirismArmorMaterials.VAMPIRE_CLOTH, slotType, new Properties().defaultDurability(ArmorMaterial.IRON.getDurabilityForSlot(slotType)).tab(VampirismMod.creativeTab));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        PlayerEntity playerEntity = VampirismMod.proxy.getClientPlayer();
        this.addFactionPoisonousToolTip(stack, worldIn, tooltip, flagIn, playerEntity);
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    @Override
    public BipedModel getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, BipedModel _default) {
        switch (this.getRegistryName().getPath()) {
            case "vampire_clothing_crown":
                return ClothingCrownModel.getInstance();
            case "vampire_clothing_legs":
                return ClothingPantsModel.getInstance();
            case "vampire_clothing_boots":
                return ClothingBootsModel.getInstance();
            case "vampire_clothing_hat":
                return VampireHatModel.getInstance();
            default:
                return DummyClothingModel.getArmorModel();
        }
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return String.format(REFERENCE.MODID + ":textures/models/armor/%s.png", this.getRegistryName().getPath());
    }

    @Nonnull
    @Override
    public IFaction<?> getExclusiveFaction() {
        return VReference.VAMPIRE_FACTION;
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        super.onArmorTick(stack, world, player);
        if (player.tickCount % 16 == 8) {
            if (!Helper.isVampire(player)) {
                player.addEffect(new EffectInstance(ModEffects.POISON.get(), 20, 1));
            }
        }
        if(stack.getItem() == ModItems.VAMPIRE_CLOTHING_CROWN.get() && stack.hasCustomHoverName() && "10000000".equals(stack.getHoverName().getString())){
            UtilLib.spawnParticlesAroundEntity(player, ParticleTypes.FIREWORK, 0.5, 4);
            if(player.tickCount % 16 == 4) {
                player.addEffect(new EffectInstance(Effects.LEVITATION,30,0));
                player.addEffect(new EffectInstance(Effects.SLOW_FALLING, 100, 2));
            }
        }
    }

}
