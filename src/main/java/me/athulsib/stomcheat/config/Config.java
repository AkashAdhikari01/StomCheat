package me.athulsib.stomcheat.config;

public record ACConfig(
        boolean loadDefaultChecks,
        boolean loadDefaultProcessors,
        int threadCount,
        String alertMessage,
        String experimental,
        String hover,
        String broadcast,
        String kickMessage
) {}