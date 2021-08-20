package com.example.food.AdminSide.MainMenuFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.AdminSide.EmployeesDetails.MemberDialog;
import com.example.food.AdminSide.FoodFragment.FoodListAdmin;
import com.example.food.AdminSide.Interface.ItemClickListener;
import com.example.food.AdminSide.Notification.newOrderListIntent;
import com.example.food.Common.GetSearchDetails_Constructor;
import com.example.food.Common.MenuMainAdapter;
import com.example.food.Common.SessionManager;
import com.example.food.Common.TopCategoryAdapter;
import com.example.food.Common.TopCategory_Constructor;
import com.example.food.R;
import com.example.food.UserSide.Database;
import com.example.food.UserSide.FoodDetails.foodDetail;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;


public class AdminMenu extends Fragment {

    RelativeLayout shutterLay;

    FloatingActionButton add, UserView, upload;
    private DatabaseReference Categoryref,foodRef;
    private DatabaseReference newRef;
    FirebaseStorage storage;
    TextInputEditText SearchBarCat11;
    StorageReference storageReference;
    private StorageTask mUploadTask;

    boolean isFABOpen,FoodList,Detail;
    ImageView dis, selected;
    boolean state,Status;
    String send,ShutterStatus;
    boolean i;
    String Link,Phone,NotificationID;
    Database db;

    //Top
    FirebaseRecyclerAdapter<TopCategory_Constructor, TopCategoryAdapter> topAdapter;
    Button Shutter1;
    public static final String ID = "Abhi";
    public static final String FoodLIST = "Food";
    public static final String DES = "Notificaton";

    //Recycler View
    private RecyclerView recyclerView,ListRecycler1,TopCategory;
    FirebaseRecyclerAdapter<TopCategory_Constructor, MenuMainAdapter> adapter;
    final static String foodList = "foodList";
    final String Food = "food";
    Query Query;
    //Adapters
    FirebaseRecyclerAdapter<GetSearchDetails_Constructor,UserHolder1> adapterFinal;
    SessionManager sessionManager;

    Animation TopUp,TopDown,IntroAnimUp,IntroAnimDown;

    //Text from upload data
    TextInputEditText CategoryDb;
    TextInputLayout CategoryLay;
    Button select, yes1, No1;
    TopCategory_Constructor newTopCategoryConstructor;
    TextView NoImageSelected1,introName;
    ImageButton sign_back_Dialog1;

    //Change Data
    TextView dynamicHeading1;

    Uri saveUri, saveUri1;
    //Auto Increment in Database
    String image,item,UserID,GetDeviceType;

    //Search
    RelativeLayout IntroName,Search,SearchSuggestion,whatLike11,AdminCatLay,LayoutTop;

    //Edit Data Hooks
    Button editfinal;
    TextInputEditText editUpload;
    TextInputLayout editLayUpload;

    private static final int PICK_IMAGE_REQUEST = 71;
    private static final int PICK_IMAGE_EDIT_REQUEST = 70;

    public static Bundle recyclerViewState;
    public final static String sendPosition = "StatusPosition";
    private static final String UserType = "AdminTab";
    Parcelable position;
    boolean check;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Status = true;

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View menu = inflater.inflate(R.layout.fragment_admin_menuadd, container, false);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        sessionManager = new SessionManager(requireContext(),SessionManager.SESSION_USERSESSION);
        HashMap<String,String> USerID = sessionManager.getUserDetailFromSession();
        UserID = USerID.get(SessionManager.KEY_USERID);
        db = new Database(getContext());
        GetDeviceType = USerID.get(SessionManager.KEY_STAFF);
        Phone = db.Phone(UserID);

        //HOOKS
        shutterLay = menu.findViewById(R.id.shutterLay);
        add = (FloatingActionButton) menu.findViewById(R.id.Add);
        upload = (FloatingActionButton) menu.findViewById(R.id.upload);
        UserView = (FloatingActionButton) menu.findViewById(R.id.UserFloat);
        SearchBarCat11 = menu.findViewById(R.id.SearchBarCat1);
        //Layout Intro Main
        IntroName = menu.findViewById(R.id.IntroLayout1);
        whatLike11 = menu.findViewById(R.id.whatLike1);
        Search = menu.findViewById(R.id.SearchAja11);
        SearchSuggestion = menu.findViewById(R.id.ListSuggestion11);
        introName = menu.findViewById(R.id.introName);

