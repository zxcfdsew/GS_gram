package com.sample.gs_gram.Activity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.sample.gs_gram.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVActivity extends AppCompatActivity {
    private TextView csv_reader;
    private Button save_button;
    private FirebaseFirestore mStore;
    private ArrayList<List<String>> contentList;


    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_csv);

        csv_reader = findViewById(R.id.csv_reader);
        save_button = findViewById(R.id.save_button);


        mStore = FirebaseFirestore.getInstance();

        try {
            loadData();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        }
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                Toast.makeText(CSVActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void loadData() throws IOException, CsvException {
        AssetManager assetManager = getAssets();
        InputStream inputStream = assetManager.open("subject.csv");
        CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream, "EUC-KR"));

        List<String[]> allContent = csvReader.readAll();
        contentList = new ArrayList<>();

        for (String[] content : allContent) {
            List<String> rowData = Arrays.asList(content);
            contentList.add(rowData);
        }
        csv_reader.setText(contentList.toString());
    }
    private void saveData(){
        for (int i = 0; i < contentList.size(); i++){
            String collectionName1 = contentList.get(i).get(0); // 학과
            String documentName1 = contentList.get(i).get(1); // 이수 구분 (교양/교선/교필/전선/전필)
            String collectionName2 = contentList.get(i).get(4);//학기
            String documentName2 =  contentList.get(i).get(6); //교과목번호

            Map<String, Object> data = new HashMap<>();
            data.put("department",contentList.get(i).get(0)); //교과목 학과
            data.put("divition",contentList.get(i).get(1)); //이수구분
            data.put("subject",contentList.get(i).get(2)); //과목명
            data.put("grade",contentList.get(i).get(3)); //학년
            data.put("term",contentList.get(i).get(4)); //학기
            data.put("credit",contentList.get(i).get(5)); //학점
            data.put("code",contentList.get(i).get(6)); //교과목번호
            data.put("field",contentList.get(i).get(7)); //교양구분

            CollectionReference collectionReference1 = mStore.collection(collectionName1);
            DocumentReference documentReference1 = collectionReference1.document(documentName1);
            CollectionReference collectionReference2 = documentReference1.collection(collectionName2);
            DocumentReference documentReference2 = collectionReference2.document(documentName2);

            mStore.collection(collectionName1).document(documentName1).collection(collectionName2).document(documentName2).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            //컬렉션, 문서가 존재할 경우
                            documentReference2.update(data);
                        }else {
                            documentReference2.set(data);
                        }
                    } else {
                        documentReference2.set(data);
                    }
                    finish();
                }
            });
        }
        Log.d("csvasdqwe","저장완료됨");
    }
}
