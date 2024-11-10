package su.vi.simply.di.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import su.vi.simply.di.annotations.Dependency
import java.util.Locale

public class AutoGenerateProcessor(
	private val codeGenerator: CodeGenerator,
	private val logger: KSPLogger
) : SymbolProcessor {

	private val mutableBindsMap: MutableMap<String, BindModel> = mutableMapOf()
	private val mutableScopeList: MutableMap<String, ScopeModel> = mutableMapOf()

	override fun process(resolver: Resolver): List<KSAnnotated> {
		loggerr = logger
		println("1!!!!!!!!!!!!!!!!!!!!!!!!!")
		logger.warn("start process !!!")
		resolver.getSymbolsWithAnnotation(Dependency::class.qualifiedName ?: return emptyList())
			.filterIsInstance<KSClassDeclaration>()
			.forEach { element ->
				logger.warn("definition(class) -> $element", element)
				logger.warn("!!!!!!!!!!!!!!!!!!!!!!!!!")
				val ksClassDeclaration = (element as? KSClassDeclaration) ?: return@forEach

				val packageName = ksClassDeclaration.packageName.asString()
				val className = ksClassDeclaration.simpleName.asString()
				val qualifier =  ksClassDeclaration.getStringQualifier()
				val annotations = element.getAnnotations()
				annotations.firstNotNullOfOrNull { (annotationName, annotation) ->
					createClassDefinition(
						annotation = annotation,
						ksClassDeclaration = ksClassDeclaration,
						annotationName = annotationName,
						packageName = packageName,
						qualifier = qualifier,
						className = className,
						annotations = annotations
					)
				}

			}

/*		val symbols = resolver.getSymbolsWithAnnotation(Dependency::class.qualifiedName!!)
		symbols.filterIsInstance<KSClassDeclaration>().forEach { classDeclaration ->
			println("2!!!!!!!!!!!!!!!!!!!!!!!!!")
			//generateCodeForClass(classDeclaration)
		}*/


		mutableScopeList.forEach {
			logger.warn("FIN - $it")
		}
		return emptyList()
	}

	private fun createClassDefinition(
		annotation: KSAnnotation,
		ksClassDeclaration: KSClassDeclaration,
		annotationName: String,
		packageName: String,
		qualifier: String?,
		className: String,
		annotations: Map<String, KSAnnotation>,
	): Unit? {


		val defaultBindings = ksClassDeclaration.superTypes.map { it.resolve().declaration }.toList()
		logger.warn("????????????????????")
		logger.warn("$packageName")
		logger.warn("$className")
		logger.warn("$defaultBindings")

		val allBindings: Map<String, BindModel> = defaultBindings.associate {
			it.simpleName.asString() + it.packageName.asString() to BindModel(
				packageName = it.packageName.asString(),
				simpleName = it.simpleName.asString()
			)
		}
		mutableBindsMap.putAll(allBindings)

		logger.warn("allBindings MAP- $allBindings")
		allBindings.forEach {
			logger.warn("allBindings - $it")
		}

		var ctorParams = ksClassDeclaration.primaryConstructor?.parameters?.getConstructorParameters() ?: listOf()
		//logger.warn("typeParameters - ${ksClassDeclaration.getAllProperties()}")
		ksClassDeclaration.getAllProperties().forEach {
			logger.warn("typeParameters - ${it.packageName}")
			logger.warn("typeParameters - ${it.simpleName}")
		}

		val ctorParamsSize = ctorParams.size
		ctorParams = ctorParams.map {
			if (mutableBindsMap.containsKey(it.packageName+it.simpleName) ) {
				it.copy(packageName = mutableBindsMap[it.packageName+it.simpleName]?.packageName)
			} else {
				it
			}
		}
		ctorParams.forEach {
			logger.warn("ctorParams - $it")
		}



		//DEPENDENCY
		val isCreateOnStart: Boolean = annotation.arguments.firstOrNull { it.name?.asString() == "isCreateOnStart" }?.value as? Boolean ?: false
		val dependency = DependencyModel(
			packageName = packageName,
			simpleName = className,
			isCreateOnStart = isCreateOnStart,
			properties = ctorParams,
			binds = allBindings.values.toList(),
		)

		//SCOPE
		val scopeName: String = annotation.arguments.firstOrNull { it.name?.asString() == "scopeName" }?.value as String? ?: ""
		var scopeNames: List<String> = annotation.arguments.firstOrNull { it.name?.asString() == "scopeNames" }?.value.toString().replace("[","").replace("]","").split(",")
		logger.warn("scopeNamesscopeNames - ${annotation.arguments.firstOrNull { it.name?.asString() == "scopeNames" }?.value.toString()}")
		val isSearchInScope: Boolean = annotation.arguments.firstOrNull { it.name?.asString() == "isSearchInScope" }?.value as? Boolean ?: false
		val isFullLogger: Boolean = annotation.arguments.firstOrNull { it.name?.asString() == "isFullLogger" }?.value as? Boolean ?: false
		val chainWith: List<String> = annotation.arguments.firstOrNull { it.name?.asString() == "chainWith" }?.value.toString().replace("[","").replace("]","").split(",")


		scopeNames = scopeNames.plus(scopeName)

		scopeNames.forEach { name ->
			if (name != "") {
				logger.warn("addScope - $name")
				addScope(
					ScopeModel(
						scopeName = name,
						isSearchInScope = isSearchInScope,
						isFullLogger = isFullLogger,
						chainedWith = chainWith.toList(),
						mutableDependencyList = listOf(dependency)
					)
				)
			}
		}



		val fileName = "${className}Generated"

		val file = codeGenerator.createNewFile(
			Dependencies(false, ksClassDeclaration.containingFile ?: return null),
			packageName,
			fileName
		)

		file.writer().use { writer ->
			writer.write("package $packageName\n\n")
			writer.write("import su.vi.simply.di.core.utils.initializeSimplyDIContainer\n\n")
			writer.write("class $fileName {\n")
			writer.write("    fun greet() = println(\"!Hello from $fileName!\")\n")
			writer.write("    fun initDependencies(): Unit {\n")
			writer.write("        \n")
			writer.write("        initializeSimplyDIContainer(scopeName = \"$scopeName\", isSearchInScope = $isSearchInScope) {\n")
			if (ctorParamsSize == 0) {
				writer.write("            $packageName.$className()\n")
			} else {
				writer.write("            $packageName.$className(\n")
				repeat(ctorParamsSize) {
					writer.write("                getDependency(),\n")
				}
				writer.write("            )\n")

			}
			writer.write("        }\n")
			writer.write("        \n")
			writer.write("    }\n")
			writer.write("    //annotationName - $annotationName\n")
			writer.write("    //packageName - $packageName\n")
			writer.write("    //qualifier - $qualifier\n")
			writer.write("    //className - $className\n")
			writer.write("    //annotations - $annotations\n")
			writer.write("    //scopeName - $scopeName\n")
			writer.write("    //isCreateOnStart - $isCreateOnStart\n")
			writer.write("    //allBindings - $allBindings\n")
			writer.write("    //chainWith - ${chainWith.toList()}\n")
			writer.write("    //scopeNames - ${scopeNames.toList()}\n")
			writer.write("    //ctorParams - ${ctorParams}\n")
			writer.write("}\n")
		}
		return null
	}

	@Synchronized
	private fun addScope(scopeModel: ScopeModel) {
		val existsScope = mutableScopeList[scopeModel.scopeName]
		mutableScopeList[scopeModel.scopeName] = existsScope?.copy(
			chainedWith = (existsScope.chainedWith + scopeModel.chainedWith).distinct(),
			mutableDependencyList = (existsScope.mutableDependencyList + scopeModel.mutableDependencyList).distinct()
		) ?: scopeModel
	}

	private fun generateCodeForClass(classDeclaration: KSClassDeclaration) {

		val packageName = classDeclaration.packageName.asString()
		val className = classDeclaration.simpleName.asString()
		val fileName = "${className}Generated"

		val file = codeGenerator.createNewFile(
			Dependencies(false, classDeclaration.containingFile ?: return),
			packageName,
			fileName
		)

		file.writer().use { writer ->
			writer.write("package $packageName\n\n")
			writer.write("class $fileName {\n")
			writer.write("    fun greet() = println(\"!Hello from $fileName!\")\n")
			writer.write("}\n")
		}
	}


}

