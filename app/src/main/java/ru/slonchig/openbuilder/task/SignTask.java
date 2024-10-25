package ru.slonchig.openbuilder.task;

import com.android.apksig.ApkSigner;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

public class SignTask {
    String path;
    String keyPath;

    public SignTask(String path, String keyPath) {
        this.path = path;
        this.keyPath = keyPath;
    }

    public String sign() {
        try {
            PrivateKey privateKey = getPrivateKey();
            List<X509Certificate> x509CertificateList = new ArrayList<X509Certificate>();
            InputStream inputStream = new FileInputStream(path + File.separator + "testkey.x509.pem");
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

            X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);
            x509CertificateList.add(x509Certificate);

            ApkSigner.SignerConfig.Builder signerConfig = new ApkSigner.SignerConfig.Builder(
                    "CERT",
                    privateKey,
                    x509CertificateList
            );
            List<ApkSigner.SignerConfig> signerConfigs = new ArrayList<ApkSigner.SignerConfig>();
            signerConfigs.add(signerConfig.build());
            ApkSigner.Builder builder = new ApkSigner.Builder(signerConfigs);

            builder.setInputApk(new File(path + File.separator + "aligned.apk"));
            builder.setOutputApk(new File(path + File.separator + "game.apk"));

            builder.setMinSdkVersion(21);
            builder.setCreatedBy("OpenBuilder");

            builder.setV1SigningEnabled(true);
            builder.setV2SigningEnabled(true);
            builder.setV3SigningEnabled(true);

            builder.build().sign();

            return "Done";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private PrivateKey getPrivateKey() throws Exception {
        byte[] keyContent = FileUtils.readFileToByteArray(new File(keyPath));

        return KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(keyContent));
    }
}
