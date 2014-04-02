package com.mobivery.utils;

/**
 * Created by mobivery on 26/03/14.
 */

import android.util.Log;

import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class CustomTrustManager implements X509TrustManager {

    protected ArrayList<X509TrustManager> x509TrustManagers = new ArrayList<X509TrustManager>();

    /**
     * Allows you to trust certificates from additional KeyStores in addition to
     * the default KeyStore
     * 'additional KeyStores' means certs locally stored. The method to load them is not implemented here.
     * http://stackoverflow.com/a/6378872
     */
    protected CustomTrustManager(KeyStore... additionalkeyStores) {
        final ArrayList<TrustManagerFactory> factories = new ArrayList<TrustManagerFactory>();

        try {
            // The default TrustManager with default keyStore
            final TrustManagerFactory original = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            original.init((KeyStore) null);
            factories.add(original);

            for( KeyStore keyStore : additionalkeyStores ) {
                final TrustManagerFactory additionalCerts = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                additionalCerts.init(keyStore);
                factories.add(additionalCerts);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        /*
         * Iterate over the returned trustmanagers, and hold on
         * to any that are X509TrustManagers
         */
        for (TrustManagerFactory tmf : factories) {
            for (TrustManager tm : tmf.getTrustManagers()) {
                if (tm instanceof X509TrustManager) {
                    x509TrustManagers.add((X509TrustManager) tm);
                }
            }
        }

        if( x509TrustManagers.size()==0 ) {
            throw new RuntimeException("Couldn't find any X509TrustManagers");
        }
    }

    /*
     * Delegate to the default trust manager.
     */
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        final X509TrustManager defaultX509TrustManager = x509TrustManagers.get(0);
        defaultX509TrustManager.checkClientTrusted(chain, authType);
    }

    /*
     * Loop over the trust managers until we find one that accepts our server
     */
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        for( X509TrustManager tm : x509TrustManagers ) {
            try {
                tm.checkServerTrusted(chain,authType);
                return;
            } catch( CertificateException e ) {
                // ignore
            }
        }
        throw new CertificateException();
    }

    public X509Certificate[] getAcceptedIssuers() {
        final ArrayList<X509Certificate> list = new ArrayList<X509Certificate>();
        for( X509TrustManager tm : x509TrustManagers ) {
            list.addAll(Arrays.asList(tm.getAcceptedIssuers()));
        }
        return list.toArray(new X509Certificate[list.size()]);
    }
}