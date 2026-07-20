package com.fren507.mcauth;

import com.fren507.mcauth.command.VerifyCommand;
import com.fren507.mcauth.manager.TokenManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MCAuthMod implements ModInitializer {
    public static final String MOD_ID = "mcauth";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static TokenManager tokenManager;

    @Override
    public void onInitialize() {
        LOGGER.info("Initialisiere MC Auth System...");
        tokenManager = new TokenManager();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            VerifyCommand.register(dispatcher);
        });
    }

    public static TokenManager getTokenManager() {
        return tokenManager;
    }
}
