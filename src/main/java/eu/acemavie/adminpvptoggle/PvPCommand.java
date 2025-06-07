package eu.acemavie.adminpvptoggle;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.function.Supplier;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.command.argument.EntityArgumentType.getPlayer;
import static net.minecraft.command.argument.EntityArgumentType.player;

public class PvPCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("pvp")
                .requires(source -> source.hasPermissionLevel(4))
                .then(CommandManager.literal("toggle")
                        .then(argument("target", player())
                                .executes(ctx -> {
                                    ServerPlayerEntity target = getPlayer(ctx, "target");
                                    boolean disabled = Adminpvptoggle.stateManager.togglePvP(target.getName().getString());

                                    if (disabled) {
                                        target.sendMessage(Text.literal("§cYour PvP has been disabled by an operator."), false);
                                        ctx.getSource().sendFeedback((Supplier<Text>) Text.literal("§aDisabled PvP for " + target.getName().getString()), true);
                                    } else {
                                        target.sendMessage(Text.literal("§aYour PvP has been enabled by an operator."), false);
                                        ctx.getSource().sendFeedback((Supplier<Text>) Text.literal("§aEnabled PvP for " + target.getName().getString()), true);
                                    }
                                    return 1;
                                })
                        )
                )
        );
    }
}
