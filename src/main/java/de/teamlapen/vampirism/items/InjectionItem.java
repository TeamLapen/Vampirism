package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

/**
 * Item with different injection types
 */
public class InjectionItem extends Item {
    private final TYPE type;

    public InjectionItem(TYPE type) {
        super(new Properties().tab(VampirismMod.creativeTab));
        this.type = type;
    }


    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level worldIn, Player playerIn, @Nonnull InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (type == TYPE.SANGUINARE) {
            playerIn.displayClientMessage(new TextComponent("Please use a ").append(new TranslatableComponent(ModBlocks.MED_CHAIR.get().getDescriptionId())), true);
        }
        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }


    public enum TYPE implements StringRepresentable {
        EMPTY("empty"), GARLIC("garlic"), SANGUINARE("sanguinare"), ZOMBIE_BLOOD("zombie_blood");

        private final String name;

        TYPE(String name) {
            this.name = name;
        }

        public String getName() {
            return this.getSerializedName();
        }

        @Override
        @Nonnull
        public String getSerializedName() {
            return name;
        }
    }
}