public var loggerr: KSPLogger? = null
internal fun KSAnnotated.getStringQualifier(): String? {
	val qualifierAnnotation = annotations.firstOrNull { a -> a.shortName.asString() == "Named" } ?: return null
	return qualifierAnnotation.arguments.getValueArgument() ?: error("Can't get value for @Named")
}

internal fun List<KSValueArgument>.getValueArgument(): String? {
	return firstOrNull { a -> a.name?.asString() == "value" }?.value as? String?
}

internal fun KSAnnotated.getAnnotations(): Map<String, KSAnnotation> {
	return annotations
		.filter { it.shortName.asString().lowercase(Locale.getDefault()) ==  Dependency::class.simpleName?.lowercase(Locale.getDefault()).toString()}
		.map { annotation -> Pair(annotation.shortName.asString(), annotation) }
		.toMap()
}

internal fun List<KSValueParameter>.getConstructorParameters(): List<PropertyModel> {
	return mapNotNull { param -> getConstructorParameter(param) }
}

private fun getConstructorParameter(param: KSValueParameter): PropertyModel? {
	val firstAnnotation = param.annotations.firstOrNull()

	val annotationName = firstAnnotation?.shortName?.asString()
	val annotationValue = firstAnnotation?.arguments?.getValueArgument()
	val fileExt = param.location as? FileLocation
	val asdasd = fileExt?.filePath?.substringBeforeLast(".")?.substringAfter("main/java/")?.replace("/", ".")
	loggerr?.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!")
	loggerr?.warn("firstAnnotation - ${firstAnnotation}")
	loggerr?.warn("annotationName - ${annotationName}")
	loggerr?.warn("annotationValue - ${annotationValue}")
	loggerr?.warn("name - ${param.name?.getShortName()}")
	loggerr?.warn("name.asString - ${param.name?.asString()}")
	loggerr?.warn("type - ${param.type}")
	loggerr?.warn("type.element - ${param.type.element}")

	loggerr?.warn("type.resolve - ${param.type.resolve()}")
	loggerr?.warn("type.resolve.annotations - ${param.type.resolve().annotations}")
	loggerr?.warn("type.resolve.arguments - ${param.type.resolve().arguments}")
	loggerr?.warn("type.resolve.nullability - ${param.type.resolve().nullability}")
	loggerr?.warn("type.resolve.declaration - ${param.type.resolve().declaration}")
	loggerr?.warn("type.resolve.isMutabilityFlexible - ${param.type.resolve().isMutabilityFlexible()}")
	loggerr?.warn("type.resolve.isCovarianceFlexible - ${param.type.resolve().isCovarianceFlexible()}")
	loggerr?.warn("type.resolve.starProjection - ${param.type.resolve().starProjection()}")
	loggerr?.warn("type.resolve.isMarkedNullable - ${param.type.resolve().isMarkedNullable}")
	loggerr?.warn("type.resolve.isError - ${param.type.resolve().isError}")
	loggerr?.warn("type.resolve.isFunctionType - ${param.type.resolve().isFunctionType}")
	loggerr?.warn("type.resolve.isSuspendFunctionType - ${param.type.resolve().isSuspendFunctionType}")

	loggerr?.warn("type.modifiers - ${param.type.modifiers}")
	loggerr?.warn("isVararg - ${param.isVararg}")
	loggerr?.warn("isNoInline - ${param.isNoInline}")
	loggerr?.warn("isCrossInline - ${param.isCrossInline}")
	loggerr?.warn("isVal - ${param.isVal}")
	loggerr?.warn("isVar - ${param.isVar}")
	loggerr?.warn("hasDefault - ${param.hasDefault}")
	loggerr?.warn("origin - ${param.origin}")
	loggerr?.warn("location - ${param.location}")
	loggerr?.warn("location.mod!!! - ${
		asdasd
	}")
	loggerr?.warn("parent - ${param.parent}")
	loggerr?.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!")

	val type = param.type.toString() //for findDependencies
	loggerr?.warn("TYPE - ${type}")
	val shortName = param.name?.getShortName() ?: return null
	val isNullable = param.type.resolve().isMarkedNullable
	return PropertyModel(parameterName = shortName, isNullable = isNullable, simpleName = type)
}



internal data class PropertyModel(
	val packageName: String? = null,
	val simpleName: String,
	val parameterName: String,
	val isNullable: Boolean,
) {
	fun isHavePackage() = packageName != null
}

internal data class BindModel(
	var packageName: String,
	var simpleName: String,
)

internal data class ScopeModel(
	val scopeName: String,
	val isSearchInScope: Boolean,
	val isFullLogger: Boolean,
	val chainedWith: List<String>,
	val mutableDependencyList: List<DependencyModel>
)

internal data class DependencyModel(
	val packageName: String,
	val simpleName: String,
	val isCreateOnStart: Boolean,
	val properties: List<PropertyModel> = emptyList(),
	val binds: List<BindModel> = emptyList()
)