        //Top Recycler
        LayoutTop = menu.findViewById(R.id.LayoutTop);
        TopCategory = menu.findViewById(R.id.Top1Category);
        TopCategory.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        recyclerView = (RecyclerView) menu.findViewById(R.id.myView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        UserView.setOnClickListener(v -> {
            MemberDialog memberDialog = new MemberDialog(getContext());
            memberDialog.show(getParentFragmentManager(),null);
        });

        //Suggest List
        ListRecycler1 = menu.findViewById(R.id.SearchContent1);
        ListRecycler1.setLayoutManager(new LinearLayoutManager(getContext()));
        // Relative Top
        AdminCatLay = menu.findViewById(R.id.AdminCatLay);
        //Button Shutter
        Shutter1 = menu.findViewById(R.id.Shutter);

        if(GetDeviceType.equalsIgnoreCase(UserType)){
            String User = "User";
            introName.setText(User);
            add.setVisibility(View.GONE);
        }else{

            add.setOnClickListener(view -> {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            });

            //Move from Fragment to activity
            upload.setOnClickListener(view -> showDialog());
        }

        FirebaseDatabase.getInstance().getReference().child("Shop").child("Shutter").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ShutterStatus = snapshot.getValue().toString();
                    UpdateButton(ShutterStatus);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

            Shutter1.setOnClickListener(v -> {
                if(ShutterStatus != null){
                    if(ShutterStatus.equals("Open")){
                        ShutterStatus = "Closed";
                    }else{
                        ShutterStatus = "Open";
                    }
                    FirebaseDatabase.getInstance().getReference().child("Shop").child("Shutter").setValue(ShutterStatus).addOnSuccessListener(aVoid -> UpdateButton(ShutterStatus));
                }
            });

        ListRecycler1.setOnTouchListener((v, event) -> {
            hideKeyboard(v);
            return true;
        });

        //Set Focus
        SearchBarCat11.setOnFocusChangeListener((v, hasFocus) -> {
            slideUp(state);
            if(hasFocus){
                ListRecycler1.setAdapter(null);
            }else{
                ListRecycler1.setAdapter(null);
                SearchBarCat11.getText().clear();
            }
        });


        SearchBarCat11.setOnKeyListener((v, keyCode, event) -> {
            if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
                SearchBarCat11.clearFocus();
                return true;
            }
            return false;
        });

