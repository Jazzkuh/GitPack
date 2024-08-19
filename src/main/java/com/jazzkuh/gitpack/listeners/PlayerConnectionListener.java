package com.jazzkuh.gitpack.listeners;

import com.jazzkuh.gitpack.GitPackPlugin;
import com.jazzkuh.gitpack.utils.ChatUtils;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.player.ResourcePackInfo;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("deprecation")
public class PlayerConnectionListener {
    @Subscribe
    public void execute(ServerConnectedEvent event) {
        Player player = event.getPlayer();

        GitPackPlugin.getInstance().getServer().getScheduler().buildTask(GitPackPlugin.getInstance(), () -> {
            String packUrl = GitPackPlugin.getInstance().getResourcePackUrl();
            if (packUrl == null) return;
            if (isActive(player, packUrl)) return;

            ResourcePackInfo packInfo = this.getPackInfo(player, packUrl);
            player.sendResourcePackOffer(packInfo);
        }).delay(player.getAppliedResourcePack() == null ? 250L : 0L, TimeUnit.MILLISECONDS).schedule();
    }

    public boolean isActive(Player player, String packUrl) {
        ResourcePackInfo packInfo = this.getPackInfo(player, packUrl);
        return packInfo != null && player.getAppliedResourcePack() != null && player.getAppliedResourcePack().getUrl().equals(packInfo.getUrl());
    }

    public ResourcePackInfo getPackInfo(Player player, String packUrl) {
        ResourcePackInfo.Builder packToApply = GitPackPlugin.getInstance().getServer().createResourcePackBuilder(packUrl);
        packToApply.setShouldForce(!player.hasPermission("gitpack.resourcepack.skip"));
        packToApply.setPrompt(ChatUtils.format("<warning>This server requires a custom resource pack to play."));
        packToApply.setHash(GitPackPlugin.getInstance().getResourcePackHash());

        return packToApply.build();
    }
}
