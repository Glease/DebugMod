package net.glease.debugmod;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;

public class ASMCallhookServer {
    static ThreadInfo[] dump;
    @Callhook
    public static void onCrashReportCreated(Throwable throwable) {
        dump = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
    }
}
