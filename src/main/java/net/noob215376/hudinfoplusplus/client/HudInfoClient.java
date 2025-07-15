package net.noob215376.hudinfoplusplus.client;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.noob215376.hudinfoplusplus.HudInfoPlusPlus;
import org.lwjgl.glfw.GLFW;
public class HudInfoClient implements ClientModInitializer {
  private static final int PADDING_X = 5;
  private static final int PADDING_Y = 5;
  private static final int LINE_HEIGHT = 10;


  private static KeyBinding toggleKey;
  private boolean wasKeyPressed = false;
  private boolean hudVisible = true;

  @Override
  public void onInitializeClient() {
    HudInfoPlusPlus.LOGGER.info("Initializing HudInfoPlusPlus (Client)");

    toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.hudinfoplusplus.toggle",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            "category.hudinfoplusplus"
    ));

    ClientTickEvents.END_CLIENT_TICK.register(client -> {
      boolean isKeyPressed = toggleKey.isPressed();
      if (isKeyPressed && !wasKeyPressed) {
        hudVisible = !hudVisible;
        if (client.player != null) {
          client.player.sendMessage(Text.literal("HUD Info++: " + (hudVisible ? "ON" : "OFF")), false);
        }
      }
      wasKeyPressed = isKeyPressed;
    });

    HudRenderCallback.EVENT.register(this::renderHud);
  }

  private void renderHud(DrawContext context, RenderTickCounter renderTickCounter) {
    MinecraftClient client = MinecraftClient.getInstance();
    TextRenderer textRenderer = client.textRenderer;
    if (client.player == null || client.world == null || client.options.hudHidden || client.getDebugHud().shouldShowDebugHud() || !hudVisible) {
      return;
    }
    int currentY = PADDING_Y;

    // --- Render FPS --- //
    if (HudInfoPlusPlus.CONFIG.showFps) {
      String fpsText = "FPS: " + client.getCurrentFps();
      context.drawTextWithShadow(textRenderer, Text.literal(fpsText), PADDING_X, currentY, HudInfoPlusPlus.CONFIG.fpsColor);
      currentY += LINE_HEIGHT;
    }

    // --- Render Coordinates --- //
    if (HudInfoPlusPlus.CONFIG.showCoordinates) {
      BlockPos pos = client.player.getBlockPos();
      String coordsText = String.format("X: %d Y: %d Z: %d", pos.getX(), pos.getY(), pos.getZ());
      context.drawTextWithShadow(textRenderer, Text.literal(coordsText), PADDING_X, currentY, HudInfoPlusPlus.CONFIG.fpsColor);
      currentY += LINE_HEIGHT;
    }

    // --- Render Days Played And Time In World --- //
    if (HudInfoPlusPlus.CONFIG.showDaysPlayed) {
      long totalTicks = client.world.getTimeOfDay();
      long days = totalTicks / 24000L;
      long ticksToday = totalTicks % 24000L;
      long hours = ticksToday / 1000L;
      long mins = (ticksToday % 1000L) * 60L / 1000L;
      String daysText = String.format("Days: %d (%02d:%02d)", days, hours, mins);
      context.drawTextWithShadow(textRenderer, Text.literal(daysText), PADDING_X, currentY, HudInfoPlusPlus.CONFIG.fpsColor);
      currentY += LINE_HEIGHT;
    }
  }
}
