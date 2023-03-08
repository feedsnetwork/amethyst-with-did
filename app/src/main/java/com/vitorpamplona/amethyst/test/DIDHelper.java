package com.vitorpamplona.amethyst.test;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import org.elastos.did.DID;
import org.elastos.did.DIDBackend;
import org.elastos.did.DIDDocument;
import org.elastos.did.DIDStore;
import org.elastos.did.DefaultDIDAdapter;
import org.elastos.did.Mnemonic;
import org.elastos.did.RootIdentity;
import org.elastos.did.exception.DIDException;
import org.elastos.did.exception.DIDResolveException;
import org.elastos.did.exception.DIDStoreException;
import org.elastos.did.exception.MnemonicException;

import java.io.File;
import java.io.IOException;
import java.util.List;

class DIDHelper {
    private static DID selfDid = null;

    private static String passphrase = "";
    private static String storepass = "mypassword";
    private static String storePath = "/storage/self/primary/tmp/";
    private static DIDStore store;
    private static DID getDid(String didStr){
       //"did:elastos:icJ4z2DULrHEzYSvjKNJpKyhqFDxvYV7pN"
       if (selfDid == null){
           initDIDBakckend();
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

   private static void initDIDBakckend(){
//       String rpcEndpoint = "mainnet";
       String rpcEndpoint = "testnet";

//       DefaultDIDAdapter defaultDIDAdapter = new DefaultDIDAdapter(rpcEndpoint);
        AssistAdapter assistAdapter = new AssistAdapter(rpcEndpoint);
       DIDBackend.initialize(assistAdapter);
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
    public static void initRootIdentity(){
        try{
            initDIDBakckend();

            File storefile = Environment.getExternalStorageDirectory();
//                File storefile = Environment.getDataDirectory();
            Log.d("wangran", "initRootIdentity: storefile====>"+storefile);
                File newDir = new File(storefile.getAbsolutePath(), "newDir2");
                newDir.mkdirs();


            Log.d("wangran", "initRootIdentity: storePath====>"+newDir);
            store = DIDStore.open(newDir);



            if (store.containsRootIdentities())
                return; // Already exists
            Mnemonic mg = Mnemonic.getInstance();
            String mnemonic = mg.generate();

            Log.d("wangran", "initRootIdentity: ====>");
            Log.d("wangran", "initRootIdentity: mnemonic ====>"+mnemonic);
            Log.d("wangran", "initRootIdentity: passphrase ====>"+passphrase);
            Log.d("wangran", "initRootIdentity: password====>"+storepass);

            RootIdentity identity = RootIdentity.create(mnemonic, passphrase, store, storepass);


        } catch (DIDStoreException e) {
            throw new RuntimeException(e);
        } catch (MnemonicException e) {
            throw new RuntimeException(e);
        }


    }

    public static DIDDocument initDid() throws DIDException {
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
//        doc.publish(storepass);
        DID myDid= doc.getSubject();
        Log.d("wangran", "initDid: "+myDid);
        System.out.println("Created the new DID : " + myDid);
        return doc;
    }


}
