package xdi2.tests.core.features.signatures;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.signatures.RSASignature;
import xdi2.core.features.signatures.Signatures;
import xdi2.core.features.signatures.AESSignature;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;

public class SignaturesTest extends TestCase {

	public void testAlgorithmAndLengthAddresses() throws Exception {

		XDIAddress keyPairXDIAddress = XDIAddress.create("$sha$256$rsa$1024");
		XDIAddress symmetricKeyXDIAddress = XDIAddress.create("$sha$384$aes$256");

		assertEquals(Signatures.getDigestAlgorithm(keyPairXDIAddress), "sha");
		assertEquals(Signatures.getDigestLength(keyPairXDIAddress), Integer.valueOf(256));
		assertEquals(Signatures.getKeyAlgorithm(keyPairXDIAddress), "rsa");
		assertEquals(Signatures.getKeyLength(keyPairXDIAddress), Integer.valueOf(1024));

		assertEquals(Signatures.getDigestAlgorithm(symmetricKeyXDIAddress), "sha");
		assertEquals(Signatures.getDigestLength(symmetricKeyXDIAddress), Integer.valueOf(384));
		assertEquals(Signatures.getKeyAlgorithm(symmetricKeyXDIAddress), "aes");
		assertEquals(Signatures.getKeyLength(symmetricKeyXDIAddress), Integer.valueOf(256));

		assertEquals(Signatures.createDataTypeXDIAddress("sha", Integer.valueOf(256), "rsa", Integer.valueOf(1024)), keyPairXDIAddress);
		assertEquals(Signatures.createDataTypeXDIAddress("sha", Integer.valueOf(384), "aes", Integer.valueOf(256)), symmetricKeyXDIAddress);
	}

	public void testSignAndValidateKeyPair() throws Exception {

		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(1024);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		graph.setStatement(XDIStatement.create("=markus<#email>/&/\"markus@projectdanube.org\""));
		graph.setStatement(XDIStatement.create("=markus/#friend/=animesh"));

		ContextNode contextNode = graph.getDeepContextNode(XDIAddress.create("=markus"));

		RSASignature signature = (RSASignature) Signatures.createSignature(contextNode, RSASignature.DIGEST_ALGORITHM_SHA, Integer.valueOf(256), RSASignature.KEY_ALGORITHM_RSA, Integer.valueOf(1024), false);
		signature.setSignatureValue(privateKey);

		signature = (RSASignature) Signatures.getSignatures(contextNode).next();

		assertEquals(signature.getDigestAlgorithm(), RSASignature.DIGEST_ALGORITHM_SHA);
		assertEquals(signature.getDigestLength(), Integer.valueOf(256));
		assertEquals(signature.getKeyAlgorithm(), RSASignature.KEY_ALGORITHM_RSA);
		assertEquals(signature.getKeyLength(), Integer.valueOf(1024));

		assertEquals(signature.getAlgorithm(), "SHA256withRSA");

		assertEquals(signature.getBaseContextNode(), contextNode);

		assertTrue(signature.validateSignature(publicKey));

		contextNode.setRelation(XDIAddress.create("#friend"), XDIAddress.create("=joseph"));

		assertFalse(signature.validateSignature(publicKey));

		graph.close();
	}

	public void testSignAndValidateSymmetricKey() throws Exception {

		KeyGenerator secretKeyGen = KeyGenerator.getInstance("AES");
		secretKeyGen.init(256);
		SecretKey secretKey = secretKeyGen.generateKey(); 

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		graph.setStatement(XDIStatement.create("=markus<#email>/&/\"markus@projectdanube.org\""));
		graph.setStatement(XDIStatement.create("=markus/#friend/=animesh"));

		ContextNode contextNode = graph.getDeepContextNode(XDIAddress.create("=markus"));

		AESSignature signature = (AESSignature) Signatures.createSignature(contextNode, AESSignature.DIGEST_ALGORITHM_SHA, Integer.valueOf(384), AESSignature.KEY_ALGORITHM_AES, Integer.valueOf(256), false);
		signature.setSignatureValue(secretKey);

		signature = (AESSignature) Signatures.getSignatures(contextNode).next();

		assertEquals(signature.getDigestAlgorithm(), AESSignature.DIGEST_ALGORITHM_SHA);
		assertEquals(signature.getDigestLength(), Integer.valueOf(384));
		assertEquals(signature.getKeyAlgorithm(), AESSignature.KEY_ALGORITHM_AES);
		assertEquals(signature.getKeyLength(), Integer.valueOf(256));

		assertEquals(signature.getAlgorithm(), "HmacSHA384");

		assertEquals(signature.getBaseContextNode(), contextNode);

		assertTrue(signature.validateSignature(secretKey));

		contextNode.setRelation(XDIAddress.create("#friend"), XDIAddress.create("=joseph"));

		assertFalse(signature.validateSignature(secretKey));

		graph.close();
	}
}
