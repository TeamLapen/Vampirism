package de.teamlapen.vampirism.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import de.teamlapen.vampirism.client.gui.components.ActionStatisticsList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.layouts.LinearLayout;
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

    @Inject(method = "initButtons", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/LinearLayout;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;", ordinal = 3, shift = At.Shift.AFTER))
    private void add(CallbackInfo ci, @Local(ordinal = 1) LinearLayout line) {
        line.visitChildren(s -> {
            if (s instanceof Button button) {
                button.setWidth(100);
            }
        });
        var button = line.addChild(Button.builder(Component.translatable("text.vampirism.actions"), x -> this.setActiveList(this.vampirism$actionStatisticsList)).width(100).build());
        if (this.vampirism$actionStatisticsList.children().isEmpty()) {
            button.active = false;
        }
    }
}
