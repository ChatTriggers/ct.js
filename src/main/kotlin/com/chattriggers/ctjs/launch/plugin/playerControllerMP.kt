package com.chattriggers.ctjs.launch.plugin

import dev.falsehonesty.asmhelper.dsl.At
import dev.falsehonesty.asmhelper.dsl.InjectionPoint
import dev.falsehonesty.asmhelper.dsl.inject
import dev.falsehonesty.asmhelper.dsl.instructions.*

fun makePlayerControllerMPInjections() {
    injectAttackEntity()
    injectHitBlock()
}

fun injectAttackEntity() = inject {
    className = "net/minecraft/client/multiplayer/PlayerControllerMP"
    methodName = "attackEntity"
    methodDesc = "(L$ENTITY_PLAYER;L$ENTITY;)V"
    at = At(InjectionPoint.HEAD)

    methodMaps = mapOf("func_78764_a" to "attackEntity")

    insnList {
        createInstance(CANCELLABLE_EVENT, "()V")
        val event = astore()

        getStatic(TRIGGER_TYPE, "ATTACK_ENTITY", "L$TRIGGER_TYPE;")
        invokeVirtual(TRIGGER_TYPE, "triggerAll", "([Ljava/lang/Object;)V") {
            array(2, "java/lang/Object") {
                aadd {
                    createInstance("com/chattriggers/ctjs/minecraft/wrappers/objects/Entity", "(L$ENTITY;)V") {
                        aload(2)
                    }
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

fun injectHitBlock() = inject {
    className = "net/minecraft/client/multiplayer/PlayerControllerMP"
    methodName = "clickBlock"
    methodDesc = "(L$BLOCK_POS;L$ENUM_FACING;)Z"
    at = At(InjectionPoint.HEAD)

    methodMaps = mapOf("func_180511_b" to "clickBlock")

    insnList {
        invokeKObjectFunction(CLIENT_LISTENER, "onHitBlock", "(L$BLOCK_POS;L$ENUM_FACING;)Z") {
            aload(1)
            aload(2)
        }

        ifClause(JumpCondition.FALSE) {
            int(0)
            ireturn()
        }
    }
}
