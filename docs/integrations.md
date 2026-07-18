# Mod integrations

Vampirism and FactionApi can integrate with other mods (JEI, TerraBlender, AppleSkin, ...) without adding a hard compile-time dependency on them to the main source set. Each integration lives in its own Gradle source set and is only compiled/packaged if it is enabled, and only loaded at runtime if the target mod is actually present.

This is implemented by `de.teamlapen.integration.Integration`.

## How it works

- Integration code lives under `src/integrations/<name>/java/...` in a project (see `projects/vampirism/src/integrations/terrablender` or `projects/faction/src/integrations/appleskin`).
- A class is marked as an integration entry point with `@Integration(modIds = {...}, dist = {...})` similar to `@EventBusSubscriber`:
  - `modIds` — one or more mod ids that must all be loaded for the integration to activate.
  - `dist` — which physical sides (`Dist.CLIENT` / `Dist.DEDICATED_SERVER`) it should load on. Defaults to both.
- On `NewRegistryEvent`, `Integrations` scans every mod's ASM annotation data for `@Integration`-annotated classes, filters out any whose required mod ids aren't loaded or whose `dist` doesn't match, then loads the matching classes and registers their static `@SubscribeEvent`-annotated methods:
  - Methods taking an `IModBusEvent` subtype are registered on the owning mod's mod event bus.
  - All other methods are registered on the `NeoForge.EVENT_BUS`.
- Only **static** methods work — no instance of the integration class is created, so `@SubscribeEvent` methods must be `static`.
- Because the class is only loaded if the target mod is present, it's safe to import the other mod's classes/API directly in an integration class — just don't reference them from code outside `src/integrations/<name>`.

## Adding a new integration

1. **Register the source set** in the project's `integrations.gradle` (e.g. `projects/vampirism/integrations.gradle`):
   ```groovy
   record Integration(String name, boolean included) {}

   ext.integrations = [
           new Integration("jei", include_jei.toBoolean()),
           new Integration("mynewmod", include_mynewmod.toBoolean()),
   ]
   ```
   The `build.gradle` in the same project already loops over `integrations` to create the source set, wire it into `sourceSets.main.runtimeClasspath`, register it with `neoForge.mods { ... }`, and include it in the `jar`/`sourcesJar` tasks — nothing else needs to change there.

2. **Add the toggle and version property** in the root `gradle.properties`:
   ```properties
   # mynewmod
   include_mynewmod=true
   mynewmod_version=1.2.3
   ```

3. **Add the (optional) compile/runtime dependency** in the project's `dependencies.gradle`, guarded by the same toggle:
   ```groovy
   if (include_mynewmod.toBoolean()) {
       compileOnly "some.group:mynewmod-api:${project.mynewmod_version}"
       localRuntime "some.group:mynewmod:${project.mynewmod_version}"
   }
   ```

4. **Create the integration class** under `src/integrations/mynewmod/java/...`, annotated with `@Integration(modIds = "mynewmod")`, with static `@SubscribeEvent` methods:
   ```java
   @Integration(modIds = "mynewmod")
   public class MyNewModIntegration {
       @SubscribeEvent
       public static void onSomeEvent(SomeEvent event) {
           // ...
       }
   }
   ```

## Examples

- **Simple event hook** — `projects/faction/src/integrations/appleskin/java/de/teamlapen/faction/client/integration/AppleSkinIntegration.java`: reacts to AppleSkin's `FoodValuesEvent` to report predictive food values for faction-specific food.
- **Setup logic beyond event handling** — `projects/vampirism/src/integrations/terrablender/java/de/teamlapen/vampirism/common/integration/terrablender/TerraBlenderIntegration.java`: on `FMLCommonSetupEvent`, conditionally registers a TerraBlender `Region` and surface rules, and reports back to the mod (`VampirismMod.integrations().useTerraBlender(...)`) that TerraBlender is handling biome placement, so other (non-integration) code can adapt.
