package eu.acemavie.adminpvptoggle;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static net.minecraft.command.argument.EntityArgumentType.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PvPCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("pvp")
                .requires(source -> Permissions.check(source, "adminpvptoggle.pvp", 4))
                .then(literal("toggle").requires(source -> Permissions.check(source, "adminpvptoggle.pvp.toggle", 4))
                        .then(argument("targets", players())
                                .executes(ctx -> {
                                    Collection<ServerPlayerEntity> targets = getPlayers(ctx, "targets");
                                    for (ServerPlayerEntity target : targets) {
                                        boolean disabled = Adminpvptoggle.pvpStateManager.togglePvP(target.getName().getString());

                                        target.sendMessage(
                                                disabled
                                                        ? Text.literal("§cYour PvP has been disabled by an operator.")
                                                        : Text.literal("§aYour PvP has been enabled by an operator."),
                                                false
                                        );

                                        ctx.getSource().sendFeedback(
                                                () -> Text.literal((disabled ? "§aDisabled" : "§aEnabled") + " PvP for " + target.getName().getString()),
                                                true
                                        );
                                    }
                                    Adminpvptoggle.pvpStateManager.save();
                                    return 1;
                                })
                                .then(argument("silent", bool())
                                        .executes(ctx -> {
                                            Collection<ServerPlayerEntity> targets = getPlayers(ctx, "targets");
                                            boolean silent = getBool(ctx, "silent");

                                            for (ServerPlayerEntity target : targets) {
                                                boolean disabled = Adminpvptoggle.pvpStateManager.togglePvP(target.getName().getString());

                                                if (!silent) {
                                                    target.sendMessage(
                                                            disabled
                                                                    ? Text.literal("§cYour PvP has been disabled by an operator.")
                                                                    : Text.literal("§aYour PvP has been enabled by an operator."),
                                                            false
                                                    );

                                                    ctx.getSource().sendFeedback(
                                                            () -> Text.literal((disabled ? "§aDisabled" : "§aEnabled") + " PvP for " + target.getName().getString()),
                                                            true
                                                    );
                                                }
                                            }
                                            Adminpvptoggle.pvpStateManager.save();
                                            return 1;
                                        })
                                )
                        )
                )
                .then(literal("mace").requires(source -> Permissions.check(source, "adminpvptoggle.pvp.mace", 4))
                        .executes(ctx -> {
                            boolean disabled = Adminpvptoggle.pvpStateManager.toggleMacePvP();
                            ctx.getSource().sendFeedback(
                                    () -> Text.literal(disabled ? "§cMace PvP has been disabled." : "§aMace PvP has been enabled."),
                                    true
                            );
                            Adminpvptoggle.pvpStateManager.save();
                            return 1;
                        })
                        .then(argument("disable", bool())
                                .executes(ctx -> {
                                    boolean disable = getBool(ctx, "disable");
                                    Adminpvptoggle.pvpStateManager.setMaceDisabled(disable);
                                    ctx.getSource().sendFeedback(
                                            () -> Text.literal(disable ? "§cMace PvP has been disabled." : "§aMace PvP has been enabled."),
                                            true
                                    );
                                    Adminpvptoggle.pvpStateManager.save();
                                    return 1;
                                })
                        )
                )
        );
    }
}
