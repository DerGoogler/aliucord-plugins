package com.dergoogler.plugins;

import android.content.Context;

import com.aliucord.Constants;
import com.aliucord.Logger;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.CommandsAPI;
import com.aliucord.entities.Plugin;

import com.dergoogler.plugins.utils.SuperUser;
import com.discord.api.commands.ApplicationCommandType;
import com.discord.api.message.embed.MessageEmbed;
import com.discord.models.commands.ApplicationCommandOption;

import java.util.Arrays;
import java.util.List;


@SuppressWarnings("unused")
@AliucordPlugin
public class SuTerminal extends Plugin {
    private final Logger log;
    private final SuperUser su;
    private final String DefaultExecute;
    private String send;

    public SuTerminal() {
        this.log = new Logger("SuTerminal");
        this.su = new SuperUser();
        this.DefaultExecute = "echo \"Hello world!\"";
        this.send = "false";
    }

    @Override
    public void start(Context context) {
        String keyDefaultEnv = "def env";
        String keyMkshrcEnv = "mkshrc env";
        String keySend = "send";
        String customMkshrcEnv = "if [ -f /system/etc/mkshrc ]; then . /system/etc/mkshrc; fi; ";
        List<MessageEmbed> embeds = null;
        List<ApplicationCommandOption> options = Arrays.asList(
                Utils.createCommandOption(ApplicationCommandType.STRING, keyDefaultEnv, "Creates an default environment"),
                Utils.createCommandOption(ApplicationCommandType.STRING, keyMkshrcEnv, "Creates an custom mkshrc environment, e.g. View Googlers-Magisk-Repo/mkshrc to get Termux environment within Discord"),
                Utils.createCommandOption(ApplicationCommandType.BOOLEAN, keySend, "Sends the output to given channel")
        );

        this.commands.registerCommand(
                "su", "Run superuser command (requires root)",
                options,
                commandCxt -> {
                    boolean sendKey = commandCxt.getBoolOrDefault(keySend, false);
                    if (commandCxt.containsArg(keyDefaultEnv)) {
                        String defaultEnv = commandCxt.getStringOrDefault(keyDefaultEnv, this.DefaultExecute);
                        return new CommandsAPI.CommandResult(this.runSu(defaultEnv), embeds, sendKey);
                    } else if (commandCxt.containsArg(keyMkshrcEnv)) {
                        String mkshrcEnv = commandCxt.getStringOrDefault(keyMkshrcEnv, this.DefaultExecute);
                        return new CommandsAPI.CommandResult(this.runSu(customMkshrcEnv + mkshrcEnv), embeds, sendKey);
                    } else {
                        log.warn("No command was executed");
                    }
                    return new CommandsAPI.CommandResult(commandCxt.getRawContent(), embeds, sendKey);
                });

    }

    private String runSu(String command) {
        log.info("Executing " + "\"" + command + "\"");
        return su.sudoForResult(command);
    }

    @Override
    public void stop(Context context) throws Throwable {
        this.commands.unregisterAll();
    }
}