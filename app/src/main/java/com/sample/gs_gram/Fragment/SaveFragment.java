package com.sample.gs_gram.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sample.gs_gram.Activity.CartActivity;
import com.sample.gs_gram.Adapter.SaveAdapter;
import com.sample.gs_gram.Data.SubjectData;
import com.sample.gs_gram.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SaveFragment extends Fragment {
    private TextView gradeText, termText, divitionText, majorText;
    private LinearLayout majorSelect;
    private EditText edittext_search;
    private FloatingActionButton cart_button;
    private RecyclerView subjectRecyclerView;
    private SaveAdapter mAdapter;
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private String UserMajor;
    private String SelectedMajor;
    private ArrayList<SubjectData> selectedItems = new ArrayList<>();
    private ArrayList<SubjectData> allSubjects = new ArrayList<>();
    private ArrayList<SubjectData> filterSubjects = new ArrayList<>();

    private ArrayList<String> selectDatas = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedIstanceState) {
        View view = inflater.inflate(R.layout.fragment_save, container, false);

        majorText = view.findViewById(R.id.majorText);
        divitionText = view.findViewById(R.id.divitionText);
        termText = view.findViewById(R.id.termText);
        gradeText = view.findViewById(R.id.gradeText);
        majorSelect = view.findViewById(R.id.majorSelect);
        cart_button = view.findViewById(R.id.cart_button);
        edittext_search = view.findViewById(R.id.edittext_search);
        subjectRecyclerView = view.findViewById(R.id.subjectRecyclerView);

        subjectRecyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), 1));
        subjectRecyclerView.setHasFixedSize(true);
        subjectRecyclerView.setLayoutManager(layoutManager);

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mStore.collection("Account").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        UserMajor = documentSnapshot.getString("major");
                        majorText.setText(UserMajor);
                        SelectedMajor = UserMajor;
                    }
                }
            }
        });

        edittext_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = editable.toString();
                mAdapter.filterData(searchText);
            }
        });

        majorSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {showMajorDialog();}
        });

        divitionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDivitionDialog();
            }
        });

        gradeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGradeDialog();
            }
        });

        termText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTermDialog();
            }
        });

        //장바구니 이동
        cart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendItem();
            }
        });

        return view;
    }


    private void showMajorDialog() {
        String[] majorOptions = {"건축공학과","기계공학과","소프트웨어학과","인공지능응용학과","전기전자공학과","정보보안학과","컴퓨터공학과","토목환경공학과"};
        showSelectionDialog("조회할 학과를 선택해주세요.", majorOptions, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String selectedOption = majorOptions[which];
                majorText.setText(selectedOption);
                SelectedMajor = selectedOption;

                ViewSubjectData();
            }
        });
    }

    private void showDivitionDialog() {
        String[] divitionOptions = {"교양", "교선", "교필", "전선", "전필"};
        showSelectionDialog("조회할 과목의 이수 구분을 선택해주세요.", divitionOptions, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String selectedOption = divitionOptions[which];
                divitionText.setText(selectedOption);

                // 선택한 이수 구분에 따라 학년 다이얼로그 실행
                if (gradeText.getText().toString().isEmpty()) {
                    showGradeDialog();
                } else {
                    ViewSubjectData();
                }
            }
        });
    }

    private void showGradeDialog() {
        String[] gradeOptions = {"1학년", "2학년", "3학년", "4학년"};
        showSelectionDialog("조회할 과목의 학년을 선택해주세요", gradeOptions, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String selectedOption = gradeOptions[which];
                gradeText.setText(selectedOption);

                // 선택한 학년에 따라 학기 다이얼로그 실행
                if (termText.getText().toString().isEmpty()) {
                    showTermDialog();
                } else {
                    ViewSubjectData();
                }
            }
        });
    }

    private void showTermDialog() {
        String[] termOptions = {"1학기", "2학기"};
        showSelectionDialog("조회할 과목의 학기를 선택해주세요.", termOptions, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String selectedOption = termOptions[which];
                termText.setText(selectedOption);

                // 학기 다이얼로그까지 선택한 후, 원하는 동작 수행 (여기서는 ViewSubjectData() 메서드 호출)
                ViewSubjectData();
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

    public void ViewSubjectData() {
        String strDivition = divitionText.getText().toString();
        String strGrade = gradeText.getText().toString();
        String strTerm = termText.getText().toString();

        if (strDivition.length() == 0) {
            Toast.makeText(getContext(), "이수 구분을 선택해주세요.", Toast.LENGTH_SHORT).show();
        } else if (strGrade.length() == 0) {
            Toast.makeText(getContext(), "교과목 학년을 선택해주세요", Toast.LENGTH_SHORT).show();
        } else if (strTerm.length() == 0) {
            Toast.makeText(getContext(), "교과목 학기를 선택해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            edittext_search.setVisibility(View.VISIBLE);
            if (strDivition.equals("전선")||strDivition.equals("전필")) {
                fetchSubjectData(SelectedMajor + "/" + strDivition + "/" + strTerm);
            } else {
                fetchSubjectData("전학과/" + strDivition + "/" + strTerm);
            }
        }
    }

    private void fetchSubjectData(String collectionPath) {
        allSubjects.clear();
        String strDivition = divitionText.getText().toString();
        String strGrade = gradeText.getText().toString();

        mStore.collection(collectionPath).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    mStore.collection("UserDataList").document(mAuth.getUid()).get().addOnCompleteListener(snapshotTask -> {
                        if (snapshotTask.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = snapshotTask.getResult();
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                // 문서가 존재하는 경우
                                ArrayList<String> FieldName = new ArrayList<>();
                                FieldName.addAll(Objects.requireNonNull(documentSnapshot.getData()).keySet());

                                for (QueryDocumentSnapshot queryDocumentSnapshotSnapshot : task.getResult()) {
                                    Map<String, Object> shot = queryDocumentSnapshotSnapshot.getData();

                                    String documentGrade = String.valueOf(shot.get("grade")); // Get the grade from the document
                                    String documentCode = String.valueOf(shot.get("code"));

                                    if (!FieldName.contains(documentCode)) {
                                        if (strGrade.equals(documentGrade) || !strDivition.equals("전필")&&!strDivition.equals("전선")) {
                                            SubjectData data = new SubjectData();
                                            data.setDivition(String.valueOf(shot.get("divition")));
                                            data.setSubject(String.valueOf(shot.get("subject")));
                                            data.setGrade(documentGrade); // Set the grade
                                            data.setTerm(String.valueOf(shot.get("term")));
                                            data.setCredit(String.valueOf(shot.get("credit")));
                                            data.setCode(documentCode);
                                            data.setField(String.valueOf(shot.get("field")));
                                            data.setDepartment(String.valueOf(shot.get("department")));
                                            data.setMajor(SelectedMajor);

                                            boolean isCodeAlreadySelected = false;
                                            for (SubjectData selectedData : selectedItems) {
                                                if (selectedData.getCode().equals(data.getCode())) {
                                                    isCodeAlreadySelected = true;
                                                    break;
                                                }
                                            }

                                            if (!isCodeAlreadySelected) {
                                                allSubjects.add(data);
                                            }
                                        }
                                    }
                                }
                            }else {
                                for (QueryDocumentSnapshot queryDocumentSnapshotSnapshot : task.getResult()) {
                                    Map<String, Object> shot = queryDocumentSnapshotSnapshot.getData();

                                    String documentGrade = String.valueOf(shot.get("grade")); // Get the grade from the document

                                    if (strGrade.equals(documentGrade) || strDivition.length()==2) {
                                        SubjectData data = new SubjectData();
                                        data.setDivition(String.valueOf(shot.get("divition")));
                                        data.setSubject(String.valueOf(shot.get("subject")));
                                        data.setGrade(documentGrade); // Set the grade
                                        data.setTerm(String.valueOf(shot.get("term")));
                                        data.setCredit(String.valueOf(shot.get("credit")));
                                        data.setCode(String.valueOf(shot.get("code")));
                                        data.setField(String.valueOf(shot.get("field")));
                                        data.setDepartment(String.valueOf(shot.get("department")));
                                        data.setMajor(SelectedMajor);

                                        boolean isCodeAlreadySelected = false;
                                        for (SubjectData selectedData : selectedItems) {
                                            if (selectedData.getCode().equals(data.getCode())) {
                                                isCodeAlreadySelected = true;
                                                break;
                                            }
                                        }

                                        if (!isCodeAlreadySelected) {
                                            allSubjects.add(data);
                                        }
                                    }

                                }

                            }
                            // 검색 결과를 필터된 데이터에 복사
                            filterSubjects.clear();
                            filterSubjects.addAll(allSubjects);

                            mAdapter = new SaveAdapter(filterSubjects);
                            subjectRecyclerView.setLayoutManager(layoutManager);
                            subjectRecyclerView.setAdapter(mAdapter);
                            if (filterSubjects.isEmpty()) {
                                Toast.makeText(getContext(), "다른 학년, 학과를 골라주세요.", Toast.LENGTH_SHORT).show();
                            }
                            mAdapter.setOnItemClickListener(new SaveAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(SubjectData clickedData) {
                                    selectedItems.add(clickedData);

                                    String selectdata = clickedData.getSubject();
                                    if (!selectDatas.contains(selectdata)) {
                                        selectDatas.add(selectdata);
                                    }
                                }
                            });
                            mAdapter.setItems(filterSubjects);
                        }
                    });
                }
            }
        });
    }

    private void SendItem() {
        //"Account" 컬렉션 안의 사용자 문서에 교과목 리스트 추가
        mStore.collection("Account").document(mAuth.getUid()).update("selectDatas", selectDatas);
        Intent intent = new Intent(getActivity(), CartActivity.class);
        intent.putExtra("selectedItems", selectedItems);
        intent.putExtra("selectDatas", selectDatas);
        startActivity(intent);
    }
    @Override
    public void onResume() {
        super.onResume();
        final DocumentReference docRef = mStore.collection("Account").document(mAuth.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //문서가 존재하는 경우
                        selectDatas = (ArrayList<String>) document.get("selectDatas");

                        if (selectDatas == null) {
                            // selectDatas가 null인 경우 빈 ArrayList로 초기화
                            selectDatas = new ArrayList<>();
                        }
                        // selectedItems 리스트를 순회하면서 selectDatas에 없는 항목을 제거할 항목 리스트를 생성
                        ArrayList<SubjectData> itemsToRemove = new ArrayList<>();
                        for (SubjectData selectedItem : selectedItems) {
                            if (!selectDatas.contains(selectedItem.getSubject())) {
                                // 해당 항목이 selectDatas에 없으면, itemsToRemove 리스트에 추가
                                itemsToRemove.add(selectedItem);
                            }
                        }

                        // selectDatas에 없는 항목을 selectedItems에서 제거
                        selectedItems.removeAll(itemsToRemove);

                    } else {
                        //문서가 존재하지 않는 경우
                        selectDatas = new ArrayList<>();
                        selectedItems = new ArrayList<>();
                    }
                }
            }
        });
    }
}