package com.chattriggers.ctjs.launch.plugin

import dev.falsehonesty.asmhelper.dsl.At
import dev.falsehonesty.asmhelper.dsl.InjectionPoint
import dev.falsehonesty.asmhelper.dsl.inject
import dev.falsehonesty.asmhelper.dsl.instructions.*

fun injectEffectRenderer() = inject {
    className = EFFECT_RENDERER
    methodName = "spawnEffectParticle"
    methodDesc = "(IDDDDDD[I)L$ENTITY_FX;"
    at = At(
        InjectionPoint.INVOKE(
            Descriptor(
                EFFECT_RENDERER,
                "addEffect",
                "(L$ENTITY_FX;)V"
            )
        )
    )

    methodMaps = mapOf(
        "getParticleFromId" to "func_179342_a",
        "func_178927_a" to "spawnEffectParticle",
        "func_78873_a" to "addEffect"
    )

    insnList {
        val entityFx = astore()

        createInstance(CANCELLABLE_EVENT, "()V")
        val event = astore()

        field(FieldAction.GET_STATIC, TRIGGER_TYPE, "SPAWN_PARTICLE", "L$TRIGGER_TYPE;")

        invoke(InvokeType.VIRTUAL, TRIGGER_TYPE, "triggerAll", "([Ljava/lang/Object;)V") {
            array(3, "java/lang/Object") {
                aadd {
                    createInstance("com/chattriggers/ctjs/minecraft/wrappers/objects/Particle", "(L$ENTITY_FX;)V") {
                        load(entityFx)
                    }
                }

                aadd {
                    invoke(InvokeType.STATIC, "net/minecraft/util/EnumParticleTypes", "getParticleFromId", "(I)Lnet/minecraft/util/EnumParticleTypes;") {
                        iload(1)
                    }
                }

                aadd { load(event) }
            }
        }

        load(event)
        invoke(InvokeType.VIRTUAL, CANCELLABLE_EVENT, "isCancelled", "()Z")

        ifClause(JumpCondition.FALSE) {
            aconst_null()
            areturn()
        }

        load(entityFx)
    }
}
