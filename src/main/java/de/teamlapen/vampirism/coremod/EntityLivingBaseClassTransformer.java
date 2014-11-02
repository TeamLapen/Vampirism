package de.teamlapen.vampirism.coremod;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import de.teamlapen.vampirism.util.Logger;
import net.minecraft.launchwrapper.IClassTransformer;

public class EntityLivingBaseClassTransformer implements IClassTransformer {

	private final static String TAG="EntityLivingBaseTransformer";
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		
		
		//Obfuscated name: sv
		if(name.equals("sv")){
			Logger.i(TAG, "INSIDE OBFUSCATED ENTITYLIVINGBASE CLASS - ABOUT TO PATCH: "+name);
			return applyPatch(name, basicClass,true);
		}
		else if(name.equals("net.minecraft.entity.EntityLivingBase")){
			Logger.i(TAG, "INSIDE ENTITYLIVINGBASE CLASS - ABOUT TO PATCH: "+name);
			return applyPatch(name, basicClass,false);
		}
		return basicClass;
		
	}
	
	public byte[] applyPatch(String name,byte[] basicClass,boolean obfuscated){
		
		String iPAMethodName="";
		if(obfuscated){
			iPAMethodName="func_82165_m";
		}
		else{
			iPAMethodName="isPotionActive";
		}
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader=new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while(methods.hasNext()){
			MethodNode m = methods.next();
			
			if(m.name.equals(iPAMethodName)&&m.desc.equals("(Lnet/minecraft/potion/Potion;)Z")){
				Logger.i(TAG, "INSIDE isPotionActive METHOD");
				
				//Inject if clause
				InsnList toIn = new InsnList();
				toIn.add(new VarInsnNode(Opcodes.ALOAD,0));
				toIn.add(new VarInsnNode(Opcodes.ALOAD,1));
				toIn.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"de/teamlapen/vampirism/coremod/CoreHandler", "shouldOverrideNightVision", "(Ljava/lang/Object;Lnet/minecraft/potion/Potion;)Z", false));
				LabelNode l1=new LabelNode();
				toIn.add(new JumpInsnNode(Opcodes.IFEQ,l1));
				LabelNode l2=new LabelNode();
				toIn.add(l2);
				toIn.add(new InsnNode(Opcodes.ICONST_1));
				toIn.add(new InsnNode(Opcodes.IRETURN));
				toIn.add(l1);
				toIn.add(new FrameNode(Opcodes.F_SAME,0,null,0,null));
				
				/*
				 
				 mv.visitVarInsn(ALOAD, 0);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitMethodInsn(INVOKESTATIC, "de/teamlapen/vampirism/coremod/CoreHandler", "shouldOverrideNightVision", "(Ljava/lang/Object;Lnet/minecraft/potion/Potion;)Z", false);
				Label l1 = new Label();
				mv.visitJumpInsn(IFEQ, l1);
				Label l2 = new Label();
				mv.visitLabel(l2);
				mv.visitLineNumber(97, l2);
				mv.visitInsn(ICONST_1);
				mv.visitInsn(IRETURN);
				mv.visitLabel(l1);
				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				 */
				m.instructions.insert(toIn);
				Logger.i(TAG, "PATCH COMPLETE");
				break;
			}
		}
		

		//ASM specific for cleaning up and returning the final bytes for JVM processing.
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS );
		classNode.accept(writer);
		return writer.toByteArray();
			
	}

}
