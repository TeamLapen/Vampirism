package de.teamlapen.vampirism.mixin.client;

import de.teamlapen.vampirism.client.gui.components.ActionStatisticsList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StatsScreen.class)
public abstract class StatsScreenMixin extends Screen {

    @Shadow public abstract void setActiveList(@Nullable ObjectSelectionList<?> pActiveList);

    @Unique
    private ActionStatisticsList vampirism$actionStatisticsList;

    private StatsScreenMixin(Component pTitle) {
        super(pTitle);
    }

    @Inject(method = "initLists()V", at = @At("RETURN"))
    private void initLists(CallbackInfo ci) {
        this.vampirism$actionStatisticsList = new ActionStatisticsList(this.minecraft, ((StatsScreen) (Object)this), width, height-96);
    }

    @Inject(method = "initButtons", at = @At("RETURN"))
    private void initButtons(CallbackInfo ci) {
        var button = this.addRenderableWidget(Button.builder(Component.translatable("text.vampirism.actions"), b -> this.setActiveList(this.vampirism$actionStatisticsList)).bounds(this.width / 2 + 120, this.height -52, 80, 20).build());
        if (this.vampirism$actionStatisticsList.children().isEmpty()) {
            button.active = false;
        }
    }
}
