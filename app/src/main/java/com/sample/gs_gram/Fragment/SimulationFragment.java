package com.sample.gs_gram.Fragment;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sample.gs_gram.Adapter.SimulatorAdapter;
import com.sample.gs_gram.Adapter.SimulatorCultureAdapter;
import com.sample.gs_gram.Adapter.SimulatorMajorAdapter;
import com.sample.gs_gram.Data.SimulationCultureData;
import com.sample.gs_gram.Data.SimulationMajorData;
import com.sample.gs_gram.Data.SimulationUserLectureData;
import com.sample.gs_gram.R;
import com.sample.gs_gram.databinding.FragmentSimulationBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimulationFragment extends Fragment {
    private TextView total_grade, major_grade, yearText, termText, culture_select_grade, culture_essential_grade, free_grade;
    private Button after_button;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private String currentUserUid;
    private FragmentSimulationBinding mBinding;

    private String field, credit, subject, divition;

    private RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    public RecyclerView.Adapter mAdapterMajor;
    public RecyclerView.Adapter mAdapterCulture;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.LayoutManager mLayoutManagerMajor;
    private RecyclerView.LayoutManager mLayoutManagerCulture;
    private List<SimulationUserLectureData> userLectureDataList = new ArrayList<>();
    private List userLectureDataListSort = new ArrayList<>();
    private List userCultureListSort = new ArrayList<>();
    private List<SimulationUserLectureData> userLectureDataList1 = new ArrayList<>();
    private List<SimulationUserLectureData> userLectureDataList2 = new ArrayList<>();
    private List<SimulationUserLectureData> userLectureDataList3 = new ArrayList<>();
    private List<SimulationUserLectureData> userLectureDataList4 = new ArrayList<>();
    private List<SimulationUserLectureData> userLectureDataList5 = new ArrayList<>();
    private List<SimulationUserLectureData> userLectureDataList6 = new ArrayList<>();
    private List<SimulationMajorData> majorList = new ArrayList<>();
    private List<SimulationCultureData> cultureList = new ArrayList<>();
    private int division_count = 0;
    private String select_lecture = "";
    private List<String> listA = new ArrayList<String>();
    private List<String> listB = new ArrayList<String>();

    private Toast mToast = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedIstanceState) {
        mBinding = FragmentSimulationBinding.inflate(inflater);
        View view = mBinding.getRoot();
        yearText = view.findViewById(R.id.yearText);
        termText = view.findViewById(R.id.termText);
        total_grade = view.findViewById(R.id.total_credit);
//        after_button = view.findViewById(R.id.after_button);

//        tv_major_grade = view.findViewById(R.id.major_grade);
//        tv_culture_select_grade = view.findViewById(R.id.culture_select_grade);
//        tv_culture_essential_grade = view.findViewById(R.id.culture_essential_grade);
//        tv_free_grade = view.findViewById(R.id.free_grade);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy년도");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("M");
        String getYear = simpleDateFormat1.format(date);
        String getMonth = simpleDateFormat2.format(date);

        if (Integer.valueOf(getMonth) > 6) {
            getMonth = "  2학기";
        } else {
            getMonth = "  1학기";
        }

        mBinding.year.setText(getYear);
        mBinding.term.setText(getMonth);

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserUid = mAuth.getCurrentUser().getUid();

//      리사이클러뷰
        mBinding.fragmentSimulationRecyclerview.setHasFixedSize(true);
        mBinding.fragmentSimulationRecyclerviewMajor.setHasFixedSize(true);
        mBinding.fragmentSimulationRecyclerviewCulture.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManagerMajor = new LinearLayoutManager(getActivity());
        mLayoutManagerCulture = new LinearLayoutManager(getActivity());
        mBinding.fragmentSimulationRecyclerview.setLayoutManager(mLayoutManager);
        mBinding.fragmentSimulationRecyclerviewMajor.setLayoutManager(mLayoutManagerMajor);
        mBinding.fragmentSimulationRecyclerviewCulture.setLayoutManager(mLayoutManagerCulture);
        userLectureDataList = new ArrayList<>();

        init(currentUserUid);

        mBinding.majorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLectureDataListSort.clear();
                mStore.collection("정보보안학과").document("전필").collection("1학기").orderBy("grade").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            SimulationMajorData simulationMajorData = new SimulationMajorData();

                            String code;
                            String credit;
                            String divition;
                            String field;
                            String grade;
                            String subject;
                            String term;

                            for (String key : documentSnapshot.getData().keySet()) {
                                Map<String, Object> shot_detail = documentSnapshot.getData();
                                code = String.valueOf(shot_detail.get("code"));
                                credit = String.valueOf(shot_detail.get("credit"));
                                divition = String.valueOf(shot_detail.get("divition"));
                                field = String.valueOf(shot_detail.get("field"));
                                grade = String.valueOf(shot_detail.get("grade")).substring(0, 1);
                                subject = String.valueOf(shot_detail.get("subject"));
                                term = String.valueOf(shot_detail.get("term")).substring(0, 1);
                                simulationMajorData.setCode(code);
                                simulationMajorData.setCredit(credit);
                                simulationMajorData.setDivition(divition);
                                simulationMajorData.setField(field);
                                simulationMajorData.setGrade(grade);
                                simulationMajorData.setSubject(subject);
                                simulationMajorData.setTerm(term);
                            }
                            majorList.add(simulationMajorData);
                        }
                        mStore.collection("정보보안학과").document("전필").collection("2학기").orderBy("grade").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    SimulationMajorData simulationMajorData = new SimulationMajorData();

                                    String code;
                                    String credit;
                                    String divition;
                                    String field;
                                    String grade;
                                    String subject;
                                    String term;

                                    for (String key : documentSnapshot.getData().keySet()) {
                                        Map<String, Object> shot_detail = documentSnapshot.getData();
                                        code = String.valueOf(shot_detail.get("code"));
                                        credit = String.valueOf(shot_detail.get("credit"));
                                        divition = String.valueOf(shot_detail.get("divition"));
                                        field = String.valueOf(shot_detail.get("field"));
                                        grade = String.valueOf(shot_detail.get("grade")).substring(0, 1);
                                        subject = String.valueOf(shot_detail.get("subject"));
                                        term = String.valueOf(shot_detail.get("term")).substring(0, 1);
                                        simulationMajorData.setCode(code);
                                        simulationMajorData.setCredit(credit);
                                        simulationMajorData.setDivition(divition);
                                        simulationMajorData.setField(field);
                                        simulationMajorData.setGrade(grade);
                                        simulationMajorData.setSubject(subject);
                                        simulationMajorData.setTerm(term);
                                    }
                                    majorList.add(simulationMajorData);
                                }

                                userLectureDataListSort = majorList.stream().sorted(Comparator.comparing(SimulationMajorData::getGrade)).collect(Collectors.toList());
                                majorList.clear();
