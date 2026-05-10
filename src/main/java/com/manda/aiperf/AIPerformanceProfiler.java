package com.manda.aiperf;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class AIPerformanceProfiler implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register(AIPerfCommands::register);
        System.out.println("[AI Performance Profiler] Mod initialisé avec succès !");
    }
}
