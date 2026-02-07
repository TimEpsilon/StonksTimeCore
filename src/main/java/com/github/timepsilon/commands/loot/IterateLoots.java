package com.github.timepsilon.commands.loot;

import com.github.timepsilon.Core;
import com.github.timepsilon.loot.TimeGearGlobalLootModifier;
import com.github.timepsilon.utils.FileManager;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class IterateLoots {

    public static int iterateLoots(CommandContext<CommandSourceStack> ctx) {
        MinecraftServer server = ctx.getSource().getServer();
        Collection<ResourceLocation> loots = server.reloadableRegistries().getKeys(Registries.LOOT_TABLE);

        LootNode root = new LootNode();

        for (ResourceLocation id : loots) {
            insertPath(root, id.getNamespace() + "/" + id.getPath());
        }

        FileManager.writeFileOnWorld("lootTableTree.json", root.toJson(), server);
        ctx.getSource().sendSystemMessage(Component.translatable("commands.stonkstimecore.generate_loot"));

        return Command.SINGLE_SUCCESS;
    }

    public static int PatternMatch(CommandContext<CommandSourceStack> ctx) {
        MinecraftServer server = ctx.getSource().getServer();
        Collection<ResourceLocation> loots = server.reloadableRegistries().getKeys(Registries.LOOT_TABLE);

        for (ResourceLocation id : loots) {
            if (TimeGearGlobalLootModifier.INSTANCE.canGenerate(id.toString())) {
                Core.LOGGER.info(id.toString());
            }
        }

        ctx.getSource().sendSystemMessage(Component.translatable("commands.stonkstimecore.match_loot"));

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
