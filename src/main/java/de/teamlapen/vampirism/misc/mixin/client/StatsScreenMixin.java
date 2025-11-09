package de.teamlapen.vampirism.misc.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.teamlapen.vampirism.client.gui.components.ActionStatisticsList;
import net.minecraft.client.gui.components.tabs.LoadingTab;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(StatsScreen.class)
public abstract class StatsScreenMixin extends Screen {

    @Unique
    private static final Component ACTIONS_BUTTON = Component.translatable("text.vampirism.actions");

    @Shadow
    @Final
    private static Component PENDING_TEXT;

    @Shadow
    @Nullable
    private TabNavigationBar tabNavigationBar;

    private StatsScreenMixin(Component title) {
        super(title);
    }

    @WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/tabs/TabNavigationBar$Builder;build()Lnet/minecraft/client/gui/components/tabs/TabNavigationBar;"))
    private TabNavigationBar addList(TabNavigationBar.Builder instance, Operation<TabNavigationBar> original) {
        instance.addTabs(new LoadingTab(this.getFont(), ACTIONS_BUTTON, PENDING_TEXT));
        return original.call(instance);
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/tabs/TabNavigationBar;setTabActiveState(IZ)V", ordinal = 2))
    private void initListNotActive(CallbackInfo ci) {
        this.tabNavigationBar.getTabs().stream().filter(x -> x.getTabTitle().equals(ACTIONS_BUTTON)).findFirst().ifPresent(x -> this.tabNavigationBar.setTabActiveState(this.tabNavigationBar.getTabs().indexOf(x), true));
    }

    @WrapOperation(method = "onStatsUpdated", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/tabs/TabNavigationBar$Builder;build()Lnet/minecraft/client/gui/components/tabs/TabNavigationBar;"))
    private TabNavigationBar addListOnStatsUpdated(TabNavigationBar.Builder instance, Operation<TabNavigationBar> original) {
        StatsScreen statsScreen = (StatsScreen) (Object) this;
        instance.addTabs(statsScreen.new StatisticsTab(ACTIONS_BUTTON, new ActionStatisticsList(this.minecraft, statsScreen)));
        return original.call(instance);
    }
}
