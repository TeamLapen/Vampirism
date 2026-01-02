package de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors.flyingsword;

import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public class UnEquipSword {

    public static Set<SensorType<? extends Sensor<? super Dracula>>> sensors() {
        return Set.of();
    }

    public static Set<MemoryModuleType<?>> memories() {
        return Set.of(ModMemoryTypes.FLYING_SWORD_EQUIPPED.get(), ModMemoryTypes.FLYING_SWORD_ACTIVE.get(), ModMemoryTypes.FLYING_SWORD_SHOT.get());
    }

    public static OneShot<Dracula> create() {
        return BehaviorBuilder.create(inst -> inst.group(
                inst.present(ModMemoryTypes.FLYING_SWORD_EQUIPPED.get()),
                inst.present(ModMemoryTypes.FLYING_SWORD_ACTIVE.get()),
                inst.present(ModMemoryTypes.FLYING_SWORD_SHOT.get())
        ).apply(inst, (equipped, active, shot) ->
                (level, dracula, gameTime) -> {
            dracula.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            equipped.erase();
            shot.erase();
            return true;
        }));
    }
}
