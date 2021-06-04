package net.glease.debugmod;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.Arrays;
import java.util.ConcurrentModificationException;

public class ASMCallhookServer {
    static ThreadInfo[] dump;
    @Callhook
    public static void onCrashReportCreated(Throwable throwable) {
        if (isCME(throwable)) {
            dump = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
        } else {
            // for whatever reason the previous crash report was discarded and didn't crash the whole thing
            // so we should clear it up.
            dump = null;
        }
    }

    private static boolean isCME(Throwable t) {
        Throwable ex = t;
        while (ex != null && !(ex instanceof ConcurrentModificationException) && Arrays.stream(t.getSuppressed()).noneMatch(ASMCallhookServer::isCME))
            ex = ex.getCause();
        return ex != null;
    }
}
