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
 * EntityPlayer class transformer, which adds a few hooks
 * 
 * @author Maxanier
 *
 */
public class PlayerClassTransformer implements IClassTransformer {

	private final static String TAG = "PlayerTransformer";
	private final static String CLASS_ENTITYPLAYER = "net.minecraft.entity.player.EntityPlayer";
	public final static String CLASS_ENTITYPLAYER_SRG = "net/minecraft/entity/player/EntityPlayer";
	private final static String CLASS_ENTITYPLAYER_NOTCH = "yz";
	private final static String METHOD_EXHAUSTION = "addExhaustion";
	private final static String METHOD_EXHAUSTION_SRG = "func_71020_j";
	private static final String METHOD_WAKE_SRG = "func_70999_a";
	private static final String METHOD_WAKE = "wakeUpPlayer";

	public byte[] applyPatch(String name, byte[] basicClass, boolean obfuscated) {
		String exhaustionMethodName = "";
		String wakeMethodName="";
		if (obfuscated) {
			exhaustionMethodName = METHOD_EXHAUSTION_SRG;
			wakeMethodName=METHOD_WAKE_SRG;
		} else {
			exhaustionMethodName = METHOD_EXHAUSTION;
			wakeMethodName=METHOD_WAKE;
		}

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(basicClass);
		classReader.accept(classNode, 0);

		Iterator<MethodNode> methods = classNode.methods.iterator();
		while (methods.hasNext()) {
			MethodNode m = methods.next();

			if (m.name.equals(exhaustionMethodName)) {
				Logger.i(TAG, "INSIDE EXHAUSTION METHOD");

				// Inject Method call
				InsnList toInject = new InsnList();
				toInject.add(new VarInsnNode(Opcodes.FLOAD, 1));
				toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));

				toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "de/teamlapen/vampirism/coremod/CoreHandler", "addExhaustion", "(FL"
						+ CLASS_ENTITYPLAYER_SRG + ";)V", false));

				m.instructions.insert(toInject);
				Logger.i(TAG, "PATCH COMPLETE");
			}
			if(m.name.equals(wakeMethodName)){
				Logger.i(TAG, "INSIDE WAKE METHOD");
				
				// Inject Method call
				InsnList toInject = new InsnList();
				LabelNode l0 = new LabelNode();
				toInject.add(l0);
				toInject.add(new VarInsnNode(Opcodes.ALOAD,0));
				toInject.add(new VarInsnNode(Opcodes.ILOAD,1));
				toInject.add(new VarInsnNode(Opcodes.ILOAD,2));
				toInject.add(new VarInsnNode(Opcodes.ILOAD,3));
				toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"de/teamlapen/vampirism/coremod/CoreHandler","shouldWakePlayer","(L"
						+ CLASS_ENTITYPLAYER_SRG + ";ZZZ)Z",false));
				LabelNode l1= new LabelNode();
				toInject.add(new JumpInsnNode(Opcodes.IFNE,l1));
				LabelNode l2=new LabelNode();
				toInject.add(l2);
				toInject.add(new InsnNode(Opcodes.RETURN));
				toInject.add(l1);
				
//				Label l0 = new Label();
//				mv.visitLabel(l0);
//				mv.visitLineNumber(27, l0);
//				mv.visitVarInsn(ALOAD, 0);
//				mv.visitMethodInsn(INVOKESTATIC, "de/teamlapen/vampirism/coremod/CoreHandler", "shouldWakePlayer", "(Lnet/minecraft/block/Block;)Z", false);
//				Label l1 = new Label();
//				mv.visitJumpInsn(IFNE, l1);
//				Label l2 = new Label();
//				mv.visitLabel(l2);
//				mv.visitLineNumber(28, l2);
//				mv.visitInsn(RETURN);
//				mv.visitLabel(l1);
				
//				if(!CoreHandler.shouldWakePlayer(this)){
//					return;
//				}
				
				m.instructions.insert(toInject);
				Logger.i(TAG, "PATCH COMPLETE");
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
		if (name.equals(CLASS_ENTITYPLAYER_NOTCH)) {
			Logger.i(TAG, "INSIDE OBFUSCATED PLAYER CLASS - ABOUT TO PATCH: " + name + " transforned: " + transformedName);
			return applyPatch(name, basicClass, true);
		} else if (name.equals(CLASS_ENTITYPLAYER)) {
			Logger.i(TAG, "INSIDE PLAYER CLASS - ABOUT TO PATCH: " + name);
			return applyPatch(name, basicClass, false);
		}
		if (name.equals(CLASS_ENTITYPLAYER_SRG))
			Logger.e(TAG, "SRG CLASS NAME");
		return basicClass;
	}

}