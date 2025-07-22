package eu.acemavie.adminpvptoggle;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
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
import java.util.Set;
import java.util.UUID;

import static net.minecraft.item.Items.MACE;

public class Adminpvptoggle implements ModInitializer {
    public static PvPStateManager pvpStateManager;
    public static PvPStateManager lindpriiStateManager;


    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                {
                    PvPCommand.register(dispatcher);
                    LindpriiCommand.register(dispatcher);
                });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!world.isClient && entity instanceof PlayerEntity victim) {

                if (Adminpvptoggle.lindpriiStateManager.isInList(victim.getName().getString())) return ActionResult.PASS;

                if (pvpStateManager != null && pvpStateManager.isMaceDisabled()) {
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


                if (pvpStateManager != null && pvpStateManager.isInList(player.getName().getString())) {
                    player.sendMessage(Text.literal("§cYour PvP is currently disabled!"), true);
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });


        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            Path pvpSavePath = server.getSavePath(WorldSavePath.ROOT).resolve("pvp_states.json");
            pvpStateManager = new PvPStateManager(pvpSavePath);
            pvpStateManager.load();

            Path lindpriiSavePath = server.getSavePath(WorldSavePath.ROOT).resolve("lindprii_states.json");
            lindpriiStateManager = new PvPStateManager(lindpriiSavePath);
            lindpriiStateManager.load();
            Set<String> temp = lindpriiStateManager.getSet();
            lindpriiStateManager.clear();

            for (String player : temp) {
                lindpriiStateManager.add(player);
            }
            lindpriiStateManager.save();

            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (!Adminpvptoggle.lindpriiStateManager.getSet().contains(player.getName().getString())) {
                    EntityAttributeInstance transmit = player.getAttributeInstance(EntityAttributes.WAYPOINT_TRANSMIT_RANGE);
                    if (transmit != null) {
                        transmit.setBaseValue(0);
                    }
                }
            }

        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            if (pvpStateManager != null) pvpStateManager.save();
            if (lindpriiStateManager != null) lindpriiStateManager.save();
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
                    other -> !other.getUuid().equals(player.getUuid()) && !other.isSpectator() && !Adminpvptoggle.lindpriiStateManager.isInList(other.getName().getString())
            ).stream().findAny().isPresent();

            if (someoneOnBlock) {
                if (Adminpvptoggle.pvpStateManager.isInList(player.getName().getString())) {
                    player.sendMessage(Text.literal("§cYou can't break this block while someone else is standing on it, you sneaky bastard!"), true);
                    return false; // Cancel block breaking
                }
            }
            return true; // Allow break
        });

    }
}
