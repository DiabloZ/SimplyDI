package su.vi.simply.di.processor

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import su.vi.simply.di.annotations.Dependency

public class AutoGenerateProcessor(
	private val codeGenerator: CodeGenerator,
	private val logger: KSPLogger
) : SymbolProcessor {

	override fun process(resolver: Resolver): List<KSAnnotated> {
		val symbols = resolver.getSymbolsWithAnnotation(Dependency::class.qualifiedName!!)

		symbols.filterIsInstance<KSClassDeclaration>().forEach { classDeclaration ->
			generateCodeForClass(classDeclaration)
		}

		return emptyList()
	}

	private fun generateCodeForClass(classDeclaration: KSClassDeclaration) {
		val packageName = classDeclaration.packageName.asString()
		val className = classDeclaration.simpleName.asString()
		val fileName = "${className}Generated"

		val file = codeGenerator.createNewFile(
			Dependencies(false, classDeclaration.containingFile!!),
			packageName,
			fileName
		)

		file.writer().use { writer ->
			writer.write("package $packageName\n\n")
			writer.write("class $fileName {\n")
			writer.write("    fun greet() = println(\"Hello from $fileName!\")\n")
			writer.write("}\n")
		}
	}


}
