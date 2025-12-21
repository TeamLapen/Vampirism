package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.client.extensions.ItemExtensions;
import net.minecraft.client.model.HumanoidModel;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

public class ModClientEnums {

    public static final EnumProxy<HumanoidModel.ArmPose> DOUBLE_CROSSBOW_CHARGE = new EnumProxy<>(HumanoidModel.ArmPose.class, true,true, ItemExtensions.DOUBLE_CROSSBOW_CHARGE_ARM_POSE_TRANSFORMER);

    public static final EnumProxy<HumanoidModel.ArmPose> DOUBLE_CROSSBOW_HOLD = new EnumProxy<>(HumanoidModel.ArmPose.class, true,true, ItemExtensions.DOUBLE_CROSSBOW_HOLD_ARM_POSE_TRANSFORMER);

}
