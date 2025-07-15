package net.noob215376.hudinfoplusplus;
import net.fabricmc.api.ModInitializer;
import net.noob215376.hudinfoplusplus.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HudInfoPlusPlus implements ModInitializer {
	public static final String MOD_ID = "hudinfoplusplus";
	public static final Logger LOGGER = LoggerFactory.getLogger("HudInfoPlusPlus");

	public static ModConfig CONFIG;

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing HudInfoPlusPlus (Common Initialization)");
		CONFIG = ModConfig.load();
	}
}