        SearchBarCat11.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0){
                    ListRecycler1.setAdapter(null);
                }
                if(s.length() > 0){
                        item = s.toString();
                        FirebaseSearch(item,true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        foodRef = FirebaseDatabase.getInstance().getReference().child("Foods");
        Categoryref = FirebaseDatabase.getInstance().getReference().child("Category");

        newRef = FirebaseDatabase.getInstance().getReference().child("Category");

        //Animation Start
        IntroAnimUp = AnimationUtils.loadAnimation(getContext(),R.anim.slide_in_top);

        IntroAnimDown = AnimationUtils.loadAnimation(getContext(),R.anim.slide_in_bottom);

        // Animation Slide Up
        TopUp = AnimationUtils.loadAnimation(getContext(),R.anim.top_send_up);

        // Animation Slide Down
        TopDown = AnimationUtils.loadAnimation(getContext(),R.anim.top_send_down);

        //Set Anim
        IntroName.setAnimation(IntroAnimUp);
        whatLike11.setAnimation(IntroAnimUp);
        LayoutTop.setAnimation(IntroAnimUp);
        Search.setAnimation(IntroAnimUp);
        recyclerView.setAnimation(IntroAnimDown);

        TopCategory1();

        TopUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                IntroName.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                
            }
        });

        try {
            if(getArguments().getString("ID") != null){
                NotificationID = getArguments().getString("ID");
                AcceptOrNot();
            }
        }catch (Exception ignored){ }

        return menu;
    }

    private void UpdateButton(String shutterStatus) {
        try{
            if(shutterStatus.equals("Open")){
                @SuppressLint("UseCompatLoadingForDrawables") Drawable Open = getContext().getResources().getDrawable(R.drawable.window);
                Shutter1.setCompoundDrawablesWithIntrinsicBounds(null,null,Open,null);
                Shutter1.setText("Open");
                shutterLay.setVisibility(View.VISIBLE);
            }else{
                @SuppressLint("UseCompatLoadingForDrawables") Drawable Closed = getContext().getResources().getDrawable(R.drawable.window_shutter);
                Shutter1.setCompoundDrawablesWithIntrinsicBounds(null,null,Closed,null);
                Shutter1.setText("Closed");
                shutterLay.setVisibility(View.VISIBLE);
            }
            Shutter1.setClickable(true);
        }catch (Exception e){
            Toast.makeText(getActivity(),"Try Again",Toast.LENGTH_SHORT).show();
        }
    }

    // Top Category
    private void TopCategory1() {

        Query Top =  FirebaseDatabase.getInstance().getReference("Foods").orderByChild("Special").equalTo("yes");

        FirebaseRecyclerOptions<TopCategory_Constructor> options1 = new FirebaseRecyclerOptions.Builder<TopCategory_Constructor>().setQuery(Top, TopCategory_Constructor.class).build();

        topAdapter = new FirebaseRecyclerAdapter<TopCategory_Constructor, TopCategoryAdapter>(options1) {
            @Override
            protected void onBindViewHolder(@NonNull final TopCategoryAdapter topCategoryAdapter, final int i, @NonNull TopCategory_Constructor topCategoryConstructor) {

                topCategoryAdapter.Name.setText(topCategoryConstructor.getName());

                Picasso.get().load(topCategoryConstructor.getImage()).into(topCategoryAdapter.Image);

                topCategoryAdapter.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                         // Admin
                        if(!GetDeviceType.equalsIgnoreCase(UserType)){
                            DialogToDelete(topAdapter.getRef(i).getKey(),i);
                        }else{
                            // Tab
                            Intent intent =new Intent(getContext(), foodDetail.class);
                            intent.putExtra("Search1",topAdapter.getItem(i).getName());
                            startActivity(intent);
                        }
                    }

                    private void DialogToDelete(final String Parent, final int Position) {

                            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            final Dialog dialog = new Dialog(getContext());
                            dialog.getWindow().setAttributes(layoutParams);
                            dialog.setContentView(R.layout.add_to_favorites);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                            dialog.getWindow().setDimAmount(0.6f);

                            final TextView HeadingLocButton = dialog.findViewById(R.id.HeadingLocButton11);
                            Button No = dialog.findViewById(R.id.NoSpecial);
                            final Button Yes = dialog.findViewById(R.id.YesSpecial);
                            HeadingLocButton.setText("Remove items ?");
                            No.setOnClickListener(v -> dialog.dismiss());

                        Yes.setOnClickListener(v -> foodRef.child(Parent).child("Special").setValue(null).addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Added.", Toast.LENGTH_SHORT).show();
                            topAdapter.notifyItemRemoved(Position);
                            topAdapter.notifyItemRangeChanged(Position, topAdapter.getItemCount());
                            dialog.dismiss();
                        }));

                        dialog.show();
                    }
                });
            }


            @NonNull
            @Override
            public TopCategoryAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View Top = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_category, parent, false);
                return new TopCategoryAdapter(Top);
            }
        };

        TopCategory.setAdapter(topAdapter);
        topAdapter.startListening();
    }

    // Notify when new Order Comes
    public void AcceptOrNot(){
        newOrderListIntent Dialog = new newOrderListIntent(getContext());
        Dialog.show(getChildFragmentManager(),null);
    }

    private void FirebaseSearch(final String item, boolean search) {
        int length = item.length();
        final String Forward = item.substring(0,1).toUpperCase().trim() + item.substring(1,length).toLowerCase();

        if(search) {
            Query = Categoryref.orderByChild("name").startAt(Forward).endAt(Forward + "\uf8ff");
            Query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        FoodList = true;
                    }else{
                        FoodList = false;
                        FirebaseSearch(item,false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            final String Forward1 = item.substring(0,1).toUpperCase() + item.substring(1,length).toLowerCase();
            Query = foodRef.orderByChild("name").startAt(Forward1).endAt(Forward1 + "\uf8ff");
            Query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.exists()){
                        Detail = false;
                    }else{
                        Detail = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        // Suggestion
        FirebaseRecyclerOptions<GetSearchDetails_Constructor> options =
                new FirebaseRecyclerOptions.Builder<GetSearchDetails_Constructor>()
                        .setQuery(Query, GetSearchDetails_Constructor.class).build();

        adapterFinal = new FirebaseRecyclerAdapter<GetSearchDetails_Constructor, UserHolder1>(options) {
            @NonNull
            @Override
            public UserHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion, parent, false);
                return new UserHolder1(view);
            }

            @Override
            public int getItemCount() {
                return  super.getItemCount();
            }

            @Override
            protected void onBindViewHolder(@NonNull final UserHolder1 holder, int i, @NonNull final GetSearchDetails_Constructor getSearchDetailsConstructor) {

                String Image = getSearchDetailsConstructor.getImage();
                if(Image.contains("firebasestorage")){
                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                    StorageReference storageReference = firebaseStorage.getReferenceFromUrl(Image);
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri).into(holder.circleImageView));
                }else{
                    Picasso.get().load(getSearchDetailsConstructor.getImage()).into(holder.circleImageView);
                }

                holder.Available.setText(null);
                holder.FoodName.setText(getSearchDetailsConstructor.getName());


                holder.setItemClickListener((view, position1, isLongClick) -> {
                    AdminCatLay.setVisibility(View.GONE);
                    Bundle bundle = new Bundle();
                    if(Detail){
                        Detail = false;
                        bundle.putString("SendNameWithPosition",adapterFinal.getItem(position1).getName());
                        Toast.makeText(getContext(),adapterFinal.getItem(position1).getName(),Toast.LENGTH_SHORT).show();
                    }else if (FoodList){
                        FoodList = false;
                        bundle.putString("SendName",adapterFinal.getItem(position1).getName());
                    }
                    //Set Argument Class
                    FoodListAdmin fragment = new FoodListAdmin();
                    fragment.setArguments(bundle);
                    assert getParentFragmentManager() != null;
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_Food,fragment,foodList).addToBackStack(Food)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                });
            }
        };
        ListRecycler1.setAdapter(adapterFinal);
        adapterFinal.startListening();
    }

    public static class UserHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener{
        View view;
        ItemClickListener itemClickListener;
        CircleImageView circleImageView;
        TextView Available,FoodName;

        public UserHolder1(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            circleImageView = view.findViewById(R.id.SearchItemPhoto);
            Available = view.findViewById(R.id.Available);
            FoodName = view.findViewById(R.id.SearchDishName);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),false);
        }
    }

    //Show Dialog for upload data
    private void showDialog() {

        final Dialog dialog = new Dialog(getActivity());
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        dialog.setContentView(R.layout.upload_category_dialog);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setDimAmount(0.6f);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //Hooks Upload data
        CategoryDb = dialog.findViewById(R.id.categoryUpload);
        //Lay Edit
        CategoryLay = dialog.findViewById(R.id.NewCatLay);
        select = dialog.findViewById(R.id.button_select);
        dis = dialog.findViewById(R.id.inspect_image);

        NoImageSelected1 = dialog.findViewById(R.id.NoImageSelected);
        sign_back_Dialog1 = dialog.findViewById(R.id.sign_back_Dialog);

        yes1 = dialog.findViewById(R.id.Yes1);
        No1 = dialog.findViewById(R.id.No1);

        setFocusNew(CategoryDb);

        sign_back_Dialog1.setOnClickListener(v -> dialog.dismiss());

        CategoryDb.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CategoryLay.setErrorEnabled(false);
                CategoryLay.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Event on  above buttons
        select.setOnClickListener(view -> {
            chooseImage(); //User will select the image from gallery
        });

        yes1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check()){
                    if(dis.getDrawable() != null){
                        if (mUploadTask != null && mUploadTask.isInProgress()) {
                            Toast.makeText(getActivity(), "Upload in Progress", Toast.LENGTH_SHORT).show();
                        }else {
                            newTopCategoryConstructor = null;
                            send = null;
                            final String Search = CategoryDb.getText().toString();
                            final String Send = Search.substring(0,1).toUpperCase()+Search.substring(1).toLowerCase();
                            Categoryref.orderByChild("name").equalTo(Send).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        Toast.makeText(getContext(),"Already Exists",Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getContext(),"Not Exists",Toast.LENGTH_SHORT).show();
                                        uploadImage(Send);
                                        dialog.dismiss();
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }else{
                        Toast.makeText(getContext(),"No Image selected",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            private boolean check() {
                if(Objects.requireNonNull(CategoryDb.getText()).toString().length() >  2){
                    CategoryLay.setError(null);
                    CategoryLay.setErrorEnabled(false);
                    return true;
                }else{
                    CategoryLay.setError("Enter Category name");
                    CategoryLay.setErrorEnabled(true);
                    setFocusNew(CategoryDb);
                    return false;
                }
            }
        });

        No1.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    //Choose Image
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //Check  image request for upload
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null) {
            saveUri = data.getData();
            NoImageSelected1.setVisibility(View.GONE);
            dis.setImageURI(saveUri);
            select.setText(R.string.uploadeImage);
        } else if (requestCode == PICK_IMAGE_EDIT_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null) {
            saveUri1 = data.getData();
            selected.setImageURI(saveUri1);
            check = true;
        }
    }

    //Upload Image to Google Storage
    private boolean uploadImage(final String Name) {

        if (saveUri != null) {
            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("Category Photos/" + imageName);
            mUploadTask = imageFolder.putFile(saveUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getActivity(), "Done.", Toast.LENGTH_SHORT).show();
                        imageFolder.getDownloadUrl().addOnSuccessListener(uri -> {
                            //Set Value for new Category
                            send = uri.toString();
                            long UUid = System.currentTimeMillis();
                            newTopCategoryConstructor = new TopCategory_Constructor(send, Name);
                            Categoryref.child(String.valueOf(UUid)).setValue(newTopCategoryConstructor);
                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                            i = true;
                        });
                    }).addOnFailureListener(e -> Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show()).addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    });
        }
        return i;
    }

    //Change Image
    public void changeImage1() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_EDIT_REQUEST);
    }

    @Override
    public void onStart() {
        super.onStart();

           if(Status) {

               //Firebase Reference
               FirebaseRecyclerOptions<TopCategory_Constructor> options =
                       new FirebaseRecyclerOptions.Builder<TopCategory_Constructor>()
                               .setQuery(Categoryref, TopCategory_Constructor.class).build();

               adapter = new FirebaseRecyclerAdapter<TopCategory_Constructor, MenuMainAdapter>(options) {

                   @Override
                   public int getItemCount() {
                       return super.getItemCount();
                   }

                   @Override
                   protected void onBindViewHolder(@NonNull final MenuMainAdapter holder, final int i, @NonNull final TopCategory_Constructor topCategoryConstructor) {

                       final String item = getRef(i).getKey();
                       if(GetDeviceType.equalsIgnoreCase(UserType)){
                           holder.edit1.setVisibility(View.GONE);
                           holder.delete1.setVisibility(View.GONE);
                       }

                       assert item != null;
                       Categoryref.child(item).addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                               if (snapshot.exists()) {
                                   holder.itemName1.setText(topCategoryConstructor.getName());
                                   Picasso.get().load(topCategoryConstructor.getImage()).into(holder.itemPhoto1);
                               }
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError error) {

                           }
                       });

                       holder.setItemClickListener((view, position, isLongClick) -> {

                           String key = adapter.getRef(position).getKey();
                           Bundle bundle = new Bundle();
                           bundle.putString("SendAdmin", key);
                           FoodListAdmin fragment = new FoodListAdmin();
                           fragment.setArguments(bundle);
                           getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(Food).add(R.id.fragment_container_Food, fragment, foodList).commit();
                       });


                       holder.delete1.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               showDialogDelete();

                           }

                           private void showDialogDelete() {

                               AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                               alertDialog.setTitle("Delete Category");
                               alertDialog.setMessage("Delete Category with Sub-Categories ?");

                               //Set Button
                               alertDialog.setPositiveButton("Yes", (dialogInterface, l) -> {

                                   // Delete Food inside
                                   final String key1 = adapter.getRef(i).getKey();

                                   // Delete Category inside
                                   final String image = getItem(i).getImage();
                                   Query query = Categoryref.orderByChild("image").equalTo(image);
                                       query.addListenerForSingleValueEvent(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                                               for (DataSnapshot ds : snapshot.getChildren()) {
                                                   if(image.contains("firebasestorage")) {
                                                       FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                                       StorageReference storageReference = firebaseStorage.getReferenceFromUrl(image);
                                                       storageReference.delete();
                                                   }
                                                   ds.getRef().removeValue();
                                                   adapter.notifyItemRemoved(i);
                                                   adapter.notifyItemRangeChanged(i,adapter.getItemCount());
                                                   DeleteFoodList(key1);
                                               }
                                           }
                                           private void DeleteFoodList(String key) {
                                               final Query query = foodRef.orderByChild("menuId").equalTo(key);
                                               query.addValueEventListener(new ValueEventListener() {
                                                   @Override
                                                   public void onDataChange(@NonNull final DataSnapshot snapshot) {
                                                       for(final DataSnapshot snapshot1: snapshot.getChildren()){
                                                           final String ChildeNode = snapshot1.getKey();
                                                           Toast.makeText(getContext(),ChildeNode,Toast.LENGTH_SHORT).show();
                                                           foodRef.child(ChildeNode).child("image").addValueEventListener(new ValueEventListener() {
                                                               @Override
                                                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                   if(snapshot.exists()){
                                                                       Link = snapshot.getValue().toString();
                                                                       Toast.makeText(getContext(),Link,Toast.LENGTH_SHORT).show();
                                                                       storage.getReferenceFromUrl(Link).delete().addOnSuccessListener(aVoid -> {
                                                                           foodRef.child(ChildeNode).getRef().removeValue();
                                                                           Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
                                                                           adapter.notifyItemRemoved(i);
                                                                           adapter.notifyItemRangeChanged(i, 1);
                                                                       }).addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
                                                                   }
                                                               }
                                                               @Override
                                                               public void onCancelled(@NonNull DatabaseError error) {

                                                               }
                                                           });

                                                       }
                                                   }
                                                   @Override
                                                   public void onCancelled(@NonNull DatabaseError error) {

                                                   }
                                               });

                                           }
                                           @Override
                                           public void onCancelled(@NonNull DatabaseError error) {
                                           }
                                       });

                                   Toast.makeText(getActivity(), "Success.", Toast.LENGTH_SHORT).show();

                                   dialogInterface.dismiss();
                               });

                               alertDialog.setNegativeButton("No", (dialogInterface, l) -> dialogInterface.dismiss());
                               alertDialog.show();

                           }
                       });

                       holder.edit1.setOnClickListener(v -> {
                           //Show Dialog for Edit data
                           final Dialog holderView = new Dialog(getActivity());
                           WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                           holderView.setContentView(R.layout.edit_food_item_dialog);
                           lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                           lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                           holderView.getWindow().setAttributes(lp);
                           holderView.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                           holderView.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                           holderView.getWindow().setDimAmount(0.6f);
                           holderView.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                           //Heading
                           dynamicHeading1 = holderView.findViewById(R.id.dynamicHeading);
                           dynamicHeading1.setText("Edit Category");

                           //Hooks Upload data

                           // Change Image
                           editfinal = holderView.findViewById(R.id.food_select1);

                           // Get Text
                           editUpload = holderView.findViewById(R.id.foodEdit);

                           // Layout
                           editLayUpload = holderView.findViewById(R.id.NewCatLay);

                           //Display
                           selected = holderView.findViewById(R.id.change_image_final);

                           final String editPlace = getItem(i).getName();
                           editUpload.setText(editPlace);

                           Picasso.get().load(topCategoryConstructor.getImage()).into(selected);

                           setFocusNew(editUpload);

                           //Select image buttons
                           editfinal.setOnClickListener(view -> changeImage1());

                           editUpload.addTextChangedListener(new TextWatcher() {
                               @Override
                               public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                               }

                               @Override
                               public void onTextChanged(CharSequence s, int start, int before, int count) {
                                   editLayUpload.setErrorEnabled(false);
                                   editLayUpload.setError(null);
                               }

                               @Override
                               public void afterTextChanged(Editable s) {

                               }
                           });

                           final Button yes = holderView.findViewById(R.id.Yes);

                           Button no = holderView.findViewById(R.id.No);

                           yes.setOnClickListener(v12 -> {
                               if (Objects.requireNonNull(editUpload.getText()).toString().length() == 0) {
                                   editLayUpload.setError("Name Cannot be empty");
                                   editLayUpload.setErrorEnabled(true);
                                   return;
                               }
                               String edit = editUpload.getText().toString().substring(0, 1).toUpperCase() + editUpload.getText().toString().substring(1).toLowerCase();
                               if (edit.equals(editPlace)) {
                                   Toast.makeText(getActivity(), "Name not Changed.", Toast.LENGTH_SHORT).show();
                               } else {
                                   Categoryref.child(Objects.requireNonNull(getRef(i).getKey())).child("name").setValue(edit).addOnCompleteListener(task -> Toast.makeText(getActivity(), "Name Changed.", Toast.LENGTH_SHORT).show());
                               }
                               if (check) {
                                   final String imageReplace = getItem(i).getImage();
                                   StorageReference replace = FirebaseStorage.getInstance().getReferenceFromUrl(imageReplace);
                                   replace.delete();

                                   if (mUploadTask != null && mUploadTask.isInProgress()) {
                                       Toast.makeText(getActivity(), "Upload in Progress", Toast.LENGTH_SHORT).show();
                                   } else {
                                       //Upload Image to Google Storage
                                       if (saveUri1 != null) {
                                           String imageName = UUID.randomUUID().toString();
                                           final StorageReference imageFolder = storageReference.child("Category Photos/" + imageName);
                                           mUploadTask = imageFolder.putFile(saveUri1)
                                                   .addOnSuccessListener(taskSnapshot -> imageFolder.getDownloadUrl().addOnSuccessListener(uri -> {
                                                       //Set Value for new Category
                                                       image = uri.toString();
                                                       newRef.child(item).child("image").setValue(image);
                                                       adapter.notifyItemChanged(i);
                                                       image = null;
                                                       Toast.makeText(getActivity(), "Photo Updated.", Toast.LENGTH_SHORT).show();
                                                   })).addOnFailureListener(e -> Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show())
                                                   .addOnProgressListener(taskSnapshot -> {
                                                       double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                                   });
                                       }
                                   }
                                   check = false;
                               }
                               holderView.dismiss();
                           });

                           no.setOnClickListener(v1 -> holderView.dismiss());

                           holderView.show();

                       });
                   }

                   @NonNull
                   @Override
                   public MenuMainAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adminadd, parent, false);
                       return new MenuMainAdapter(view);
                   }

               };

               Status = false;
           }

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public void showFABMenu() {
        isFABOpen = true;
        upload.animate().translationY(-getResources().getDimension(R.dimen.standard_20));
        upload.setVisibility(View.VISIBLE);
        UserView.animate().translationY(-getResources().getDimension(R.dimen.standard_30));
        UserView.setVisibility(View.VISIBLE);
    }

    public void closeFABMenu() {
        isFABOpen = false;
        UserView.animate().translationY(0);
        UserView.setVisibility(View.GONE);
        upload.animate().translationY(0);
        upload.setVisibility(View.GONE);
    }

    // Animate Name
    public void slideUp(boolean State){

        // False up
        if(!State){
            //Name Go
            IntroName.startAnimation(TopUp);
            // Line
            whatLike11.startAnimation(TopUp);
            // Search
            Search.startAnimation(TopUp);
            // Suggestion List
            SearchSuggestion.setVisibility(View.VISIBLE);
            SearchSuggestion.startAnimation(TopUp);
            AdminCatLay.setVisibility(View.GONE);
            state = true;
        }
        // True Down
        else{
            //Name Go
            IntroName.startAnimation(TopDown);
            IntroName.setVisibility(View.VISIBLE);
            whatLike11.setVisibility(View.VISIBLE);
            // Line
            whatLike11.startAnimation(TopDown);
            // Search
            Search.startAnimation(TopDown);
            // Suggestion List
            SearchSuggestion.setVisibility(View.GONE);
            SearchSuggestion.startAnimation(TopDown);
            AdminCatLay.setVisibility(View.VISIBLE);
            state = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SearchBarCat11.getText().clear();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!Status){
            recyclerViewState = new Bundle();
            position = recyclerView.getLayoutManager().onSaveInstanceState();
            recyclerViewState.putParcelable(sendPosition,position);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(recyclerViewState != null){
            position = recyclerViewState.getBundle(sendPosition);
            recyclerView.getLayoutManager().onRestoreInstanceState(position);
        }
    }

    public void setFocusNew(TextInputEditText editText){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        editText.requestFocus();
        editText.setCursorVisible(true);
        imm.showSoftInput(editText,InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

}


