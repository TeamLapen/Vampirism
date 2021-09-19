package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.client.model.armor.*;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class VampireClothingItem extends ArmorItem implements IFactionExclusiveItem {
    private final String regName;


    public VampireClothingItem(EquipmentSlot slotType, String regName) {
        super(ArmorMaterials.LEATHER, slotType, new Properties().defaultDurability(ArmorMaterials.IRON.getDurabilityForSlot(slotType)).tab(VampirismMod.creativeTab));
        this.regName = regName;
        this.setRegistryName(REFERENCE.MODID, regName);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
        Player playerEntity = VampirismMod.proxy.getClientPlayer();
        this.addFactionPoisonousToolTip(stack, worldIn, tooltip, flagIn, playerEntity);
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, A _default) {
                return switch (regName) {
                    case "vampire_clothing_crown" -> (A) ClothingCrownModel.getInstance();
                    case "vampire_clothing_legs" -> (A) ClothingPantsModel.getInstance();
                    case "vampire_clothing_boots" -> (A) ClothingBootsModel.getInstance();
                    case "vampire_clothing_hat" -> (A) VampireHatModel.getInstance();
                    default -> (A) DummyClothingModel.getArmorModel();
                };
            }

        });
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return String.format(REFERENCE.MODID + ":textures/models/armor/%s.png", regName);
    }

    @Nullable
    @Override
    public IFaction<?> getExclusiveFaction(@Nonnull ItemStack stack) {
        return VReference.VAMPIRE_FACTION;
    }

    @Override
    public void onArmorTick(ItemStack stack, Level world, Player player) {
        super.onArmorTick(stack, world, player);
        if (player.tickCount % 16 == 8) {
            if (!Helper.isVampire(player)) {
                player.addEffect(new MobEffectInstance(ModEffects.poison, 20, 1));
            }
        }
    }

}
