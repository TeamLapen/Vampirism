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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Mixin(SplashManager.class)
public class SplashManagerMixin {

    @Unique
    private static final String VAMPIRISM_SPLASHES_LOCATION = "/assets/" + REFERENCE.MODID + "/texts/splashes.txt";
    @Unique
    private static final Logger vampirism$LOGGER = LogManager.getLogger();
    @Unique
    private static final RandomSource vampirism$RANDOM = RandomSource.create();

    @Final
    @Shadow
    private List<String> splashes;
    @Unique
    private List<String> vampirism$customSplashes = Collections.emptyList();

    @Inject(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Ljava/util/List;", at = @At("RETURN"))
    private void vampirism$prepare(ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfoReturnable<List<String>> cir) {
        try (InputStream inputStream = VampirismMod.class.getResourceAsStream(VAMPIRISM_SPLASHES_LOCATION)) {
            if (inputStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    vampirism$customSplashes = reader.lines().map(String::trim).filter(line -> !line.isEmpty()).toList();

                    vampirism$LOGGER.info("Loaded {} Vampirism splashes", vampirism$customSplashes.size());
                }
            }
        } catch (Exception e) {
            vampirism$LOGGER.warn("Failed to load Vampirism splash file", e);
        }
    }

    @Inject(method = "apply(Ljava/util/List;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("TAIL"))
    private void vampirism$apply(List<String> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        if (vampirism$customSplashes.isEmpty()) return;

        double chance = 0.2;
        int originalSize = splashes.size();
        int timesAdded = (int) Math.ceil((chance * originalSize) / ((1 - chance) * vampirism$customSplashes.size()));

        for (int i = 0; i < timesAdded; i++) {
            for (String splash : vampirism$customSplashes) {
                splashes.add(vampirism$RANDOM.nextInt(splashes.size() + 1), splash);
            }
        }

        vampirism$LOGGER.info("Successfully loaded and added {} Vampirism splashes {} times each. Final list size: {}", vampirism$customSplashes.size(), timesAdded, this.splashes.size());
    }

    @Inject(method = "getSplash", at = @At("HEAD"), cancellable = true)
    private void getCustomSplash(CallbackInfoReturnable<SplashRenderer> cir) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        if (calendar.get(Calendar.MONTH) == Calendar.AUGUST && calendar.get(Calendar.DATE) == 11) {
            cir.setReturnValue(new SplashRenderer("Happy anniversary, Vampirism!"));
        } else if (calendar.get(Calendar.MONTH) == Calendar.NOVEMBER && calendar.get(Calendar.DATE) == 16) {
            cir.setReturnValue(new SplashRenderer("Happy anniversary, Vampire's Delight!"));
        }
    }
}
