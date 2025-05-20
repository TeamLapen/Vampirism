package de.teamlapen.vampirism.mixin.client;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.server.packs.resources.ResourceManager;
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
import java.util.Date;
import java.util.List;

@Mixin(SplashManager.class)
public class SplashManagerMixin {

    @Unique
    private static final String VAMPIRISM_SPLASHES_LOCATION = "/assets/" + REFERENCE.MODID + "/texts/splashes.txt";

    @Unique
    private static final Logger vampirism$LOGGER = LogManager.getLogger();

    @Final
    @Shadow
    private List<String> splashes;

    @Inject(method = "apply(Ljava/util/List;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("TAIL"))
    private void applyCustomSplashes(List<String> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        try (InputStream inputStream = VampirismMod.class.getResourceAsStream(VAMPIRISM_SPLASHES_LOCATION)) {
            if (inputStream != null) {
                try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    List<String> customSplashes = bufferedReader.lines().map(String::trim).filter(line -> !line.isEmpty()).toList();

                    // Vampirism's splashes are added more than one time in order to increase their chance of being displayed, otherwise they would unlikely be rolled at all
                    double chance = 0.2;
                    int timesAdded = (int) Math.ceil((chance * splashes.size()) / ((1 - chance) * customSplashes.size()));
                    for (int i = 0; i < timesAdded; i++) {
                        this.splashes.addAll(customSplashes);
                    }

                    vampirism$LOGGER.info("Successfully loaded and added {} Vampirism splashes {} times to {} vanilla ones", customSplashes.size(), timesAdded, splashes.size() - customSplashes.size() * timesAdded);
                } catch (Exception exception) {
                    vampirism$LOGGER.warn("Failed to load Vampirism splash file", exception);
                }
            }
        } catch (IOException exception) {
            vampirism$LOGGER.warn("Failed to load Vampirism splashes", exception);
        }
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
