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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.noob215376.hudinfoplusplus.HudInfoPlusPlus;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class HudInfoClient implements ClientModInitializer {
  private static final int PADDING_X = 5;
  private static final int PADDING_Y = 5;
  private static final int LINE_HEIGHT = 10;

  private static KeyBinding toggleKey;
  private boolean wasKeyPressed = false;
  private boolean hudVisible = true;

  private static final Map<Identifier, Integer> BIOME_COLORS = new HashMap<>();

  static {
    BIOME_COLORS.put(Identifier.of("minecraft:plains"), 0xFF55FF55);
    BIOME_COLORS.put(Identifier.of("minecraft:forest"), 0xFF33AA33);
    BIOME_COLORS.put(Identifier.of("minecraft:dark_forest"), 0xFF228822);
    BIOME_COLORS.put(Identifier.of("minecraft:birch_forest"), 0xFF66DD66);
    BIOME_COLORS.put(Identifier.of("minecraft:taiga"), 0xFF006600);

    BIOME_COLORS.put(Identifier.of("minecraft:snowy_taiga"), 0xFF005500);

    BIOME_COLORS.put(Identifier.of("minecraft:old_growth_pine_taiga"), 0xFF004400);
    BIOME_COLORS.put(Identifier.of("minecraft:mountains"), 0xFF999999);
    BIOME_COLORS.put(Identifier.of("minecraft:extreme_hills"), 0xFF888888);
    BIOME_COLORS.put(Identifier.of("minecraft:desert"), 0xFFFFAA00);
    BIOME_COLORS.put(Identifier.of("minecraft:swamp"), 0xFF445544);
    BIOME_COLORS.put(Identifier.of("minecraft:ocean"), 0xFF0000AA);
    BIOME_COLORS.put(Identifier.of("minecraft:deep_ocean"), 0xFF000066);
    BIOME_COLORS.put(Identifier.of("minecraft:river"), 0xFF0055FF);
    BIOME_COLORS.put(Identifier.of("minecraft:beach"), 0xFFDDDD77);
    BIOME_COLORS.put(Identifier.of("minecraft:mushroom_fields"), 0xFFFF55FF);
    BIOME_COLORS.put(Identifier.of("minecraft:badlands"), 0xFFAA0000);
    BIOME_COLORS.put(Identifier.of("minecraft:wooded_badlands"), 0xFFAA3300);
    BIOME_COLORS.put(Identifier.of("minecraft:jungle"), 0xFF00BB00);
    BIOME_COLORS.put(Identifier.of("minecraft:sparse_jungle"), 0xFF00AA00);
    BIOME_COLORS.put(Identifier.of("minecraft:bamboo_jungle"), 0xFF00CC00);
    BIOME_COLORS.put(Identifier.of("minecraft:savanna"), 0xFFBBBB00);
    BIOME_COLORS.put(Identifier.of("minecraft:windswept_savanna"), 0xFFAAAA00);

    BIOME_COLORS.put(Identifier.of("minecraft:snowy_plains"), 0xFFCCDDCC);

    BIOME_COLORS.put(Identifier.of("minecraft:ice_spikes"), 0xFFDDDDFF);
    BIOME_COLORS.put(Identifier.of("minecraft:frozen_ocean"), 0xFFCCDDFF);
    BIOME_COLORS.put(Identifier.of("minecraft:frozen_river"), 0xFFBBDDFF);
    BIOME_COLORS.put(Identifier.of("minecraft:lukewarm_ocean"), 0xFF0077BB);
    BIOME_COLORS.put(Identifier.of("minecraft:warm_ocean"), 0xFF00AAAA);
    BIOME_COLORS.put(Identifier.of("minecraft:cold_ocean"), 0xFF004488);

    BIOME_COLORS.put(Identifier.of("minecraft:nether_wastes"), 0xFFAA0000);
    BIOME_COLORS.put(Identifier.of("minecraft:crimson_forest"), 0xFFFF3333);
    BIOME_COLORS.put(Identifier.of("minecraft:warped_forest"), 0xFF00AAAA);
    BIOME_COLORS.put(Identifier.of("minecraft:soul_sand_valley"), 0xFF554433);
    BIOME_COLORS.put(Identifier.of("minecraft:basalt_deltas"), 0xFF777777);

    BIOME_COLORS.put(Identifier.of("minecraft:the_end"), 0xFFBB55FF);
    BIOME_COLORS.put(Identifier.of("minecraft:end_highlands"), 0xFF9933CC);
    BIOME_COLORS.put(Identifier.of("minecraft:end_midlands"), 0xFFCC66FF);
    BIOME_COLORS.put(Identifier.of("minecraft:small_end_islands"), 0xFFDD99FF);
    BIOME_COLORS.put(Identifier.of("minecraft:end_barrens"), 0xFF7722AA);

    BIOME_COLORS.put(Identifier.of("minecraft:deep_dark"), 0xFF333344);
  }

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

    if (HudInfoPlusPlus.CONFIG.showFps) {
      String fpsText = "FPS: " + client.getCurrentFps();
      context.drawTextWithShadow(textRenderer, Text.literal(fpsText), PADDING_X, currentY, HudInfoPlusPlus.CONFIG.fpsColor);
      currentY += LINE_HEIGHT;
    }

    if (HudInfoPlusPlus.CONFIG.showCoordinates) {
      BlockPos pos = client.player.getBlockPos();
      String coordsText = String.format("X: %d Y: %d Z: %d", pos.getX(), pos.getY(), pos.getZ());
      context.drawTextWithShadow(textRenderer, Text.literal(coordsText), PADDING_X, currentY, HudInfoPlusPlus.CONFIG.fpsColor);
      currentY += LINE_HEIGHT;
    }

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

    if (HudInfoPlusPlus.CONFIG.showBiome) {
      BlockPos pos = client.player.getBlockPos();
      RegistryEntry<Biome> biomeEntry = client.world.getBiome(pos);

      Text biomeNameText;
      Identifier biomeId = null;
      if (biomeEntry.hasKeyAndValue()) {
        biomeId = biomeEntry.getKey().get().getValue();
        biomeNameText = Text.translatable("biome." + biomeId.getNamespace() + "." + biomeId.getPath());
      } else {
        biomeNameText = Text.literal("Unknown Biome");
      }

      int biomeNameColor = 0xFFCCCCCC;
      if (biomeId != null && BIOME_COLORS.containsKey(biomeId)) {
        biomeNameColor = BIOME_COLORS.get(biomeId);
      }

      String prefix = "Biome: ";
      context.drawTextWithShadow(textRenderer, Text.literal(prefix), PADDING_X, currentY, 0xFFFFFFFF);

      int biomeNameX = PADDING_X + textRenderer.getWidth(prefix);

      context.drawTextWithShadow(textRenderer, biomeNameText, biomeNameX, currentY, biomeNameColor);

      currentY += LINE_HEIGHT;
    }
  }
}