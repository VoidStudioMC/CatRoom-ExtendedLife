package catroom.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * It can be disabled if you know that your mods and plugins don't use old Guava Futures API,
 * but I think It should be enabled always.
 * Some mods and plugins use old Guava Futures API. For example, WorldEdit and FAWE.
 * This class fixes Guava Futures API compatibility issues by transforming old API calls to new ones:
 * - Futures.addCallback(future, callback) -> Futures.addCallback(future, callback, executor)
 * - Futures.transform(future, function) -> Futures.transform(future, function, executor)
 */
public class GuavaFuturesFix implements IClassTransformer {
    public static byte[] transform(byte[] bytecode) {
        try {
            ClassReader classReader = new ClassReader(bytecode);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            ClassVisitor classVisitor = new GuavaFuturesClassVisitor(Opcodes.ASM9, classWriter);
            classReader.accept(classVisitor, 0);
            return classWriter.toByteArray();
        } catch (Exception e) {
            return bytecode;
        }
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) {
            return null;
        }

        if (transformedName.startsWith("java.") ||
            transformedName.startsWith("javax.") ||
            transformedName.startsWith("sun.") ||
            transformedName.startsWith("com.google.")) {
            return basicClass;
        }

        return transform(basicClass);
    }

    private static class GuavaFuturesClassVisitor extends ClassVisitor {

        public GuavaFuturesClassVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
            return new GuavaFuturesMethodVisitor(this.api, methodVisitor);
        }
    }

    private static class GuavaFuturesMethodVisitor extends MethodVisitor {

        public GuavaFuturesMethodVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if (opcode == Opcodes.INVOKESTATIC && owner.equals("com/google/common/util/concurrent/Futures")) {
                if (name.equals("addCallback") &&
                    descriptor.equals("(Lcom/google/common/util/concurrent/ListenableFuture;Lcom/google/common/util/concurrent/FutureCallback;)V")) {

                    super.visitMethodInsn(Opcodes.INVOKESTATIC,
                        "com/google/common/util/concurrent/MoreExecutors",
                        "directExecutor",
                        "()Ljava/util/concurrent/Executor;",
                        false);

                    super.visitMethodInsn(opcode, owner, name,
                        "(Lcom/google/common/util/concurrent/ListenableFuture;Lcom/google/common/util/concurrent/FutureCallback;Ljava/util/concurrent/Executor;)V",
                        isInterface);
                    return;
                }

                if (name.equals("transform")) {
                    if (descriptor.equals("(Lcom/google/common/util/concurrent/ListenableFuture;Lcom/google/common/base/Function;)Lcom/google/common/util/concurrent/ListenableFuture;")) {
                        super.visitMethodInsn(Opcodes.INVOKESTATIC,
                            "com/google/common/util/concurrent/MoreExecutors",
                            "directExecutor",
                            "()Ljava/util/concurrent/Executor;",
                            false);

                        super.visitMethodInsn(opcode, owner, name,
                            "(Lcom/google/common/util/concurrent/ListenableFuture;Lcom/google/common/base/Function;Ljava/util/concurrent/Executor;)Lcom/google/common/util/concurrent/ListenableFuture;",
                            isInterface);
                        return;
                    }

                    if (descriptor.equals("(Lcom/google/common/util/concurrent/ListenableFuture;Lcom/google/common/util/concurrent/AsyncFunction;)Lcom/google/common/util/concurrent/ListenableFuture;")) {
                        super.visitMethodInsn(Opcodes.INVOKESTATIC,
                            "com/google/common/util/concurrent/MoreExecutors",
                            "directExecutor",
                            "()Ljava/util/concurrent/Executor;",
                            false);

                        super.visitMethodInsn(opcode, owner, name,
                            "(Lcom/google/common/util/concurrent/ListenableFuture;Lcom/google/common/util/concurrent/AsyncFunction;Ljava/util/concurrent/Executor;)Lcom/google/common/util/concurrent/ListenableFuture;",
                            isInterface);
                        return;
                    }
                }
            }

            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }
    }
}
