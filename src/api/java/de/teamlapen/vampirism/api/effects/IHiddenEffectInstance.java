package de.teamlapen.vampirism.api.effects;

/**
 * hides the effect instance from rendering
 * <p>
 * used for vampirism night vision effect
 * @deprecated this interface is no longer in use. If you want to make an effect instance invisible, you need to use the {@link de.teamlapen.vampirism.api.entity.effect.EffectInstanceWithSource} interface and set the source to {@code vampirism:permanent}
 */
@Deprecated
public interface IHiddenEffectInstance {
}
