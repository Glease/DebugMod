package net.glease.debugmod;

import com.google.common.collect.ImmutableMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.util.ReportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;

import static net.glease.debugmod.LoadingPlugin.debugOutputDir;
import static org.objectweb.asm.Opcodes.ASM5;

public class Transformer implements IClassTransformer {
    static final Logger log = LogManager.getLogger("DebugModTransformer");
    private static final boolean DEBUG = Boolean.getBoolean("glease.debugasm");
    private final Map<String, TransformerFactory> transformers = ImmutableMap.<String, TransformerFactory>builder()
            .put("net.minecraft.crash.CrashReport", new TransformerFactory(CrashReportVisitor::new))
            .build();

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        TransformerFactory factory = transformers.get(transformedName);
        if (factory == null || factory.isInactive()) {
            return basicClass;
        }
        log.info("Transforming class {}", name);
        ClassReader cr = new ClassReader(basicClass);
        ClassWriter cw = new ClassWriter(factory.isExpandFrames() ? ClassWriter.COMPUTE_FRAMES : 0);
        // we are very probably the last one to run.
        try {
            cr.accept(factory.apply(ASM5, cw), (factory.isExpandFrames() ? ClassReader.SKIP_FRAMES : 0));
        } catch (Exception e) {
            Util.catching(e);
        }
        byte[] transformedBytes = cw.toByteArray();
        if (transformedBytes == null || transformedBytes.length == 0) {
            if (DEBUG) {
                Util.catching(new RuntimeException("Null or empty byte array created. This will not work well!"));
            } else {
                log.fatal("Null or empty byte array created. Transforming will rollback as a last effort attempt to make things work! However features will not function!");
                return basicClass;
            }
        } else {
            if (DEBUG) {
                try (PrintWriter pw = new PrintWriter(new File(debugOutputDir, name + ".txt"), "UTF-8")) {
                    new ClassReader(transformedBytes).accept(new TraceClassVisitor(pw), 0);
                } catch (Exception e) {
                    log.warn("Unable to dump debug output!", e);
                }
            }
        }
        return transformedBytes;
    }

    private static class Util {
        static void catching(Exception e) {
            log.fatal("Something went very wrong with class transforming! Aborting!!!", e);
            throw new ReportedException(CrashReport.makeCrashReport(e, "Transforming class"));
        }
    }
}
