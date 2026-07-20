package com.fren507.mcauth.command;

import com.fren507.mcauth.MCAuthMod;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class VerifyCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("verify")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();

                            if (player == null) {
                                return 0;
                            }

                            String token = MCAuthMod.getTokenManager()
                                    .generateToken(
                                            player.getUUID(),
                                            player.getName().getString()
                                    );

                            player.sendSystemMessage(
                                    Component.literal(
                                            "§aDein Verifizierungstoken:\n§b"
                                                    + token
                                                    + "\n§7Gültig für 2 Stunden."
                                    )
                            );

                            return 1;
                        })
        );
    }
}