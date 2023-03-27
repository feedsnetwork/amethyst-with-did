package com.vitorpamplona.amethyst.service;

import android.os.Environment;
import android.util.Log;

import org.elastos.did.DID;
import org.elastos.did.DIDBackend;
import org.elastos.did.DIDDocument;
import org.elastos.did.DIDStore;
import org.elastos.did.Mnemonic;
import org.elastos.did.RootIdentity;
import org.elastos.did.exception.DIDException;
import org.elastos.did.exception.DIDResolveException;
import org.elastos.did.exception.DIDStoreException;
import org.elastos.did.exception.MnemonicException;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DIDHelper {
    private static final String TAG = "wangran";
    private static DID selfDid = null;

    private static String passphrase = "";
    private static String storepass = "mypassword";
    private static String childStorePath = "did";
    private static DIDStore store;

    private static DIDDocument currentDIDDocument;

    public DIDDocument createNewDid() throws DIDException {
        //init DIDBakckend
        initDIDBakckend();

        //init RootIdentity
        initRootIdentity();

        //create did
        return initDid();
    }


    private void initDIDBakckend(){
        String rpcEndpoint = "mainnet";
        AssistAdapter assistAdapter = new AssistAdapter(rpcEndpoint);
        DIDBackend.initialize(assistAdapter);
    }

    private void initRootIdentity(){
        try{
            File storefile = Environment.getExternalStorageDirectory();
//            File storefile = Environment.getExternalStorageDirectory();
            Log.d("wangran", "initRootIdentity: storefile0000AAA====>"+storefile);
            File storePath = new File(storefile.getAbsolutePath(), DIDHelper.childStorePath);
//            deleteFile(storePath);

            storePath.mkdirs();

            Log.d("wangran", "initRootIdentity: storePath1111AAA====>"+storePath);
            store = DIDStore.open(storePath);
            Log.d("wangran", "aaaaaaaaaaaaaa");
            if (store.containsRootIdentities()){
                Log.d("wangran", "bbbbbbbbbbbbb");
                return; // Already exists
            }
            Log.d("wangran", "cccccccccc");
            Mnemonic mg = Mnemonic.getInstance();
            Log.d("wangran", "ddddddddddddd");
            String mnemonic = mg.generate();

            Log.d("wangran", "initRootIdentity: ====>");
            Log.d("wangran", "initRootIdentity: mnemonic ====>"+mnemonic);
            Log.d("wangran", "initRootIdentity: passphrase ====>"+passphrase);
            Log.d("wangran", "initRootIdentity: password====>"+storepass);

            RootIdentity.create(mnemonic, passphrase, store, storepass);

        } catch (DIDStoreException e) {
            throw new RuntimeException(e);
        } catch (MnemonicException e) {
            throw new RuntimeException(e);
        }
    }

    public DIDDocument initDid() throws DIDException {
        // Check the DID store already contains owner's DID(with private key).
        List<DID> dids = store.listDids((did) -> {
            try {
                return (did.getMetadata().getAlias().equals("me") &&
                        store.containsPrivateKeys(did));
            } catch (DIDException e) {
                return false;
            }
        });

        if (dids.size() > 0) {
            // Already has DID
            DID myDid = dids.get(0);
            Log.d("wangran", "initDid: "+myDid);
            System.out.println("Using existing DID: " + myDid);

            return null;
        }

        RootIdentity id = store.loadRootIdentity();
        DIDDocument doc = id.newDid(storepass);
        Log.d("wangran", "initDid: DIDDocument"+doc);
        doc.getMetadata().setAlias("me");
        Log.d("wangran", "initDid: "+doc.getSubject().toString());
        doc.publish(storepass);
        DID myDid= doc.getSubject();
        Log.d("wangran", "initDid: "+myDid);
        System.out.println("Created the new DID : " + myDid);
        return doc;
    }

    public String restore(String mnemonic) throws DIDException {
        initDIDBakckend();
        return restore(mnemonic, passphrase);
    }
    public String restore(String mnemonic, String passphrase) throws DIDException {
        File storefile = Environment.getExternalStorageDirectory();
        Log.d("wangran", "initRootIdentity: storefile1111====>"+storefile);
        Log.d("wangran", "------------------");
        File storePath = new File(storefile.getAbsolutePath(), DIDHelper.childStorePath);
        Log.d("wangran", "000000000000");
        deleteFile(storePath);
        Log.d("wangran", "1111111111");
        DIDStore store = DIDStore.open(storePath);
        Log.d("wangran", "2222222222222222");

        RootIdentity id = RootIdentity.create(mnemonic, passphrase, store, storepass);
        Log.d("wangran", "333333333333333");
        id.synchronize();
        Log.d("wangran", "44444444444444");
        List<DID> dids = store.listDids();
        Log.d(TAG, dids.size() + " DIDs restored.");
        Log.d(TAG, dids.get(0).toString() + " dids.get(0).toString()");
        if (dids.size() > 0) {
            for (DID did : dids) {
                Log.d(TAG, "DID is "+did.toString());
                Log.d(TAG, "DIDDocument is "+store.loadDid(did).toString());
            }
        } else {
            Log.d(TAG, "No dids restored.");
        }

        return dids.get(0).toString();
    }

    private static void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            for (File child : children)
                deleteFile(child);
        }

        file.delete();
    }


    public static void loadDIDStore(String didString){
        try {
            File storefile = Environment.getExternalStorageDirectory();
            File storePath = new File(storefile.getAbsolutePath(), DIDHelper.childStorePath);
            DIDStore didStore = DIDStore.open(storePath);
            currentDIDDocument = didStore.loadDid(didString);

            Log.d(TAG, "loadDIDStore: ====1111====="+currentDIDDocument.getSubject());
        } catch (DIDStoreException e) {
            Log.d(TAG, "loadDIDStore: ====22222222222222222====="+e.toString());
            throw new RuntimeException(e);
        }
    }

    public static String signData(byte[] data){
        String result = "";
        try{
            if (currentDIDDocument == null){
                Log.d(TAG, "signData: 11111 null");
            }else{
                Log.d(TAG, "signData: 2222 not null");
            }
            Log.d(TAG, "signData: did is "+currentDIDDocument.getSubject());
            Log.d(TAG, "signData: id is "+new String(data));

            result = currentDIDDocument.sign(storepass,data);

            Log.d(TAG, "signData: sign result is "+result);
        } catch (DIDStoreException e) {
            throw new RuntimeException(e);
        }

        return result;
    }


    public static void verify(String sig, byte[] data){
        if (currentDIDDocument == null){
            Log.d(TAG, "signData: 11111 null");
        }else{
            Log.d(TAG, "signData: 2222 not null");
        }
        Log.d(TAG, "signData: did is "+currentDIDDocument.getSubject());
        Log.d(TAG, "signData: id is "+new String(data));

        boolean isVerify = currentDIDDocument.verify(sig, data);
        Log.d(TAG, "verify: isVerify "+isVerify);
    }

    private static DID getDid(String didStr){
       //"did:elastos:icJ4z2DULrHEzYSvjKNJpKyhqFDxvYV7pN"
       if (selfDid == null){
           selfDid = new DID(didStr);
       }
       return selfDid;
   }

   public static DIDDocument getDidDocument(String didStr){
       DID did = getDid(didStr);
       DIDDocument document = null;
       try {
           document = did.resolve(true);
       } catch (DIDResolveException e) {
           throw new RuntimeException(e);
       }
       return document;
   }

   public static String signData(String didStr, String data){

       return null;
   }

   public static String signData(String didStr, byte[]... data){
       DIDDocument didDocument = getDidDocument(didStr);
       String result = "";
       try{
           result = didDocument.sign(storepass,data);
       } catch (DIDStoreException e) {
           throw new RuntimeException(e);
       }

       return result;
   }

    public static String signData(DIDDocument didDocument, byte[]... data){
        String result = "";
        try{
            result = didDocument.sign(storepass,data);
        } catch (DIDStoreException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

   public static byte[] getDefaultPublicKey(String didString){
    return getBytePublicKey(didString);
   }

   private static String getPublicKey(String didString){
       DIDDocument.PublicKey publicKey = getDidDocument(didString).getDefaultPublicKey();
       String pkString = publicKey.getPublicKeyBase58();
       return pkString;
   }

    private static byte[] getBytePublicKey(String didString){
       DIDDocument.PublicKey publicKey = getDidDocument(didString).getDefaultPublicKey();
       return publicKey.getPublicKeyBytes();
   }



    private void test(){
        //init DIDBakckend
        initDIDBakckend();

        //init RootIdentity

        //init DID
    }
    private static final int WRITE_REQUEST_CODE = 43;
//    private void createFile(String mimeType, String fileName) {
//        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
//
//        // Filter to only show results that can be "opened", such as
//        // a file (as opposed to a list of contacts or timezones).
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//        // Create a file with the requested MIME type.
//        intent.setType(mimeType);
//        intent.putExtra(Intent.EXTRA_TITLE, fileName);
//        Activity.startActivityForResult(intent, WRITE_REQUEST_CODE);
//    }




    private void publish(DIDDocument document){
        AssistAdapter assistAdapter = new AssistAdapter("testnet");
//        document.publish(storepass, assistAdapter);
    }
    private void createIdTransaction(String payload){
    }

    private void assistPerformRequest(String endPointUrl, String payload){


    }

}
