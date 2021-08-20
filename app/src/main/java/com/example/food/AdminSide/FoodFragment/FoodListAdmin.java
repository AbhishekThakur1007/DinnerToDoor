package com.example.food.AdminSide.FoodFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.AdminSide.AddNewFood.NewItem1;
import com.example.food.AdminSide.Interface.ItemClickListener;
import com.example.food.R;
import com.example.food.Common.FoodAdapter;
import com.example.food.UserSide.FoodDetails.foodDetail;
import com.example.food.Common.SessionManager;
import com.example.food.Common.Food_Constructor;
import com.example.food.Common.GetSearchDetails_Constructor;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class FoodListAdmin extends Fragment {

    // Food List And Search Box Recycler plus Adapter
    RecyclerView recyclerView,SearchBox;
    FirebaseRecyclerAdapter<Food_Constructor, FoodAdapter> adapter;
    FirebaseRecyclerAdapter<GetSearchDetails_Constructor, UserHolderClass> searchAdapter;

    // Database
    DatabaseReference foodRef,Display;

    // Floating Action Button
    FloatingActionButton add;

    String Value,Value1, item,link,Value2;
    String Check;
    boolean state;

    String send,GetDeviceType;
    FirebaseStorage storage;
    StorageReference storageReference;
    TextInputEditText SearchText;
    TextInputLayout AdminSearchBoxXect;
    TextView NameCategory,numberOfItems;
    ImageView ImageCat;
    RelativeLayout SuggestionLay,ImageTop,searchSuggestion,LayHeading;
    Animation TopUp,TopDown,IntroDown,IntroUp;
    List<String> suggestList = new ArrayList<>();
    public static final String NewFragment = "NewItem";
    private static final String UserType = "AdminTab";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fooList = inflater.inflate(R.layout.fragment_food_list_admin, container, false);

        recyclerView = fooList.findViewById(R.id.myView3);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SearchBox = fooList.findViewById(R.id.AdminSearchList);
        SearchBox.setLayoutManager(new LinearLayoutManager(getContext()));

        SessionManager sessionManager = new SessionManager(requireContext(),SessionManager.SESSION_USERSESSION);
        HashMap<String,String> USerID = sessionManager.getUserDetailFromSession();
        GetDeviceType = USerID.get(SessionManager.KEY_STAFF);

        //HOOKS
        add = (FloatingActionButton) fooList.findViewById(R.id.AddFood);

        if(GetDeviceType.equalsIgnoreCase(UserType)){
            add.setVisibility(View.GONE);
        }

        // Search
        AdminSearchBoxXect = fooList.findViewById(R.id.AdminSearchBoxXect);
        SearchText = fooList.findViewById(R.id.AdminSearchBar);

        // Name and Item
        NameCategory = fooList.findViewById(R.id.AdminnameTopFood);
        numberOfItems = fooList.findViewById(R.id.ItemCount);
        //Image of Category
        ImageCat = fooList.findViewById(R.id.AdminfromUpToDown);

        foodRef = FirebaseDatabase.getInstance().getReference().child("Foods");
        Display =  FirebaseDatabase.getInstance().getReference().child("Category");

        // Food Relative Layout
        ImageTop = fooList.findViewById(R.id.AdminTopHead);
        searchSuggestion = fooList.findViewById(R.id.AdminListVisibility);
        SuggestionLay = fooList.findViewById(R.id.AdminSearchAAja);
        LayHeading = fooList.findViewById(R.id.LayHeading);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogFood();
            }
        });

        //Get Intent From RetailerDashboardActivity
        Bundle bundle = this.getArguments();
        assert bundle != null;
        Value = bundle.getString("SendAdmin");
        Value1 = bundle.getString("SendName");
        Value2 = bundle.getString("SendNameWithPosition");

        // Animation
        IntroDown = AnimationUtils.loadAnimation(getContext(),R.anim.slide_in_bottom);
        IntroUp = AnimationUtils.loadAnimation(getContext(),R.anim.slide_in_top);
        TopUp = AnimationUtils.loadAnimation(getContext(),R.anim.food_from_down_to_up);
        TopDown = AnimationUtils.loadAnimation(getContext(),R.anim.top_send_down);

        //Set Animation
        ImageTop.setAnimation(IntroUp);
        LayHeading.setAnimation(IntroUp);
        AdminSearchBoxXect.setAnimation(IntroUp);
        recyclerView.setAnimation(IntroDown);

        if (Value != null) {
            if (!Value.isEmpty()) {
                Display(Value);
                loadEditMenu(Value);
            }
        }else{
            if(Value1 != null){
                Value = Value1;
                Display.orderByChild(Value1).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snap : snapshot.getChildren() ){
                            if(Objects.requireNonNull(snap.child("name").getValue()).toString().equals(Value1)){
                                String GetId = snap.getKey();
                                loadEditMenu(GetId);
                                Display(GetId);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }else{
                Value = Value2;
                foodRef.orderByChild("name").equalTo(Value).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            String Id= snapshot1.child("menuId").getValue().toString();
                            loadEditMenu(Id);
                            Display(Id);
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

        SearchBox.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(v);
                return true;
            }
        });

        ImageTop.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(v);
                return true;
            }
        });

        SearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0) {
                    SearchBox.setAdapter(null);
                }
                if (s.length() >= 1) {
                     send = s.toString();
                     String Send = send.substring(0, 1).toUpperCase() + send.substring(1);
                     firebaseSearch(Send);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Set Focus
        SearchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                slideUp(state);
                if(hasFocus){
                    SearchBox.setAdapter(null);
                }else{
                    SearchBox.setAdapter(null);
                    SearchText.getText().clear();
                }
            }
        });

        SearchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
                    SearchText.clearFocus();
                    return true;
                }
                return false;
            }
        });

    // Inflate the layout for this fragment
        return fooList;
    }

    private void firebaseSearch(String Text) {
        int length = Text.length();
        final String name = Text.substring(0,1).toUpperCase() + Text.substring(1,length).toLowerCase();

            Query firebaseQuery = foodRef.orderByChild("name").startAt(name).endAt(name + "\uf8ff");

            FirebaseRecyclerOptions<GetSearchDetails_Constructor> options =
                    new FirebaseRecyclerOptions.Builder<GetSearchDetails_Constructor>()
                            .setQuery(firebaseQuery, GetSearchDetails_Constructor.class).build();

        searchAdapter = new FirebaseRecyclerAdapter<GetSearchDetails_Constructor, UserHolderClass>(options) {
                @NonNull
                @Override
                public UserHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion, parent, false);
                    return new UserHolderClass(view);
                }

                @Override
                protected void onBindViewHolder(@NonNull final UserHolderClass holder, int i, @NonNull final GetSearchDetails_Constructor getSearchDetailsConstructor) {

                    String Image = getSearchDetailsConstructor.getImage();

                    if(Image.contains("firebasestorage")){
                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(Image);
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(holder.circleImageView);
                            }
                        });
                    }else{
                        Picasso.get().load(getSearchDetailsConstructor.getImage()).into(holder.circleImageView);
                    }

                    holder.Available.setText(null);
                    holder.FoodName.setText(getSearchDetailsConstructor.getName());

                    holder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            Intent it = new Intent(getActivity(), foodDetail.class);
                            it.putExtra("Search1",searchAdapter.getItem(position).getName());
                            startActivity(it);
                        }
                    });

                }
            };
            SearchBox.setAdapter(searchAdapter);
           searchAdapter.startListening();
    }

    private void Display(String MenuId) {

        Display.child(MenuId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    NameCategory.setText(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                    link = Objects.requireNonNull(snapshot.child("image").getValue()).toString();
                    if(link.isEmpty()){
                        Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Picasso.get().load(link).into(ImageCat);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        foodRef.orderByChild("menuId").equalTo(Value).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot: snapshot.getChildren()){
                    Food_Constructor item = postSnapshot.getValue(Food_Constructor.class);
                    suggestList.add(item.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDialogFood() {
        NewItem1 newItem1= new NewItem1();
        newItem1.NewItem1(Value,"New");
        newItem1.show(getParentFragmentManager().beginTransaction().addToBackStack("food1"),NewFragment);
    }

    private void loadEditMenu(final String value) {

        //Firebase Reference
        FirebaseRecyclerOptions<Food_Constructor> options =
                new FirebaseRecyclerOptions.Builder<Food_Constructor>()
                        .setQuery(foodRef.orderByChild("menuId").equalTo(value), Food_Constructor.class).build();

        adapter = new FirebaseRecyclerAdapter<Food_Constructor, FoodAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodAdapter foodAdapter, final int i, @NonNull final Food_Constructor foodConstructor) {

                if(GetDeviceType.equalsIgnoreCase(UserType)){
                    foodAdapter.delete1.setVisibility(View.GONE);
                    foodAdapter.edit1.setVisibility(View.GONE);
                }

                item = getRef(i).getKey();
                foodAdapter.foodName.setText(foodConstructor.getName());
                Picasso.get().load(foodConstructor.getImage()).into(foodAdapter.foodPhoto);

                foodAdapter.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });

                foodRef.child(adapter.getRef(i).getKey()).child("Special").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            foodAdapter.Fav.setBackgroundResource(R.drawable.ic_baseline_favorite_red);
                        }else{
                            foodAdapter.Fav.setBackgroundResource(R.drawable.ic_baseline_favorite);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if(Value2 != null){
                    if(Value2.equals(adapter.getItem(i).getName())){
                        Toast.makeText(getContext(),Value2,Toast.LENGTH_SHORT).show();
                        recyclerView.smoothScrollToPosition(i);
                    }
                }

                foodRef.child(adapter.getRef(i).getKey()).child("Available").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String get = snapshot.getValue().toString();
                            if(get.equals("Yes")){
                                foodAdapter.Availability.setText("Available");
                            }else{
                                foodAdapter.Availability.setText("Not Available");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

                numberOfItems.setText(String.valueOf(Objects.requireNonNull(recyclerView.getAdapter()).getItemCount()));

                // Special Items
                foodAdapter.Fav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!GetDeviceType.equalsIgnoreCase(UserType)){
                            SpecialItemDialog(adapter.getRef(i).getKey(),foodAdapter.getAdapterPosition());
                        }
                    }

                    private void SpecialItemDialog(String GetParentNode,int position) {

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

                       foodRef.child(GetParentNode).child("Special").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    Check = "yes";
                                }else{
                                    Check = "No";
                                }
                                Send(Check,position);
                            }

                           private void Send(String check,int position) {
                               if(check.equals("No")){
                                   HeadingLocButton.setText("Add to special items ?");
                                   Check = "";
                                   Yes.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           String Parent = adapter.getRef(i).getKey();
                                           foodRef.child(Parent).child("Special").setValue("yes").addOnSuccessListener(new OnSuccessListener<Void>() {
                                               @Override
                                               public void onSuccess(Void aVoid) {
                                                   foodAdapter.Fav.setBackgroundResource(R.drawable.ic_baseline_favorite_red);
                                                   Toast.makeText(getContext(),"Added",Toast.LENGTH_SHORT).show();
                                                   adapter.notifyItemChanged(position);
                                                   dialog.dismiss();
                                               }
                                           });
                                       }
                                   });
                               }else{
                                   Check = "";
                                   HeadingLocButton.setText("Remove Item ?");
                                   Yes.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           final String Parent = adapter.getRef(i).getKey();
                                           foodRef.child(Parent).child("Special").setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                                               @Override
                                               public void onSuccess(Void aVoid) {
                                                   foodAdapter.Fav.setBackgroundResource(R.drawable.ic_baseline_favorite_red);
                                                   Toast.makeText(getContext(),"Item Removed",Toast.LENGTH_SHORT).show();
                                                   adapter.notifyItemChanged(Integer.parseInt(Parent));
                                                   dialog.dismiss();
                                               }
                                           });

                                       }
                                   });
                               }
                           }

                           @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                        No.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });


                        dialog.show();
                    }
                });

                // Available Or not
                foodAdapter.Availability.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckAvail(adapter.getRef(i).getKey(),foodAdapter.getAdapterPosition());

                    }
                    private void CheckAvail(String GetParentNode,int position) {

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

                        HeadingLocButton.setText("Item Available ?");
                        final DatabaseReference Avail = foodRef.child(GetParentNode).child("Available");

                        Yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Avail.setValue("Yes").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(),"Updated",Toast.LENGTH_SHORT).show();
                                        adapter.notifyItemChanged(position);
                                        HeadingLocButton.setText("Available");
                                        dialog.dismiss();
                                    }
                                });

                            }
                        });

                        No.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Avail.setValue("No").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(),"Updated",Toast.LENGTH_SHORT).show();
                                        adapter.notifyDataSetChanged();
                                        HeadingLocButton.setText("Not Available");
                                        foodAdapter.ViewCustom.setAlpha(Float.parseFloat("0.6"));
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });

                        dialog.show();

                    }
                });

                //Delete Food item
                foodAdapter.delete1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialogDelete();
                    }


                    private void showDialogDelete() {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle("Delete Food ?");

                        //Set Button
                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int l) {

                                final String image = getItem(i).getImage();

                                Query query = foodRef.orderByChild("image").equalTo(image);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                            StorageReference storageReference = firebaseStorage.getReferenceFromUrl(image);
                                            storageReference.delete();
                                            ds.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                Toast.makeText(getActivity(), "Success.", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                            }
                        });

                        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int l) {
                                dialogInterface.dismiss();
                            }
                        });
                        alertDialog.show();

                    }
                });

               //Edit Item
                foodAdapter.edit1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        NewItem1 newItem = new NewItem1();
                        newItem.NewItem1(adapter.getRef(i).getKey(),"Update");
                        newItem.show(getParentFragmentManager().beginTransaction().addToBackStack("food1"),NewFragment);
                    }
                });
            }


            @NonNull
            @Override
            public FoodAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_list_admin, parent, false);
                return new FoodAdapter(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    // Animate Name
    public void slideUp(boolean State){

        // False up
        if(!State){
            //Name Go
            LayHeading.startAnimation(TopUp);
            TopUp.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    LayHeading.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            // Search
            SuggestionLay.startAnimation(TopUp);
            // Suggestion List
            searchSuggestion.setVisibility(View.VISIBLE);
            searchSuggestion.startAnimation(TopUp);
            recyclerView.setVisibility(View.GONE);
            state = true;
        }
        // True Down
        else{
            //Name Go
            LayHeading.setVisibility(View.VISIBLE);
            LayHeading.startAnimation(TopDown);
            // Search
            SuggestionLay.startAnimation(TopDown);
            // Suggestion List
            searchSuggestion.startAnimation(TopDown);
            searchSuggestion.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.startAnimation(TopDown);
            state = false;
        }
    }

    public void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    public static class UserHolderClass extends RecyclerView.ViewHolder implements View.OnClickListener{

        View view;
        ItemClickListener itemClickListener;
        CircleImageView circleImageView;
        TextView Available,FoodName;

        public UserHolderClass(@NonNull View itemView) {
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

}