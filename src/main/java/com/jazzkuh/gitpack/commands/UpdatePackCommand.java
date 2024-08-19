package com.jazzkuh.gitpack.commands;

import com.jazzkuh.commandlib.common.annotations.Command;
import com.jazzkuh.commandlib.common.annotations.Description;
import com.jazzkuh.commandlib.common.annotations.Main;
import com.jazzkuh.commandlib.common.annotations.Permission;
import com.jazzkuh.commandlib.velocity.AnnotationCommand;
import com.jazzkuh.gitpack.GitPackPlugin;
import com.jazzkuh.gitpack.utils.ChatUtils;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.player.ResourcePackInfo;
import lombok.SneakyThrows;

@Command("updatepack")
public class UpdatePackCommand extends AnnotationCommand {
	@Main
	@Permission("gitpack.commands.updatepack")
	@Description("Resource pack update command.")
	@SneakyThrows
	public void main(CommandSource commandSource) {
		commandSource.sendMessage(ChatUtils.prefix("Resourcepack", "<gray>Resource pack updated."));
		GitPackPlugin.getInstance().loadResourcePack(GitPackPlugin.getDefaultConfiguration()).whenComplete((aVoid, throwable) -> {
			if (throwable != null) {
				commandSource.sendMessage(ChatUtils.prefix("Resourcepack", "<error>Failed to update resource pack."));
				return;
			}

			for (Player player : GitPackPlugin.getInstance().getServer().getAllPlayers()) {
				ResourcePackInfo.Builder packToApply = GitPackPlugin.getInstance().getServer().createResourcePackBuilder(GitPackPlugin.getInstance().getResourcePackUrl());
				packToApply.setShouldForce(!player.hasPermission("gitpack.resourcepack.skip"));
				packToApply.setPrompt(ChatUtils.format("<warning>This server requires a custom resource pack to play."));
				packToApply.setHash(GitPackPlugin.getInstance().getResourcePackHash());

				player.sendMessage(ChatUtils.prefix("Resourcepack", "<gray>There has been a resourcepack update, reapplying..."));
				player.sendResourcePackOffer(packToApply.build());
			}
		});
	}
}
