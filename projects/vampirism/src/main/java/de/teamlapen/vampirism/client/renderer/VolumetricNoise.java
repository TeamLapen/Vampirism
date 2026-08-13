package de.teamlapen.vampirism.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class VolumetricNoise {

    public static final int SIZE = 256;
    private static final int SLICE_STEP_X = 37;
    private static final int SLICE_STEP_Y = 17;
    private static final long SEED = 0x5F3759DFL; // Fixed so the volumes look the same on every client and across restarts

    private static @Nullable GpuTexture texture;
    private static @Nullable GpuTextureView view;

    /**
     * @return the noise sheet that is created when this method is accessed the first time.
     * Must be called on the render thread.
     */
    public static GpuTextureView view() {
        GpuTextureView current = view;
        if (current == null) {
            current = create();
        }
        return current;
    }

    private static GpuTextureView create() {
        byte[] first = new byte[SIZE * SIZE];
        byte[] second = new byte[SIZE * SIZE];
        Random random = new Random(SEED);
        random.nextBytes(first);
        random.nextBytes(second);

        try (NativeImage image = new NativeImage(NativeImage.Format.RGBA, SIZE, SIZE, false)) {
            for (int y = 0; y < SIZE; y++) {
                for (int x = 0; x < SIZE; x++) {
                    int here = index(x, y);
                    int nextSlice = index(x + SLICE_STEP_X, y + SLICE_STEP_Y);
                    int red = first[nextSlice] & 0xFF;
                    int green = first[here] & 0xFF;
                    int blue = second[nextSlice] & 0xFF;
                    int alpha = second[here] & 0xFF;
                    image.setPixelABGR(x, y, alpha << 24 | blue << 16 | green << 8 | red);
                }
            }
            texture = RenderSystem.getDevice().createTexture("Vampirism volumetric noise", GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_TEXTURE_BINDING, TextureFormat.RGBA8, SIZE, SIZE, 1, 1);
            RenderSystem.getDevice().createCommandEncoder().writeToTexture(texture, image);
        }

        GpuTextureView created = RenderSystem.getDevice().createTextureView(texture);
        view = created;

        return created;
    }

    private static int index(int x, int y) {
        return (y & SIZE - 1) * SIZE + (x & SIZE - 1);
    }
}
