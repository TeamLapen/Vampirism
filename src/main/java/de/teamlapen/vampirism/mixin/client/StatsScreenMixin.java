package de.teamlapen.vampirism.mixin.client;

import de.teamlapen.vampirism.core.ModStats;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(StatsScreen.GeneralStatisticsList.class)
public class StatsScreenMixin {

    @ModifyArg(method = "<init>(Lnet/minecraft/client/gui/screens/achievement/StatsScreen;Lnet/minecraft/client/Minecraft;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/achievement/StatsScreen$GeneralStatisticsList;addEntry(Lnet/minecraft/client/gui/components/AbstractSelectionList$Entry;)I"))
    private AbstractSelectionList.Entry actionKey(AbstractSelectionList.Entry par1) {
        ModStats.getStatDisplay(((StatsEntryAccessor) par1).getStat().getValue()).ifPresent(((StatsEntryAccessor) par1)::setStatDisplay);
        return par1;
    }
}
