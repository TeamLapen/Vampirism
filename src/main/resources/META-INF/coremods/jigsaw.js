function initializeCoreMod() {
    return {
        'checkpiece': {
            'target': {
                'type': 'METHOD',
                'class': 'net/minecraft/world/gen/feature/jigsaw/JigsawManager$Assembler',
                'methodName': 'func_214881_a', //func_214881_a
                'methodDesc': '(Lnet/minecraft/world/gen/feature/structure/AbstractVillagePiece;Ljava/util/concurrent/atomic/AtomicReference;II)V'
            },
            'transformer': function (method) {
                var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
                var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
                var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');


                //Find insert point -> locate second hasNext in this method
                var firstHasNext = ASM.findFirstMethodCall(method, ASM.MethodType.INTERFACE, "java/util/Iterator", "hasNext", "()Z");
                var indexOfFirstHasNext = method.instructions.indexOf(firstHasNext);
                var secondHasNext = ASM.findFirstMethodCallAfter(method, ASM.MethodType.INTERFACE, "java/util/Iterator", "hasNext", "()Z", indexOfFirstHasNext + 1); //Search for next hasNext (IMPORTANT findFirstMethodCallAfter requires +1 to look for next one
                var indexOfSecondHasNext = method.instructions.indexOf(secondHasNext);
                // print("Found "+indexOfFirstHasNext+ " "+secondHasNext);

                var loopBeginLabel = method.instructions.get(indexOfSecondHasNext - 3);
                // print("Found begin label: "+loopBeginLabel+ " "+loopBeginLabel.getType());

                var loopStart = method.instructions.get(indexOfSecondHasNext + 6);
                // print("Found loop start "+loopStart+" "+loopStart.getType());
                // for(var i=-3;i<10;i++){
                //     print("N "+i+ " "+method.instructions.get(indexOfSecondHasNext+6+i));
                // }


                //Insert                                 if(ASMHooks.checkStructures(this.structurePieces,lvt_27_1_))continue ;
                var newInstructions = new InsnList();

                newInstructions.add(new LabelNode());
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                newInstructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/gen/feature/jigsaw/JigsawManager$Assembler", ASM.mapField("field_214886_e"), "Ljava/util/List;")); //structurePieces
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 27));
                newInstructions.add(ASM.buildMethodCall("de/teamlapen/vampirism/util/ASMHooks", "checkStructures", "(Ljava/util/List;Lnet/minecraft/world/gen/feature/jigsaw/JigsawPiece;)Z", ASM.MethodType.STATIC));
                newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, loopStart));
                newInstructions.add(new JumpInsnNode(Opcodes.GOTO, loopBeginLabel));
                //Jump to iterator begin

                method.instructions.insertBefore(loopStart, newInstructions);


                /*
                Should look like this afterwards

                L36
                LINENUMBER 139 L36
               FRAME APPEND [java/util/Iterator]
                ALOAD 26
                INVOKEINTERFACE java/util/Iterator.hasNext ()Z (itf)
                IFEQ L37
               L38
                LINENUMBER 140 L38
                ALOAD 26
                INVOKEINTERFACE java/util/Iterator.next ()Ljava/lang/Object; (itf)
                CHECKCAST net/minecraft/world/gen/feature/jigsaw/JigsawPiece
                ASTORE 27
               L39
                LINENUMBER 141 L39
                ALOAD 0
                GETFIELD de/teamlapen/vampirism/util/JigsawManager$Assembler.structurePieces : Ljava/util/List;
                ALOAD 27
                INVOKESTATIC de/teamlapen/vampirism/util/ASMHooks.checkStructures (Ljava/util/List;Lnet/minecraft/world/gen/feature/jigsaw/JigsawPiece;)Z
                IFEQ L40
                GOTO L36
               L40
                LINENUMBER 143 L40
               FRAME APPEND [net/minecraft/world/gen/feature/jigsaw/JigsawPiece]
                ALOAD 27
                GETSTATIC net/minecraft/world/gen/feature/jigsaw/EmptyJigsawPiece.INSTANCE : Lnet/minecraft/world/gen/feature/jigsaw/EmptyJigsawPiece;
                IF_ACMPNE L41
               L42
                 */

                // print("Completed transformation");
                return method;
            }
        }
    }
}