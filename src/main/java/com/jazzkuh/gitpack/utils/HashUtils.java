package com.jazzkuh.gitpack.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;

@UtilityClass
public class HashUtils {
    @SneakyThrows
    public byte[] getHash(String url) {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        InputStream inputStream = new BufferedInputStream(new URL(url).openStream());

        int num = 0;
        byte[] buffer = new byte[8192];
        while (num != -1) {
            num = inputStream.read(buffer);
            if (num > 0) {
                messageDigest.update(buffer, 0, num);
            }
        }

        inputStream.close();
        return messageDigest.digest();
    }
}