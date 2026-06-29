package com.github.timepsilon.utils;

import com.github.timepsilon.Core;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import javax.annotation.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void writeFileOnWorld(String name, Object content, MinecraftServer server) {
        // Config directory
        Path dir = makeServerSideDirectory(server);

        // Serialization
        Path file = dir.resolve(name);
        String json = GSON.toJson(content);

        // Write to file
        try {
            Files.writeString(
                    file,
                    json,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to write " + name, e);
        }
    }

    @Nullable
    public static <T> T readFileOnWorld(String name, Class<T> type, MinecraftServer server) {
        Path file = makeServerSideDirectory(server).resolve(name);
        if (!Files.exists(file)) {
            return null;
        }
        try {
            return GSON.fromJson(Files.readString(file), type);
        } catch (IOException e) {
            Core.LOGGER.error("Failed to read {}", name, e);
            return null;
        }
    }

    public static Path makeServerSideDirectory(MinecraftServer server) {
        Path dir = server.getWorldPath(LevelResource.ROOT).resolve(Core.MODID);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create config folder", e);
        }
        return dir;
    }
}
