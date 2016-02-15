package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

/**
 * Vampirism's achievements
 */
public class Achievements {
    public static Achievement becomingAVampire = new Achievement("achievement.becoming_vampire", REFERENCE.MODID + ".becoming_vampire", 0, 0, ModItems.vampireFang, null);
    public static Achievement suckingBlood = new Achievement("achievement.sucking_blood", REFERENCE.MODID + ".sucking_blood", 1, 2, (ItemStack) null, becomingAVampire);//TODO new ItemStack(ModItems.bloodBottle, 1, ItemBloodBottle.MAX_BLOOD)

    public static void registerAchievement() {
        becomingAVampire.registerStat();
        AchievementPage.registerAchievementPage(new AchievementPage("Vampirism Achievements", becomingAVampire, suckingBlood));
    }
}
