package com.chattriggers.ctjs.launch.plugin

import dev.falsehonesty.asmhelper.dsl.At
import dev.falsehonesty.asmhelper.dsl.InjectionPoint
import dev.falsehonesty.asmhelper.dsl.inject
import dev.falsehonesty.asmhelper.dsl.instructions.*

fun injectPacketThreadUtil() = inject {
    className = "net/minecraft/network/PacketThreadUtil"
    methodName = "checkThreadAndEnqueue"
    methodDesc = "(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V"

    methodMaps = mapOf("func_180031_a" to "checkThreadAndEnqueue")

    at = At(InjectionPoint.TAIL)

    insnList {
        aload(1) //INetHandler
        instanceof("net/minecraft/client/network/NetHandlerPlayClient")
        ifClause(JumpCondition.FALSE) {

            createInstance(CANCELLABLE_EVENT, "()V")
            val event = astore()

            getStatic(TRIGGER_TYPE, "PACKET_RECEIVED", "L$TRIGGER_TYPE;")
            invokeVirtual(TRIGGER_TYPE, "triggerAll", "([Ljava/lang/Object;)V") {
                array(2, "java/lang/Object") {
                    aadd {
                        aload(0)
                    }
                    aadd {
                        load(event)
                    }
                }
            }

            load(event)
            invoke(InvokeType.VIRTUAL, CANCELLABLE_EVENT, "isCancelled", "()Z")
            ifClause(JumpCondition.FALSE) {
                getStatic("net/minecraft/network/ThreadQuickExitException", "INSTANCE", "Lnet/minecraft/network/ThreadQuickExitException;")
                athrow()
            }
        }
    }
}
