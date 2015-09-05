package de.teamlapen.vampirism.coremod;

import de.teamlapen.vampirism.util.Logger;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

/**
 * EntityRenderer class transformer, which modifies the render algorithm for a custom night vision effect
 * 
 * @author Maxanier
 *
 */
public class EntityRendererClassTransformer implements IClassTransformer {

	private final static String TAG = "EntityRendererTransformer";
	private final static String CLASS_ENTITYRENDERER = "net.minecraft.client.renderer.EntityRenderer";
	private final static String CLASS_ENTITYRENDERER_NOTCH = "blt";
	private final static String METHOD_GETNVB = "getNightVisionBrightness";
	private final static String METHOD_GETNVB_SRG = "func_82830_a";

	public byte[] applyPatch(String name, byte[] basicClass, boolean obfuscated) {
		String gNVBMethodName = "";
		if (obfuscated) {
			gNVBMethodName = METHOD_GETNVB_SRG;

		} else {
			gNVBMethodName = METHOD_GETNVB;

		}

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);

		Iterator<MethodNode> methods = classNode.methods.iterator();
		while (methods.hasNext()) {
			MethodNode m = methods.next();

			if (m.name.equals(gNVBMethodName)) {
				Logger.d(TAG, "INSIDE getNightVisionBrightness METHOD");

				// Inject if clause
				InsnList toIn = new InsnList();

				LabelNode l0 = new LabelNode();
				toIn.add(l0);

				toIn.add(new VarInsnNode(Opcodes.ALOAD, 1));
				toIn.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "de/teamlapen/vampirism/coremod/CoreHandler", "getNightVisionLevel", "(L" + PlayerClassTransformer.CLASS_ENTITYPLAYER_SRG + ";)F",
						false));
				toIn.add(new InsnNode(Opcodes.FCONST_0));
				toIn.add(new InsnNode(Opcodes.FCMPL));
				LabelNode l1 = new LabelNode();
				toIn.add(new JumpInsnNode(Opcodes.IFLE, l1));
				LabelNode l2 = new LabelNode();
				toIn.add(l2);
				toIn.add(new VarInsnNode(Opcodes.ALOAD, 1));
				toIn.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "de/teamlapen/vampirism/coremod/CoreHandler", "getNightVisionLevel", "(L" + PlayerClassTransformer.CLASS_ENTITYPLAYER_SRG + ";)F",
						false));
				toIn.add(new InsnNode(Opcodes.FRETURN));
				toIn.add(l1);

				/*
				 * Label l0 = new Label(); mv.visitLabel(l0); mv.visitVarInsn(ALOAD, 1); mv.visitMethodInsn(INVOKESTATIC, "de/teamlapen/vampirism/coremod/CoreHandler", "getNightVisionLevel",
				 * "(Lnet/minecraft/entity/player/EntityPlayer;)F", false); mv.visitInsn(FCONST_0); mv.visitInsn(FCMPL); Label l1 = new Label(); mv.visitJumpInsn(IFLE, l1); Label l2 = new Label();
				 * mv.visitLabel(l2); mv.visitLineNumber(94, l2); mv.visitVarInsn(ALOAD, 1); mv.visitMethodInsn(INVOKESTATIC, "de/teamlapen/vampirism/coremod/CoreHandler", "getNightVisionLevel",
				 * "(Lnet/minecraft/entity/player/EntityPlayer;)F", false); mv.visitInsn(FRETURN); mv.visitLabel(l1);
				 */
				m.instructions.insert(toIn);
				Logger.d(TAG, "PATCH COMPLETE");
				break;
			}
		}

		// ASM specific for cleaning up and returning the final bytes for JVM
		// processing.
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {

		// Obfuscated name: blt (or maybe: blu,blv,blw)
		if (name.equals(CLASS_ENTITYRENDERER_NOTCH)) {
			Logger.i(TAG, "INSIDE OBFUSCATED RENDERER CLASS - ABOUT TO PATCH: %s", name);
			return applyPatch(name, basicClass, true);
		} else if (name.equals(CLASS_ENTITYRENDERER)) {
			Logger.i(TAG, "INSIDE RENDERER CLASS - ABOUT TO PATCH: %s", name);
			return applyPatch(name, basicClass, false);
		}
		return basicClass;

	}

}
