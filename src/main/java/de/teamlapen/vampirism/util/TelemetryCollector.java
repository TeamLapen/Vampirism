package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.config.VampirismConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.versions.mcp.MCPVersion;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class TelemetryCollector {

    public static final Logger LOGGER = LogManager.getLogger();

    public static void execute() {
        // TODO use server telemetry when available
        if (DistExecutor.unsafeRunForDist(() -> () -> Minecraft.getInstance().allowsTelemetry(), () -> () -> true) && VampirismConfig.COMMON.collectStats.get()) {
            send();
        }
    }

    private static void send() {
        try {
            URIBuilder builder = new URIBuilder(REFERENCE.SETTINGS_API);
            addPathSegment(builder,"telemetry", "basic");
            builder.addParameter("mod_version", REFERENCE.VERSION.toString());
            builder.addParameter("mc_version", MCPVersion.getMCVersion());
            builder.addParameter("mod_count", Integer.toString(ModList.get().size()));
            builder.addParameter("side", (EffectiveSide.get() == LogicalSide.CLIENT ? "client" : "server"));
            HttpClient.newHttpClient().send(HttpRequest.newBuilder().uri(builder.build()).build(), HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            LOGGER.error("Failed to send telemetry data", e);
        }
    }

    private static void addPathSegment(URIBuilder builder, @SuppressWarnings("SameParameterValue") String... segments) {
        List<String> pathSegments = builder.getPathSegments();
        pathSegments.removeIf(String::isBlank);
        pathSegments.addAll(Arrays.stream(segments).toList());
        builder.setPathSegments(pathSegments);
    }
}
