package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Item with different injection types
 */
public class InjectionItem extends Item {
    private final TYPE type;

    public InjectionItem(TYPE type) {
        super(new Properties().tab(VampirismMod.creativeTab));
        this.type = type;
    }


    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (type == TYPE.SANGUINARE) {
            playerIn.displayClientMessage(Component.literal("Please use a ").append(Component.translatable(ModBlocks.MED_CHAIR.get().getDescriptionId())), true);
        }
        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }


    public enum TYPE implements StringRepresentable {
        EMPTY("empty"), GARLIC("garlic"), SANGUINARE("sanguinare"), ZOMBIE_BLOOD("zombie_blood");

        private final String name;

        TYPE(String name) {
            this.name = name;
        }

        public @NotNull String getName() {
            return this.getSerializedName();
        }

        @Override
        @NotNull
        public String getSerializedName() {
            return name;
        }
    }
}
