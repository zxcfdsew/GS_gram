package com.sample.gs_gram.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.sample.gs_gram.Fragment.HomeFragment;
import com.sample.gs_gram.Fragment.InfoFragment;
import com.sample.gs_gram.Fragment.InquireFragment;
import com.sample.gs_gram.Fragment.SaveFragment;
import com.sample.gs_gram.Fragment.SimulationFragment;
import com.sample.gs_gram.R;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.Toolbar);
        bottomNavigationView = findViewById(R.id.bottomnavigation);
        openFragment(new HomeFragment());
        getSupportActionBar().setTitle("졸업시뮬레이션");
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    openFragment(new HomeFragment());
                    getSupportActionBar().setTitle("홈");
                    return true;
                } else if (itemId == R.id.inquire) {
                    openFragment(new InquireFragment());
                    getSupportActionBar().setTitle("이수 현황");
                    return true;
                } else if (itemId == R.id.save) {
                    openFragment(new SaveFragment());
                    getSupportActionBar().setTitle("과목 조회");
                    return true;
                } else if (itemId == R.id.simulation) {
                    openFragment(new SimulationFragment());
                    getSupportActionBar().setTitle("졸업 시뮬레이션");
                    return true;
                } else if (itemId == R.id.info) {
                    openFragment(new InfoFragment());
                    getSupportActionBar().setTitle("내 정보");
                    return true;
                }
                return false;
            }
        });
    }
    private void openFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        if (fm != null) {
            // FragmentTransaction 호출 및 필요한 트랜잭션 작업 수행
            fm.beginTransaction().replace(R.id.frame_layout, fragment).commit();
        }
    }
}