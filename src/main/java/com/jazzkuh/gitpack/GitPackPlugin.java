package com.jazzkuh.gitpack;

import com.google.inject.Inject;
import com.jazzkuh.commandlib.velocity.VelocityCommandLoader;
import com.jazzkuh.gitpack.commands.UpdatePackCommand;
import com.jazzkuh.gitpack.configuration.DefaultConfiguration;
import com.jazzkuh.gitpack.listeners.PlayerConnectionListener;
import com.jazzkuh.gitpack.utils.ChatUtils;
import com.jazzkuh.gitpack.utils.HashUtils;
import com.jazzkuh.gitpack.utils.RequestUtils;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Plugin(
        id = "gitpack",
        name = "gitpack",
        version = "1.0-SNAPSHOT"
)
@Getter
public class GitPackPlugin {

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private static GitPackPlugin instance;

    private final Logger logger;
    private final ProxyServer server;
    private final Path folder;

    @Getter
    private static DefaultConfiguration defaultConfiguration;

    private String resourcePackUrl = null;
    private byte[] resourcePackHash = null;

    @Inject
    private GitPackPlugin(ProxyServer server, Logger logger, @DataDirectory final Path folder) {
        this.server = server;
        this.logger = logger;
        this.folder = folder;
    }


    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        setInstance(this);
        VelocityCommandLoader.setFormattingProvider((commandException, message) -> ChatUtils.prefix("Resourcepack", message));

        defaultConfiguration = new DefaultConfiguration(this.getFolder());
        defaultConfiguration.saveConfiguration();

        this.loadResourcePack(defaultConfiguration);
        new UpdatePackCommand().register(this.getServer().getCommandManager());
        this.getServer().getEventManager().register(this, new PlayerConnectionListener());

        this.getServer().getScheduler().buildTask(this, () -> {
            RequestUtils.executorService.submit(() -> this.loadResourcePack(defaultConfiguration));
        }).repeat(5, TimeUnit.MINUTES).schedule();
    }

    @SneakyThrows
    public CompletableFuture<Void> loadResourcePack(DefaultConfiguration configuration) {
        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

        URL url = new URL("https://api.github.com/repos/" + configuration.getGithubOrganization() + "/" + configuration.getGithubRepository() + "/releases/latest");
        Map<String, String> properties = Map.of(
                "Content-Type", "application/json",
                "Accept", "application/vnd.github.v3+json"
        );

        RequestUtils.get(url.toString(), properties).whenComplete((jsonObject, throwable) -> {
            if (throwable != null) {
                throwable.printStackTrace();
                this.getLogger().severe("Failed to load resource pack from github repository.");
                return;
            }

            this.getLogger().info("Loaded resource pack from github repository.");
            this.resourcePackUrl = jsonObject.getAsJsonArray("assets")
                    .get(0).getAsJsonObject()
                    .get("browser_download_url").getAsString();
            this.resourcePackHash = HashUtils.getHash(this.resourcePackUrl);
           completableFuture.complete(null);
        });

        return completableFuture;
    }
}
