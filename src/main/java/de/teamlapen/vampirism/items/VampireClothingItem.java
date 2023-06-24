package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.client.model.armor.*;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.util.ArmorMaterial;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class VampireClothingItem extends ArmorItem implements IFactionExclusiveItem {

    public static final ArmorMaterial VAMPIRE_CLOTH = new ArmorMaterial("vampire_cloth", 15, ArmorMaterial.createReduction(1, 2, 3, 1), 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0, 0, () -> Ingredient.of(ModTags.Items.HEART));

    public VampireClothingItem(@NotNull ArmorItem.Type type) {
        super(VAMPIRE_CLOTH, type, new Properties().defaultDurability(ArmorMaterials.IRON.getDurabilityForType(type)));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        this.addFactionToolTips(stack, worldIn, tooltip, flagIn, VampirismMod.proxy.getClientPlayer());
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull Model getGenericArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                return switch (RegUtil.id(VampireClothingItem.this).getPath()) {
                    case "vampire_clothing_crown" -> ClothingCrownModel.getAdjustedInstance(original);
                    case "vampire_clothing_legs" -> ClothingPantsModel.getAdjustedInstance(original);
                    case "vampire_clothing_boots" -> ClothingBootsModel.getAdjustedInstance(original);
                    case "vampire_clothing_hat" -> VampireHatModel.getAdjustedInstance(original);
                    default -> DummyClothingModel.getAdjustedInstance(original);
                };
            }
        });
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return String.format(REFERENCE.MODID + ":textures/models/armor/%s.png", RegUtil.id(this).getPath());
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        return super.canEquip(stack, armorType, entity) && Helper.isVampire(entity);
    }

    @Nullable
    @Override
    public IFaction<?> getExclusiveFaction(@NotNull ItemStack stack) {
        return VReference.VAMPIRE_FACTION;
    }

    @Override
    public void onArmorTick(ItemStack stack, Level world, @NotNull Player player) {
        super.onArmorTick(stack, world, player);
        if (player.tickCount % 16 == 8) {
            if (!Helper.isVampire(player)) {
                player.addEffect(new MobEffectInstance(ModEffects.POISON.get(), 20, 1));
            }
        }
        if (stack.getItem() == ModItems.VAMPIRE_CLOTHING_CROWN.get() && stack.hasCustomHoverName() && "10000000".equals(stack.getHoverName().getString()) && VampirismAPI.settings().isSettingTrue("vampirism:10000000d")) {
            UtilLib.spawnParticlesAroundEntity(player, ParticleTypes.ELECTRIC_SPARK, 0.5, 4);
            if (player.tickCount % 16 == 4) {
                player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 30, 0));
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 2));
            }
        }
    }

}
