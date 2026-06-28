package de.teamlapen.vampirism.processor;

import de.teamlapen.vampirism.processor.generator.ClientGenerator;
import de.teamlapen.vampirism.processor.generator.ConvertedEntityClassGenerator;
import de.teamlapen.vampirism.processor.generator.DatagenGenerator;
import de.teamlapen.vampirism.processor.generator.RegistrarGenerator;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates the converted-entity infrastructure from {@link ConvertedCreature}.
 * <p>
 * The annotated class must also carry {@link ModId} — its value becomes the mod ID used for
 * {@code DeferredRegister}, {@code @EventBusSubscriber}, and datagen providers, and is embedded
 * in the generated class names so multiple mods can coexist in one compilation unit.
 * <ul>
 *     <li>an abstract {@code Converted&lt;Name&gt;} base per creature (see {@link de.teamlapen.vampirism.processor.generator.ConvertedEntityClassGenerator}),</li>
 *     <li>one aggregate {@code Generated<ModId>ConvertedEntities} registrar per mod ID (see {@link de.teamlapen.vampirism.processor.generator.RegistrarGenerator}),</li>
 *     <li>one aggregate {@code Generated<ModId>ConvertedEntitiesData} datagen class per mod ID (see {@link de.teamlapen.vampirism.processor.generator.DatagenGenerator}),</li>
 *     <li>one aggregate {@code Generated<ModId>ConvertedEntitiesClient} renderer class per mod ID, only when any entry declares a {@link ConvertedCreature#renderer()} (see {@link de.teamlapen.vampirism.processor.generator.ClientGenerator}).</li>
 * </ul>
 */
@SupportedAnnotationTypes({
        "de.teamlapen.vampirism.processor.ConvertedCreature",
        "de.teamlapen.vampirism.processor.ConvertedCreatures",
        "de.teamlapen.vampirism.processor.AdditionalConverter",
        "de.teamlapen.vampirism.processor.AdditionalConverters",
        "de.teamlapen.vampirism.processor.ModId"
})
public class ConvertedCreatureProcessor extends AbstractProcessor {

    private boolean done;

    @UnknownNullability
    private Messager messager = null;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (done) {
            return true;
        }
        List<CreatureModel> models = new ArrayList<>();
        for (Element element : annotatedWith(roundEnv, ConvertedCreature.class, ConvertedCreatures.class)) {
            for (ConvertedCreature a : element.getAnnotationsByType(ConvertedCreature.class)) {
                CreatureModel m = toModel(element, a);
                if (m != null) {
                    models.add(m);
                }
            }
        }
        if (models.isEmpty()) {
            return true;
        }
        done = true;

        List<AdditionalConverterModel> additionalModels = new ArrayList<>();
        for (Element element : annotatedWith(roundEnv, AdditionalConverter.class, AdditionalConverters.class)) {
            for (AdditionalConverter a : element.getAnnotationsByType(AdditionalConverter.class)) {
                AdditionalConverterModel m = toAdditionalModel(element, a);
                if (m != null) {
                    additionalModels.add(m);
                }
            }
        }

        // Generate individual entity classes (not grouped by modId — each lives in its own package)
        for (CreatureModel m : models) {
            ProcessorUtil.write(processingEnv, messager, m.element(), m.packageName(), m.className(), ConvertedEntityClassGenerator.generate(m));
        }

        // Generate aggregate classes once per modId
        Map<String, List<CreatureModel>> byModId = models.stream().collect(Collectors.groupingBy(CreatureModel::modId, LinkedHashMap::new, Collectors.toList()));
        Map<String, List<AdditionalConverterModel>> additionalByModId = additionalModels.stream().collect(Collectors.groupingBy(AdditionalConverterModel::modId, LinkedHashMap::new, Collectors.toList()));

        for (Map.Entry<String, List<CreatureModel>> entry : byModId.entrySet()) {
            String modId = entry.getKey();
            List<CreatureModel> group = entry.getValue();
            List<AdditionalConverterModel> groupAdditional = additionalByModId.getOrDefault(modId, List.of());

            String pkg = group.getFirst().packageName();
            Element origin = group.getFirst().element();

            ProcessorUtil.write(processingEnv, messager, origin, pkg,
                    RegistrarGenerator.className(modId), RegistrarGenerator.generate(pkg, modId, group));
            ProcessorUtil.write(processingEnv, messager, origin, pkg,
                    DatagenGenerator.className(modId), DatagenGenerator.generate(pkg, pkg, modId, group, groupAdditional));
            if (group.stream().anyMatch(CreatureModel::hasRenderer)) {
                ProcessorUtil.write(processingEnv, messager, origin, pkg,
                        ClientGenerator.className(modId), ClientGenerator.generate(pkg, pkg, modId, group));
            }
        }
        return true;
    }

    private Set<Element> annotatedWith(RoundEnvironment roundEnv, Class<? extends java.lang.annotation.Annotation> single, Class<? extends java.lang.annotation.Annotation> container) {
        Set<Element> elements = new LinkedHashSet<>();
        elements.addAll(roundEnv.getElementsAnnotatedWith(single));
        elements.addAll(roundEnv.getElementsAnnotatedWith(container));
        return elements;
    }

    @Nullable
    private AdditionalConverterModel toAdditionalModel(Element element, AdditionalConverter a) {
        ModId modIdAnnotation = element.getAnnotation(ModId.class);
        if (modIdAnnotation == null) {
            ProcessorUtil.error(messager, element, "@AdditionalConverter requires @ModId on the same class");
            return null;
        }
        String modId = modIdAnnotation.value();

        TypeMirror vanilla = ProcessorUtil.mirror(a::vanilla);
        if (!(vanilla instanceof DeclaredType vt) || !(vt.asElement() instanceof TypeElement ve)) {
            ProcessorUtil.error(messager, element, "@AdditionalConverter vanilla must be a class");
            return null;
        }
        String entityType = ProcessorUtil.upper(ve.getSimpleName().toString());

        String convertedField = a.convertedField();
        int lastDot = convertedField.lastIndexOf('.');
        if (lastDot < 0) {
            ProcessorUtil.error(messager, element, "@AdditionalConverter convertedField must be a fully-qualified field reference");
            return null;
        }
        String classFqn = convertedField.substring(0, lastDot);
        String fieldName = convertedField.substring(lastDot + 1);
        String simpleClass = classFqn.substring(classFqn.lastIndexOf('.') + 1);

        return new AdditionalConverterModel(modId, entityType, classFqn, simpleClass, fieldName);
    }

    @Nullable
    private CreatureModel toModel(Element element, ConvertedCreature a) {
        ModId modIdAnnotation = element.getAnnotation(ModId.class);
        if (modIdAnnotation == null) {
            ProcessorUtil.error(messager, element, "@ConvertedCreature requires @ModId on the same class");
            return null;
        }
        String modId = modIdAnnotation.value();

        TypeMirror base = ProcessorUtil.mirror(a::value);
        if (!(base instanceof DeclaredType bt) || !(bt.asElement() instanceof TypeElement be)) {
            ProcessorUtil.error(messager, element, "@ConvertedCreature value must be a class");
            return null;
        }
        String baseSimple = be.getSimpleName().toString();
        String registryName = a.registryName().isBlank() ? "converted_" + ProcessorUtil.lower(baseSimple) : a.registryName();
        String className = a.className().isBlank() ? "Converted" + baseSimple : a.className();
        String entityType = a.entityType().isBlank() ? ProcessorUtil.upper(baseSimple) : a.entityType();
        String holderField = ProcessorUtil.upper(registryName);
        String packageName = ProcessorUtil.packageOf(processingEnv, element);

        @Nullable String subclassFqn = null;
        @Nullable String registeredSimple = null;
        TypeMirror sub = ProcessorUtil.mirror(a::subclass);
        if (sub != null && sub.getKind() != TypeKind.VOID && sub instanceof DeclaredType st && st.asElement() instanceof TypeElement se) {
            subclassFqn = se.getQualifiedName().toString();
            registeredSimple = se.getSimpleName().toString();
        }
        if (registeredSimple == null) {
            registeredSimple = className;
        }

        @Nullable String rendererFqn = null;
        @Nullable String rendererSimple = null;
        TypeMirror renderer = ProcessorUtil.mirror(a::renderer);
        if (renderer != null && renderer.getKind() != TypeKind.VOID && renderer instanceof DeclaredType rt && rt.asElement() instanceof TypeElement re) {
            rendererFqn = re.getQualifiedName().toString();
            rendererSimple = re.getSimpleName().toString();
        }

        return new CreatureModel(element, packageName, modId, be.getQualifiedName().toString(), baseSimple,
                className, entityType, registryName, holderField,
                a.width(), a.height(), a.mobCategory(), a.attributeMethod(), a.spawnRulesFrom(), a.immuneToSweetBerryBush(),
                subclassFqn, registeredSimple, rendererFqn, rendererSimple);
    }
}