//                                mBinding.simulOrginMajorEssential.setVisibility(View.VISIBLE);
                                mBinding.simulOrginCultureEssential.setVisibility(View.VISIBLE);
                                mBinding.simulOrgin.setVisibility(View.GONE);

                                mAdapterMajor = new SimulatorMajorAdapter(userLectureDataListSort, getActivity(), listA);
                                mBinding.fragmentSimulationRecyclerviewMajor.setAdapter(mAdapterMajor);
                                mBinding.fragmentSimulationRecyclerviewMajor.setVisibility(View.VISIBLE);
                                mBinding.fragmentSimulationRecyclerview.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        });
//
//        mBinding.majorLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mStore.collection("UserDataList").document(currentUserUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        Map<String, Object> shot = task.getResult().getData();
//                        select_lecture = "major";
//                        division_count =0;
//                        if (shot != null) {
//                            userLectureDataList.clear();
//                            userLectureDataList1.clear();
//                            userLectureDataList2.clear();
//                            for (String key : shot.keySet()) {
//                                SimulationUserLectureData userLectureData = new SimulationUserLectureData();
//                                Map<String, Object> shot_detail = (Map<String, Object>) shot.get(key);
//
//                                String subject = String.valueOf(shot_detail.get("subject"));
//                                String credit = String.valueOf(shot_detail.get("credit"));
//                                String divition = String.valueOf(shot_detail.get("divition"));
//                                String code = String.valueOf(shot_detail.get("code"));
//                                userLectureData.setSimulation_code(code);
//                                userLectureData.setSimulation_subject(subject);
//                                userLectureData.setSimulation_divition(divition);
////                                userLectureData.setSimulation_culture_area(field);
//                                userLectureData.setSimulation_credit(credit);
//                                if(divition.equals("전선") || divition.equals("전필")){
//                                    userLectureDataList.add(userLectureData);
//                                    if(divition.equals("전필")){
//                                        userLectureDataList1.add(userLectureData);
//                                    }else{
//                                        userLectureDataList2.add(userLectureData);
//                                    }
//                                }
//                            }
//                            mBinding.simulOrgin.setVisibility(View.VISIBLE);
//                            mAdapter = new SimulatorAdapter(userLectureDataList);
//                            mBinding.fragmentSimulationRecyclerview.setAdapter(mAdapter);
//                            mBinding.fragmentSimulationRecyclerview.setVisibility(View.VISIBLE);
//                            mBinding.fragmentSimulationRecyclerviewMajor.setVisibility(View.GONE);
//                        }
//                    }
//                });
//            }
//        });
        mBinding.cultureSelectLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStore.collection("UserDataList").document(currentUserUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Map<String, Object> shot = task.getResult().getData();
                        select_lecture = "culture_select";
                        division_count = 0;

                        if (shot != null) {
                            userLectureDataList.clear();
                            userLectureDataList1.clear();
                            userLectureDataList2.clear();
                            userLectureDataList3.clear();
                            userLectureDataList4.clear();
                            userLectureDataList5.clear();
                            userLectureDataList6.clear();
                            for (String key : shot.keySet()) {
                                SimulationUserLectureData userLectureData = new SimulationUserLectureData();
                                Map<String, Object> shot_detail = (Map<String, Object>) shot.get(key);

                                String subject = String.valueOf(shot_detail.get("subject"));
                                String credit = String.valueOf(shot_detail.get("credit"));
                                String divition = String.valueOf(shot_detail.get("divition"));
                                String field = "(" + shot_detail.get("field") + ")";
                                String code = String.valueOf(shot_detail.get("code"));
                                userLectureData.setSimulation_code(code);
                                userLectureData.setSimulation_subject(subject);
                                userLectureData.setSimulation_divition(divition);
                                userLectureData.setSimulation_culture_area(field);
                                userLectureData.setSimulation_credit(credit);
                                if (divition.equals("교선") || divition.equals("교양")) {
                                    userLectureDataList.add(userLectureData);
                                    if (divition.equals("교양")) {
                                        if (field.equals("(자연과기술)")) {
                                            userLectureDataList2.add(userLectureData);
                                        } else if (field.equals("(사회와가치)")) {
                                            userLectureDataList3.add(userLectureData);
                                        } else if (field.equals("(인간과역사)")) {
                                            userLectureDataList4.add(userLectureData);
                                        } else if (field.equals("(세계와문화)")) {
                                            userLectureDataList5.add(userLectureData);
                                        } else {
                                            userLectureDataList6.add(userLectureData);
                                        }
                                    } else {
                                        userLectureDataList1.add(userLectureData);
                                    }
                                }
                            }
                            userLectureDataListSort = userLectureDataList.stream().sorted(Comparator.comparing(SimulationUserLectureData::getSimulation_culture_area).reversed()).collect(Collectors.toList());
                            mBinding.simulOrgin.setVisibility(View.VISIBLE);
                            mBinding.simulOrginCultureEssential.setVisibility(View.GONE);

                            mBinding.fragmentSimulationRecyclerview.setVisibility(View.VISIBLE);
                            mBinding.fragmentSimulationRecyclerviewMajor.setVisibility(View.GONE);

                            mAdapter = new SimulatorAdapter(userLectureDataListSort);
                            mBinding.fragmentSimulationRecyclerview.setAdapter(mAdapter);
                        }
                    }
                });
            }
        });
        mBinding.cultureEssentialLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userCultureListSort.clear();
                mStore.collection("정보보안학과").document("교필").collection("1학기").orderBy("grade").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            SimulationCultureData simulationCultureData = new SimulationCultureData();

                            String code;
                            String credit;
                            String divition;
                            String field;
                            String grade;
                            String subject;
                            String term;

                            for (String key : documentSnapshot.getData().keySet()) {
                                Map<String, Object> shot_detail = documentSnapshot.getData();
                                code = String.valueOf(shot_detail.get("code"));
                                credit = String.valueOf(shot_detail.get("credit"));
                                divition = String.valueOf(shot_detail.get("divition"));
                                field = String.valueOf(shot_detail.get("field"));
                                grade = String.valueOf(shot_detail.get("grade")).substring(0, 1);
                                subject = String.valueOf(shot_detail.get("subject"));
                                term = String.valueOf(shot_detail.get("term")).substring(0, 1);
                                simulationCultureData.setCode(code);
                                simulationCultureData.setCredit(credit);
                                simulationCultureData.setDivition(divition);
                                simulationCultureData.setField(field);
                                simulationCultureData.setGrade(grade);
                                simulationCultureData.setSubject(subject);
                                simulationCultureData.setTerm(term);

                            }
                            cultureList.add(simulationCultureData);
                        }
                        mStore.collection("정보보안학과").document("교필").collection("2학기").orderBy("grade").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    SimulationCultureData simulationCultureData = new SimulationCultureData();

                                    String code;
                                    String credit;
                                    String divition;
                                    String field;
                                    String grade;
                                    String subject;
                                    String term;
                                    for (String key : documentSnapshot.getData().keySet()) {
                                        Map<String, Object> shot_detail = documentSnapshot.getData();
                                        code = String.valueOf(shot_detail.get("code"));
                                        credit = String.valueOf(shot_detail.get("credit"));
                                        divition = String.valueOf(shot_detail.get("divition"));
                                        field = String.valueOf(shot_detail.get("field"));
                                        grade = String.valueOf(shot_detail.get("grade")).substring(0, 1);
                                        subject = String.valueOf(shot_detail.get("subject"));
                                        term = String.valueOf(shot_detail.get("term")).substring(0, 1);
                                        simulationCultureData.setCode(code);
                                        simulationCultureData.setCredit(credit);
                                        simulationCultureData.setDivition(divition);
                                        simulationCultureData.setField(field);
                                        simulationCultureData.setGrade(grade);
                                        simulationCultureData.setSubject(subject);
                                        simulationCultureData.setTerm(term);
                                        Log.e("2222222222", term);
                                    }
                                    cultureList.add(simulationCultureData);
                                }
                                userCultureListSort = cultureList.stream().sorted(Comparator.comparing(SimulationCultureData::getField).thenComparing(SimulationCultureData::getSubject)).collect(Collectors.toList());
                                cultureList.clear();

                                mBinding.fragmentSimulationRecyclerviewMajor.setVisibility(View.GONE);
                                mBinding.simulOrgin.setVisibility(View.GONE);
                                mBinding.simulOrginCultureEssential.setVisibility(View.VISIBLE);

                                mBinding.fragmentSimulationRecyclerview.setVisibility(View.VISIBLE);
                                mAdapter = new SimulatorCultureAdapter(userCultureListSort, getActivity(), listA);
                                mBinding.fragmentSimulationRecyclerview.setAdapter(mAdapter);
                            }

                        });
                    }

                });


