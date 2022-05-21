package com.dergoogler.plugins;

import android.content.Context;

import com.aliucord.Constants;
import com.aliucord.Logger;
import com.aliucord.Utils;
import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.api.CommandsAPI;
import com.aliucord.entities.Plugin;
import com.aliucord.utils.RxUtils;

import com.dergoogler.plugins.utils.SuperUser;
import com.discord.api.commands.ApplicationCommandType;
import com.discord.models.commands.ApplicationCommandOption;
import com.discord.utilities.rest.RestAPI;


@SuppressWarnings("unused")
@AliucordPlugin
public class SuTerminal extends Plugin {
    private Logger log;
    private SuperUser su;

    public SuTerminal() {
        this.log = new Logger("SuTerminal");
        this.su = new SuperUser();
    }

	@Override
    public void start(Context context) {
		ApplicationCommandOption options = Utils.createCommandOption(ApplicationCommandType.STRING, "command", "Note: requires root");
        this.commands.registerCommand(
                "su", "Run superuser command",
				options,
                commandContext -> {
                    if (Constants.ALIUCORD_GUILD_ID == commandContext.getChannel().getGuildId()) {
                        RxUtils.subscribe(RestAPI.Companion.getApi().leaveGuild(Constants.ALIUCORD_GUILD_ID), unused -> null);
                        return null;
                    }
                    return new CommandsAPI.CommandResult(this.runSu(commandContext.getStringOrDefault("command", "echo \"Hello world!\"")), null, false);
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