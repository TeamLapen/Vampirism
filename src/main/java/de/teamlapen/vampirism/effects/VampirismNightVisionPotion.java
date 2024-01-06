package de.teamlapen.vampirism.effects;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.effects.IHiddenEffectInstance;
import de.teamlapen.vampirism.api.entity.effect.EffectInstanceWithSource;
import de.teamlapen.vampirism.client.extensions.EffectExtensions;
import de.teamlapen.vampirism.entity.player.IVampirismPlayer;
import de.teamlapen.vampirism.mixin.MixinPlayerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Potion which replaces the vanilla night vision one.
 */
public class VampirismNightVisionPotion extends MobEffect {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "permanent");

    public VampirismNightVisionPotion() {
        super(MobEffectCategory.BENEFICIAL, 2039713);
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientMobEffectExtensions> consumer) {
        consumer.accept(EffectExtensions.NIGHT_VISION);
    }


}
