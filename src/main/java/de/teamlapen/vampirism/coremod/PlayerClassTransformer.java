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
	private final static String CLASS_ENTITYPLAYER="net.minecraft.entity.player.EntityPlayer";
	public final static String CLASS_ENTITYPLAYER_SRG="net/minecraft/entity/player/EntityPlayer";
	private final static String CLASS_ENTITYPLAYER_NOTCH="yz";
	private final static String METHOD_EXHAUSTION="addExhaustion";
	private final static String METHOD_EXHAUSTION_SRG="func_71020_j";
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if(name.equals(CLASS_ENTITYPLAYER_NOTCH)){
			Logger.i(TAG, "INSIDE OBFUSCATED PLAYER CLASS - ABOUT TO PATCH: "+name+" transforned: "+transformedName);
			return applyPatch(name, basicClass,true);
		}
		else if(name.equals(CLASS_ENTITYPLAYER)){
			Logger.i(TAG, "INSIDE PLAYER CLASS - ABOUT TO PATCH: "+name);
			return applyPatch(name, basicClass,false);
		}
		if(name.equals(CLASS_ENTITYPLAYER_SRG))Logger.e(TAG, "SRG CLASS NAME");
		return basicClass;
	}
	
	public byte[] applyPatch(String name,byte[] basicClass,boolean obfuscated){
		String exhaustionMethodName="";
		if(obfuscated){
			exhaustionMethodName=METHOD_EXHAUSTION_SRG;
		}
		else{
			exhaustionMethodName=METHOD_EXHAUSTION;
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
				
				toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "de/teamlapen/vampirism/coremod/CoreHandler","addExhaustion","(FL"+CLASS_ENTITYPLAYER_SRG+";)V",false));
				
				
				m.instructions.insert(toInject);
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