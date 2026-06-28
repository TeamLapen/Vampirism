package de.teamlapen.vampirism.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Generates an abstract {@code Converted&lt;Name&gt;} base class for the given vanilla creature and wires up
 * its registration + usages.
 * <p>
 * For every annotation the processor emits:
 * <ul>
 *     <li>an abstract {@code Converted&lt;Name&gt;} base (extends the creature, implements
 *     {@code CurableConvertedCreature}, all common boilerplate). When {@link #subclass()} is unset it also
 *     carries the {@code getAttributeBuilder()} / {@code checkSpawnRules(...)} statics; when a subclass is
 *     given those live in the handwritten subclass instead.</li>
 *     <li>an entry in the generated {@code GeneratedConvertedEntities} registrar (DeferredHolder +
 *     attribute + spawn-placement registration + the {@code ALL} datagen descriptor list).</li>
 * </ul>
 * Place it on any holder type; it is {@link Repeatable}:
 * <pre>{@code
 * @ConvertedCreature(value = Cow.class, width = 0.9F, height = 1.4F)
 * @ConvertedCreature(value = Horse.class, width = 1.39F, height = 1.6F,
 *                    subclass = ConvertedHorseEntity.class, overlay = "horse")
 * public final class GeneratedConvertedCreatures {}
 * }</pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(ConvertedCreatures.class)
public @interface ConvertedCreature {

    /**
     * The vanilla creature to convert, e.g. {@code Cow.class}. The generated base extends this type.
     */
    Class<?> value();

    /**
     * Registry id, e.g. {@code "converted_cow"}. Defaults to {@code "converted_" + lower(simpleName)}.
     * The registrar field name is this value upper-cased (e.g. {@code CONVERTED_COW}).
     */
    String registryName() default "";

    /**
     * The {@code net.minecraft.world.entity.EntityType} constant name used for curing / naming,
     * e.g. {@code "COW"}. Defaults to the upper-cased simple name of {@link #value()}.
     */
    String entityType() default "";

    /**
     * Overrides the generated base' simple name. Defaults to {@code "Converted" + simpleName(value())}.
     */
    String className() default "";

    /**
     * Optional handwritten concrete subclass (kept in the normal source set) used as the registered
     * entity. When set, the registration factory is {@code Subclass::new} and the subclass must provide
     * the {@code getAttributeBuilder()} / {@code checkSpawnRules(...)} statics. When unset, the abstract
     * base is registered via an anonymous subclass and carries those statics itself.
     */
    Class<?> subclass() default void.class;

    /**
     * Vanilla {@code EntityRenderer} class for client-side registration, e.g. {@code CowRenderer.class}.
     * The generated {@code GeneratedConvertedEntitiesClient} wraps it with the converted-overlay layer.
     * When unset ({@code void.class}) no client entry is generated for this creature.
     * Renderers needing extra constructor arguments (e.g. {@code DonkeyRenderer}) stay handwritten.
     */
    Class<?> renderer() default void.class;

    /**
     * Overlay texture key used for the vanilla&rarr;converted data map
     * ({@code textures/entity/vanilla/&lt;overlay&gt;_overlay.png}). Defaults to {@code lower(simpleName)}.
     */
    String overlay() default "";

    /**
     * Hitbox width passed to {@code EntityType.Builder.sized(width, height)}.
     * When {@code -1} (the default), {@code EntityType.<name>.getDimensions().width()} is used instead.
     */
    float width() default -1f;

    /**
     * Hitbox height passed to {@code EntityType.Builder.sized(width, height)}.
     * When {@code -1} (the default), {@code EntityType.<name>.getDimensions().height()} is used instead
     * and {@code EntityType.<name>.getDimensions().eyeHeight()} is also applied.
     */
    float height() default -1f;

    /** {@code net.minecraft.world.entity.MobCategory} constant name. Defaults to {@code "CREATURE"}. */
    String mobCategory() default "CREATURE";

    /**
     * Static method on the vanilla class used to build the attribute set, e.g. {@code "createAttributes"} or
     * {@code "createBaseHorseAttributes"}. The generated {@code getAttributeBuilder()} calls
     * {@code <VanillaClass>.<attributeMethod>().add(ATTACK_DAMAGE, ...).add(SUNDAMAGE, ...)}.
     */
    String attributeMethod() default "createAttributes";

    /**
     * Static spawn-rule method on the base type used by the generated {@code checkSpawnRules}, e.g.
     * {@code "checkGoatSpawnRules"}. Defaults to {@code "checkAnimalSpawnRules"}. Only consulted when
     * {@link #subclass()} is unset (otherwise the subclass owns the spawn rule).
     */
    String spawnRulesFrom() default "checkAnimalSpawnRules";

    /** Adds {@code .immuneTo(Blocks.SWEET_BERRY_BUSH)} to the entity-type builder (vanilla fox quirk). */
    boolean immuneToSweetBerryBush() default false;
}
