package su.vi.simply.di.core

import kotlin.reflect.KClass

internal class ScopeRegistry {
    private val scopes = mutableMapOf<String, SimplyDIScope>()
    private val chains = mutableMapOf<String, MutableList<List<String>>>()

    fun create(name: String, isSearchInScope: Boolean): SimplyDIScope =
        scopes.getOrPut(name) { SimplyDIScope(isSearchInScope) }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> findInChains(scopeName: String, kClass: KClass<*>): T? {
        chains[scopeName]?.forEach { chainedScopes ->
            chainedScopes.forEach { chainedName ->
                if (scopeName != chainedName) {
                    scopes[chainedName]?.getNullableDependency<T>(kClass)?.let { return it }
                }
            }
        }
        return null
    }

    fun addChains(listOfScopes: List<String>) {
        for (scopeName in listOfScopes) {
            val scope = chains.getOrPut(scopeName) { mutableListOf() }
            scope.add(listOfScopes)
        }
    }

    fun removeChains(listOfScopes: List<String>) {
        for (scopeName in listOfScopes) {
            chains[scopeName]?.remove(listOfScopes)
        }
    }

    fun get(name: String): SimplyDIScope? = scopes[name]

    fun destroyScope(name: String): SimplyDIScope? {
        val scope = scopes.remove(name)
        scope?.close()
        chains.remove(name)
        return scope
    }

    fun allScopes(): Map<String, SimplyDIScope> = scopes.toMap()
}
