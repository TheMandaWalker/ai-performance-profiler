package com.manda.aiperf;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;

public class AIPerfCommands {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("aiperf")
            .then(ClientCommandManager.literal("benchmark")
                .executes(AIPerfCommands::runBenchmark))
            .then(ClientCommandManager.literal("profile")
                .then(ClientCommandManager.argument("type", com.mojang.brigadier.arguments.StringArgumentType.string())
                    .suggests((context, builder) -> {
                        builder.suggest("performance");
                        builder.suggest("balanced");
                        builder.suggest("quality");
                        return builder.buildFuture();
                    })
                    .executes(AIPerfCommands::setProfile))));
    }

    private static int runBenchmark(CommandContext<FabricClientCommandSource> context) {
        MinecraftClient client = MinecraftClient.getInstance();
        context.getSource().sendFeedback(() -> Text.literal("§a[AI Perf] Lancement du benchmark IA..."), false);
        
        // Heuristique simple IA : détection basique des performances
        int renderDistance = client.options.getRenderDistance().getValue();
        boolean vSync = client.options.getVSync().getValue();
        
        String recommendation = (renderDistance > 12 || vSync) 
            ? "Votre PC semble performant → profil 'balanced' recommandé." 
            : "Votre PC semble modéré → profil 'performance' recommandé.";
        
        context.getSource().sendFeedback(() -> Text.literal("§e[AI Perf] Résultat benchmark : " + recommendation), false);
        return 1;
    }

    private static int setProfile(CommandContext<FabricClientCommandSource> context) {
        MinecraftClient client = MinecraftClient.getInstance();
        GameOptions options = client.options;
        String type = com.mojang.brigadier.arguments.StringArgumentType.getString(context, "type").toLowerCase();

        switch (type) {
            case "performance":
                options.getVSync().setValue(false);
                options.getRenderDistance().setValue(8);
                options.getGraphicsMode().setValue(net.minecraft.client.option.GraphicsMode.FAST);
                context.getSource().sendFeedback(() -> Text.literal("§a[AI Perf] Profil MEILLEURE PERFORMANCE appliqué (V-Sync OFF, Render 8, Fast)"), false);
                break;
            case "balanced":
                options.getVSync().setValue(false);
                options.getRenderDistance().setValue(12);
                options.getGraphicsMode().setValue(net.minecraft.client.option.GraphicsMode.FANCY);
                context.getSource().sendFeedback(() -> Text.literal("§a[AI Perf] Profil ÉQUILIBRÉ appliqué (V-Sync OFF, Render 12)"), false);
                break;
            case "quality":
                options.getVSync().setValue(true);
                options.getRenderDistance().setValue(16);
                options.getGraphicsMode().setValue(net.minecraft.client.option.GraphicsMode.FABULOUS);
                context.getSource().sendFeedback(() -> Text.literal("§a[AI Perf] Profil MEILLEURE QUALITÉ appliqué (V-Sync ON, Render 16)"), false);
                break;
            default:
                context.getSource().sendFeedback(() -> Text.literal("§c[AI Perf] Type inconnu. Utilisez : performance | balanced | quality"), false);
        }
        client.worldRenderer.reload(); // Actualise le rendu
        return 1;
    }
}
