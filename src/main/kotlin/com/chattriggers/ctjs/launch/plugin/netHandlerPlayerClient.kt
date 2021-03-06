package com.chattriggers.ctjs.launch.plugin

import dev.falsehonesty.asmhelper.dsl.At
import dev.falsehonesty.asmhelper.dsl.InjectionPoint
import dev.falsehonesty.asmhelper.dsl.inject
import dev.falsehonesty.asmhelper.dsl.instructions.*

fun injectNetHandlerPlayClient() = inject {
    className = "net/minecraft/client/network/NetHandlerPlayClient"
    methodName = "addToSendQueue"
    methodDesc = "(L$PACKET;)V"
    at = At(InjectionPoint.HEAD)

    methodMaps = mapOf("func_147297_a" to "addToSendQueue")

    insnList {
        createInstance(CANCELLABLE_EVENT, "()V")
        val event = astore()

        getStatic(TRIGGER_TYPE, "PACKET_SENT", "L$TRIGGER_TYPE;")
        invokeVirtual(TRIGGER_TYPE, "triggerAll", "([Ljava/lang/Object;)V") {
            array(2, "java/lang/Object") {
                aadd {
                    aload(1)
                }

                aadd {
                    load(event)
                }
            }
        }

        load(event)
        invoke(InvokeType.VIRTUAL, CANCELLABLE_EVENT, "isCancelled", "()Z")

        ifClause(JumpCondition.FALSE) {
            methodReturn()
        }
    }
}
