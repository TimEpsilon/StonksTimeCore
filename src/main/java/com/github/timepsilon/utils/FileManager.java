package com.github.timepsilon.utils;

import com.github.timepsilon.Core;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

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
