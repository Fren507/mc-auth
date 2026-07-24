package com.fren507.mcauth.command;

import com.fren507.mcauth.MCAuthMod;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
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

                            String formatted = "§b" +
                                    token.substring(0, 4) + "§7-§b" +
                                    token.substring(4, 8) + "§7-§b" +
                                    token.substring(8, 12);

                            player.sendSystemMessage(
                                    Component.literal("§aDein Verifizierungstoken: ")
                                            .append(
                                                    Component.literal("\n" + formatted)
                                                            .withStyle(style -> style.withClickEvent(
                                                                    new ClickEvent.CopyToClipboard(token)
                                                            ))
                                                            .withStyle(style -> style.withHoverEvent(
                                                                    new HoverEvent.ShowText(
                                                                            Component.literal("§aKlicke zum Kopieren!")
                                                                    )
                                                            ))
                                            )
                                            .append(
                                                    Component.literal(" §7(Zum kopieren anklicken)\n§7Gültig für 2 Stunden.")
                                            )
                            );

                            return 1;
                        })
        );
    }
}