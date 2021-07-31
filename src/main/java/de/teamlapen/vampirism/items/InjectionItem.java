package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Item with different injection types
 */
public class InjectionItem extends VampirismItem {


    private final static String regName = "injection";
    private final TYPE type;

    public InjectionItem(TYPE type) {
        super(regName + "_" + type.getName(), new Properties().tab(VampirismMod.creativeTab));
        this.type = type;
    }


    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (type == TYPE.SANGUINARE) {
            playerIn.displayClientMessage(new StringTextComponent("Please use a ").append(new TranslationTextComponent(ModBlocks.med_chair.getDescriptionId())), true);
        }
        return new ActionResult<>(ActionResultType.PASS, stack);
    }


    public enum TYPE implements IStringSerializable {
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
