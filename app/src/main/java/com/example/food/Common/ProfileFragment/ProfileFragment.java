package com.example.food.Common.ProfileFragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.food.Common.FullScreenMap;
import com.example.food.Common.SessionManager;
import com.example.food.Common.UserDetails_Constructor;
import com.example.food.R;
import com.example.food.UserSide.Database;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hbb20.CountryCodePicker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    View ProfileView;
    TextView UserId,UserPhone,DOB1,Mail1,Address1;
    Button CameraUpload,GalleryUpload;
    public DatabaseReference user,Coordinates;
    de.hdodenhof.circleimageview.CircleImageView circleImageView,getCircleImageView;
    private static final int PICK_PROFILE_GALLERY = 100;
    private static final int ACCESS_FILE = 101;
    private static final int PICK_CAMERA_REQUEST = 80;
    private static final int PICK_CAMERA_ACCESS = 80;
    TextView profifleheul;
    String Kidhar;
    Database db;
    ImageView ImagePreview;
    Button UpadtePhoto,CancelPhoto;
    String Cancel,Ok,Upload_Image;
    FirebaseStorage storage;
    StorageReference storageReference;
    RelativeLayout loading,Loading_animationViewPhone,Nohai,Nohai1;
    androidx.constraintlayout.widget.ConstraintLayout profileinfo;
    Dialog dialog;
    FirebaseAuth mAuth;
    String get;
    String DateEdit,MonthEdit,YearEdit;
    //DialogNew();
    ImageButton editText;
    Button DialogCancel,DialogSave;
    String codeBySystem1;
    String EditName_Text,EditAddress_Text,EditDOB_Text,EditMail_Text;
    String AES = "AES";
    TextView EditDOB_Type;
    PinView pin_view;
    RelativeLayout OldRelLay,SetNew,OTPSend;

    // Phone Change
    BottomSheetDialog bottomSheetDialog;
    Button cancel,Send,CheckOTP;
    CountryCodePicker countryCodePicker;
    EditText getPhone;
    PinView pin_view1;
    DatabaseReference firebaseDatabase1;
    boolean load,Check;

    //Address
    String OldAddress,DbLong,DbLat;
    TextView EditAddress_Type;

    //Every User Data
    static String FinalID,_Staff,_fullName,_phoneNo,_date,_Address,_Lat,_Long,_mail,_password;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ProfileView = inflater.inflate(R.layout.fragment_profile, container, false);

        //Text View
        UserId = ProfileView.findViewById(R.id.dis_name);
        UserPhone = ProfileView.findViewById(R.id.dis_phone);
        DOB1 = ProfileView.findViewById(R.id.dis_DOB);
        Mail1 = ProfileView.findViewById(R.id.dis_Mail);
        Address1 = ProfileView.findViewById(R.id.dis_Address1);
        circleImageView = ProfileView.findViewById(R.id.Profileimage);

        // Button
        loading = ProfileView.findViewById(R.id.Loading_animationView);
        editText = ProfileView.findViewById(R.id.editText);
        loading.setVisibility(View.VISIBLE);
        // Profile info  Text Layout
        profileinfo = ProfileView.findViewById(R.id.profileinfo);
        profileinfo.setVisibility(View.GONE);

        // Start session
        SessionManager sessionManager = new SessionManager(getContext(),SessionManager.SESSION_USERSESSION);
        HashMap<String,String> UserDetails = sessionManager.getUserDetailFromSession();

        // Get Id
        FinalID = UserDetails.get(SessionManager.KEY_USERID);

        CallUpdate();

        //User DB Reference
        user = FirebaseDatabase.getInstance().getReference("Users").child(FinalID);


        // Firebase Storage Reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        DisplayFromSQL(_fullName,_date,_phoneNo,_mail,_Address,_password);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogNew();
            }
        });

        return ProfileView;
    }

    private void CallUpdate() {
        // Display From Firebase
        db = new Database(getContext());
        UserDetails_Constructor userDetailsConstructor =  db.UserDetails(FinalID);

        _fullName = userDetailsConstructor.getFullName();
        _phoneNo =  userDetailsConstructor.getPhoneNo();
        _date =     userDetailsConstructor.getDate();
        _password = userDetailsConstructor.getPassword();
        _mail =     userDetailsConstructor.getEmail();
        _Lat =      userDetailsConstructor.getDLatitude();
        _Long =     userDetailsConstructor.getDLongitude();
        String Add = userDetailsConstructor.getAddress();
        int Add1 = Add.indexOf("+");
        String First = Add.substring(0,Add1);
        String Second = Add.substring(Add1+1);
        _Address =  First + "," + Second;
        _Staff =    userDetailsConstructor.getIsstaff();
    }

    //Display
    private void DisplayFromSQL(String Name,String Date,String Phone,String Mail,String Address,String Password) {

        DisplayImage(circleImageView);

        // Name
        UserId.setText(Name);

        //DOB1
        DOB1.setText(Date);

        //Phone
        if(Phone != null){
            UserPhone.setText(Phone.substring(3));
        }

        //Mail
        if(Mail == null){
            Mail1.setText("Not Provided.");
        }else{
            if(Mail.length() == 0){
                Mail1.setText("Not Provided.");
            }else{
                Mail1.setText(Mail);
            }
        }

        //Address
        Address1.setText(_Address);

        loading.setVisibility(View.GONE);
        profileinfo.setVisibility(View.VISIBLE);

    }

    private void DisplayImage(CircleImageView circleImageView) {
        //Image Display
        ContextWrapper cw1 = new ContextWrapper(getContext());
        File directory = cw1.getDir("UserProfile", Context.MODE_PRIVATE);
        if(directory.exists()){
            File directory1 = cw1.getDir("UserProfile", Context.MODE_PRIVATE);
            File file1 = new File(directory1, "Profile" + ".jpg");
            circleImageView.setImageDrawable(Drawable.createFromPath(file1.toString()));
        }
    }

    //Edit Details
    private void DialogNew() {

        dialog = new Dialog(getContext());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        dialog.setContentView(R.layout.edit_profile_details_dialog);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setDimAmount(0.6f);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Image View
        getCircleImageView = dialog.findViewById(R.id.Profileimage1);
        Button ChangePhoto = dialog.findViewById(R.id.PhotoAdd1);

        final EditText EditName_Type = dialog.findViewById(R.id.EditName_Type);
        final EditText EditMail_Type = dialog.findViewById(R.id.EditEmail_Type);
        final TextView EditPhone_Type = dialog.findViewById(R.id.EditPhone_Type);
        EditAddress_Type = dialog.findViewById(R.id.EditAddress_Type);
        EditDOB_Type = dialog.findViewById(R.id.EditDOB_Type);
        TextView Password = dialog.findViewById(R.id.StarPass);
        final ImageButton Back = dialog.findViewById(R.id.Cancel);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogChangePassword();
            }
        });

        //Display Image
        if(circleImageView.getDrawable() != null){
           DisplayImage(getCircleImageView);
            ChangePhoto.setText("Change Photo");
        }else{
            ChangePhoto.setText("Add Photo");
        }

        //Change Photo
        ChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Kidhar = "Gallery";
                Dialog(Kidhar,null);
            }
        });

        // Name
        EditName_Type.setText(_fullName);

        //Phone
        EditPhone_Type.setText(_phoneNo.substring(3));

        //Mail
        EditMail_Type.setText(_mail);

        //DoB
        EditDOB_Type.setText(_date);

        //Address
        EditAddress_Type.setText(_Address);

        int monthStart = _date.indexOf("/");
        int monthEnd = _date.indexOf("/",monthStart+1);
        DateEdit = _date.substring(0,monthStart-1).trim();
        YearEdit = _date.substring(monthEnd+1).trim();
        MonthEdit = _date.substring(monthStart+1,monthEnd).trim();
        final int Date = Integer.parseInt(DateEdit);
        final int Year = Integer.parseInt(YearEdit);
        final int Month = Integer.parseInt(MonthEdit)-1;

        EditDOB_Type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        DateEdit = String.valueOf(dayOfMonth);
                        MonthEdit = String.valueOf(month+1);
                        YearEdit = String.valueOf(year);
                        String Total = DateEdit + "/" + MonthEdit + "/" + YearEdit;
                        EditDOB_Type.setText(Total);
                    }
                },Year,Month,Date);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        DialogCancel = dialog.findViewById(R.id.DialogCancel);
        DialogSave = dialog.findViewById(R.id.DialogSave);

        EditPhone_Type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                DialogPhoneChange();
            }
        });

        EditAddress_Type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogAddressChange();
            }
        });

        DialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"No changes",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        DialogSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditName_Text = EditName_Type.getText().toString();
                EditAddress_Text = EditAddress_Type.getText().toString();
                EditMail_Text = EditMail_Type.getText().toString();

                // Check if empty
                if(EditName_Text.length() == 0){
                    Toast.makeText(getContext(),"Enter Name",Toast.LENGTH_SHORT).show();
                    EditName_Type.setError("Please Enter Name");
                    return;
                }

                if(!validateemail(EditMail_Type)){
                }
                checkText(EditName_Text,EditMail_Text);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void DialogChangePassword() {

        final Dialog dialog = new Dialog(getContext());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.setContentView(R.layout.type_old_password);
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setDimAmount(0.6f);

        ImageButton oldPassBack;
        Button ok,Nex,forgot_1;
        final TextInputLayout new_pass,con_pass;


        oldPassBack = dialog.findViewById(R.id.oldPassBack);
        forgot_1 = dialog.findViewById(R.id.forgot_1);
        pin_view = dialog.findViewById(R.id.pin_view);
        Nex = dialog.findViewById(R.id.changeOld);
        ok = dialog.findViewById(R.id.ok);
        OldRelLay = dialog.findViewById(R.id.OldRelLay);
        SetNew = dialog.findViewById(R.id.SetNew);
        OTPSend = dialog.findViewById(R.id.OTPSend);
        new_pass = dialog.findViewById(R.id.new_pass);
        final TextInputLayout LayOld1 = dialog.findViewById(R.id.LayOld);
        con_pass = dialog.findViewById(R.id.con_pass);

        oldPassBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Objects.requireNonNull(con_pass.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                con_pass.setError(null);
                con_pass.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Objects.requireNonNull(LayOld1.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LayOld1.setError(null);
                LayOld1.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Objects.requireNonNull(new_pass.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new_pass.setError(null);
                new_pass.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        con_pass.setError(null);
        con_pass.setErrorEnabled(false);

        if(!Check){
            dialog.setCancelable(true);
            OldRelLay.setVisibility(View.VISIBLE);
            SetNew.setVisibility(View.GONE);
        }else{
            dialog.setCancelable(false);
            OldRelLay.setVisibility(View.GONE);
            OTPSend.setVisibility(View.GONE);
            SetNew.setVisibility(View.VISIBLE);
        }

        Nex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CheckInput(LayOld1)){
                    /*setFocusNew(LayOld1);*/
                    return;
                }
                String Typed = Objects.requireNonNull(LayOld1.getEditText()).getText().toString();
                String Check = null;
                try {
                    Check = Decrypt(_password, Typed);
                } catch (Exception e) {
                    e.printStackTrace();
                    Check = _password;
                }
                assert Check != null;
                if(Check.equals(Typed)){
                    OldRelLay.setVisibility(View.GONE);
                    SetNew.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(getContext(),"Wrong Password",Toast.LENGTH_SHORT).show();
                }
            }
        });

        forgot_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallUpdate();
                Check = true;
                FirebaseDatabase.getInstance().getReference("Users").child(FinalID).child("phoneNo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mAuth = FirebaseAuth.getInstance();
                        OldRelLay.setVisibility(View.GONE);
                        OTPSend.setVisibility(View.VISIBLE);
                        TextView number = dialog.findViewById(R.id.phone_Display);
                        String Get = Objects.requireNonNull(snapshot.getValue()).toString();
                        number.setText(Get);
                        sendVerificationCodeToUser(Get);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CheckInput(new_pass)){
                   /* setFocusNew(new_pass);*/
                    return;
                }else{
                    if(!CheckInput(con_pass)){
                        /*setFocusNew(con_pass);*/
                        return;
                    }else{
                        if(!Objects.requireNonNull(con_pass.getEditText()).getText().toString().equals(Objects.requireNonNull(new_pass.getEditText()).getText().toString())){
                            Toast.makeText(getContext(),"Password not Same. Please Check",Toast.LENGTH_SHORT).show();
                        }else{
                            String SendPass = con_pass.getEditText().getText().toString().trim();
                            if (SendPass.length() < 5) {
                                new_pass.setError("Minimum 5 characters!");
                                new_pass.setErrorEnabled(true);
                                /*setFocusNew(new_pass);*/
                            }else{
                                String Encrypt;
                                try {
                                    Encrypt  = encrypt(SendPass);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Encrypt  = (SendPass);
                                }
                                new_pass.setErrorEnabled(false);
                                new_pass.setError(null);
                                Database db = new Database(getContext());
                                Bundle bundle = new Bundle();
                                bundle.putString("Password",Encrypt);
                                long Check = db.UpdateUserData(FinalID,4,bundle);
                                if(Check > -1){
                                    Toast.makeText(getContext(),"Password Changed",Toast.LENGTH_SHORT).show();
                                    CallUpdate();
                                    dialog.dismiss();
                                }
                            }
                        }
                    }
                }
            }
        });

        dialog.show();
    }

    private String Decrypt(String data,String pass) throws Exception{
        SecretKeySpec key = EncryptPassword(pass);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE,key);
        byte[] decode = Base64.decode(data,Base64.DEFAULT);
        byte[] decode1 = c.doFinal(decode);
        String decodeValue = new String(decode1);
        return decodeValue;
    }

    private SecretKeySpec EncryptPassword(String passwordS) throws Exception {
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte []  bytes = passwordS.getBytes("UTF-8");
        messageDigest.update(bytes,0,bytes.length);
        byte[] key = messageDigest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key,"AES");
        return secretKeySpec ;
    }

    private boolean CheckInput(TextInputLayout textInputLayout) {
        if(Objects.requireNonNull(Objects.requireNonNull(textInputLayout.getEditText()).getText()).toString().length() == 0){
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError("Please Enter Password");
            return false;
        }else{
            textInputLayout.setErrorEnabled(false);
            textInputLayout.setError(null);
            return true;
        }
    }

    //Dialog for upload photo
    private void Dialog(String Key, Bitmap bitmap) {

        final Dialog dialog = new Dialog(getContext());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.setContentView(R.layout.profile_dialog);
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setDimAmount(0.6f);


        //Gallery and Camera Button
        CameraUpload =  dialog.findViewById(R.id.CameraJaa);
        GalleryUpload = dialog.findViewById(R.id.GalleryJaa);
        final Button Remove = dialog.findViewById(R.id.Remove);

        //Heading
        profifleheul = dialog.findViewById(R.id.profifleheul);
        ImagePreview =  dialog.findViewById(R.id.ImagePreview);

        //Button Text
        //Image Button
        CancelPhoto = dialog.findViewById(R.id.Gallery);
        UpadtePhoto = dialog.findViewById(R.id.Camera_photo);

        ContextWrapper cw = new ContextWrapper(getContext());
        File directory = cw.getDir("UserProfile", Context.MODE_PRIVATE);
        File file = new File(directory, "Profile" + ".jpg");
        if(file.exists()){
            ImagePreview.setImageDrawable(Drawable.createFromPath(file.toString()));
            ImagePreview.setVisibility(View.VISIBLE);
            Remove.setVisibility(View.VISIBLE);
            CancelPhoto.setVisibility(View.GONE);
            UpadtePhoto.setVisibility(View.GONE);
        }

        Remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContextWrapper cw1 = new ContextWrapper(getContext());
                File directory = cw1.getDir("UserProfile", Context.MODE_PRIVATE);
                if(directory.exists()) {
                    File file1 = new File(directory, "Profile" + ".jpg");
                    if (file1.exists()) {
                        boolean Check = file1.delete();
                        if(Check){
                            Toast.makeText(getActivity(),"Deleted",Toast.LENGTH_SHORT).show();
                            circleImageView.setVisibility(View.INVISIBLE);
                            getCircleImageView.setVisibility(View.INVISIBLE);
                            dialog.dismiss();
                        }else{
                            Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        //Gallery or Camera
        if(Key.equals("Gallery")){

            //Gallery
            GalleryUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Kidhar = "";
                    GalleryImage();
                    dialog.dismiss();
                }
            });

            //Camera
            CameraUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CameraPermission();
                    dialog.dismiss();
                }
            });
        }

        //Upload
        else if(Key.equals("Final")) {
            Cancel = "Cancel";
            Ok = "Change";
            Upload_Image = "Upload Image";
            ImagePreview.setImageBitmap(bitmap);
            ImagePreview.setVisibility(View.VISIBLE);
            CameraUpload.setVisibility(View.GONE);
            Remove.setVisibility(View.GONE);
            GalleryUpload.setVisibility(View.GONE);
            profifleheul.setText(Upload_Image);
            CancelPhoto.setVisibility(View.VISIBLE);
            UpadtePhoto.setVisibility(View.VISIBLE);
            CancelPhoto.setText(Cancel);
            UpadtePhoto.setText(Ok);

            //Upload to storage
            UpadtePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ImagePreview.getDrawable() == null){
                        Toast.makeText(getContext(),"Please Select Image",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dialog.dismiss();
                    ContextWrapper cw1 = new ContextWrapper(getContext());
                    File directory = cw1.getDir("UserProfile", Context.MODE_PRIVATE);
                    if(directory.exists()){
                        File file1 = new File(directory, "Profile" + ".jpg");
                        if(file1.exists()){
                            boolean  Check = file1.delete();
                            if(Check){
                                uploadImage();
                            }
                        }else{
                            uploadImage();
                        }
                        /*circleImageView.setImageDrawable(Drawable.createFromPath(file1.toString()));*/
                    }
                }
            });

            //Cancel
            CancelPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

        dialog.show();
    }

    public void setFocusNew(EditText editText){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        editText.requestFocus();
        editText.setCursorVisible(true);
        imm.showSoftInput(editText,InputMethodManager.SHOW_IMPLICIT);
    }

    private void DialogAddressChange() {

        Coordinates = FirebaseDatabase.getInstance().getReference("Users").child(FinalID).child("Address").child("Default");
        Coordinates.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                //Sending to Dialog
                if(snapshot1.exists()){
                        DbLat =  Objects.requireNonNull(snapshot1.child("Coordinates").child("Latitude").getValue()).toString();
                        DbLong = Objects.requireNonNull(snapshot1.child("Coordinates").child("Longitude").getValue()).toString();
                        OldAddress = snapshot1.child("Address").getValue().toString();

                        //Send Data to Fragment
                        FullScreenMap dialogFragment = new FullScreenMap();
                        dialogFragment.FullScreenMap(DbLat,DbLong,OldAddress,FinalID);
                        FragmentResultListener fragmentResultListener = new FragmentResultListener() {
                        @Override
                        public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                            if(requestKey != null){
                                _Address =  result.getString("Add").replace("+",",");
                                Address1.setText(_Address);
                                EditAddress_Type.setText(_Address);
                            }
                        }
                    };
                    dialogFragment.show(getChildFragmentManager(),null);
                    getChildFragmentManager().setFragmentResultListener("Send",ProfileFragment.this,fragmentResultListener);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void DialogPhoneChange() {

        bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetDialog);
        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        final View bottom = LayoutInflater.from(getContext()).inflate(
                R.layout.bottom_phone_change,null);

        countryCodePicker = bottom.findViewById(R.id.code_picker_Change);
        getPhone = bottom.findViewById(R.id.PhoneChange);
        cancel = bottom.findViewById(R.id.OTPCancel);
        Send = bottom.findViewById(R.id.DialogSave);
        //Before OTP
        Nohai = bottom.findViewById(R.id.HideAfterOtp);
        //Type OTP
        Nohai1 = bottom.findViewById(R.id.typeOTP);
        //Load to verify
        Loading_animationViewPhone = bottom.findViewById(R.id.Loading_animationViewPhone);
        pin_view1 = bottom.findViewById(R.id.pin_view1);
        CheckOTP = bottom.findViewById(R.id.CheckOTP);
        //Heading
        final TextView Heading = bottom.findViewById(R.id.changeNo);

        /*setFocusNew(getPhone);*/

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        getPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 10){
                    Toast.makeText(getContext(),"10 digits completed",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String typedNo = getPhone.getText().toString();
                get = "+" + countryCodePicker.getFullNumber() + typedNo;
                String display = UserPhone.getText().toString();
                int length = typedNo.length();
                if(length < 10){
                    getPhone.setError("10 digits required");
                    setFocusNew(getPhone);
                    return;
                }
                 if(display.equals(typedNo)){
                        Toast.makeText(getContext(),"Same Number",Toast.LENGTH_SHORT).show();
                    }else{
                        bottomSheetDialog.setCancelable(false);
                        Heading.setText("Type OTP");
                        Toast.makeText(getContext(),"OTP Send.",Toast.LENGTH_SHORT).show();
                        mAuth = FirebaseAuth.getInstance();
                        sendVerificationCodeToUser(get);
                        Nohai.setVisibility(View.GONE);
                        Nohai1.setVisibility(View.VISIBLE);
                    }
            }
        });

        CheckOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedPinView();
            }
        });
        bottomSheetDialog.setContentView(bottom);
        bottomSheetDialog.show();
    }

    private void sendVerificationCodeToUser(String get) {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(get)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(requireActivity())                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codeBySystem1 = s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        if(Check){
                            pin_view.setText(code);
                        }else{
                            pin_view1.setText(code);
                        }
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem1, code);
        signInWithPhoneAuthCredential(credential);
    }

    // Tell me where to go
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Verification Updated.", Toast.LENGTH_SHORT).show();
                            if(!Check){
                                Bundle bundle = new Bundle();
                                bundle.putString("Contact",get);
                                Database db = new Database(getContext());
                                long Check = db.UpdateUserData(FinalID,3,bundle);
                                if(Check > 0){
                                    firebaseDatabase1 = FirebaseDatabase.getInstance().getReference("Users").child(FinalID);
                                    firebaseDatabase1.child("phoneNo").setValue(get).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            UserPhone.setText(get.substring(3));
                                            load = false;
                                            Toast.makeText(getContext(),"Number Updated",Toast.LENGTH_SHORT).show();
                                            bottomSheetDialog.dismiss();
                                        }
                                    });
                                }
                            }else{
                                OldRelLay.setVisibility(View.GONE);
                                OTPSend.setVisibility(View.GONE);
                                SetNew.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getContext(), "Verification Not Completed! Try Again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    //Call this method to check
    public void savedPinView() {
        String code = pin_view1.getText().toString();
        if (!code.isEmpty()) {
            verifyCode(code);
        }
    }

    private String encrypt(String passwordS) throws Exception{
        SecretKeySpec key = EncryptPassword(passwordS);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE,key);
        byte[] Val = c.doFinal(passwordS.getBytes());
        String encrypt = Base64.encodeToString(Val,Base64.DEFAULT);
        return encrypt;
    }

    private void checkText(final String editName_text, final  String editEmail_Type) {

        final Bundle bundle = new Bundle();
        int i = 0;

        if(!_fullName.equals(editName_text)){
            bundle.putString("editName",editName_text);
            _fullName = editName_text;
            i =1;
        }

        // display in DOB
        int monthStart = _date.indexOf("/");
        int monthEnd = _date.indexOf("/",monthStart+1);
        String Day = _date.substring(0,monthStart);
        String Yeas = _date.substring(monthEnd+1);
        String Mon = _date.substring(monthStart+1,monthEnd);

        if(!Day.equals(DateEdit) || !Yeas.equals(YearEdit) || Mon.equals(MonthEdit)  ){
            EditDOB_Text = DateEdit + " / " + MonthEdit  + " / " + YearEdit;
            bundle.putString("editDOB",EditDOB_Text);
            _date = EditDOB_Text;
            i = 1;
        }

        if(_mail != null){
            if(!_mail.equals(editEmail_Type)){
                bundle.putString("editMail",editEmail_Type);
                _mail = editEmail_Type;
                i = 1;
            }
        }else{
            if(editEmail_Type.length() >4){
                bundle.putString("editMail",editEmail_Type);
                _mail = editEmail_Type;
                i = 1;
            }
        }

        if(i == 1){
            Database db = new Database(getContext());
            long Updated = db.UpdateUserData(FinalID,1,bundle);
            if (Updated > 0 ){
                //Mail
                if(editEmail_Type.length() == 0){
                    user.child("email").setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Mail1.setText("Not Provided");
                        }
                    });
                }else{
                    user.child("email").setValue(editEmail_Type).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Mail1.setText(editEmail_Type);
                        }
                    });
                }

                //DOB1
                DOB1.setText(_date);
                //Name
                user.child("fullName").setValue(editName_text).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        UserId.setText(editName_text);
                    }
                });

            }else{
                Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Gallery
        if(requestCode == ACCESS_FILE && (resultCode == getActivity().RESULT_OK) && data.getData() != null) {
            Uri imageUri = data.getData();
            Crop(imageUri);
        }

        //Camera
        if(requestCode == PICK_CAMERA_REQUEST && resultCode == getActivity().RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG,0,bytes);
            String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),photo,"Photo",null);
            Uri uri = Uri.parse(path);
            Crop(uri);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == getActivity().RESULT_OK) {
                Uri saveUri = result.getUri();
                InputStream inputStream;
                try{
                    inputStream = getActivity().getContentResolver().openInputStream(saveUri);
                    Bitmap photo = BitmapFactory.decodeStream(inputStream);
                    Kidhar= "Final";
                    Dialog(Kidhar,photo);
                }catch (FileNotFoundException e ){
                    e.printStackTrace();
                }

            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception exception = result.getError();
                Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void Crop(Uri imageUri) {
        // for fragment (DO NOT use `getActivity()`)
        Intent intent =  CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setActivityTitle("Crop Image")
                .setFixAspectRatio(true)
                .setAutoZoomEnabled(false)
                .setCropMenuCropButtonTitle("Select")
                .getIntent(requireContext());

        startActivityForResult(intent,CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);

    }

    //Email check
    private boolean validateemail(EditText email) {
        String value = email.getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";

        if (value.isEmpty()) {
            email.setError(null);
            return true;
        } else if (!value.matches(checkEmail)) {
            email.setError("Invalid E-Mail");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    //Upload to Storage
    private void uploadImage() {

        BitmapDrawable drawable = (BitmapDrawable) ImagePreview.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        ContextWrapper cw = new ContextWrapper(getContext());
        File directory = cw.getDir("UserProfile", Context.MODE_PRIVATE);
        File file = new File(directory, "Profile" + ".jpg");
        if (!file.exists()) {
            Log.d("path", file.toString());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                boolean DisplayProfile =  bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                if(DisplayProfile){
                    circleImageView.setVisibility(View.VISIBLE);
                    getCircleImageView.setVisibility(View.VISIBLE);
                    circleImageView.setImageDrawable(Drawable.createFromPath(file.toString()));
                    getCircleImageView.setImageDrawable(Drawable.createFromPath(file.toString()));
                }
                fos.flush();
                fos.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }

       /* if (mUploadTask != null && mUploadTask.isInProgress()) {
            Toast.makeText(getActivity(), "Upload in Progress", Toast.LENGTH_SHORT).show();
        } else {
            if (saveUri != null) {

                if(ProfileImageDisplay.equals("Present")){

                    firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild("Profile Photo")){
                                String view = snapshot.child("Profile Photo").getValue().toString();
                                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                StorageReference storageReference= firebaseStorage.getReferenceFromUrl(view);
                                storageReference.delete();
                                ProfileImageDisplay = "";
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                String imageName = UUID.randomUUID().toString();
                final StorageReference imageFolder = storageReference.child("Profile photos/" + imageName);
                mUploadTask = imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getActivity(), "Done.", Toast.LENGTH_SHORT).show();
                        imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Set Value for new Category
                                image = uri.toString();
                                firebaseDatabase.child("Profile Photo").setValue(image);
                                Picasso.get().load(uri).into(circleImageView);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    }
                });
            }
        }*/
        }
    }

    //Select From Galley and ask permission
    private void GalleryImage() {

        if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PICK_PROFILE_GALLERY);
        }else{
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"),ACCESS_FILE);
        }
    }

    //Click photo and ask permission
    private void CameraPermission() {
        if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),new String[]{Manifest.permission.CAMERA},PICK_CAMERA_REQUEST);
        }
        else {
            Intent CameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(CameraIntent,PICK_CAMERA_ACCESS);
        }
    }

}