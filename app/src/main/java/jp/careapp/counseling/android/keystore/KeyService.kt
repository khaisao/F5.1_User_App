package jp.careapp.counseling.android.keystore

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import jp.careapp.counseling.android.utils.Define
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.MGF1ParameterSpec
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyService @Inject constructor() {

    companion object {
        private const val KEY_PROVIDER = "AndroidKeyStore"
        private const val ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
    }

    private lateinit var keystore: KeyStore

    /**
     * KeyStoreをロードする。
     */
    fun prepareKeyStore(): KeyService {
        try {
            // Keystore.getInstanceの引数に"AndroidKeyStore"を指定し、Android KeyStoreのインスタンスを取得する。
            // このインスタンスはフィールドにAndroidKeyStoreSpiのインスタンスを保持する。
            keystore =
                KeyStore.getInstance(KEY_PROVIDER)
            // Android KeyStoreをロードする。内部のAndroidKeystoreSpiがこのメソッドにより初期化されるため、この処理が必要
            keystore.load(null)
            createNewKey(keystore)
        } catch (ex: Exception) {
            Timber.e(ex.toString())
        }

        return this
    }

    /**
     * ペア鍵を作成する。
     *
     * @param keyStore keyStore
     */
    private fun createNewKey(keyStore: KeyStore) {
        try {
            // すでにKeyStoreに同様の別名が存在する場合はスキップする。
            if (!keyStore.containsAlias(Define.KEY_ALIAS)) {
                // 鍵ペアを作成するためのKeyPairGeneratorのインスタンスを取得する
                // 引数のproviderに"AndroidKeyStore"を指定することで、
                // Android Keystoreに鍵ペアを作成するKeyPairGeneratorSpiインスタンスを取得する
                val keyPairGenerator: KeyPairGenerator = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_RSA,
                    KEY_PROVIDER
                )
                // 鍵ペア(公開鍵、秘密鍵)を作成するためのスペックを設定
                keyPairGenerator.initialize(
                    KeyGenParameterSpec.Builder( // この鍵は、暗号化・復号を目的として作成しますということを明示してます
                        // Android KeyStoreからエントリーを取得する際はこのエイリアスを使用する。
                        Define.KEY_ALIAS,  // 鍵の使用目的を指定する。
                        KeyProperties.PURPOSE_DECRYPT
                    ) // 使用するダイジェストのアルゴリズムをSHA-256&SHA512にする。これ以外のダイジェストアルゴリズムの使用は拒否される。
                        .setDigests(
                            KeyProperties.DIGEST_SHA256,
                            KeyProperties.DIGEST_SHA512
                        ) // 使用するパディングモードの指定。RSA鍵ペアを生成する場合は指定必須。指定したパディングモード以外は拒否される。
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                        .build()
                )
                // 鍵ペアを作成する。
                keyPairGenerator.generateKeyPair()
            }
        } catch (ex: java.lang.Exception) {
            Timber.e(ex.toString())
        }
    }

    /**
     * テキストを暗号化する。
     *
     * @param alias     alias
     * @param plainText 平文
     */
    fun encrypt(alias: String?, plainText: String): String? {
        var encryptText: String? = null
        try {
            // 公開鍵を取得する。
            val publicKey: PublicKey = keystore.getCertificate(alias).publicKey

            //
            val spec = OAEPParameterSpec(
                "SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT
            )
            // 256 ビット鍵を使用する CBC モードまたは GCM モードの AES（AES/GCM/NoPadding）
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, publicKey, spec)
            val outputStream = ByteArrayOutputStream()
            // CipherOutputStreamに暗号データが出力される。
            val cipherOutputStream = CipherOutputStream(
                outputStream, cipher
            )
            cipherOutputStream.write(plainText.toByteArray(StandardCharsets.UTF_8))
            cipherOutputStream.close()
            val bytes = outputStream.toByteArray()
            encryptText = Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (ex: java.lang.Exception) {
            Timber.e(ex.toString())
        }
        return encryptText
    }

    /**
     * テキストを復号化する。
     *
     * @param alias         alias
     * @param encryptedText 暗号文
     */
    fun decrypt(alias: String?, encryptedText: String?): String? {
        var plainText: String? = null
        try {
            // 秘密鍵を取得
            val privateKey = keystore.getKey(alias, null) as PrivateKey

            // 256 ビット鍵を使用する CBC モードまたは GCM モードの AES（AES/GCM/NoPadding）
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, privateKey)

            // CipherInputStreamに暗号データが読み込まれるされる。
            val cipherInputStream = CipherInputStream(
                ByteArrayInputStream(Base64.decode(encryptedText, Base64.DEFAULT)), cipher
            )
            val outputStream = ByteArrayOutputStream()
            var b: Int
            while (cipherInputStream.read().also { b = it } != -1) {
                outputStream.write(b)
            }
            outputStream.close()
            plainText = outputStream.toString("UTF-8")
        } catch (ex: java.lang.Exception) {
            Timber.e(ex.toString())
        }
        return plainText
    }
}