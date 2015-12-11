package library.streethawk.com.appmarshmallow;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class ThirdActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
    }

    public void animate(View view){
        LinearLayout dialog   = (LinearLayout)findViewById(R.id.dialog);
        dialog.setVisibility(LinearLayout.VISIBLE);
        Animation animation   =    AnimationUtils.loadAnimation(this, R.anim.anim);
        animation.setDuration(500);
        dialog.setAnimation(animation);
        dialog.animate();
        animation.start();
    }


}
