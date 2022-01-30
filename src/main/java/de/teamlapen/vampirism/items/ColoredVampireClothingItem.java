package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.model.armor.CloakModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Consumer;


public class ColoredVampireClothingItem extends VampireClothingItem {
    private final String baseName;
    private final EnumClothingColor color;
    private final EnumModel model;

    public ColoredVampireClothingItem(EquipmentSlot slotType, EnumModel model, String baseRegName, EnumClothingColor color) {
        super(slotType, baseRegName + "_" + color.getName());
        this.baseName = baseRegName;
        this.color = color;
        this.model = model;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
                            @SuppressWarnings({"DuplicateBranchesInSwitch", "SwitchStatementWithTooFewBranches", "UnnecessaryDefault"})
                            @NotNull
                            @Override
                            public Model getBaseArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
                                return switch (model) {
                                    case CLOAK -> CloakModel.getAdjustedCloak(_default);
                                    default -> CloakModel.getAdjustedCloak(_default);
                                };
                            }
                        }
        );
    }


    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return String.format(REFERENCE.MODID + ":textures/models/armor/%s/%s_%s.png", baseName, baseName,
                color.getName());
    }

    public enum EnumModel {
        CLOAK
    }

    public enum EnumClothingColor implements StringRepresentable {
        REDBLACK("red_black"), BLACKRED("black_red"), BLACKWHITE("black_white"), WHITEBLACK(
                "white_black"), BLACKBLUE("black_blue");


        private final String name;

        EnumClothingColor(String nameIn) {
            this.name = nameIn;
        }

        public String getName() {
            return getSerializedName();
        }

        @Nonnull
        @Override
        public String getSerializedName() {
            return this.name;
        }


    }
}
