package io.github.wulkanowy.security;



import android.annotation.TargetApi;
import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;


import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

public class Scrambler {

    public KeyStore keyStore;
    public static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    public Context context;

    public Scrambler(Context context){
        this.context = context;
    }

    public void loadKeyStore(){

        try{
            keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public ArrayList<String> getAllAliases(){

        ArrayList<String> keyAliases = new ArrayList<>();
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                keyAliases.add(aliases.nextElement());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return keyAliases;
    }

    @TargetApi(18)
    public void generateNewKey(String alias){

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        end.add(Calendar.YEAR, 1);

        try{
            if (!keyStore.containsAlias(alias)){

                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=" + alias))
                        .setSerialNumber(BigInteger.TEN)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();

                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", ANDROID_KEYSTORE);
                keyPairGenerator.initialize(spec);
                keyPairGenerator.generateKeyPair();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void deleteKey(String alias){

        try{
            keyStore.deleteEntry(alias);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public String encryptString(String alias, String text){

        try{
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias, null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

            Cipher input = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
            input.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, input);
            cipherOutputStream.write(text.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte [] vals = outputStream.toByteArray();
            return Base64.encodeToString(vals, Base64.DEFAULT);
        }
        catch (Exception e){

            e.printStackTrace();
            return text;
        }
    }

    public String decryptString(String alias, String text){

        try{
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias, null);
            RSAPrivateKey privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();

            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
            output.init(Cipher.DECRYPT_MODE, privateKey);

            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(text, Base64.DEFAULT)), output);

            ArrayList<Byte> values = new ArrayList<>();

            int nextByte;

            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte)nextByte);
            }

            Byte[] bytes = values.toArray(new Byte[values.size()]);

            return new String(ArrayUtils.toPrimitive(bytes), 0, bytes.length, "UTF-8");

        }
        catch (Exception e){
            e.printStackTrace();
            return text;
        }
    }
}
