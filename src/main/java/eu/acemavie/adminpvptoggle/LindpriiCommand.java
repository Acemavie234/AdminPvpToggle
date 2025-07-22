package eu.acemavie.adminpvptoggle;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.minecraft.command.argument.EntityArgumentType.getPlayers;
import static net.minecraft.command.argument.EntityArgumentType.players;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class LindpriiCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(createLindpriiCommand("lindprii"));
        dispatcher.register(createLindpriiCommand("outlaw"));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> createLindpriiCommand(String name) {
        return literal(name)
                .requires(source -> Permissions.check(source, "adminpvptoggle.outlaw", 4))
                .then(literal("add")
                        .then(argument("targets", players())
                                .executes(ctx -> {
                                    Collection<ServerPlayerEntity> targets = getPlayers(ctx, "targets");
                                    for (ServerPlayerEntity target : targets) {
                                        Adminpvptoggle.lindpriiStateManager.add(target.getName().getString());
                                        ctx.getSource().getServer().getPlayerManager().broadcast(Text.literal("§c§l" + target.getName().getString() + "§r is now an outlaw! Check your locator bar. Happy hunting!"), false);

                                        EntityAttributeInstance transmit = target.getAttributeInstance(EntityAttributes.WAYPOINT_TRANSMIT_RANGE);
                                        if (transmit != null) {
                                            transmit.setBaseValue(60000000);
                                        }

                                        EntityAttributeInstance recieve = target.getAttributeInstance(EntityAttributes.WAYPOINT_RECEIVE_RANGE);
                                        if (recieve != null) {
                                            recieve.setBaseValue(0);
                                        }
                                    }

                                    for (ServerPlayerEntity player : ctx.getSource().getServer().getPlayerManager().getPlayerList()) {
                                        if (!Adminpvptoggle.lindpriiStateManager.getSet().contains(player.getName().getString())) {
                                            EntityAttributeInstance transmit = player.getAttributeInstance(EntityAttributes.WAYPOINT_TRANSMIT_RANGE);
                                            if (transmit != null) {
                                                transmit.setBaseValue(0);
                                            }
                                        }
                                    }

                                    Adminpvptoggle.lindpriiStateManager.save();
                                    if (!Adminpvptoggle.lindpriiStateManager.getSet().isEmpty()) {
                                        ctx.getSource().getServer().getOverworld().getGameRules().get(GameRules.LOCATOR_BAR).set(true, ctx.getSource().getServer());
                                    } else {
                                        ctx.getSource().getServer().getOverworld().getGameRules().get(GameRules.LOCATOR_BAR).set(false, ctx.getSource().getServer());
                                    }

                                    return 1;
                                })))
                .then(literal("remove")
                        .then(argument("targets", players())
                                .executes(ctx -> {
                                    Collection<ServerPlayerEntity> targets = getPlayers(ctx, "targets");
                                    for (ServerPlayerEntity target : targets) {

                                        Adminpvptoggle.lindpriiStateManager.remove(target.getName().getString());
                                        ctx.getSource().getServer().getPlayerManager().broadcast(Text.literal("§2§l" + target.getName().getString() + "§r is no longer an outlaw!"), false);

                                        EntityAttributeInstance transmit = target.getAttributeInstance(EntityAttributes.WAYPOINT_TRANSMIT_RANGE);
                                        if (transmit != null) {
                                            transmit.clearModifiers();
                                            transmit.setBaseValue(0);
                                        }

                                        EntityAttributeInstance recieve = target.getAttributeInstance(EntityAttributes.WAYPOINT_RECEIVE_RANGE);
                                        if (recieve != null) {
                                            recieve.clearModifiers();
                                            recieve.setBaseValue(500);
                                        }
                                    }

                                    for (ServerPlayerEntity player : ctx.getSource().getServer().getPlayerManager().getPlayerList()) {
                                        if (!Adminpvptoggle.lindpriiStateManager.getSet().contains(player.getName().getString())) {
                                            EntityAttributeInstance transmit = player.getAttributeInstance(EntityAttributes.WAYPOINT_TRANSMIT_RANGE);
                                            if (transmit != null) {
                                                transmit.setBaseValue(0);
                                            }
                                        }
                                    }

                                    Adminpvptoggle.lindpriiStateManager.save();

                                    if (Adminpvptoggle.lindpriiStateManager.getSet().isEmpty()) {
                                        ctx.getSource().getServer().getOverworld().getGameRules().get(GameRules.LOCATOR_BAR).set(false, ctx.getSource().getServer());

                                        for (ServerPlayerEntity player : ctx.getSource().getServer().getPlayerManager().getPlayerList()) {
                                            EntityAttributeInstance transmit = player.getAttributeInstance(EntityAttributes.WAYPOINT_TRANSMIT_RANGE);
                                            if (transmit != null) {
                                                transmit.setBaseValue(500);
                                            }
                                        }
                                    }

                                    return 1;
                                })))
                .then(literal("list")
                        .executes(ctx -> {
                            Adminpvptoggle.lindpriiStateManager.getSet();

                            Text text = Text.literal("Current outlaws: \n");
                            for (String mcname : Adminpvptoggle.lindpriiStateManager.getSet()) {
                                ((MutableText) text).append("\n§c" + mcname);
                            }

                            ctx.getSource().sendFeedback(() -> text,true);
                            return 1;
                        }));
    }
}

