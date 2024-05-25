
import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptionUtils {
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val KEY_SIZE = 256
    private const val IV_SIZE = 12
    private const val TAG_LENGTH = 128

    private fun getSecretKey(context: Context): SecretKey {
        val keyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = EncryptedSharedPreferences.create(
            "secret_shared_prefs",
            keyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val key = sharedPreferences.getString("secret_key", null)
        return if (key == null) {
            val keyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(KEY_SIZE)
            val secretKey = keyGenerator.generateKey()
            sharedPreferences.edit().putString("secret_key", Base64.encodeToString(secretKey.encoded, Base64.DEFAULT)).apply()
            secretKey
        } else {
            val keyBytes = Base64.decode(key, Base64.DEFAULT)
            SecretKeySpec(keyBytes, "AES")
        }
    }

    fun encrypt(context: Context, plainText: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        val secretKey = getSecretKey(context)
        val iv = ByteArray(IV_SIZE)
        SecureRandom().nextBytes(iv)
        val gcmSpec = GCMParameterSpec(TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)
        val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val ivAndCipherText = iv + cipherText
        return Base64.encodeToString(ivAndCipherText, Base64.DEFAULT)
    }

    fun decrypt(context: Context, encryptedText: String): String {
        val ivAndCipherText = Base64.decode(encryptedText, Base64.DEFAULT)
        val iv = ivAndCipherText.copyOfRange(0, IV_SIZE)
        val cipherText = ivAndCipherText.copyOfRange(IV_SIZE, ivAndCipherText.size)
        val cipher = Cipher.getInstance(ALGORITHM)
        val secretKey = getSecretKey(context)
        val gcmSpec = GCMParameterSpec(TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
        val plainText = cipher.doFinal(cipherText)
        return String(plainText, Charsets.UTF_8)
    }
}
