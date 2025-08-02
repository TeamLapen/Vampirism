package de.teamlapen.vampirism.mixin.client;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Mixin(SplashManager.class)
public class SplashManagerMixin {

    @Unique
    private static final String VAMPIRISM_SPLASHES_LOCATION = "/assets/" + REFERENCE.MODID + "/texts/splashes.txt";
    @Unique
    private static final Logger vampirism$LOGGER = LogManager.getLogger();
    @Unique
    private static final RandomSource vampirism$RANDOM = RandomSource.create();

    @Inject(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Ljava/util/List;", at = @At("RETURN"), cancellable = true)
    private void vampirism$prepare(ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfoReturnable<List<String>> cir) {
        List<String> baseSplashes = cir.getReturnValue();
        List<String> customSplashes = Collections.emptyList();

        try (InputStream inputStream = VampirismMod.class.getResourceAsStream(VAMPIRISM_SPLASHES_LOCATION)) {
            if (inputStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    customSplashes = reader.lines().map(String::trim).filter(line -> !line.isEmpty()).toList();

                    vampirism$LOGGER.info("Loaded {} Vampirism splashes", customSplashes.size());
                }
            }
        } catch (Exception e) {
            vampirism$LOGGER.warn("Failed to load Vampirism splash file", e);
        }

        if (!customSplashes.isEmpty()) {
            double chance = 0.3;
            int originalSize = baseSplashes.size();
            int timesAdded = (int) Math.ceil((chance * originalSize) / ((1 - chance) * customSplashes.size()));

            for (int i = 0; i < timesAdded; i++) {
                for (String splash : customSplashes) {
                    baseSplashes.add(vampirism$RANDOM.nextInt(baseSplashes.size() + 1), splash);
                }
            }

            vampirism$LOGGER.info("Successfully loaded and added {} Vampirism splashes {} times each. Final size: {}", customSplashes.size(), timesAdded, baseSplashes.size());
        }

        cir.setReturnValue(baseSplashes);
    }

    @Inject(method = "getSplash", at = @At("HEAD"), cancellable = true)
    private void getCustomSplash(CallbackInfoReturnable<SplashRenderer> cir) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (calendar.get(Calendar.MONTH) == Calendar.AUGUST && calendar.get(Calendar.DATE) == 11) {
            cir.setReturnValue(new SplashRenderer("Happy anniversary, Vampirism!"));
        }
    }
}
