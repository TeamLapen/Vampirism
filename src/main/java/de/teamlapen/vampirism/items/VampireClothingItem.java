package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.client.extensions.ItemExtensions;
import de.teamlapen.vampirism.core.ModArmorMaterials;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class VampireClothingItem extends ArmorItem implements IFactionExclusiveItem {

    public VampireClothingItem(@NotNull ArmorItem.Type type) {
        super(ModArmorMaterials.VAMPIRE_CLOTH, type, new Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(15)));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        this.addFactionToolTips(stack, context, tooltip, flagIn, VampirismMod.proxy.getClientPlayer());
    }


    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(ItemExtensions.VAMPIRE_CLOTHING);
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
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (pEntity instanceof LivingEntity living && pSlotId >= 36 && pSlotId <= 39) {
            if (living.tickCount % 16 == 8) {
                if (!Helper.isVampire(living)) {
                    living.addEffect(new MobEffectInstance(ModEffects.POISON, 20, 1));
                }
            }
            if (pStack.getItem() == ModItems.VAMPIRE_CLOTHING_CROWN.get() && pStack.has(DataComponents.CUSTOM_NAME) && "10000000".equals(pStack.getHoverName().getString()) && VampirismAPI.settings().isSettingTrue("vampirism:10000000d")) {
                UtilLib.spawnParticlesAroundEntity(living, ParticleTypes.ELECTRIC_SPARK, 0.5, 4);
                if (living.tickCount % 16 == 4) {
                    living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 30, 0));
                    living.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 2));
                }
            }
        }
    }
}
