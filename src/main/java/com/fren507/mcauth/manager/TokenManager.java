package com.fren507.mcauth.manager;

import com.fren507.mcauth.model.TokenData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class TokenManager {
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("mcauth");
    private static final Path TOKENS_FILE = CONFIG_DIR.resolve("tokens.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final List<TokenData> tokens = new CopyOnWriteArrayList<>();

    public TokenManager() {
        loadTokens();
    }

    public void loadTokens() {
        try {
            if (!Files.exists(CONFIG_DIR)) {
                Files.createDirectories(CONFIG_DIR);
            }
            if (Files.exists(TOKENS_FILE)) {
                try (Reader reader = Files.newBufferedReader(TOKENS_FILE)) {
                    List<TokenData> loaded = GSON.fromJson(reader, new TypeToken<List<TokenData>>(){}.getType());
                    if (loaded != null) {
                        tokens.clear();
                        tokens.addAll(loaded);
                    }
                }
            }
            cleanExpiredTokens();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveTokens() {
        try {
            try (Writer writer = Files.newBufferedWriter(TOKENS_FILE)) {
                GSON.toJson(tokens, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String generateToken(UUID playerUUID, String playerName) {
        // Altes Token invalidieren
        findPlayerToken(playerUUID).ifPresent(t -> t.setValid(false));

        String tokenString = generateSecureTokenString();
        Instant now = Instant.now();
        Instant expires = now.plus(2, ChronoUnit.HOURS);

        TokenData newToken = new TokenData(
                tokenString,
                playerUUID,
                playerName,
                now.toString(),
                expires.toString(),
                true
        );

        tokens.add(newToken);
        saveTokens();
        return tokenString;
    }

    private String generateSecureTokenString() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(SECURE_RANDOM.nextInt(chars.length())));
        }
        // Format: XXXX-XXXX-XXXX
        return sb.substring(0, 4) + "-" + sb.substring(4, 8) + "-" + sb.substring(8, 12);
    }

    public void cleanExpiredTokens() {
        Instant now = Instant.now();
        boolean changed = false;
        for (TokenData token : tokens) {
            if (token.isValid() && Instant.parse(token.getExpires()).isBefore(now)) {
                token.setValid(false);
                changed = true;
            }
        }
        if (changed) saveTokens();
    }

    public Optional<TokenData> getToken(String tokenStr) {
        cleanExpiredTokens();
        return tokens.stream()
                .filter(t -> t.getToken().equals(tokenStr))
                .findFirst();
    }

    public Optional<TokenData> findPlayerToken(UUID playerUUID) {
        cleanExpiredTokens();
        return tokens.stream()
                .filter(t -> t.getPlayerUUID().equals(playerUUID) && t.isValid())
                .findFirst();
    }
}
