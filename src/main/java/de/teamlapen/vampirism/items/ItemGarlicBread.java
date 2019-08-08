package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;


public class ItemGarlicBread extends ItemFood {
    public ItemGarlicBread() {
        super(6, 0.7f, false);
        this.setRegistryName(REFERENCE.MODID, "garlic_bread");
        this.setTranslationKey("vampirism.garlic_bread");
    }

    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
        if (!worldIn.isRemote) {
            IVampirePlayer vampire = VampirePlayer.get(player);
            if (vampire.getLevel() > 0) {
                DamageHandler.affectVampireGarlicDirect(vampire, EnumStrength.MEDIUM);
            }
        }
    }
}
