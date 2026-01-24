package com.github.timepsilon.commands.loot;

import com.github.timepsilon.utils.FileManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class IterateLoots {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static int iterateLoots(CommandContext<CommandSourceStack> ctx) {
        MinecraftServer server = ctx.getSource().getServer();
        Collection<ResourceLocation> loots = server.reloadableRegistries().getKeys(Registries.LOOT_TABLE);

        LootNode root = new LootNode();

        for (ResourceLocation id : loots) {
            insertPath(root, id.getNamespace() + "/" + id.getPath());
        }

        FileManager.writeFileOnWorld("lootTableTree.json", root.toJson(), server);

        return Command.SINGLE_SUCCESS;
    }

    private static void insertPath(LootNode root, String path) {
        String[] parts = path.split("/");
        LootNode current = root;
        for (String part : parts) {
            current = current.children.computeIfAbsent(part, k -> new LootNode());
        }
    }

    private static final class LootNode {
        Map<String, LootNode> children = new TreeMap<>();

        public JsonObject toJson() {
            JsonObject root = new JsonObject();
            for (Map.Entry<String, LootNode> entry : this.children.entrySet()) {
                root.add(entry.getKey(), entry.getValue().toJson());
            }
            return root;
        }
    }
}
