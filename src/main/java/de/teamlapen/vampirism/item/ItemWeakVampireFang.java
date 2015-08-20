package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.Configs;
import de.teamlapen.vampirism.ModPotion;
import de.teamlapen.vampirism.entity.player.VampirePlayer;
import de.teamlapen.vampirism.util.BALANCE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemWeakVampireFang extends BasicItem {
    public static final String name = "weakVampireFang";

    public ItemWeakVampireFang() {
        super(name);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (Configs.realismMode) return stack;//Only strong fangs can turn
        if (!player.isPotionActive(ModPotion.sanguinare) && VampirePlayer.get(player).getLevel() == 0) {
            player.addPotionEffect(new PotionEffect(ModPotion.sanguinare.id, BALANCE.VAMPIRE_PLAYER_SANGUINARE_DURATION * 20));
            player.addPotionEffect(new PotionEffect(Potion.poison.id, 60));
        }
        return stack;
    }
}