//
//                mStore.collection("UserDataList").document(currentUserUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    //                mStore.collection("1학기").orderBy()
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        select_lecture = "culture_essential";
//                        Map<String, Object> shot = task.getResult().getData();
//                        if (shot != null) {
//                            userLectureDataList.clear();
//                            for (String key : shot.keySet()) {
//                                SimulationUserLectureData userLectureData = new SimulationUserLectureData();
//                                Map<String, Object> shot_detail = (Map<String, Object>) shot.get(key);
//
//                                String subject = String.valueOf(shot_detail.get("subject"));
//                                String credit = String.valueOf(shot_detail.get("credit"));
//                                String divition = String.valueOf(shot_detail.get("divition"));
//                                String field = String.valueOf(shot_detail.get("field"));
//                                String code = String.valueOf(shot_detail.get("code"));
//                                userLectureData.setSimulation_code(code);
//                                userLectureData.setSimulation_subject(subject);
//                                userLectureData.setSimulation_divition(divition);
//                                userLectureData.setSimulation_culture_area(field);
//                                userLectureData.setSimulation_credit(credit);
//                                if (divition.equals("교필")) {
//                                    userLectureDataList.add(userLectureData);
//                                }
//                            }
//                            mBinding.simulOrgin.setVisibility(View.VISIBLE);
//                            mBinding.simulOrginMajorEssential.setVisibility(View.GONE);
//
//                            mAdapter = new SimulatorAdapter(userLectureDataList);
//                            mBinding.fragmentSimulationRecyclerview.setAdapter(mAdapter);
//                            mBinding.fragmentSimulationRecyclerview.setVisibility(View.VISIBLE);
//
//                            mBinding.fragmentSimulationRecyclerviewMajor.setVisibility(View.GONE);
//
//                        }
//
//                    }
//                });
            }
        });
        mBinding.free.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStore.collection("UserDataList").document(currentUserUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Map<String, Object> shot = task.getResult().getData();
                        select_lecture = "free";
                        userLectureDataList.clear();
                        if (shot != null) {
                            for (String key : shot.keySet()) {
                                SimulationUserLectureData userLectureData = new SimulationUserLectureData();
                                Map<String, Object> shot_detail = (Map<String, Object>) shot.get(key);

                                String subject = String.valueOf(shot_detail.get("subject"));
                                String credit = String.valueOf(shot_detail.get("credit"));
                                String divition = String.valueOf(shot_detail.get("divition"));
                                String field = String.valueOf(shot_detail.get("field"));
                                String code = String.valueOf(shot_detail.get("code"));
                                userLectureData.setSimulation_code(code);
                                userLectureData.setSimulation_subject(subject);
                                userLectureData.setSimulation_divition(divition);
                                userLectureData.setSimulation_culture_area(field);
                                userLectureData.setSimulation_credit(credit);
                                if (divition.equals("전필") || divition.equals("전선") || divition.equals("교필") || divition.equals("교선") || divition.equals("교양")) {
                                } else {
                                    userLectureDataList.add(userLectureData);
                                }
                            }
                            mBinding.simulOrgin.setVisibility(View.GONE);
                            mBinding.simulOrginCultureEssential.setVisibility(View.GONE);
                            mBinding.fragmentSimulationRecyclerview.setVisibility(View.VISIBLE);

                            mAdapter = new SimulatorAdapter(userLectureDataList);
                            mBinding.fragmentSimulationRecyclerview.setAdapter(mAdapter);
                            mBinding.fragmentSimulationRecyclerviewMajor.setVisibility(View.GONE);

                        }

                    }
                });
            }
        });
