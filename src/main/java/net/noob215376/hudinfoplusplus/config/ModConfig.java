package net.noob215376.hudinfoplusplus.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.noob215376.hudinfoplusplus.HudInfoPlusPlus;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class ModConfig {
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
  private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve(HudInfoPlusPlus.MOD_ID + ".json");
  public boolean showFps = true;
  public boolean showCoordinates = true;
  public boolean showDaysPlayed = true;
  public boolean showBiome = true;
  public int fpsColor = 0xFFFFFFFF;

  public static ModConfig load() {
    ModConfig config = new ModConfig();
    if (CONFIG_FILE.toFile().exists()) {
      try (FileReader reader = new FileReader(CONFIG_FILE.toFile())) {
        config = GSON.fromJson(reader, ModConfig.class);
        HudInfoPlusPlus.LOGGER.info("Loaded config from: {}", CONFIG_FILE);
      } catch (IOException e) {
        HudInfoPlusPlus.LOGGER.error("Failed to load config from {}, using default settings: {}", CONFIG_FILE, e.getMessage());
      }
    } else {
      HudInfoPlusPlus.LOGGER.info("Config file not found, creating default at: {}", CONFIG_FILE);
      config.save();
    }
    return config;
  }

  public void save() {
    try (FileWriter writer = new FileWriter(CONFIG_FILE.toFile())) {
      GSON.toJson(this, writer);
      HudInfoPlusPlus.LOGGER.info("Saved config to: {}", CONFIG_FILE);
    } catch (IOException e) {
      HudInfoPlusPlus.LOGGER.error("Failed to save config to {}: {}", CONFIG_FILE, e.getMessage());
    }
  }
}
