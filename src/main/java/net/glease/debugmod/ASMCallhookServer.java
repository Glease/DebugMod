package net.glease.debugmod;

import net.minecraft.crash.CrashReport;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.ConcurrentModificationException;

public class ASMCallhookServer {
    static ThreadInfo[] dump;
    @Callhook
    public static void onCrashReportCreated(Throwable throwable) {
        if (throwable instanceof ConcurrentModificationException) {
            dump = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
        } else {
            // for whatever reason the previous crash report was discarded and didn't crash the whole thing
            // so we should clear it up.
            dump = null;
        }
    }
}
