package stp.system;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public abstract class STPSecurityHelper {
	
	protected STPSecurityHelper() {
		
	}
	
	private static final String CERTIFICATE_TYPE = "X.509";
	private static final String SSL_PROTOCOL_TLS = "TLS";
	
	public static SSLContext getSSLContextTrustingAll() throws STPException {
		try {
			final TrustManager[] trustManagers = new TrustManager[] { new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
				
				@Override
				public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
					
				}
				
				@Override
				public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
					
				}
			} };
			
			final SSLContext sslContext = SSLContext.getInstance(SSL_PROTOCOL_TLS);
			sslContext.init(null, trustManagers, null);
			
			return sslContext;
		} catch (final KeyManagementException | NoSuchAlgorithmException exception) {
			throw new STPException(STPException.EXCEPTION_CODE_SECURITY_EXCEPTION, exception);
		}
	}
	
	public static SSLContext getSSLContextForCertificate(final InputStream certificateInputStream, final String certificatePassword) throws STPException {
		try {
			final CertificateFactory certificateFactory = CertificateFactory.getInstance(CERTIFICATE_TYPE);
			final X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(certificateInputStream);
			
			TrustManager[] trustManagers = new TrustManager[] { new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
				
				@Override
				public void checkClientTrusted(final X509Certificate[] x509Certificates, final String authType) {
					
				}
				
				@Override
				public void checkServerTrusted(final X509Certificate[] x509Certificates, final String authType) throws CertificateException {
					if (x509Certificates.length == 0) {
						throw new CertificateException("Invalid x509Certificates length");
					}
					
					if (!Arrays.equals(x509Certificates[0].getPublicKey().getEncoded(), x509Certificate.getPublicKey().getEncoded())) {
						throw new CertificateException("Invalid peer certificate");
					}
				}
			} };
			
			final SSLContext sslContext = SSLContext.getInstance(SSL_PROTOCOL_TLS);
			sslContext.init(null, trustManagers, null);
			
			return sslContext;
		} catch (final NoSuchAlgorithmException | CertificateException | KeyManagementException exception) {
			throw new STPException(STPException.EXCEPTION_CODE_SECURITY_EXCEPTION, exception);
		}
	}
	
	public static SSLContext getSSLContextForKeyStore(final InputStream keyStoreInputStream, final String keyStorePassword) throws STPException {
		try {
			final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(keyStoreInputStream, keyStorePassword.toCharArray());
			
			final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());
			
			final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(keyStore);
			
			final SSLContext sslContext = SSLContext.getInstance(SSL_PROTOCOL_TLS);
			
			final TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
			
			sslContext.init(keyManagerFactory.getKeyManagers(), trustManagers, null);
			
			return sslContext;
		} catch (final IOException | KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException | CertificateException | KeyManagementException exception) {
			throw new STPException(STPException.EXCEPTION_CODE_SECURITY_EXCEPTION, exception);
		}
	}
}
