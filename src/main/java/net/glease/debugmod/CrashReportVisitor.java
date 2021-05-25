package net.glease.debugmod;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static net.glease.debugmod.ASMConstants.ASMCALLHOOKSERVER_INTERNAL_NAME;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class CrashReportVisitor extends ClassVisitor {
    private static class EnhanceCrashReportVisitor extends MethodVisitor {
        public EnhanceCrashReportVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitCode() {
            super.visitCode();
            super.visitVarInsn(ALOAD, 0);
            super.visitMethodInsn(INVOKESTATIC, ASMCALLHOOKSERVER_INTERNAL_NAME, "enhanceCrashReport", "(Lnet/minecraft/crash/CrashReport;)V", false);
        }
    }

    public CrashReportVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("func_71504_g") || name.equals("populateEnvironment"))
            return new EnhanceCrashReportVisitor(api, mv);
        return mv;
    }
}
