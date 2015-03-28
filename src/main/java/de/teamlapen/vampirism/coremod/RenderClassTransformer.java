package de.teamlapen.vampirism.coremod;

import java.util.Iterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import de.teamlapen.vampirism.util.Logger;

/**
 * Used to add a hook to the bindEntityTexture method, so the texture can be replaced by a vampire version if required
 * @author Maxanier
 *
 */
public class RenderClassTransformer implements IClassTransformer {

	private final static String TAG = "RenderTransformer";
	private final static String CLASS_RENDER = "net.minecraft.client.renderer.entity.Render";
	private final static String CLASS_RENDER_NOTCH = "bno";
	private final static String METHOD_BET = "bindEntityTexture";
	private final static String METHOD_BET_SRG = "func_110777_b";
	private final static String METHOD_GET = "getEntityTexture";
	private final static String METHOD_GET_SRG = "func_110775_a";
	private final static String METHOD_BT = "bindTexture";
	private final static String METHOD_BT_SRG = "func_110776_a";

	public byte[] applyPatch(String name, byte[] basicClass, boolean obfuscated) {
		String betMethodName = "";
		String getMethodName="";
		String btMethodName="";
		if (obfuscated) {
			betMethodName = METHOD_BET_SRG;
			getMethodName= METHOD_GET_SRG;
			btMethodName=METHOD_BT_SRG;
		} else {
			betMethodName = METHOD_BET;
			getMethodName= METHOD_GET;
			btMethodName=METHOD_BT;
		}

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);

		Iterator<MethodNode> methods = classNode.methods.iterator();
		while (methods.hasNext()) {
			MethodNode m = methods.next();

			if (m.name.equals(betMethodName)) {
				Logger.i(TAG, "INSIDE bindEntityTexture METHOD");

				InsnList toIn = new InsnList();

				LabelNode l0 = new LabelNode();
				toIn.add(l0);
				toIn.add(new VarInsnNode(Opcodes.ALOAD, 0));
				toIn.add(new VarInsnNode(Opcodes.ALOAD, 1));
				toIn.add(new VarInsnNode(Opcodes.ALOAD, 0));
				toIn.add(new VarInsnNode(Opcodes.ALOAD, 1));
				toIn.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CLASS_RENDER.replace('.', '/'), getMethodName, "(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/ResourceLocation;", false));
				toIn.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "de/teamlapen/vampirism/coremod/CoreHandler", "checkVampireTexture", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/util/ResourceLocation;", false));
				toIn.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CLASS_RENDER.replace('.', '/'), btMethodName, "(Lnet/minecraft/util/ResourceLocation;)V", false));
				LabelNode l1 = new LabelNode();
				toIn.add(l1);
				toIn.add(new InsnNode(Opcodes.RETURN));
				m.instructions.insert(toIn);
				Logger.i(TAG, "PATCH COMPLETE");
				
//				Label l0 = new Label();
//				mv.visitLabel(l0);
//				mv.visitLineNumber(59, l0);
//				mv.visitVarInsn(ALOAD, 0);
//				mv.visitVarInsn(ALOAD, 1);
//				mv.visitVarInsn(ALOAD, 0);
//				mv.visitVarInsn(ALOAD, 1);
//				mv.visitMethodInsn(INVOKEVIRTUAL, "de/teamlapen/vampirism/client/render/Render", "getEntityTexture", "(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/ResourceLocation;", false);
//				mv.visitMethodInsn(INVOKESTATIC, "de/teamlapen/vampirism/coremod/CoreHandler", "checkVampireTexture", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/util/ResourceLocation;", false);
//				mv.visitMethodInsn(INVOKEVIRTUAL, "de/teamlapen/vampirism/client/render/Render", "bindTexture", "(Lnet/minecraft/util/ResourceLocation;)V", false);
//				Label l1 = new Label();
//				mv.visitLabel(l1);
//				mv.visitLineNumber(61, l1);
//				mv.visitInsn(RETURN);
				
				break;
			}
		}

		// ASM specific for cleaning up and returning the final bytes for JVM
		// processing.
		//ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS| ClassWriter.COMPUTE_FRAMES );
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS  );
		classNode.accept(writer);
		return writer.toByteArray();
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {

		if (name.equals(CLASS_RENDER_NOTCH)) {
			Logger.i(TAG, "INSIDE OBFUSCATED RENDER CLASS - ABOUT TO PATCH: " + name);
			return applyPatch(name, basicClass, true);
		} else if (name.equals(CLASS_RENDER)) {
			Logger.i(TAG, "INSIDE RENDER CLASS - ABOUT TO PATCH: " + name);
			return applyPatch(name, basicClass, false);
		}
		return basicClass;

	}

}
