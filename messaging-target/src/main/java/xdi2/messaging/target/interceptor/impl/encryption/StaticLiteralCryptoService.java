package xdi2.messaging.target.interceptor.impl.encryption;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * A LiteralCryptoService that performs encryption and decryption using a statically
 * configured AES secret key.
 */
public class StaticLiteralCryptoService extends AbstractLiteralCryptoService implements LiteralCryptoService {

	// TODO: ECB is insecure. should switch to CBC and support an initialization vector

	public static final String DEFAULT_ALGORITHM = "AES";
	public static final String DEFAULT_TRANSFORMATION = "AES/ECB/PKCS5Padding";

	private String secretKeyString;
	private String algorithm;
	private String transformation;

	private SecretKey secretKey;

	public StaticLiteralCryptoService() {

		this.secretKeyString = null;
		this.algorithm = DEFAULT_ALGORITHM;
		this.transformation = DEFAULT_TRANSFORMATION;
	}

	/*
	 * Instance methods
	 */

	@Override
	public void init() throws Exception {

		if (this.getSecretKeyString() == null) throw new NullPointerException("No secret key string.");

		this.secretKey = new SecretKeySpec(Base64.decodeBase64(this.getSecretKeyString().getBytes(StandardCharsets.UTF_8)), this.getAlgorithm());
	}

	@Override
	public void shutdown() throws Exception {

		this.secretKey = null;
	}

	@Override
	public String encryptLiteralDataString(String literalDataString) throws Exception {

		String encryptedLiteralDataString;

		Cipher cipher = Cipher.getInstance(this.getTransformation());
		cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
		byte[] encryptedLiteralDataBytes = cipher.doFinal(literalDataString.getBytes(StandardCharsets.UTF_8));
		encryptedLiteralDataString = new String(Base64.encodeBase64(encryptedLiteralDataBytes), StandardCharsets.UTF_8);

		return encryptedLiteralDataString;
	}

	@Override
	public String decryptLiteralDataString(String encryptedLiteralDataString) throws Exception {

		String literalDataString;

		Cipher cipher = Cipher.getInstance(this.getTransformation());
		cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
		byte[] literalDataBytes = cipher.doFinal(Base64.decodeBase64(encryptedLiteralDataString.getBytes(StandardCharsets.UTF_8)));
		literalDataString = new String(literalDataBytes, StandardCharsets.UTF_8);

		return literalDataString;
	}

	/*
	 * Getters and setters
	 */

	public String getSecretKeyString() {

		return this.secretKeyString;
	}

	public void setSecretKeyString(String secretKeyString) {

		this.secretKeyString = secretKeyString;
	}

	public String getAlgorithm() {

		return this.algorithm;
	}

	public void setAlgorithm(String algorithm) {

		this.algorithm = algorithm;
	}

	public String getTransformation() {

		return this.transformation;
	}

	public void setTransformation(String transformation) {

		this.transformation = transformation;
	}
}
