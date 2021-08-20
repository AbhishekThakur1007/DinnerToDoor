package com.example.food.Login;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.food.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class intoViewPagerAdapter extends PagerAdapter {

    Context context;
    List<ScreenItem> screenItemList;

    public intoViewPagerAdapter(Context context, List<ScreenItem> screenItemList) {
        this.context = context;
        this.screenItemList = screenItemList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_pager,null);
        ImageView Image = view.findViewById(R.id.imageView);
        TextView Title = view.findViewById(R.id.textView2);
        TextView Description = view.findViewById(R.id.textView3);

        Title.setText(screenItemList.get(position).title);
        Description.setText(screenItemList.get(position).description);
        Image.setImageResource(screenItemList.get(position).getScreenImg());
        container.addView(view);

        return view;

    }

    @Override
    public int getCount() {
        return screenItemList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
