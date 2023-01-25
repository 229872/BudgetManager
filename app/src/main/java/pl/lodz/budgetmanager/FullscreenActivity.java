package pl.lodz.budgetmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class FullscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        String imageUrl = getIntent().getStringExtra("image_url");
        ImageView imageView = findViewById(R.id.receiptPhoto);


        System.out.println(imageUrl);
        System.out.println(imageView);

        if(imageView!=null && imageUrl!=null)
            Picasso.get().load(imageUrl).into(imageView);
    }

}