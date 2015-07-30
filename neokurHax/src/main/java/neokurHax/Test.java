
package neokurHax;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.KeyAgreement;
import javax.crypto.spec.DHParameterSpec;

import org.apache.http.client.ClientProtocolException;

public class Test {

	public static void main(String[] args)
			throws ClientProtocolException, IOException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeySpecException, InvalidKeyException {

		// Create identical DHSpec for client and server
		String a = "936C4FAB070E46F88BC59F96AEA5B379AAAE236AC8AD47879F8AE42573E1DB26D46E0BDB218343F789A895E2C9B2304215855549A9574840B91F41FB9B6B5620C07278E68F8F41EBAEA899D5BE0DDE5AF6E4E5E0A24F4B5BBFEFA4144FDFCD7334DE301E5FF24D9EA5E5E7BEA53C906DB466933FCC634819B95BC2F4A1544DA4";
		BigInteger clientModulus = new BigInteger(a, 16);
		BigInteger base = BigInteger.valueOf(2l);
		DHParameterSpec dhSpec = new DHParameterSpec(clientModulus, base);

		// Generate keys for client.
		KeyPairGenerator clientKPG = KeyPairGenerator.getInstance("DH");
		clientKPG.initialize(dhSpec);
		KeyPair clientKeyPair = clientKPG.generateKeyPair();
		PublicKey clientPublicKey = clientKeyPair.getPublic();
		PrivateKey clientPrivateKey = clientKeyPair.getPrivate();
		byte[] clientPublicKeyAsBytes = clientPublicKey.getEncoded();
		// Client has sent his public at his point.

		KeyPairGenerator serverKpg = KeyPairGenerator.getInstance("DH");
		serverKpg.initialize(dhSpec);
		KeyPair serverKeypair = serverKpg.generateKeyPair();
		PublicKey serverPublicKey = serverKeypair.getPublic();
		byte[] serverPublicKeyAsBytes = serverPublicKey.getEncoded();
		X509EncodedKeySpec serverXencodedPublicKeySpec = new X509EncodedKeySpec(clientPublicKeyAsBytes);
		KeyFactory serverKeyFactory = KeyFactory.getInstance("DH");
		PublicKey receivedClientPublicKey = serverKeyFactory.generatePublic(serverXencodedPublicKeySpec);
		KeyAgreement serverKeyAggreement = KeyAgreement.getInstance("DH");
		serverKeyAggreement.init(serverKpg.generateKeyPair().getPrivate());
		serverKeyAggreement.doPhase(receivedClientPublicKey, true);
		byte[] serverSecret = serverKeyAggreement.generateSecret();
		// Server found secret, now server sending public key to client.

		X509EncodedKeySpec clientXencodedPublicKeySpec = new X509EncodedKeySpec(serverPublicKeyAsBytes);
		PublicKey receivedServerPublicKey = serverKeyFactory.generatePublic(clientXencodedPublicKeySpec);
		KeyAgreement clientKeyAggreement = KeyAgreement.getInstance("DH");
		clientKeyAggreement.init(clientPrivateKey);
		clientKeyAggreement.doPhase(receivedClientPublicKey, true);
		clientKeyAggreement.doPhase(receivedServerPublicKey, true);
		byte[] clientSecret = clientKeyAggreement.generateSecret();

		if (Arrays.equals(serverSecret, clientSecret))
			System.out.println("It works!");

	}

}
