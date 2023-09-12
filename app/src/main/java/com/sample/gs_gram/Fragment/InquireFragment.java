package com.sample.gs_gram.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sample.gs_gram.Adapter.CartAdapter;
import com.sample.gs_gram.Adapter.InquireAdapter;
import com.sample.gs_gram.Data.SubjectData;
import com.sample.gs_gram.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InquireFragment extends Fragment {
    private TextView yearText, termText;
    private HorizontalScrollView chip_group;
    private Chip chip_whole, chip1, chip2, chip3, chip4, chip5, chip6;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private InquireAdapter mAdapter;
    private RecyclerView inquireRecyclerView;
    private RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedIstanceState){
        View view = inflater.inflate(R.layout.fragment_inquire,container,false);

        yearText = view.findViewById(R.id.yearText);
        termText = view.findViewById(R.id.termText);
        inquireRecyclerView = view.findViewById(R.id.inquireRecyclerView);

        inquireRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(),1));
        inquireRecyclerView.setHasFixedSize(true);
        inquireRecyclerView.setLayoutManager(layoutManager);

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        yearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showYearDialog();
            }
        });

        termText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTermDialog();
            }
        });

        chip_group = view.findViewById(R.id.chip_group);
        chip_whole = view.findViewById(R.id.chip_whole);
        chip1 = view.findViewById(R.id.chip1);
        chip2 = view.findViewById(R.id.chip2);
        chip3 = view.findViewById(R.id.chip3);
        chip4 = view.findViewById(R.id.chip4);
        chip5 = view.findViewById(R.id.chip5);
        chip6 = view.findViewById(R.id.chip6);

        View.OnClickListener subjectClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int viewId = view.getId();

                if (viewId == R.id.chip_whole){
                    //전체
                    InquireData(null);
                }else if (viewId == R.id.chip1){
                    //전필
                    InquireData("전필");
                }else if (viewId == R.id.chip2){
                    //전선
                    InquireData("전선");
                }else if (viewId == R.id.chip3){
                    //교필
                    InquireData("교필");
                }else if (viewId == R.id.chip4){
                    //교선
                    InquireData("교선");
                }else if (viewId == R.id.chip5){
                    //교양
                    InquireData("교양");
                }else if (viewId == R.id.chip6){
                    //기타
                    InquireData("기타");
                }
            }
        };

        chip_whole.setOnClickListener(subjectClickListener);
        chip1.setOnClickListener(subjectClickListener);
        chip2.setOnClickListener(subjectClickListener);
        chip3.setOnClickListener(subjectClickListener);
        chip4.setOnClickListener(subjectClickListener);
        chip5.setOnClickListener(subjectClickListener);
        chip6.setOnClickListener(subjectClickListener);

        return view;
    }

    private void showYearDialog() {
        String[] yearOptions = {"2023년", "2022년", "2021년", "2020년", "2019년", "2018년", "2017년"};
        showSelectionDialog("이수 년도를 선택해주세요.", yearOptions, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String selectedOption = yearOptions[which];
                yearText.setText(selectedOption);

                if (termText.getText().toString().isEmpty()) {
                    showTermDialog();
                } else {
                    InquireData(null);
                }
            }
        });
    }

    private void showTermDialog() {
        String[] termOptions = {"1학년 1학기", "1학년 2학기","2학년 1학기","2학년 2학기","3학년 1학기","3학년 2학기","4학년 1학기","4학년 2학기","5학년 1학기","5학년 2학기","1학년 여름계절학기","1학년 겨울계절학기","2학년 여름계절학기","2학년 겨울계절학기","3학년 여름계절학기","3학년 겨울계절학기","4학년 여름계절학기","4학년 겨울계절학기"};
        showSelectionDialog("과목을 이수한 학기를 선택해주세요.", termOptions, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String selectedOption = termOptions[which];
                termText.setText(selectedOption);

                InquireData(null);
            }
        });
    }
    private void showSelectionDialog(String title, String[] options, DialogInterface.OnClickListener clickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title)
                .setItems(options, clickListener)
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void InquireData(String filterDivition){
        String strYear = yearText.getText().toString();
        String strTerm = termText.getText().toString();
        String period = strYear + " " +strTerm;

        ArrayList<SubjectData> mDatas = new ArrayList<>();

        if (strYear.length() == 0) {
            Toast.makeText(getContext(), "조회할 년도를 선택해주세요.", Toast.LENGTH_SHORT).show();
        } else if (strTerm.length() == 0) {
            Toast.makeText(getContext(), "조회할 학기를 선택해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            chip_group.setVisibility(View.VISIBLE);
            CollectionReference collectionReference = mStore.collection("UserDataList");
            DocumentReference documentReference = collectionReference.document(mAuth.getUid());

            documentReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        Map<String, Object> userData = documentSnapshot.getData();

                        // userData에서 각 item을 추출하여 mDatas에 추가
                        for (Map.Entry<String, Object> entry : userData.entrySet()) {
                            String code = entry.getKey();
                            Map<String, Object> itemData = (Map<String, Object>) entry.getValue();

                            String itemperiod = String.valueOf(itemData.get("period"));
                            String divition = String.valueOf(itemData.get("divition"));

                            if ((filterDivition == null || filterDivition.isEmpty()) || divition.equals(filterDivition)) {
                                if (itemperiod.equals(period)) {
                                    SubjectData data = new SubjectData();
                                    data.setCode(code);
                                    data.setSubject(String.valueOf(itemData.get("subject")));
                                    data.setDivition(divition);
                                    data.setTerm(String.valueOf(itemData.get("term")));
                                    data.setCredit(String.valueOf(itemData.get("credit")));
                                    data.setField(String.valueOf(itemData.get("field")));

                                    mDatas.add(data);
                                }
                            }
                        }
                        // 여기서부터는 어댑터 설정과 화면 업데이트 코드가 들어갑니다.
                        mAdapter = new InquireAdapter(mDatas);
                        inquireRecyclerView.setLayoutManager(layoutManager);
                        inquireRecyclerView.setAdapter(mAdapter);
                        mAdapter.setOnItemClickListener(new CartAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(SubjectData clickedData) {
                                mDatas.remove(clickedData);
                                mAdapter.notifyDataSetChanged();
                                String removedata = clickedData.getCode();
                                //"UserDataList" 컬렉션 안의 사용자 문서에 과목코드 리스트 삭제
                                mStore.collection("UserDataList").document(mAuth.getUid()).update(removedata, FieldValue.delete());
                            }
                        });
                    }
                }
            });
        }
    }
}