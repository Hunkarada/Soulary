//package hunkarada.soulary.client.hud;
//
//import com.mojang.blaze3d.systems.RenderSystem;
//import com.mojang.blaze3d.vertex.BufferBuilder;
//import com.mojang.blaze3d.vertex.DefaultVertexFormat;
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mojang.blaze3d.vertex.Tesselator;
//import hunkarada.soulary.capabilities.souls.SoulCapability;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.player.LocalPlayer;
//import net.minecraft.client.renderer.GameRenderer;
//import net.minecraft.util.Mth;
//import net.minecraftforge.client.gui.ForgeIngameGui;
//import net.minecraftforge.client.gui.IIngameOverlay;
//
//import static com.mojang.blaze3d.vertex.VertexFormat.Mode.TRIANGLE_FAN;
//
//public class SoulHud implements IIngameOverlay {
//
//    /**
//     * Made by ConnorTron110 and a lot of caffeine and stress, Feel free to use where ever. <br>
//     * The Code is horrible especially on the iteration step, cba to figure it out cos I got it working by hard coding and im not getting paid to do this.
//     * My Username in minecraft is just ConnorTron110
//     * And my favourite colour's hex is #0a7a7a
//     * @param percentage The percentage of the chart
//     * @param innerScale The size of the inner part of the circle
//     * @param outerScale The size of the outer part of the circle
//     * @param posX Center X position of the circle in relation from the bottom right corner
//     * @param posY Center Y position of the circle in relation from the bottom right corner
//     *
//     */
//    public static void renderHollowCircle(int r, int g, int b, double percentage, float innerScale, float outerScale, int posX, int posY, ForgeIngameGui gui) {
//        gui.setupOverlayRenderState(true, true, null);
//        RenderSystem.setShader(GameRenderer::getPositionColorShader);
//        percentage = Mth.clamp(percentage, 0 ,100); //No need to do anything above 100
//        Tesselator tesselator = Tesselator.getInstance();
//        BufferBuilder bufferbuilder = tesselator.getBuilder();
//        int posX1 = Minecraft.getInstance().getWindow().getGuiScaledWidth() - posX;
//        int posY1 = Minecraft.getInstance().getWindow().getGuiScaledHeight() - posY;
//        //https://www.desmos.com/calculator/ufiszjenxu for best explanation of this
//        int l = Mth.floor(percentage / 4.0D) + 1;
//        for (int i2 = l-1; i2 >= 0; --i2) {
//            bufferbuilder.begin(TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
//            float fst = (float) ((percentage * (double) (i2) / (double) l) * (double) ((float) Math.PI * 2F) / 100.0D);
//            float fnd = (float) ((percentage * (double) (i2+1) / (double) l) * (double) ((float) Math.PI * 2F) / 100.0D);
//            bufferbuilder.vertex((float) posX1 + (Mth.sin(fst) * outerScale), (float) posY1 - (Mth.cos(fst) * outerScale), 0.0D).color(r, g, b, 100).endVertex();
//            bufferbuilder.vertex((float) posX1 + (Mth.sin(fst) * innerScale), (float) posY1 - (Mth.cos(fst) * innerScale), 0.0D).color(r, g, b, 100).endVertex();
//            bufferbuilder.vertex((float) posX1 + (Mth.sin(fnd) * innerScale), (float) posY1 - (Mth.cos(fnd) * innerScale), 0.0D).color(r, g, b, 100).endVertex();
//            bufferbuilder.vertex((float) posX1 + (Mth.sin(fnd) * outerScale), (float) posY1 - (Mth.cos(fnd) * outerScale), 0.0D).color(r, g, b, 100).endVertex();
//            tesselator.end();
//        }
//    }
//    @Override
//    public void render(ForgeIngameGui gui, PoseStack popStack, float partialTicks, int width, int height) {
//        LocalPlayer player = Minecraft.getInstance().player;
//        if (player != null) {
//            player.getCapability(SoulCapability.Provider.SOUL_CAPABILITY).ifPresent(capability -> {
//                renderHollowCircle(0, 101, 196, capability.getStat("will"), 60, 70, 390, 90, gui);
//                renderHollowCircle(253, 127, 3, capability.getStat("stability"), 50, 60, 390, 90, gui);
//                if (capability.getStat("joy/sadness") >= 0){
//                    renderHollowCircle(255, 255, 51, capability.getStat("joy/sadness"), 40, 50, 390, 90, gui);
//                }
//                else {
//                    renderHollowCircle(0, 0, 201, capability.getStat("joy/sadness")*-1, 40, 50, 390, 90, gui);
//                }
//                if (capability.getStat("trust/disgust") >= 0){
//                    renderHollowCircle(0, 255, 0, capability.getStat("trust/disgust"), 30, 40, 390, 90, gui);
//                }
//                else {
//                    renderHollowCircle(255, 0, 255, capability.getStat("trust/disgust")*-1, 30, 40, 390, 90, gui);
//                }
//                if (capability.getStat("fear/anger") >= 0){
//                    renderHollowCircle(255, 0, 0, capability.getStat("fear/anger"), 20, 30, 390, 90, gui);
//                }
//                else {
//                    renderHollowCircle(0, 128, 0, capability.getStat("fear/anger")*-1, 20, 30, 390, 90, gui);
//                }
//                if (capability.getStat("surprise/anticipation") >= 0){
//                    renderHollowCircle(255, 128, 0, capability.getStat("surprise/anticipation"), 10, 20, 390, 90, gui);
//                }
//                else {
//                    renderHollowCircle(0, 128, 255, capability.getStat("surprise/anticipation")*-1, 10, 20, 390, 90, gui);
//                }
//                renderHollowCircle(0, 0, 0, 100, 69, 71, 390, 90, gui);
//                renderHollowCircle(0, 0, 0, 100, 59, 61, 390, 90, gui);
//                renderHollowCircle(0, 0, 0, 100, 49, 51, 390, 90, gui);
//                renderHollowCircle(0, 0, 0, 100, 39, 41, 390, 90, gui);
//                renderHollowCircle(0, 0, 0, 100, 29, 31, 390, 90, gui);
//                renderHollowCircle(0, 0, 0, 100, 19, 21, 390, 90, gui);
//                renderHollowCircle(0, 0, 0, 100, 0, 11, 390, 90, gui);
//                }
//            );
//        }
//    }
//
//}
