package hunkarada.soulary.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import hunkarada.soulary.capabilities.souls.SoulCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

import java.awt.*;

import static com.mojang.blaze3d.vertex.VertexFormat.Mode.TRIANGLE_FAN;

public class SoulHud implements IIngameOverlay {

    /**
     * Made by ConnorTron110 and a lot of caffeine and stress, Feel free to use where ever. <br>
     * The Code is horrible especially on the iteration step, cba to figure it out cos I got it working by hard coding and im not getting paid to do this.
     * My Username in minecraft is just ConnorTron110
     * And my favourite colour's hex is #0a7a7a
     * @param color Uses {@link java.awt.Color} as it shows what colour it will be on IDEA (can change this)
     * @param percentage The percentage of the chart
     * @param innerScale The size of the inner part of the circle
     * @param outerScale The size of the outer part of the circle
     * @param posX Center X position of the circle in relation from the bottom right corner
     * @param posY Center Y position of the circle in relation from the bottom right corner
     *
     */
    public static void renderHollowCircle(Color color, double percentage, float innerScale, float outerScale, int posX, int posY, ForgeIngameGui gui) {
        gui.setupOverlayRenderState(true, true, null);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        percentage = Mth.clamp(percentage, 0 ,100); //No need to do anything above 100
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        int posX1 = Minecraft.getInstance().getWindow().getGuiScaledWidth() - posX;
        int posY1 = Minecraft.getInstance().getWindow().getGuiScaledHeight() - posY;
        //https://www.desmos.com/calculator/ufiszjenxu for best explanation of this
        int l = Mth.floor(percentage / 4.0D) + 1;
        for (int i2 = l-1; i2 >= 0; --i2) {
            bufferbuilder.begin(TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
            float fst = (float) ((percentage * (double) (i2) / (double) l) * (double) ((float) Math.PI * 2F) / 100.0D);
            float fnd = (float) ((percentage * (double) (i2+1) / (double) l) * (double) ((float) Math.PI * 2F) / 100.0D);
            bufferbuilder.vertex((float) posX1 + (Mth.sin(fst) * outerScale), (float) posY1 - (Mth.cos(fst) * outerScale), 0.0D).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
            bufferbuilder.vertex((float) posX1 + (Mth.sin(fst) * innerScale), (float) posY1 - (Mth.cos(fst) * innerScale), 0.0D).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
            bufferbuilder.vertex((float) posX1 + (Mth.sin(fnd) * innerScale), (float) posY1 - (Mth.cos(fnd) * innerScale), 0.0D).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
            bufferbuilder.vertex((float) posX1 + (Mth.sin(fnd) * outerScale), (float) posY1 - (Mth.cos(fnd) * outerScale), 0.0D).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
            tesselator.end();
        }
    }
    @Override
    public void render(ForgeIngameGui gui, PoseStack popStack, float partialTicks, int width, int height) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.getCapability(SoulCapability.Provider.SOUL_CAPABILITY).ifPresent(capability -> {
                renderHollowCircle(new Color(0x0065A9), capability.soulStats.get("soulWill"), 50, 60, 100, 100, gui);
                renderHollowCircle(new Color(0xFD7F08), capability.soulStats.get("soulStability"), 40, 50, 100, 100, gui);
                renderHollowCircle(new Color(0xFF0000), capability.soulFeelings.get("joy/sadness"), 30, 40, 100, 100, gui);
                }
            );
        }
    }
}
