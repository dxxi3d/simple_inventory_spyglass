package dxxi3d.simple_inventory_spyglass;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ModLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(SharedConstants.MOD_NAME);

    private ModLogger() {
    }

    public static void sendInitialized() {
        LOGGER.info("Mod initialized!");
    }

    public static void info(String message, Object... params) {
        LOGGER.info(message, params);
    }

    public static void warn(String message, Object... params) {
        LOGGER.warn(message, params);
    }

    public static void error(String message, Object... params) {
        LOGGER.error(message, params);
    }
}
