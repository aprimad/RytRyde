package com.example.rytryde.utils;

import com.facebook.android.crypto.keychain.SecureRandomFix;
import com.facebook.crypto.cipher.NativeGCMCipher;
import com.facebook.crypto.exception.KeyChainException;
import com.facebook.crypto.keychain.KeyChain;
import com.facebook.crypto.mac.NativeMac;

import java.security.SecureRandom;
import java.util.Arrays;

public class CustomKeyChain implements KeyChain {

    private static final SecureRandomFix sSecureRandomFix = new SecureRandomFix();
    private final SecureRandom mSecureRandom;
    protected byte[] mCipherKey;
    protected boolean mSetCipherKey;

    protected byte[] mMacKey;
    protected boolean mSetMacKey;
    private String key = null;

    public CustomKeyChain(String key) {
        mSecureRandom = new SecureRandom();
        this.key = key;
    }

    @Override
    public synchronized byte[] getCipherKey() throws KeyChainException {
        if (!mSetCipherKey) {
            mCipherKey = maybeGenerateKey(NativeGCMCipher.KEY_LENGTH);
        }
        mSetCipherKey = true;
        return mCipherKey;
    }

    @Override
    public byte[] getMacKey() throws KeyChainException {
        if (!mSetMacKey) {
            mMacKey = maybeGenerateKey(NativeMac.KEY_LENGTH);
        }
        mSetMacKey = true;
        return mMacKey;
    }

    @Override
    public byte[] getNewIV() throws KeyChainException {
        sSecureRandomFix.tryApplyFixes();
        byte[] iv = new byte[NativeGCMCipher.IV_LENGTH];
        mSecureRandom.nextBytes(iv);
        return iv;
    }

    @Override
    public synchronized void destroyKeys() {
        mSetCipherKey = false;
        mSetMacKey = false;
        Arrays.fill(mCipherKey, (byte) 0);
        Arrays.fill(mMacKey, (byte) 0);
        mCipherKey = null;
        mMacKey = null;
    }

    /**
     * Generates a key associated with a preference.
     */
    private byte[] maybeGenerateKey(int length) throws KeyChainException {

        String key;
        if (length == NativeGCMCipher.KEY_LENGTH)
            key = this.key;
        else if (length == NativeMac.KEY_LENGTH)
            key = (this.key + this.key + this.key + this.key);
        else
            key = this.key;

        return key.getBytes();

    }

}
