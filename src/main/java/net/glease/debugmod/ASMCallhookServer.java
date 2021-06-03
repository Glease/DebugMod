package net.glease.debugmod;

import net.minecraft.crash.CrashReport;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.ConcurrentModificationException;

public class ASMCallhookServer {
    static ThreadInfo[] dump;
    @Callhook
    public static void enhanceCrashReport(CrashReport cr) {
        if (cr.getCrashCause() instanceof ConcurrentModificationException) {
            dump = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
        }
    }
}
