package com.jazzkuh.gitpack.configuration;

import com.jazzkuh.gitpack.utils.ConfigurateConfig;
import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.file.Path;

@Getter
public class DefaultConfiguration extends ConfigurateConfig {
    private final String version;

    private final String githubOrganization;
    private final String githubRepository;

    @SneakyThrows
    public DefaultConfiguration(Path folder) {
        super(folder, "config.yml");

        this.version = rootNode.node("_version").getString("1");

        this.githubOrganization = rootNode.node("github", "organization").getString("organization");
        this.githubRepository = rootNode.node("github", "repository").getString("resourcepack");
    }
}
