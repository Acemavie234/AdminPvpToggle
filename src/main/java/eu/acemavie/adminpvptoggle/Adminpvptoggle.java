package eu.acemavie.adminpvptoggle;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.Box;

import java.nio.file.Path;

import static net.minecraft.item.Items.MACE;

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
                    player.sendMessage(Text.literal("§cYour PvP is currently disabled!"), true);
                    return ActionResult.FAIL;
                }

                if (stateManager != null && stateManager.isMaceDisabled()) {
                    ItemStack item = player.getStackInHand(hand);
                    if (item.isOf(MACE)) {
                        player.sendMessage(Text.literal("§cMace PvP is currently disabled on this server."), true);

                        ((ServerPlayerEntity) player).networkHandler.sendPacket(
                                new ParticleS2CPacket(
                                        ParticleTypes.HEART,
                                        true,
                                        true,
                                        entity.getX(), entity.getBodyY(0), entity.getZ(),
                                        0.5f, 0.2f, 0.5f,
                                        0.5f,
                                        10
                                )
                        );

                        ServerPlayerEntity player2 = (ServerPlayerEntity) entity;

                        player2.playSound(SoundEvents.ENTITY_CAT_PURREOW, 1 , 1);
                        return ActionResult.FAIL;
                    }
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



        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            Box box = new Box(
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    pos.getX() + 1,
                    pos.getY() + 1.9,
                    pos.getZ() + 1
            );

            boolean someoneOnBlock = world.getEntitiesByClass(PlayerEntity.class, box,
                    other -> !other.getUuid().equals(player.getUuid()) && !other.isSpectator()
            ).stream().findAny().isPresent();

            if (someoneOnBlock) {
                if (Adminpvptoggle.stateManager.isPvPDisabled(player.getName().getString())) {
                    player.sendMessage(Text.literal("§cYou can't break this block while someone else is standing on it, you sneaky bastard!"), true);
                    return false; // Cancel block breaking
                }
            }
            return true; // Allow break
        });

    }
}
