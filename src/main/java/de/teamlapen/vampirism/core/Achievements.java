package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.items.ItemBloodBottle;
import de.teamlapen.vampirism.items.ItemInjection;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

/**
 * Vampirism's achievements
 */
public class Achievements {
    public static Achievement becomingAVampire = new Achievement("achievement.becoming_vampire", REFERENCE.MODID + ".becoming_vampire", 0, 1, ModItems.vampireFang, null);
    public static Achievement suckingBlood = new Achievement("achievement.sucking_blood", REFERENCE.MODID + ".sucking_blood", 0, 3, (ItemStack) new ItemStack(ModItems.bloodBottle, 1, ItemBloodBottle.AMOUNT), becomingAVampire);
    public static Achievement becomingAHunter = new Achievement("achievement.becoming_hunter", REFERENCE.MODID + ".becoming_hunter", 0, -1, new ItemStack(ModItems.injection, 1, ItemInjection.META_GARLIC), null);
    public static Achievement stake = new Achievement("achievement.stake", REFERENCE.MODID + ".stake", 0, -3, ModItems.stake, becomingAHunter);
    public static Achievement weaponTable = new Achievement("achievement.weapon_table", REFERENCE.MODID + ".weapon_table", 1, -4, ModBlocks.weaponTable, stake);
    public static Achievement bloodTable = new Achievement("achievement.blood_table", REFERENCE.MODID + ".blood_table", -1, -4, ModBlocks.bloodPotionTable, stake);

    public static void registerAchievement() {
        becomingAVampire.registerStat();
        suckingBlood.registerStat();
        becomingAHunter.registerStat();
        stake.registerStat();
        weaponTable.registerStat();
        bloodTable.registerStat();
        AchievementPage.registerAchievementPage(new AchievementPage("Vampirism Achievements", becomingAVampire, suckingBlood, becomingAHunter, stake, bloodTable, weaponTable));
    }
}
