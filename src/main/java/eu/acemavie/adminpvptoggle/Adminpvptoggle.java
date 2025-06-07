package eu.acemavie.adminpvptoggle;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.WorldSavePath;

import java.nio.file.Path;

public class Adminpvptoggle implements ModInitializer {
    public static PvPStateManager stateManager;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                PvPCommand.register(dispatcher)
        );

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient && entity instanceof PlayerEntity) {
                if (stateManager != null && stateManager.isPvPDisabled(player.getName().getString())) {
                    player.sendMessage(Text.literal("§cYour PvP is currently disabled!"), false);
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            Path savePath = server.getSavePath(WorldSavePath.ROOT).resolve("pvp_states.json");
            stateManager = new PvPStateManager(savePath);
            stateManager.load();
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            if (stateManager != null) stateManager.save();
        });
    }
}
