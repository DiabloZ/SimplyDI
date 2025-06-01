package su.vi.sometestmodule

import su.vi.kdi.core.KDIContainer
import su.vi.kdi.core.entry_point.getDependency
import su.vi.kdi.core.entry_point.initialize

/**
 * Класс SomePublicClassFromOtherModule предоставляет доступ к хранилищу данных через контейнер зависимостей.
 *
 * @property container Контейнер зависимостей, используемый для получения экземпляра хранилища данных.
 */
object SomePublicClassFromOtherModule222 {
    private lateinit var container: KDIContainer

    /**
     * Инициализирует контейнер зависимостей и добавляет зависимость для хранилища данных.
     */
    fun init() {
        container = KDIContainer.initialize(CONTAINER_NAME20) {
            addDependency<SomeStorage2> {
                SomeStorageImpl("secret_shhhhhh...")
            }
            //addDependencyAuto<SomeStorageImpl>()
            //addChainScopes(listOfScopes = listOf(CONTAINER_NAME1, CONTAINER_NAME2))
        }
    }

    /**
     * Возвращает строку из хранилища данных.
     *
     * @return Строка из хранилища данных.
     */
    fun getOther(): String {
        val so = container.getDependency<SomeStorage2>().getOtherString()
        return so
    }
}


private val CONTAINER_NAME10 = "testSomeContainer10"
private val CONTAINER_NAME20 = "testSomeContainer20"



public interface SomeStorage2 {
	fun getSecretString(): String
	fun setOtherString(other: String)
	fun getOtherString(): String
}

internal class SomeStorageImpl(
	private val secretString: String,
) : SomeStorage2 {

	override fun getSecretString(): String = secretString

	private var _otherString = ""
	override fun setOtherString(other: String) {
		_otherString = other
	}

	override fun getOtherString(): String {
		println("THIS !!!!!!!!!!!!!! - $this")
		return _otherString
	}
}