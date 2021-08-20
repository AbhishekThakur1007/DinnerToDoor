package com.example.food.UserSide.FoodFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.food.AdminSide.Interface.ItemClickListener;
import com.example.food.Common.FoodAdapter;
import com.example.food.R;
import com.example.food.Common.Food_Constructor;
import com.example.food.Common.GetSearchDetails_Constructor;
import com.example.food.UserSide.FoodDetails.foodDetail;
import com.example.food.Common.SessionManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class FoodList extends Fragment {

    RecyclerView recyclerView, SearchRecycler;
    DatabaseReference foodRef;
    FirebaseRecyclerAdapter<Food_Constructor, FoodAdapter> adapter;
    FirebaseRecyclerAdapter<GetSearchDetails_Constructor, UserHolderClass> adapterFinal;
    String receiveValue, SendFinalSearch, GetId;
    boolean state;

    // Search
    TextInputEditText SearchText;

    DatabaseReference CategorySearch;

    RelativeLayout ListVisibility, SearchBoxXectFinal, ItemNumber, topImageLayout;
    String send, link, Staff;
    ImageView fromCategory1;
    TextView Name1, ItemCount;
    Animation TopUp, TopDown, IntroDown, IntroUp;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fooList = inflater.inflate(R.layout.fragment_food_list, container, false);

        //Suggestion List
        ListVisibility = fooList.findViewById(R.id.ListVisibility);

        // Food list Layout
        recyclerView = fooList.findViewById(R.id.myView2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Top layout
        topImageLayout = fooList.findViewById(R.id.TopHead);
        fromCategory1 = fooList.findViewById(R.id.fromUpToDown);
        ItemNumber = fooList.findViewById(R.id.ItemNumber);
        // Top Image
        Name1 = fooList.findViewById(R.id.nameTopFood);
        ItemCount = fooList.findViewById(R.id.ItemCount);

        SessionManager sessionManager = new SessionManager(getContext(), SessionManager.SESSION_USERSESSION);
        final HashMap<String, String> userDetails = sessionManager.getUserDetailFromSession();
        Staff = Objects.requireNonNull(userDetails.get(SessionManager.KEY_STAFF));

        // Animation
        IntroDown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_bottom);
        IntroUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_top);
        TopUp = AnimationUtils.loadAnimation(getContext(), R.anim.food_from_down_to_up);
        TopDown = AnimationUtils.loadAnimation(getContext(), R.anim.top_send_down);

        // Reference
        CategorySearch = FirebaseDatabase.getInstance().getReference().child("Category");
        foodRef = FirebaseDatabase.getInstance().getReference().child("Foods");

        // Search Layout
        SearchBoxXectFinal = fooList.findViewById(R.id.SearchAAja);
        SearchRecycler = fooList.findViewById(R.id.SearchList);
        SearchRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        //Edit Text
        SearchText = fooList.findViewById(R.id.SearchBar);

        //Set Animation
        topImageLayout.setAnimation(IntroUp);
        ItemNumber.setAnimation(IntroUp);
        SearchBoxXectFinal.setAnimation(IntroUp);
        recyclerView.setAnimation(IntroDown);

        SearchRecycler.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(v);
                return true;
            }
        });

        topImageLayout.setOnTouchListener(new View.OnTouchListener() {
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
                if (s.length() == 0) {
                    SearchRecycler.setAdapter(null);
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
                if (hasFocus) {
                    SearchRecycler.setAdapter(null);
                } else {
                    SearchRecycler.setAdapter(null);
                    SearchText.getText().clear();
                }
            }
        });

        SearchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    SearchText.clearFocus();
                    return true;
                }
                return false;
            }
        });


        //Get Intent From RetailerDashboardActivity
        Bundle bundle = this.getArguments();
        assert bundle != null;
        receiveValue = bundle.getString("Send");
        SendFinalSearch = bundle.getString("SendFinal");

        if (receiveValue != null) {
            /*chl.setText(receiveValue);*/
            if (!receiveValue.isEmpty()) {

                loadMenu(receiveValue);
            }

            //Got name
        } else {
            CategorySearch.orderByChild(SendFinalSearch).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Search name matches with snap then Get ID main  after that get MenuId and order by menu Id
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        if (Objects.requireNonNull(snap.child("name").getValue()).toString().equals(SendFinalSearch)) {
                            GetId = snap.getKey();
                            loadMenu(GetId);
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        return fooList;
    }

    // Animate Name
    public void slideUp(boolean State) {

        // False up
        if (!State) {
            //Name Go
            ItemNumber.startAnimation(TopUp);
            TopUp.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ItemNumber.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            // Search
            SearchBoxXectFinal.startAnimation(TopUp);
            // Suggestion List
            ListVisibility.setVisibility(View.VISIBLE);
            ListVisibility.startAnimation(TopUp);
            recyclerView.setVisibility(View.GONE);
            state = true;
        }
        // True Down
        else {
            //Name Go
            ItemNumber.setVisibility(View.VISIBLE);
            ItemNumber.startAnimation(TopDown);
            // Search
            SearchBoxXectFinal.startAnimation(TopDown);
            // Suggestion List
            ListVisibility.startAnimation(TopDown);
            ListVisibility.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.startAnimation(TopDown);
            state = false;
        }
    }

    private void firebaseSearch(String Text) {

        Query firebaseQuery = foodRef.orderByChild("name").startAt(Text).endAt(Text + "\uf8ff");

        FirebaseRecyclerOptions<GetSearchDetails_Constructor> options =
                new FirebaseRecyclerOptions.Builder<GetSearchDetails_Constructor>()
                        .setQuery(firebaseQuery, GetSearchDetails_Constructor.class).build();

        adapterFinal = new FirebaseRecyclerAdapter<GetSearchDetails_Constructor, UserHolderClass>(options) {
            @NonNull
            @Override
            public UserHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion, parent, false);
                return new UserHolderClass(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final UserHolderClass holder, int i, @NonNull final GetSearchDetails_Constructor getSearchDetailsConstructor) {

                String Image = getSearchDetailsConstructor.getImage();

                if (Image.contains("firebasestorage")) {
                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                    StorageReference storageReference = firebaseStorage.getReferenceFromUrl(Image);
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(holder.circleImageView);
                        }
                    });
                } else {
                    Picasso.get().load(getSearchDetailsConstructor.getImage()).into(holder.circleImageView);
                }

                holder.Available.setText(null);
                holder.FoodName.setText(getSearchDetailsConstructor.getName());

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent it = new Intent(getActivity(), foodDetail.class);
                        it.putExtra("Search1", adapterFinal.getItem(position).getName());
                        startActivity(it);
                    }
                });

            }
        };
        SearchRecycler.setAdapter(adapterFinal);
        adapterFinal.startListening();
    }

    public static class UserHolderClass extends RecyclerView.ViewHolder implements View.OnClickListener {

        View view;
        ItemClickListener itemClickListener;
        CircleImageView circleImageView;
        TextView Available, FoodName;

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
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }
    }

    private void loadMenu(String receiveValue) {

        CategorySearch.child(receiveValue).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Name1.setText(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                link = Objects.requireNonNull(snapshot.child("image").getValue()).toString();
                Picasso.get().load(link).into(fromCategory1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Firebase Reference
        FirebaseRecyclerOptions<Food_Constructor> options =
                new FirebaseRecyclerOptions.Builder<Food_Constructor>()
                        .setQuery(foodRef.orderByChild("menuId").equalTo(receiveValue), Food_Constructor.class).build();

        adapter = new FirebaseRecyclerAdapter<Food_Constructor, FoodAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodAdapter foodAdapter, int i, @NonNull Food_Constructor foodConstructor) {

                foodAdapter.ViewCustom.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_bottom));

                foodAdapter.foodName.setText(foodConstructor.getName());
                Picasso.get().load(foodConstructor.getImage()).into(foodAdapter.foodPhoto);

                foodAdapter.Favourites.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Picasso.get().load(R.drawable.ic_baseline_favorite_red).into(foodAdapter.Favourites);
                    }
                });

                final Food_Constructor clickItem = foodConstructor;
                ItemCount.setText(String.valueOf(Objects.requireNonNull(recyclerView.getAdapter()).getItemCount()));
                foodAdapter.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent i = new Intent(getActivity(), foodDetail.class);
                        i.putExtra("Details", adapter.getRef(position).getKey());
                        startActivity(i);
                    }
                });

            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }

            @NonNull
            @Override
            public FoodAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_list, parent, false);
                return new FoodAdapter(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}

