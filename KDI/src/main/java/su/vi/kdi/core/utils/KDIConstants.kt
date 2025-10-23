package su.vi.kdi.core.utils

public object KDIConstants {
	public const val DEFAULT_SCOPE_NAME: String = "MAIN CONTAINER"
	internal const val TAG = "KDI CONTAINER"
	internal const val LOG_INIT = "🚀 Scope with name - %s has been initialized 🚀"
	internal const val LOG_INIT_CHAIN = "🔗 Created a chain with scopes - %s 🔗"
	internal const val LOG_DELETE_CHAIN = "🗑️ Deleted a chain with scopes - %s 🗑️"
	internal const val LOG_INIT_ALREADY = "⛔ Scope with name - %s has already been initialized ⛔"
	internal const val SCOPE_IS_NOT_INITIALIZED = "∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇∇\n" +
		"|||||||||||||||| 1.Scope is not initialized.\n" +
		"|||||||||||||||| 2.It is with parameter \"isSearchInScope = false\".\n" +
		"|||||||||||||||| 3.There isn't any dependency.\n" +
		"|||||||||||||||| 4.You try get a dependency from other scope.\n" +
		"|||||||||||||||| !!!WARNING scope by default is \"$DEFAULT_SCOPE_NAME\"!!! ||||||||||||||||"
	internal const val TRY_TO_CREATE_DEP_WHEN_SCOPE_IS_NOT_CREATED =
		"🔥 You try to create dependency !!!\"%s\"!!! in to scope with name !!!\"%s\"!!! but it's not created. 🔥"
	internal const val CREATE_DEP_LAZY = "➕ Added dependency by lazy - "
	internal const val GET_DEP_SINGLE = "✅ Call dependency with add to container - "
	internal const val GET_DEP_FACTORY_WITH_ERROR = "✅ Call dependency without add to container with error - "
	internal const val GET_DEP_FACTORY_NULLABLE = "✅ Call dependency without add to container with nullable - "
	internal const val NOT_FOUND_ERROR =
		"🛑 In the beginning, you need to register such a service - %s with name - %s, before calling it 🛑"
	internal const val REPLACE_ERR =
		"💡 You try to replace - \"%s\" in scope \"%s\"?\nPls try the methods - \"replaceNow\"|\"replaceLater\". 💡"

	internal const val TRY_TO_RELOAD_CONTAINER =
		"🔄 You try to reload container with name - \"%s\". 🔄"
}
