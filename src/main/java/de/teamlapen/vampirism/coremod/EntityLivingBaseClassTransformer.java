package de.teamlapen.vampirism.coremod;

import de.teamlapen.vampirism.util.Logger;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

/**
 * EntityLivingBase class transformer, which inserts a few hooks
 * 
 * @author Maxanier
 *
 */
public class EntityLivingBaseClassTransformer implements IClassTransformer {

	private final static String TAG = "EntityLivingBaseTransformer";
	private final static String CLASS_ENTITYLIVINGBASE = "net.minecraft.entity.EntityLivingBase";
	private final static String CLASS_ENTITYLIVINGBASE_NOTCH = "sv";
	private final static String METHOD_IPA = "isPotionActive";
	private final static String METHOD_IPA_MCP = "func_70644_a";
	private final static String METHOD_GAP = "getActivePotionEffect";
	private final static String METHOD_GAP_MCP = "func_70660_b";

	// private final static String OB_METHOD_IPA="func_82165_m";
	private final static String CLASS_POTION_SRG = "net/minecraft/potion/Potion";
	private static final String CLASS_POTIONEFFECT_SRG = "net/minecraft/potion/PotionEffect";

	public byte[] applyPatch(String name, byte[] basicClass, boolean obfuscated) {

		String iPAMethodName = "";
		if (obfuscated) {
			iPAMethodName = METHOD_IPA_MCP;
		} else {
			iPAMethodName = METHOD_IPA;
		}

		String gAPMethodName = "";
		if (obfuscated) {
			gAPMethodName = METHOD_GAP_MCP;
		} else {
			gAPMethodName = METHOD_GAP;
		}

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);

		Iterator<MethodNode> methods = classNode.methods.iterator();
		while (methods.hasNext()) {
			MethodNode m = methods.next();
			if (m.name.equals(iPAMethodName) && m.desc.equals("(L" + CLASS_POTION_SRG + ";)Z")) {
				Logger.d(TAG, "INSIDE isPotionActive METHOD");

				// Inject if clause
				InsnList toIn = new InsnList();
				toIn.add(new VarInsnNode(Opcodes.ALOAD, 0));
				toIn.add(new VarInsnNode(Opcodes.ALOAD, 1));
				toIn.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "de/teamlapen/vampirism/coremod/CoreHandler", "shouldOverrideNightVision", "(Ljava/lang/Object;L" + CLASS_POTION_SRG + ";)Z", false));
				LabelNode l1 = new LabelNode();
				toIn.add(new JumpInsnNode(Opcodes.IFEQ, l1));
				LabelNode l2 = new LabelNode();
				toIn.add(l2);
				toIn.add(new InsnNode(Opcodes.ICONST_1));
				toIn.add(new InsnNode(Opcodes.IRETURN));
				toIn.add(l1);
				toIn.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));

				m.instructions.insert(toIn);
				Logger.d(TAG, "PATCH COMPLETE");
			} else if (m.name.equals(gAPMethodName)) {
				Logger.d(TAG, "INSIDE getActivePotionEffect METHOD");

				InsnList toIn = new InsnList();
				LabelNode l0 = new LabelNode();
				toIn.add(l0);
				toIn.add(new VarInsnNode(Opcodes.ALOAD, 0));
				toIn.add(new VarInsnNode(Opcodes.ALOAD, 1));
				toIn.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "de/teamlapen/vampirism/coremod/CoreHandler", "shouldOverrideNightVision", "(Ljava/lang/Object;L" + CLASS_POTION_SRG + ";)Z", false));
				LabelNode l1 = new LabelNode();
				toIn.add(new JumpInsnNode(Opcodes.IFEQ, l1));
				LabelNode l2 = new LabelNode();
				toIn.add(l2);
				toIn.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "de/teamlapen/vampirism/coremod/CoreHandler", "getFakeNightVisionEffect", "()L" + CLASS_POTIONEFFECT_SRG + ";", false));
				toIn.add(new InsnNode(Opcodes.ARETURN));
				toIn.add(l1);
				m.instructions.insert(toIn);

				// mv = cw.visitMethod(ACC_PUBLIC, "getActivePotionEffect", "(Lnet/minecraft/potion/Potion;)Lnet/minecraft/potion/PotionEffect;", null, null);
				// mv.visitCode();
				// Label l0 = new Label();
				// mv.visitLabel(l0);
				// mv.visitLineNumber(75, l0);
				// mv.visitVarInsn(ALOAD, 0);
				// mv.visitVarInsn(ALOAD, 1);
				// mv.visitMethodInsn(INVOKESTATIC, "de/teamlapen/vampirism/coremod/CoreHandler", "shouldOverrideNightVision", "(Ljava/lang/Object;Lnet/minecraft/potion/Potion;)Z", false);
				// Label l1 = new Label();
				// mv.visitJumpInsn(IFEQ, l1);
				// Label l2 = new Label();
				// mv.visitLabel(l2);
				// mv.visitLineNumber(76, l2);
				// mv.visitMethodInsn(INVOKESTATIC, "de/teamlapen/vampirism/coremod/CoreHandler", "getFakeNightVisionEffect", "()Lnet/minecraft/potion/PotionEffect;", false);
				// mv.visitInsn(ARETURN);
				// mv.visitLabel(l1);

				Logger.d(TAG, "PATCH COMPLETE");
			}
		}

		// ASM specific for cleaning up and returning the final bytes for JVM
		// processing.
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();

	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {

		if (name.equals(CLASS_ENTITYLIVINGBASE_NOTCH)) {
			Logger.i(TAG, "INSIDE OBFUSCATED ENTITYLIVINGBASE CLASS - ABOUT TO PATCH: %s (%s)", name, transformedName);
			return applyPatch(name, basicClass, true);
		} else if (name.equals(CLASS_ENTITYLIVINGBASE)) {
			Logger.i(TAG, "INSIDE ENTITYLIVINGBASE CLASS - ABOUT TO PATCH: %s", name);
			return applyPatch(name, basicClass, false);
		}
		return basicClass;

	}

}
