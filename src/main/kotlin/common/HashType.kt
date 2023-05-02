package common

enum class HashType
{MD5, SHA1, SHA256;

	fun toDigestName(): String =
		when (this)  {
			MD5 -> "MD5"
			SHA1 -> "SHA-1"
			SHA256 -> "SHA-256"
		}
}
