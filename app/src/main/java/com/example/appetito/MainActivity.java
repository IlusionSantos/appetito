package com.example.appetito;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

/**
 *  ID Cliente
 *  593423946898-2fbsvaigrpbf8oin6ugaeqf4g3p03ibe.apps.googleusercontent.com
 * */
public class MainActivity extends AppCompatActivity {

    /*Cloud Vision Variables*/
    private static final String CLOUD_VISION_API_KEY = BuildConfig.API_KEY;
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;

    /*Camara and Galery Variables*/
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;
    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private static final int REQUEST_IMAGE_PICK = 1;

    private TextView mImageDetails;
    private ImageView mMainImage;

    /*Floating Button for login facebook and gmail*/
    private FloatingActionButton fabfb;
    private int statefb = 0; /*0 - login 1 - logout*/
    private FloatingActionButton fabgmail;
    private int stategmail = 0; /*0 - login 1 - logout*/
    private int fborgmail = 0; /*0 - ninguno 1 - fb 2 - gmail*/
    private FloatingActionButton fabdata;
    public static String platillo = "";
    private CallbackManager callbackManager;
    public String fbdata = "";
    private Uri passimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageDetails = (TextView) findViewById(R.id.image_details);
        mMainImage = (ImageView) findViewById(R.id.ivpicture);
        fabfb = (FloatingActionButton) findViewById(R.id.fabfb);
        fabgmail = (FloatingActionButton) findViewById(R.id.fabgmail);
        fabdata = (FloatingActionButton) findViewById(R.id.fabdata);

        fabdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,DataActivity.class);
                if(!platillo.equals("") && statefb == 1) {
                    intent.putExtra("platillo", platillo);
                    intent.setData(passimage);
                    startActivity(intent);
                }else{
                    if(statefb == 0) {
                        Toast.makeText(getApplicationContext(), "Necesitas realizar login para obtener más información", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Seleccione una imagen para obtener información", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        fabfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (fborgmail){
                    case 0: /*Metodo login*/
                        //fborgmail = 1;

                        switch (statefb){
                            case 0:
                                statefb = 1;
                                callbackManager = CallbackManager.Factory.create();
                                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("user_photos", "email"));
                                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                                            @Override
                                            public void onSuccess(LoginResult loginResults) {

                                                GraphRequest request = GraphRequest.newMeRequest(
                                                        loginResults.getAccessToken(),
                                                        new GraphRequest.GraphJSONObjectCallback() {
                                                            @Override
                                                            public void onCompleted(
                                                                    JSONObject object,
                                                                    GraphResponse response) {
                                                                // Application code
                                                                fbdata = response.toString();
                                                                String[] data=fbdata.split(",");
                                                                String fbname = ((data[2]).split(":"))[1];
                                                                Log.v("LoginActivity", response.toString());
                                                                Toast.makeText(getApplicationContext(),"Login in Facebook USER: "+fbname,Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                                Bundle parameters = new Bundle();
                                                parameters.putString("fields", "id,name,email");
                                                request.setParameters(parameters);
                                                request.executeAsync();

                                            }
                                            @Override
                                            public void onCancel() {

                                                Log.e("dd","facebook login canceled");

                                            }


                                            @Override
                                            public void onError(FacebookException e) {



                                                Log.e("dd", "facebook login failed error");

                                            }
                                        });

                                break;
                            case 1:
                                alertTwoButtons();
                                break;
                        }

                         break;
                    case 1: Toast.makeText(getApplicationContext(),"Ya realizaste login en facebook", Toast.LENGTH_LONG).show(); break;
                    case 2: Toast.makeText(getApplicationContext(),"Ya realizaste login en gmail", Toast.LENGTH_LONG).show(); break;
                }
            }
        });
        fabgmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (fborgmail){
                    case 0: /*Metodo login*/ fborgmail = 2; break;
                    case 1: Toast.makeText(getApplicationContext(),"Ya realizaste login en facebook", Toast.LENGTH_LONG).show(); break;
                    case 2: Toast.makeText(getApplicationContext(),"Ya realizaste login en gmail", Toast.LENGTH_LONG).show(); break;
                }
            }
        });



        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
            //uploadAutoML(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
            //uploadAutoML(photoUri);
        }else if (resultCode==Activity.RESULT_OK){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.navigation_camara:
                    startCamera();
                    return true;

                case R.id.navigation_gallery:
                    startGalleryChooser();
                    return true;
            }
            return false;
        }
    };

  

    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                    GALLERY_IMAGE_REQUEST);
        }
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(this, CAMERA_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }




    public File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    public JsonObject creatingRequestJson(Uri uri){
        JsonObject request = new JsonObject();
        JsonObject payload = new JsonObject();
        JsonObject imagebyte = new JsonObject();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try{
            Bitmap bitmap = scaleBitmapDown(MediaStore.Images.Media.getBitmap(getContentResolver(), uri), MAX_DIMENSION);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        }catch (IOException e){
            Log.e("ERROR",e.toString());
        }
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String imageByteString = new String(imageBytes);
        imagebyte.addProperty("imageBytes",imageByteString);
        payload.add("image",imagebyte);
        request.add("payload",payload);
        Log.v("PAYLOAD",request.toString());
        return request;
    }

    public void uploadAutoML(Uri uri){
        final Uri url = uri;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // All your networking logic
                // should be here
                try{
                    URL githubEndpoint = new URL("https://automl.googleapis.com/v1beta1/projects/grand-lamp-233601/locations/us-central1/models/ICN1385182451619859076:predict -d "+creatingRequestJson(url));
                    HttpsURLConnection myConnection = (HttpsURLConnection) githubEndpoint.openConnection();
                    myConnection.setRequestMethod("POST");
                    myConnection.setRequestProperty("Content-Type", "application/json");
                    myConnection.setRequestProperty("Authorization","Bearer ya29.GqQBGAeNLTOoIMCRtxH2OGsYNnUGS-Wk6BIp36Dnc-SPjSNIE_3uM3vITtsddlSREIV5-C-topcoMJ96dDacedKPZeF9VshJAc5XvP3jrQ1S2LPA22X_xrWggz5u0M2sxgmy5NvWpC7dtZo1lMRAUdZD8zVRpHIiU7f1FpGCP5llP-qnrbpuNBArOfI80gxamnjped3viJwtaNH3zkmVA7Ser-BNsEw");
                    //myConnection.setRequestProperty("Authorization","Bearer $(gcloud auth application-default print-access-token)");
                    Log.v("CODE","----------------"+myConnection.getResponseCode());
                    if (myConnection.getResponseCode() == 200) {
                        // Success
                        // Further processing here
                        InputStream responseBody = myConnection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);
                        jsonReader.beginObject(); // Start processing the JSON object
                        while (jsonReader.hasNext()) { // Loop through all keys
                            String key = jsonReader.nextName(); // Fetch the next key
                            if (key.equals("organization_url")) { // Check if desired key
                                // Fetch the value as a String
                                String value = jsonReader.nextString();
                                Log.v("JSON DATA","-----------------------"+value);
                                // Do something with the value
                                // ...

                                break; // Break out of the loop
                            } else {
                                jsonReader.skipValue(); // Skip values of other keys
                            }
                        }
                        Log.v("AUTOML","FUNCIONA ---------------------------------------------------");
                        jsonReader.close();
                        myConnection.disconnect();
                    } else {
                        // Error handling code goes here
                        Log.e("ERROR", "NO HAY DATOS -------------------------");
                    }
                }catch (Exception e){
                    Log.e("ERROR AUTOML",e.toString());
                }
            }
        });
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                passimage = uri;
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                MAX_DIMENSION);
                callCloudVision(bitmap);
                mMainImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private Vision.Images.Annotate prepareAnnotationRequest(final Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("LABEL_DETECTION");
                labelDetection.setMaxResults(MAX_LABEL_RESULTS);
                add(labelDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    private static class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<MainActivity> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        LableDetectionTask(MainActivity activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        protected void onPostExecute(String result) {
            MainActivity activity = mActivityWeakReference.get();
            if (activity != null && !activity.isFinishing()) {
                TextView imageDetail = activity.findViewById(R.id.image_details);
                //platillo
                imageDetail.setText(result);
            }
        }
    }

    private void callCloudVision(final Bitmap bitmap) {
        // Switch text to loading
        mImageDetails.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask(this, prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        StringBuilder message = new StringBuilder("Información:\n\n");

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                if (label.getScore() > 0.500) {
                    message.append(String.format(Locale.US, "%s", label.getDescription()));
                    message.append("\n");
                }else{
                    break;
                }
            }
        } else {
            message.append("No se encontró resultado");
        }
        platillo = "Fiambre";
        return message.toString();
    }

    public void alertTwoButtons() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Logout")
                .setMessage(getString(R.string.logout))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                            LoginManager.getInstance().logOut();
                            statefb = 0;
                            Toast.makeText(getApplicationContext(),"Logout of Facebook",Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
    }
}
