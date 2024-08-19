package com.jazzkuh.gitpack.utils;

import lombok.experimental.PackagePrivate;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

@UtilityClass
public final class ChatUtils {
    public static TextColor PRIMARY = TextColor.fromHexString("#e8196a");
    public static TextColor SUCCESS = TextColor.fromHexString("#12ff2a");
    public static TextColor ERROR = TextColor.fromHexString("#FC3838");
    public static TextColor WARNING = TextColor.fromHexString("#FBFB00");

    public static Component prefix(String prefix, String message, TextColor textColor, Object... args) {
        return format(getPrefix(prefix, textColor) + message, textColor, args);
    }

    public static Component prefix(String prefix, String message, Object... args) {
        return format(getPrefix(prefix, PRIMARY) + message, args);
    }

    @PackagePrivate
    private static String getPrefix(String prefix, TextColor textColor) {
        return "<" + tint(textColor.asHexString(), 0.15) + ">•<" + textColor.asHexString() + ">● " + prefix + " <dark_gray>┃ <gray>";
    }

    public static Component format(String message, Object... args) {
        return format(message, PRIMARY, args);
    }

    public static Component format(String message, TextColor textColor, Object... args) {
        MiniMessage extendedInstance = MiniMessage.builder()
                .editTags(tags -> {
                    tags.resolver(TagResolver.resolver("primary", Tag.styling(PRIMARY)));
                    tags.resolver(TagResolver.resolver("success", Tag.styling(SUCCESS)));
                    tags.resolver(TagResolver.resolver("error", Tag.styling(ERROR)));
                    tags.resolver(TagResolver.resolver("warning", Tag.styling(WARNING)));

                    tags.resolver(TagResolver.resolver("color", Tag.styling(textColor)));
                    tags.resolver(TagResolver.resolver("color_alt", Tag.styling(TextColor.fromHexString(tint(textColor.asHexString(), 0.15)))));
                }).build();

        return extendedInstance.deserialize(replaceArguments(message, args)).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    @PackagePrivate
    private static String replaceArguments(String message, Object... replacements) {
        for (int i = 0; i < replacements.length; i++) {
            String placeholder = "%" + (i + 1);
            message = message.replaceAll(placeholder + "(?![0-9])", String.valueOf(replacements[i]));
        }
        return message;
    }

    @PackagePrivate
    private static String tint(String hexColor, double factor) {
        int red = Integer.parseInt(hexColor.substring(1, 3), 16);
        int green = Integer.parseInt(hexColor.substring(3, 5), 16);
        int blue = Integer.parseInt(hexColor.substring(5, 7), 16);

        red = (int) Math.round(Math.min(255, red + (255 - red) * factor));
        green = (int) Math.round(Math.min(255, green + (255 - green) * factor));
        blue = (int) Math.round(Math.min(255, blue + (255 - blue) * factor));

        return String.format("#%02x%02x%02x", red, green, blue);
    }
}