package de.teamlapen.vampirism.coremod;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import de.teamlapen.vampirism.util.Logger;
import net.minecraft.launchwrapper.IClassTransformer;


/**
 * EntityPlayer class transformer, which adds a few hooks
 * @author Max
 *
 */
public class PlayerClassTransformer implements IClassTransformer {

	private final static String TAG="PlayerTransformer";
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if(name.equals("yz")){
			Logger.i(TAG, "INSIDE OBFUSCATED PLAYER CLASS - ABOUT TO PATCH: "+name);
			return applyPatch(name, basicClass,true);
		}
		else if(name.equals("net.minecraft.entity.player.EntityPlayer")){
			Logger.i(TAG, "INSIDE PLAYER CLASS - ABOUT TO PATCH: "+name);
			return applyPatch(name, basicClass,false);
		}
		return basicClass;
	}
	
	public byte[] applyPatch(String name,byte[] basicClass,boolean obfuscated){
		String exhaustionMethodName="";
		if(obfuscated){
			exhaustionMethodName="func_71020_j";
		}
		else{
			exhaustionMethodName="addExhaustion";
		}
		
		
		ClassNode classNode = new ClassNode();
		ClassReader classReader=new ClassReader(basicClass);
		classReader.accept(classNode, 0);
		
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while(methods.hasNext()){
			MethodNode m = methods.next();
			
			if(m.name.equals(exhaustionMethodName)){
				Logger.i(TAG, "INSIDE EXHAUSTION METHOD");
				
				//Inject Method call
				InsnList toInject = new InsnList();
				toInject.add(new VarInsnNode(Opcodes.FLOAD,1));
				toInject.add(new VarInsnNode(Opcodes.ALOAD,0));
				toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "de/teamlapen/vampirism/coremod/CoreHandler","addExhaustion","(FLnet/minecraft/entity/player/EntityPlayer;)V",false));
				m.instructions.insert(toInject);
				Logger.i(TAG, "PATCH COMPLETE");
				break;
			}
			
			
		}
		
		
		
		//ASM specific for cleaning up and returning the final bytes for JVM processing.
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}

}