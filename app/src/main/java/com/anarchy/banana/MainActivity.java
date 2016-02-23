package com.anarchy.banana;

import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.anarchy.banana.widget.Banana;
import com.anarchy.banana.widget.BananaLayout;
import com.anarchy.banana.widget.CircleImageView;


public class MainActivity extends AppCompatActivity {
    Banana mBanana;
    BananaLayout mBananaLayout;
    SoundPool mSoundPool;
    ImageView mImageView;
    int soundId;
    int[] bananas = new int[]{R.mipmap.icon_banana_1,R.mipmap.icon_banana_2,R.mipmap.icon_banana_3
    ,R.mipmap.icon_banana_4,R.mipmap.icon_banana_5};
    int[] multiples = new int[]{R.mipmap.banana_x1,R.mipmap.banana_x2,R.mipmap.banana_x3,
            R.mipmap.banana_x4,R.mipmap.banana_x5};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBananaLayout = (BananaLayout) findViewById(R.id.banana_layout);
        mBanana = (Banana) findViewById(R.id.banana);
        mImageView = (ImageView) findViewById(R.id.banana_multiple);
        mBanana.setBananas(bananas);
        mBanana.putBanana(5);
        updateMultiCount();
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        soundId = mSoundPool.load(this,R.raw.banana,1);
        mBananaLayout.setBananaActionListener(new BananaLayout.BananaActionListener() {

            @Override
            public void onBananaIsAte(Banana banana) {
                Toast.makeText(MainActivity.this,"投食成功!",Toast.LENGTH_LONG).show();
                mSoundPool.play(soundId,1,1,0,0,1);
            }
        });

    }

    public void onMinusBanana(View view){
        mBanana.previous();
        updateMultiCount();
    }

    public void onPlusBanana(View view){
        mBanana.next();
        updateMultiCount();
    }

    private void updateMultiCount(){
        mImageView.setImageResource(multiples[mBanana.getBananaCount()-1]);
    }
}
