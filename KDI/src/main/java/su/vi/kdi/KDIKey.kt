package su.vi.kdi

internal class KDIKey(val type: Class<*>, val name: String? = null) {

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is KDIKey) return false

		if (type != other.type) return false
		if (name != other.name) return false

		return true
	}

	override fun hashCode(): Int {
		var result = type.hashCode()
		result = 31 * result + (name?.hashCode() ?: 0)
		return result
	}

	override fun toString(): String {
		return "Key(type=$type, name=$name)"
	}
}