//        yearText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String[] yearOptions = {"2023년", "2022년", "2021년", "2020년", "2019년", "2018년", "2017년"};
//                showSelectionDialog("년도를 선택해주세요.", yearOptions, yearText);
//            }
//        });
//
//        termText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String[] termOptions = {"1학기", "2학기"};
//                showSelectionDialog("학기를 선택해주세요.", termOptions, termText);
//            }
//        });

//        DocumentReference documentReference = mStore.collection("UserDataList").document(mAuth.getUid());
//        after_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String period = yearText.getText().toString() + " " + termText.getText().toString();
//
//                Log.d("asdqwe", "click");
//
//            }
//        });

        mBinding.textviewDivitionDivision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (select_lecture.equals("major")) {
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    if (division_count == 0) {
                        mToast = Toast.makeText(getActivity(), "전공 필수", Toast.LENGTH_SHORT);
                        mToast.show();
                        mAdapter = new SimulatorAdapter(userLectureDataList1);
                        mBinding.fragmentSimulationRecyclerview.setAdapter(mAdapter);
                        division_count += 1;
                    } else if (division_count == 1) {

                        mToast = Toast.makeText(getActivity(), "전공 선택", Toast.LENGTH_SHORT);
                        mToast.show();
                        mAdapter = new SimulatorAdapter(userLectureDataList2);
                        mBinding.fragmentSimulationRecyclerview.setAdapter(mAdapter);
                        division_count += 1;
                    } else {
                        mToast = Toast.makeText(getActivity(), "전공 전체", Toast.LENGTH_SHORT);
                        mToast.show();
                        mAdapter = new SimulatorAdapter(userLectureDataList);
                        mBinding.fragmentSimulationRecyclerview.setAdapter(mAdapter);
                        division_count = 0;
                    }
                } else if (select_lecture.equals("culture_select")) {
                    if (mToast != null) {
                        mToast.cancel();
                    }
                    if (division_count == 0) {
                        mToast = Toast.makeText(getActivity(), "인간과 역사", Toast.LENGTH_SHORT);
                        mToast.show();
                        mAdapter = new SimulatorAdapter(userLectureDataList4);
                        mBinding.fragmentSimulationRecyclerview.setAdapter(mAdapter);
                        division_count += 1;
                    } else if (division_count == 1) {
                        mToast = Toast.makeText(getActivity(), "사회와 가치", Toast.LENGTH_SHORT);
                        mToast.show();
                        mAdapter = new SimulatorAdapter(userLectureDataList3);
                        mBinding.fragmentSimulationRecyclerview.setAdapter(mAdapter);
                        division_count += 1;
                    } else if (division_count == 2) {
                        mToast = Toast.makeText(getActivity(), "자연과 기술", Toast.LENGTH_SHORT);
                        mToast.show();
                        mAdapter = new SimulatorAdapter(userLectureDataList2);
                        mBinding.fragmentSimulationRecyclerview.setAdapter(mAdapter);
                        division_count += 1;

                    } else if (division_count == 3) {
                        mToast = Toast.makeText(getActivity(), "문학과 예술", Toast.LENGTH_SHORT);
                        mToast.show();
                        mAdapter = new SimulatorAdapter(userLectureDataList6);
                        mBinding.fragmentSimulationRecyclerview.setAdapter(mAdapter);
                        division_count += 1;
                    } else if (division_count == 4) {
                        mToast = Toast.makeText(getActivity(), "세계와 문화", Toast.LENGTH_SHORT);
                        mToast.show();
                        mAdapter = new SimulatorAdapter(userLectureDataList5);
                        mBinding.fragmentSimulationRecyclerview.setAdapter(mAdapter);
                        division_count += 1;
                    } else {
                        mToast = Toast.makeText(getActivity(), "교양 전체", Toast.LENGTH_SHORT);
                        mToast.show();
                        mAdapter = new SimulatorAdapter(userLectureDataListSort);
                        mBinding.fragmentSimulationRecyclerview.setAdapter(mAdapter);
                        division_count = 0;
                    }
                }
            }

        });

        return view;
    }

    private void init(String uid) {
        mStore.collection("UserDataList").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                double total_grade = 0.0;
                double major_select_grade = 0.0;
                double major_essential_grade = 0.0;
                double culture_select_grade = 0.0;
                double culture_essential_grade = 0.0;
                double free_grade = 0.0;

                Map<String, Object> shot = task.getResult().getData();

                if (shot != null) {
                    for (String key : shot.keySet()) {

                        Map<String, Object> shot_detail = (Map<String, Object>) shot.get(key);

                        String credit = String.valueOf(shot_detail.get("credit")); //학점
                        String divition = String.valueOf(shot_detail.get("divition")); //전필 전선
                        Log.e("ss", shot_detail.get("divition").toString());

                        switch (divition) {
                            case "교필":
                                culture_essential_grade += Double.parseDouble(credit);
                                break;
                            case "교양":
                                culture_select_grade += Double.parseDouble(credit);
                                break;
                            case "교선":
                                culture_select_grade += Double.parseDouble(credit);
                                break;
                            case "전필":
                                major_essential_grade += Double.parseDouble(credit);
                                break;
                            case "전선":
                                major_select_grade += Double.parseDouble(credit);
                                break;
                            default:
                                free_grade += Double.parseDouble(credit);
                                break;
                        }
                    }
                }

                isInteger(culture_essential_grade, culture_select_grade, major_essential_grade, major_select_grade, free_grade);


//                major_essential_grade.set
//                mBinding.totalCredit.setText(String.valueOf(major_essential_grade+major_select_grade+culture_select_grade+culture_essential_grade+free_grade));
//                mBinding.tvMajorGrade.setText(String.valueOf(major_essential_grade+major_select_grade));
//                mBinding.tvCultureSelectGrade.setText(String.valueOf(culture_select_grade));
//                mBinding.tvCultureEssentialGrade.setText(String.valueOf(culture_essential_grade));
//                mBinding.tvFreeGrade.setText(String.valueOf(free_grade));

                mStore.collection("UserDataList").document(currentUserUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Map<String, Object> shot = task.getResult().getData();
                        String code = "";
                        if (shot != null) {
                            for (String key : shot.keySet()) {
                                SimulationUserLectureData userLectureData = new SimulationUserLectureData();
                                Map<String, Object> shot_detail = (Map<String, Object>) shot.get(key);

                                String subject = String.valueOf(shot_detail.get("subject"));
                                String credit = String.valueOf(shot_detail.get("credit"));
                                String divition = String.valueOf(shot_detail.get("divition"));
                                code = String.valueOf(shot_detail.get("code"));

                                userLectureData.setSimulation_code(code);
                                userLectureData.setSimulation_subject(subject);
                                userLectureData.setSimulation_divition(divition);
                                userLectureData.setSimulation_credit(credit);
                                listA.add(code);
                            }

                        }
                    }
                });



            }
        });
    }


    private void showSelectionDialog(String title, String[] options, final TextView targetTextView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title).setItems(options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String selectedItem = options[which];
                targetTextView.setText(selectedItem);
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void isInteger(double culture_essential_grade, double culture_select_grade, double major_essential_grade, double major_select_grade, double free_grade) {

        if ((major_essential_grade + major_select_grade + culture_select_grade + culture_essential_grade + free_grade) % 1 == 0.0) {
            int intTotal = (int) (major_essential_grade + major_select_grade + culture_select_grade + culture_essential_grade + free_grade);
            mBinding.totalCredit.setText(String.valueOf(intTotal));
        } else {
            mBinding.totalCredit.setText(String.valueOf(major_essential_grade + major_select_grade + culture_select_grade + culture_essential_grade + free_grade));
        }

        if ((major_essential_grade + major_select_grade) % 1 == 0.0) {
            int intTotal = (int) (major_essential_grade + major_select_grade);
            mBinding.tvMajorGrade.setText(String.valueOf(intTotal));
        } else {
            mBinding.tvMajorGrade.setText(String.valueOf(major_essential_grade + major_select_grade));
        }

        if (culture_select_grade % 1 == 0.0) {
            int intTotal = (int) culture_select_grade;
            mBinding.tvCultureSelectGrade.setText(String.valueOf(intTotal));
        } else {
            mBinding.tvCultureSelectGrade.setText(String.valueOf(culture_select_grade));
        }

        if (culture_essential_grade % 1 == 0.0) {
            int intTotal = (int) (culture_essential_grade);
            mBinding.tvCultureEssentialGrade.setText(String.valueOf(intTotal));
        } else {
            mBinding.tvCultureEssentialGrade.setText(String.valueOf(culture_essential_grade));
        }

        if (free_grade % 1 == 0.0) {
            int intTotal = (int) (free_grade);
            mBinding.tvFreeGrade.setText(String.valueOf(intTotal));
        } else {
            mBinding.tvFreeGrade.setText(String.valueOf(free_grade));
        }
    }
}