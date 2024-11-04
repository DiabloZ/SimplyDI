package su.vi.simply.di.processor

import com.google.devtools.ksp.processing.*

public class AutoGenerateProcessorProvider : SymbolProcessorProvider {
	override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
		return AutoGenerateProcessor(
			environment.codeGenerator,
			environment.logger
		)
	}
}
