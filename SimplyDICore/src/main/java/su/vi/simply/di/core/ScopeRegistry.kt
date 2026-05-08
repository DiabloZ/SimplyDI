package su.vi.simply.di.core

internal class ScopeRegistry {
    private val scopes = mutableMapOf<String, SimplyDIScope>()

    fun create(name: String, isSearchInScope: Boolean): SimplyDIScope =
        scopes.getOrPut(name) { SimplyDIScope(isSearchInScope) }

    fun get(name: String): SimplyDIScope? = scopes[name]

    fun destroyScope(name: String): SimplyDIScope? {
        val scope = scopes.remove(name)
        scope?.close()
        return scope
    }

    fun allScopes(): Map<String, SimplyDIScope> = scopes.toMap()
}
