package com.example.food.UserSide.MenuFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food.AdminSide.Interface.ItemClickListener;
import com.example.food.Common.MenuMainAdapter;
import com.example.food.Common.TopCategory_Constructor;
import com.example.food.Common.UserDetails_Constructor;
import com.example.food.UserSide.Database;
import com.example.food.R;
import com.example.food.Common.GetSearchDetails_Constructor;
import com.example.food.Common.TopCategoryAdapter;
import com.example.food.UserSide.FoodDetails.foodDetail;
import com.example.food.UserSide.FoodFragment.FoodList;
import com.example.food.Common.SessionManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
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

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserMenuFragment extends Fragment {

    //Recycler View
    private RecyclerView recyclerView,ListRecycler,TopCategory;

    //Adapters
    FirebaseRecyclerAdapter<TopCategory_Constructor, MenuMainAdapter> adapter;
    FirebaseRecyclerAdapter<GetSearchDetails_Constructor,UserHolder> adapterFinal;
    FirebaseRecyclerAdapter<TopCategory_Constructor, TopCategoryAdapter> topAdapter;

    private DatabaseReference Categoryref, foodRef;

    Query Query;
    TextView introName1;
    public final static String foodListTag = "foodFrag";
    String ID,item;
    RelativeLayout IntroName,Search,TopCat,Go,IntroLayoutStart,whatLike1,SearchSuggestion;
    Animation TopUp,TopDown,IntroAnimUp,IntroAnimDown;
    TextInputLayout textInputSearch1;
    TextInputEditText SearchItem;
    boolean state;
    private Parcelable position = null;
    public static Bundle recyclerViewState;
    public final static String sendPosition = "StatusPosition";
    boolean Status,FoodList,Detail;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Status = true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NotNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View menu = inflater.inflate(R.layout.fragment_retailer_dashboard, container, false);

        state =false;
        //Layout Intro Main
        IntroName = menu.findViewById(R.id.IntroLayout);
        Search = menu.findViewById(R.id.SearchAja);
        SearchSuggestion = menu.findViewById(R.id.ListSuggestion);
        //All Top Category Main
        Go = menu.findViewById(R.id.CatLay);

        // Inside Intro
        IntroLayoutStart = menu.findViewById(R.id.IntroLayout1);
        whatLike1 = menu.findViewById(R.id.whatLike);

        //Inside Search Bar
        textInputSearch1 = menu.findViewById(R.id.textInputSearch); //Text Layout
        SearchItem = menu.findViewById(R.id.SearchBarCat);//Edit Text

        //Suggest List
        ListRecycler = menu.findViewById(R.id.SearchContent);
        ListRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // Top Category Relative
        TopCat = menu.findViewById(R.id.LayoutTop);
        recyclerView = menu.findViewById(R.id.myView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //Top category Recycler View
        TopCategory = menu.findViewById(R.id.TopCategory);
        TopCategory.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        //Animation Start
        IntroAnimUp = AnimationUtils.loadAnimation(getContext(),R.anim.slide_in_top);

        IntroAnimDown = AnimationUtils.loadAnimation(getContext(),R.anim.slide_in_bottom);

        // Animation Slide Up
        TopUp = AnimationUtils.loadAnimation(getContext(),R.anim.top_send_up);

        // Animation Slide Down
        TopDown = AnimationUtils.loadAnimation(getContext(),R.anim.top_send_down);

        //Set Anim
        IntroName.setAnimation(IntroAnimUp);
        whatLike1.setAnimation(IntroAnimUp);
        Search.setAnimation(IntroAnimUp);
        TopCat.setAnimation(IntroAnimUp);
        Go.setAnimation(IntroAnimDown);


        //Get Bundle for name
        introName1 = menu.findViewById(R.id.introName);
        SessionManager sessionManager = new SessionManager(requireContext(),SessionManager.SESSION_USERSESSION);
        HashMap<String, String> userDetails = sessionManager.getUserDetailFromSession();
        ID = userDetails.get(SessionManager.KEY_USERID);
        Database bd = new Database(getContext());
        UserDetails_Constructor userDetailsConstructor = bd.UserDetails(ID);
        introName1.setText(userDetailsConstructor.getFullName());



        //List
        Categoryref = FirebaseDatabase.getInstance().getReference().child("Category");
        foodRef = FirebaseDatabase.getInstance().getReference().child("Foods");

        //Order On Touch Listener > On Focus Listener > on Click Listener
        ListRecycler.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(v);
                    return true;
                }
            });

        //Set Focus
        SearchItem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                slideUp(state);
                if(hasFocus)
                {
                    ListRecycler.setAdapter(null);
                }else{
                    ListRecycler.setAdapter(null);
                    SearchItem.getText().clear();
                }
            }
        });

         SearchItem.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
                        SearchItem.clearFocus();
                        return true;
                    }
                    return false;
                }
            });

        SearchItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0){
                    ListRecycler.setAdapter(null);
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

            //When Search is pressed
            TopUp.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    IntroLayoutStart.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        return menu;
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
                        Intent intent =new Intent(getContext(), foodDetail.class);
                        intent.putExtra("Search1",topAdapter.getItem(i).getName());
                        startActivity(intent);
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

    // Search
    private void FirebaseSearch(final String item, boolean search) {
        int length = item.length();
        final String Forward = item.substring(0,1).toUpperCase() + item.substring(1,length).toLowerCase();

        if(search) {
            Query = Categoryref.orderByChild("name").startAt(Forward).endAt(Forward + "\uf8ff");
            Query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        FoodList = true;
                        /*Query = foodRef.orderByChild("name").startAt(Forward).endAt(Forward + "\uf8ff");*/
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
            final String Forward1 = item.toUpperCase();
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

        adapterFinal = new FirebaseRecyclerAdapter<GetSearchDetails_Constructor, UserHolder>(options) {
            @NonNull
            @Override
            public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion, parent, false);
                if(getItemCount() == 0){
                    Toast.makeText(getContext(),"Haha",Toast.LENGTH_SHORT).show();
                }
                return new UserHolder(view);
            }

            @Override
            public int getItemCount() {
                return  super.getItemCount();
            }

            @Override
            protected void onBindViewHolder(@NonNull final UserHolder holder, int i, @NonNull final GetSearchDetails_Constructor getSearchDetailsConstructor) {

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
                    public void onClick(View view, int position1, boolean isLongClick) {
                        if(Detail){
                         Detail = false;
                        Intent it = new Intent(getActivity(), foodDetail.class);
                        it.putExtra("Search1",adapterFinal.getItem(position1).getName());
                        startActivity(it);
                        }else if (FoodList){
                            FoodList = false;
                            Bundle bundle = new Bundle();
                            bundle.putString("SendFinal",adapterFinal.getItem(position1).getName());
                            //Set Argument Class
                            com.example.food.UserSide.FoodFragment.FoodList send = new FoodList();
                            send.setArguments(bundle);
                            FoodList fragment = new FoodList();
                            fragment.setArguments(bundle);
                            assert getParentFragmentManager() != null;
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment,foodListTag).addToBackStack("Hidden")
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                        }
                    }
                });

            }
        };
        ListRecycler.setAdapter(adapterFinal);
        adapterFinal.startListening();
    }

    public static class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        View view;
        ItemClickListener itemClickListener;
        CircleImageView circleImageView;
        TextView Available,FoodName;

        public UserHolder(@NonNull View itemView) {
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

    // Animate Name
    public void slideUp(boolean State){

        // False up
        if(!State){
            //Name Go
            IntroLayoutStart.startAnimation(TopUp);
            // Line
            whatLike1.startAnimation(TopUp);
            // Search
            Search.startAnimation(TopUp);
            // Suggestion List
            SearchSuggestion.setVisibility(View.VISIBLE);
            SearchSuggestion.startAnimation(TopUp);
            Go.setVisibility(View.GONE);
            state = true;
        }

        // True Down
        else{
            //Name Go
            IntroLayoutStart.startAnimation(TopDown);
            IntroLayoutStart.setVisibility(View.VISIBLE);
            // Line
            whatLike1.startAnimation(TopDown);
            // Search
            Search.startAnimation(TopDown);
            // Suggestion List
            SearchSuggestion.setVisibility(View.GONE);
            Go.setVisibility(View.VISIBLE);
            state = false;
        }
    }

    //List Category Display
    @Override
    public void onStart() {
        super.onStart();

        if(Status){
        //Firebase Reference
        FirebaseRecyclerOptions<TopCategory_Constructor> options =
                new FirebaseRecyclerOptions.Builder<TopCategory_Constructor>()
                .setQuery(Categoryref, TopCategory_Constructor.class).build();

         adapter = new FirebaseRecyclerAdapter<TopCategory_Constructor, MenuMainAdapter>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final MenuMainAdapter holder, int i, @NonNull TopCategory_Constructor topCategoryConstructor) {


                holder.itemName.setText(topCategoryConstructor.getName());
                Picasso.get().load(topCategoryConstructor.getImage()).into(holder.itemPhoto);

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        String key = adapter.getRef(position).getKey();
                        Bundle bundle = new Bundle();
                        bundle.putString("Send",key);

                        //Set Argument Class
                        FoodList fragment = new FoodList();
                        fragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("Hidden").add(R.id.fragment_container,fragment,foodListTag).commit();
                    }
                });
            }
            @NonNull
            @Override
            public MenuMainAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_menupage, parent, false);
                return new MenuMainAdapter(view);
            }

             @Override
             public int getItemCount() {
                 return super.getItemCount();
             }
         };
            Status = false;
        }
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                /*position = recyclerView.get.getLayoutManager()*/
            }
        });

        TopCategory1();
    }

    @Override
    public void onResume() {
        super.onResume();
        SearchItem.getText().clear();
        SearchItem.clearFocus();
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

    public void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

}



