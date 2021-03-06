package com.chattriggers.ctjs.engine.langs.js

import com.chattriggers.ctjs.engine.loader.ILoader
import com.chattriggers.ctjs.engine.langs.Lang
import com.chattriggers.ctjs.engine.module.Module
import com.chattriggers.ctjs.engine.module.ModuleManager.modulesFolder
import com.chattriggers.ctjs.triggers.OnTrigger
import com.chattriggers.ctjs.utils.console.Console
import dev.falsehonesty.asmhelper.dsl.At
import dev.falsehonesty.asmhelper.dsl.applyField
import dev.falsehonesty.asmhelper.dsl.inject
import dev.falsehonesty.asmhelper.dsl.instructions.InsnListBuilder
import dev.falsehonesty.asmhelper.dsl.remove
import dev.falsehonesty.asmhelper.dsl.writers.AccessType
import org.mozilla.javascript.*
import org.mozilla.javascript.Function
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider
import org.mozilla.javascript.commonjs.module.Require
import org.mozilla.javascript.commonjs.module.provider.StrongCachingModuleScriptProvider
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider
import java.io.File
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.net.URI
import java.net.URL
import java.util.*
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.Comparator

object JSLoader : ILoader {
    override var triggers: SortedSet<OnTrigger> = ConcurrentSkipListSet()
    override val console by lazy { Console(this) }

    private lateinit var moduleContext: Context
    private lateinit var evalContext: Context
    private lateinit var scope: Scriptable
    private lateinit var require: CTRequire
    private lateinit var ASMLib: Any

    private val INVOKE_JS_CALL = MethodHandles.lookup().findStatic(
        JSLoader::class.java,
        "asmInvoke",
        MethodType.methodType(Any::class.java, Callable::class.java, Array<Any?>::class.java)
    )

    override fun setup(jars: List<URL>) {
        instanceContexts(jars)

        wrapInContext {
            val asmProvidedLibs = saveResource(
                "/js/asmProvidedLibs.js",
                File(modulesFolder.parentFile, "chattriggers-asm-provided-libs.js"),
                true
            )

            try {
                moduleContext.evaluateString(
                    scope,
                    asmProvidedLibs,
                    "asmProvided",
                    1, null
                )
            } catch (e: Throwable) {
                e.printStackTrace()
                console.printStackTrace(e)
            }
        }
    }

    override fun asmSetup() = wrapInContext {
        val asmLibFile = File(modulesFolder.parentFile, "chattriggers-asmLib.js")

        saveResource("/js/asmLib.js", asmLibFile, true)

        try {
            val returned = require.loadCTModule("ASMLib", "ASMLib", asmLibFile.toURI())

            // Get the default export, the ASM Helper
            ASMLib = ScriptableObject.getProperty(returned, "default")
        } catch (e: Throwable) {
            e.printStackTrace()
            console.printStackTrace(e)
        }
    }

    override fun asmPass(module: Module, asmURI: URI) = wrapInContext {
        try {
            val returned = require.loadCTModule(module.name, module.metadata.asmEntry!!, asmURI)

            val asmFunction = ScriptableObject.getProperty(returned, "default") as? Function

            if (asmFunction == null) {
                console.out.println("Asm entry for module ${module.name} has an invalid export. " +
                        "An Asm entry must have a default export of a function.")
                return@wrapInContext
            }

            ScriptableObject.putProperty(ASMLib, "currentModule", module.name)
            asmFunction.call(moduleContext, scope, scope, arrayOf(ASMLib))
        } catch (e: Throwable) {
            println("Error loading asm entry for module ${module.name}")
            e.printStackTrace()
            console.out.println("Error loading asm entry for module ${module.name}")
            console.printStackTrace(e)
        }
    }

    override fun entrySetup() = wrapInContext {
        val moduleProvidedLibs = saveResource(
            "/js/moduleProvidedLibs.js",
            File(modulesFolder.parentFile, "chattriggers-modules-provided-libs.js"),
            true
        )

        try {
            moduleContext.evaluateString(
                scope,
                moduleProvidedLibs,
                "moduleProvided",
                1, null
            )
        } catch (e: Throwable) {
            e.printStackTrace()
            console.printStackTrace(e)
        }
    }

    override fun entryPass(module: Module, entryURI: URI) = wrapInContext {
        try {
            require.loadCTModule(module.name, module.metadata.entry!!, entryURI)
        } catch (e: Throwable) {
            println("Error loading module ${module.name}")
            e.printStackTrace()
            console.out.println("Error loading module ${module.name}")
            console.printStackTrace(e)
        }
    }

