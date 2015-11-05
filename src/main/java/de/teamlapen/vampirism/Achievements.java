package de.teamlapen.vampirism;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

/**
 * Achievements holder
 */
public class Achievements {

    public static Achievement becomingAVampire = new Achievement("achievement.becoming_vampire", REFERENCE.MODID + ".becoming_vampire", 0, 0, ModItems.vampireFang, null);

    public static Achievement suckingBlood = new Achievement("achievement.sucking_blood", REFERENCE.MODID + ".sucking_blood", 0, 0, ModItems.bloodBottle, becomingAVampire);
    public static Achievement becomingALord = new Achievement("achievement.becoming_lord", REFERENCE.MODID + ".becoming_lord", 0, 0, ModItems.gemOfBinding, suckingBlood);

    public static void registerAchievements() {
        becomingAVampire.registerStat();
        becomingALord.registerStat();
        suckingBlood.registerStat();
        AchievementPage.registerAchievementPage(new AchievementPage("Vampirism Achievements", becomingAVampire, becomingALord, suckingBlood));
    }
}
