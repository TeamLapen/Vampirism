package de.teamlapen.faction.misc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.teamlapen.faction.api.Factions;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionHelper;
import de.teamlapen.faction.common.world.items.consume.FactionConsumableListener;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ConsumableListener;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Mixin(Consumable.class)
public class ConsumableMixin {

    @WrapOperation(method = "onConsume", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;forEach(Ljava/util/function/Consumer;)V"))
    private void factions$skipWrongFactions(Stream<ConsumableListener> instance, Consumer<ConsumableListener> consumer, Operation<Void> original, Level level, LivingEntity entity, ItemStack stack) {
        var entityFaction = IFactionHelper.get().getFaction(entity);

        List<ConsumableListener> listeners = instance.toList();
        boolean hasFactionListener = listeners.stream().anyMatch(listener -> listener instanceof FactionConsumableListener);

        if (hasFactionListener) {
            Stream<ConsumableListener> filtered = listeners.stream();
            if (IFaction.is(entityFaction, Factions.NEUTRAL)) {
                filtered = filtered.filter(listener -> !(listener instanceof FactionConsumableListener));
            } else {
                filtered = filtered.filter(listener -> {
                    if (listener == DataComponents.FOOD) return false;

                    if (!(listener instanceof FactionConsumableListener factionListener)) {
                        return true;
                    }

                    return factionListener.isCorrectFaction(entityFaction);
                });
            }

            original.call(filtered, consumer);
        } else {
            original.call(listeners.stream(), consumer);
        }
    }

    @Inject(method = "canConsume(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void factions$allowFactionConsume(LivingEntity entity, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof Player) {
            var entityFaction = IFactionHelper.get().getFaction(entity);
            if (stack.getAllOfType(FactionConsumableListener.class).anyMatch(listener -> listener.isCorrectFaction(entityFaction))) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }
}
