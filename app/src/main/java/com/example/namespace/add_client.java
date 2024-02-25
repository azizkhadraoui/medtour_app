package com.example.namespace;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class add_client extends AppCompatActivity {
    Button button_passport;
    FirebaseAuth auth;
    Button button_image;
    Button done;
    EditText fname;
    EditText lname;
    EditText pass;
    EditText birth;
    EditText gen;
    ImageView capturedPassport;
    ImageView capturedImage;
    private static final int REQUEST_CAMERA_CODE = 100;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private int numberOfClientsToAdd;
    private int clientsAdded;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);
        // Add this at the top with other member variables
        Spinner mySpinner;

// Add this in onCreate after initializing other views
        mySpinner = findViewById(R.id.spinner);

// Example spinner data
        String[] items = new String[]{"Item 1", "Item 2", "Item 3"};

// Setting up the adapter for spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        mySpinner.setAdapter(adapter);

// Handling spinner item selection
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle the selection
                Log.d("Spinner", "Selected: " + items[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });


        button_passport = findViewById(R.id.scanPassportButton);
        button_image = findViewById(R.id.ScanImageButton);
        done = findViewById(R.id.addClientButton);
        fname = findViewById(R.id.clientFirstName);
        lname = findViewById(R.id.clientLastName);
        pass = findViewById(R.id.clientPassport);
        gen = findViewById(R.id.clientSex);
        birth = findViewById(R.id.clientBirth);
        capturedPassport = findViewById(R.id.passportImageView);
        capturedImage = findViewById(R.id.clientImageView);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
        }
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Register result launchers for CropImage
        ActivityResultLauncher<CropImageContractOptions> cropPassport = registerForActivityResult(new CropImageContract(), result -> {
            if (result.isSuccessful()) {
                Bitmap cropped = BitmapFactory.decodeFile(result.getUriFilePath(getApplicationContext(), true));
                capturedPassport.setImageBitmap(cropped);
                getPassportText(cropped);
            }
        });

        ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), result -> {
            if (result.isSuccessful()) {
                Bitmap cropped = BitmapFactory.decodeFile(result.getUriFilePath(getApplicationContext(), true));
                capturedImage.setImageBitmap(cropped);
            }
        });

        button_passport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImageOptions cropImageOptions = new CropImageOptions();
                cropImageOptions.imageSourceIncludeGallery = true;
                cropImageOptions.imageSourceIncludeCamera = true;
                CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(null, cropImageOptions);
                cropPassport.launch(cropImageContractOptions);
            }
        });

        button_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImageOptions cropImageOptions = new CropImageOptions();
                cropImageOptions.imageSourceIncludeGallery = true;
                cropImageOptions.imageSourceIncludeCamera = true;
                CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(null, cropImageOptions);
                cropImage.launch(cropImageContractOptions);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadDataToFirestore(fname.getText(), lname.getText(), pass.getText(), birth.getText(), gen.getText());
                capturedPassport.setDrawingCacheEnabled(true);
                capturedPassport.buildDrawingCache();
                Bitmap bitmap = Bitmap.createBitmap(capturedPassport.getDrawingCache());
                capturedPassport.setDrawingCacheEnabled(false);
                uploadImageToFirebaseStorage(bitmap,"passport/", String.valueOf(pass.getText()));
                capturedImage.setDrawingCacheEnabled(true);
                capturedImage.buildDrawingCache();
                Bitmap bitmap2 = Bitmap.createBitmap(capturedImage.getDrawingCache());
                capturedImage.setDrawingCacheEnabled(false);
                uploadImageToFirebaseStorage(bitmap2,"image/", String.valueOf(pass.getText()));
                numberOfClientsToAdd = getIntent().getIntExtra("numberOfClientsToAdd", 0);
                clientsAdded++;  // Increment clientsAdded counter
                Log.d("Debug", "clientsAdded: " + (clientsAdded));
                Log.d("Debug", "clientsToAdd: " + (numberOfClientsToAdd));


                if (clientsAdded >= numberOfClientsToAdd) {
                    Intent payIntent = new Intent(add_client.this, pay.class);
                    if(getIntent().getStringExtra("groupId")==null){
                        payIntent.putExtra("groupId", pass.getText());
                        startActivity(payIntent);
                        finish();
                    }else {
                        payIntent.putExtra("groupId", getIntent().getStringExtra("groupId"));
                        startActivity(payIntent);
                        finish();
                    }
                }
            }
        });
    }

    private void getPassportText(Bitmap bitmap) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        Task<Text> result = recognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text visionText) {
                        try {
                            String[] list1 = visionText.getText().split("P<TUN");
                            String[] list2 = list1[1].split("<");
                            String firstname = list2[0];
                            String lastname = list2[2];
                            String[] list3 = list1[1].split("\n");
                            String[] list4 = list3[1].split("<");
                            String passport = list4[0];
                            String sex ="";
                            if (list4[2].contains(Character.toString('F'))) {
                                sex ="female";
                            } else if(list4[2].contains(Character.toString('M'))){
                                sex ="male";
                            }
                            String birthday ="";
                            String[] list5 = list4[2].split("TUN");
                            if (sex.equals("female")){
                                String[] list6 = list5[1].split("F");
                                birthday=list6[0];
                            }
                            else if (sex.equals("male")){
                                String[] list6 = list5[1].split("M");
                                birthday=list6[0];
                            }
                            birthday = birthday.substring(0, birthday.length() - 1);
                            birthday = formatCustomDate(birthday);
                            fname.setText(firstname);
                            lname.setText(lastname);
                            pass.setText(passport);
                            birth.setText(birthday);
                            gen.setText(sex);
                        } catch (Exception e) {
                            // Handle the exception
                            Toast.makeText(add_client.this, "Error extracting data from passport image. Please retake the image.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the exception
                        Toast.makeText(add_client.this, "Error processing passport image. Please retake the image.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public static String formatCustomDate(String inputDate) {
        String formattedDate = null;
        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyMMdd");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = inputDateFormat.parse(inputDate);
            formattedDate = outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    private void uploadDataToFirestore(Editable firstName, Editable lastName, Editable passport, Editable birthday, Editable sex) {
        String firstNameStr = firstName.toString();
        String lastNameStr = lastName.toString();
        String passportStr = passport.toString();
        String birthdayStr = birthday.toString();
        String sexStr = sex.toString();
        Intent intent2 = getIntent();

        Map<String, Object> clientData = new HashMap<>();
        clientData.put("firstName", firstNameStr);
        clientData.put("lastName", lastNameStr);
        clientData.put("passportNumber", passportStr);
        clientData.put("birthday", birthdayStr);
        clientData.put("sex", sexStr);
        clientData.put("product",intent2.getStringExtra("type") );
        clientData.put("flight_date", intent2.getStringExtra("date"));
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        clientData.put("from", user.getEmail());


        Intent intent = getIntent();
        clientData.put("group_id", intent.getStringExtra("groupId"));

        db.collection("clients")
                .add(clientData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(add_client.this, "Data uploaded successfully", Toast.LENGTH_SHORT).show();
                        fname.getText().clear();
                        lname.getText().clear();
                        pass.getText().clear();
                        gen.getText().clear();
                        birth.getText().clear();
                        capturedPassport.setImageResource(android.R.color.transparent);
                        capturedImage.setImageResource(android.R.color.transparent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(add_client.this, "Data upload failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadImageToFirebaseStorage(Bitmap imageBitmap, String ur, String id) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        StorageReference imageRef = storageReference.child("images/" + ur + id + ".jpg");

        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        // Perform additional tasks or store the URL if needed
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle the exception
            }
        });
    }
}