    override fun asmInvokeLookup(module: Module, functionURI: URI): MethodHandle {
        wrapInContext {
            try {
                val returned = require.loadCTModule(module.name, File(functionURI).name, functionURI)
                val func = ScriptableObject.getProperty(returned, "default") as Callable

                // When a call to this function ID is made, we always want to point it
                // to our asmInvoke method, which in turn should always call [func].
                return INVOKE_JS_CALL.bindTo(func)
            } catch (e: Throwable) {
                println("Error loading asm function $functionURI in module ${module.name}.")
                e.printStackTrace()
                console.out.println("Error loading asm function $functionURI in module ${module.name}.")
                console.printStackTrace(e)

                // If we can't resolve the target function correctly, we will return
                //  a no-op method handle that will always return null.
                //  It still needs to match the method type (Object[])Object, so we drop the arguments param.
                return MethodHandles.dropArguments(
                    MethodHandles.constant(Any::class.java, null),
                    0,
                    Array<Any?>::class.java
                )
            }
        }

        // Will never happen
        return null as MethodHandle
    }

    @JvmStatic
    fun asmInvoke(func: Callable, args: Array<Any?>): Any? {
        wrapInContext {
            return func.call(moduleContext, scope, scope, args)
        }

        // Will never happen
        return null
    }

    private inline fun wrapInContext(context: Context = moduleContext, block: () -> Unit) {
        // TODO for Kotlin 1.4: make block a CALL_EXACTLY_ONCE contract

        val missingContext = Context.getCurrentContext() == null
        if (missingContext) {
            try {
                JSContextFactory.enterContext(context)
            } catch (e: Throwable) {
                JSContextFactory.enterContext()
            }
        }

        try {
            block()
        } finally {
            if (missingContext) Context.exit()
        }
    }

    @JvmStatic
    fun asmInjectHelper(
        _className: String,
        _at: At,
        _methodName: String,
        _methodDesc: String,
        _fieldMaps: Map<String, String>,
        _methodMaps: Map<String, String>,
        _insnList: (Wrapper) -> Unit
    ) {
        inject {
            className = _className
            methodName = _methodName
            methodDesc = _methodDesc
            at = _at
            fieldMaps = _fieldMaps
            methodMaps = _methodMaps

            insnList {
                wrapInContext {
                    _insnList(NativeJavaObject(scope, this, InsnListBuilder::class.java))
                }
            }
        }
    }

    @JvmStatic
    fun asmRemoveHelper(
        _className: String,
        _at: At,
        _methodName: String,
        _methodDesc: String,
        _methodMaps: Map<String, String>,
        _numberToRemove: Int
    ) {
        remove {
            className = _className
            methodName = _methodName
            methodDesc = _methodDesc
            at = _at
            methodMaps = _methodMaps
            numberToRemove = _numberToRemove
        }
    }

    @JvmStatic
    fun asmFieldHelper(
        _className: String,
        _fieldName: String,
        _fieldDesc: String,
        _initialValue: Any?,
        _accessTypes: List<AccessType>
    ) {
        applyField {
            className = _className
            fieldName = _fieldName
            fieldDesc = _fieldDesc
            initialValue = _initialValue
            accessTypes = _accessTypes
        }
    }

    override fun eval(code: String): String? {
        wrapInContext(evalContext) {
            return Context.toString(evalContext.evaluateString(scope, code, "<eval>", 1, null))
        }

        // Will never happen
        return null
    }

    override fun getLanguage() = Lang.JS

    override fun trigger(trigger: OnTrigger, method: Any, args: Array<out Any?>) {
        wrapInContext {
            try {
                if (method !is Function)
                    throw IllegalArgumentException("Need to pass actual function to the register function, not the name!")

                method.call(Context.getCurrentContext(), scope, scope, args)
            } catch (e: Throwable) {
                console.printStackTrace(e)
                removeTrigger(trigger)
            }
        }
    }

    private fun instanceContexts(files: List<URL>) {
        JSContextFactory.addAllURLs(files)

        moduleContext = JSContextFactory.enterContext()
        scope = ImporterTopLevel(moduleContext)

        val sourceProvider = UrlModuleSourceProvider(listOf(modulesFolder.toURI()), listOf())
        val moduleProvider = StrongCachingModuleScriptProvider(sourceProvider)
        require = CTRequire(moduleProvider)
        require.install(scope)

        Context.exit()

        JSContextFactory.optimize = false
        evalContext = JSContextFactory.enterContext()
        Context.exit()
        JSContextFactory.optimize = true
    }

    class CTRequire(moduleProvider: ModuleScriptProvider) : Require(moduleContext, scope, moduleProvider, null, null, false) {
        fun loadCTModule(name: String, entry: String, uri: URI): Scriptable {
            return getExportedModuleInterface(moduleContext, name + File.separator + entry, uri, null, false)
        }
    }
}
