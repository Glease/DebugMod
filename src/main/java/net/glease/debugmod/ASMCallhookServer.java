package net.glease.debugmod;

import net.minecraft.crash.CrashReport;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.stream.Collectors;

public class ASMCallhookServer {
    @Callhook
    public static void enhanceCrashReport(CrashReport cr) {
        if (cr.getCrashCause() instanceof ConcurrentModificationException) {
            ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
            cr.getCategory().addCrashSection("Thread dump", Arrays.stream(threads).map(ThreadInfo::toString).collect(Collectors.joining("","\n","")));
        }
    }
}
