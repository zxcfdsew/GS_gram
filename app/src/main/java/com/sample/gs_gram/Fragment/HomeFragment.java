package com.sample.gs_gram.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sample.gs_gram.Activity.CSVActivity;
import com.sample.gs_gram.Data.ScheduleCellData;
import com.sample.gs_gram.R;
import com.sample.gs_gram.databinding.AddScheduleDialogBinding;
import com.sample.gs_gram.databinding.FragmentHomeBinding;

import org.checkerframework.checker.units.qual.A;
import org.w3c.dom.Text;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding mBinding;
    private int cellrow = 9;
    private int cellcol = 5;
    private boolean modifySchedule = false;
    private Map<String, TextView> cells = new LinkedHashMap<>();
    private String[][] cellStatus = new String[cellrow][cellcol];
    private ScheduleCellData[][] scheduleCellDatas = new ScheduleCellData[cellrow][cellcol];
    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private Integer[] colors = {Color.parseColor("#FFCCCC"), Color.parseColor("#FAE0D4"), Color.parseColor("#FAF4C0"), Color.parseColor("#E4F7BA"), Color.parseColor("#D4F4FA"), Color.parseColor("#D9E5FF"), Color.parseColor("#E6D9FF")};
    private int colorIndex = 0;
    private String currentGrade = "1학년 1학기";
    private Map<TextView, String> removedTextView = new HashMap<>();
    private ArrayList<String> titleArray = new ArrayList<>();
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedIstanceState) {
        mBinding = FragmentHomeBinding.inflate(inflater);
        View view = mBinding.getRoot();

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        baseSetting();
        initCellStatus();
        for (String cellCoordinate : cells.keySet()) {
            TextView textView = cells.get(cellCoordinate);
            int row = getTextViewCoordinate(cellCoordinate)[0];
            int col = getTextViewCoordinate(cellCoordinate)[1];
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (modifySchedule) {
                        if (cellStatus[row][col].equals("selected")) {
                            changeCellBackgroundColor(textView, Color.parseColor("#FFFFFF"));
                            cellStatus[row][col] = "none";
                        } else if (cellStatus[row][col].equals("none")) {
                            changeCellBackgroundColor(textView, Color.parseColor("#D3D3D3"));
                            cellStatus[row][col] = "selected";
                        } else if (cellStatus[row][col].equals("using")) {
                            cellStatus[row][col] = "deleting";
                            changeCellBackgroundColor(textView, Color.RED);
                        } else if (cellStatus[row][col].equals("deleting")) {
                            ScheduleCellData data = scheduleCellDatas[row][col];
                            changeCellBackgroundColor(textView, data.getColor());
                            cellStatus[row][col] = "using";
                        }
                    } else {
                        Toast.makeText(getContext(), cellCoordinate, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        String[] spinnerItems = {"1학년 1학기", "1학년 2학기", "2학년 1학기", "2학년 2학기", "3학년 1학기", "3학년 2학기", "4학년 1학기", "4학년 2학기", "5학년 1학기", "5학년 2학기"};
        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, spinnerItems);
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBinding.gradeSpinner.setAdapter(gradeAdapter);

        mBinding.gradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentGrade = spinnerItems[position];
                refreshCellLayout();
                addView();
                initCellStatus();
                downloadFromFireStore();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBinding.addScheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checking = false;
                ArrayList<String> deletingArray = returnCellInfo("deleting");
                if (deletingArray.size() == 0) {
                    showAddDialog();
                } else {
                    Toast.makeText(getActivity(), "다시 선택해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBinding.modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modifySchedule) {
                    uploadToFireStore();
                    modifySchedule = false;
                    mBinding.modifyBtn.setText("수정");
                    mBinding.addScheduleBtn.setVisibility(View.GONE);
                    mBinding.initCellStateBtn.setVisibility(View.GONE);
                    mBinding.gradeSpinner.setVisibility(View.VISIBLE);
                    setCheckedCelltoDefault();
                } else {
                    modifySchedule = true;
                    mBinding.modifyBtn.setText("저장");
                    mBinding.addScheduleBtn.setVisibility(View.VISIBLE);
                    mBinding.initCellStateBtn.setVisibility(View.VISIBLE);
                    mBinding.gradeSpinner.setVisibility(View.GONE);
                }
            }
        });

        mBinding.initCellStateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checking = "false";
                for (int i = 0; i < cellStatus.length; i++) {
                    for (int j = 0; j < cellStatus[i].length; j++) {
                        if (cellStatus[i][j].equals("deleting")) {
                            checking = "deleting";
                        } else if (cellStatus[i][j].equals("selected")) {
                            checking = "selected";
                            break;
                        }
                    }
                }
                if (checking.equals("deleting")) {
                    deleteSchedule();
                    addView();
                    refreshCellLayout();
                    changeCellStatus();
                    mergeCell();
                } else if (checking.equals("selected")) {
                    Toast.makeText(getActivity(), "다시 선택해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "시간대를 선택해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBinding.csvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CSVActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        addView();
        refreshCellLayout();
        setClassCellStyle();
        mergeCell();
        downloadFromFireStore();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void initCellStatus() {
        for (String cellCoordinate : cells.keySet()) {
            int row = getTextViewCoordinate(cellCoordinate)[0];
            int col = getTextViewCoordinate(cellCoordinate)[1];
            TextView textView = cells.get(cellCoordinate);
            textView.setText("");
            changeCellBackgroundColor(textView, Color.parseColor("#FFFFFF"));
            cellStatus[row][col] = "none";
            scheduleCellDatas[row][col] = null;
        }
        setCheckedCelltoDefault();
        changeCellStatus();

    }

    private void changeCellBackgroundColor(TextView textView, int color) {
        GradientDrawable drawable = (GradientDrawable) textView.getBackground();
        drawable.setColor(color);
    }

    private int[] getTextViewCoordinate(String coor) {
        String[] temp = coor.split(",");
        int[] coordinate = new int[2];
        coordinate[0] = Integer.parseInt(temp[0]);
        coordinate[1] = Integer.parseInt(temp[1]);
        return coordinate;
    }


    private ArrayList<String> returnCellInfo(String statusType) {
        ArrayList<String> arrayList = new ArrayList<>();
        if (statusType.equals("none")) {
            for (int i = 0; i < cellStatus.length; i++) {
                for (int j = 0; j < cellStatus[i].length; j++) {
                    if (cellStatus[i][j].equals("none")) {
                        arrayList.add(i + "," + j);
                    }
                }
            }
        } else if (statusType.equals("selected")) {
            for (int i = 0; i < cellStatus.length; i++) {
                for (int j = 0; j < cellStatus[i].length; j++) {
                    if (cellStatus[i][j].equals("selected")) {
                        arrayList.add(i + "," + j);
                    }
                }
            }
        } else if (statusType.equals("deleting")) {
            for (int i = 0; i < cellStatus.length; i++) {
                for (int j = 0; j < cellStatus[i].length; j++) {
                    if (cellStatus[i][j].equals("deleting")) {
                        arrayList.add(i + "," + j);
                    }
                }
            }
        } else if (statusType.equals("using")) {
            for (int i = 0; i < cellStatus.length; i++) {
                for (int j = 0; j < cellStatus[i].length; j++) {
                    if (cellStatus[i][j].equals("using")) {
                        arrayList.add(i + "," + j);
                    }
                }
            }
        }
        return arrayList;
    }

    private void showAddDialog() {
        ArrayList<String> selectedCoordinate = returnCellInfo("selected");
        AddScheduleDialogBinding addScheduleDialogBinding = AddScheduleDialogBinding.inflate(getLayoutInflater());
        View dialogView = addScheduleDialogBinding.getRoot();
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!titleArray.contains(addScheduleDialogBinding.titleTxt.getText().toString())) {
                            ArrayList<Integer> sideColors = new ArrayList<>();
                            for (String k : selectedCoordinate) {
                                int row = getTextViewCoordinate(k)[0];
                                int col = getTextViewCoordinate(k)[1];
                                if (colorIndex >= colors.length - 1) {
                                    colorIndex = 0;
                                }
                                int top = 0;
                                try {
                                    top = scheduleCellDatas[row - 1][col] == null ? Color.parseColor("#FFFFFF") : scheduleCellDatas[row - 1][col].getColor();
                                } catch (Exception e) {

                                }
                                int left = 0;
                                try {
                                    left = scheduleCellDatas[row][col - 1] == null ? Color.parseColor("#FFFFFF") : scheduleCellDatas[row][col - 1].getColor();
                                } catch (Exception e) {

                                }
                                int right = 0;
                                try {
                                    right = scheduleCellDatas[row][col + 1] == null ? Color.parseColor("#FFFFFF") : scheduleCellDatas[row][col + 1].getColor();
                                } catch (Exception e) {

                                }
                                int bottom = 0;
                                try {
                                    bottom = scheduleCellDatas[row + 1][col] == null ? Color.parseColor("#FFFFFF") : scheduleCellDatas[row + 1][col].getColor();
                                } catch (Exception e) {

                                }
                                if (!sideColors.contains(top)) {
                                    sideColors.add(top);
                                }
                                if (!sideColors.contains(left)) {
                                    sideColors.add(left);
                                }
                                if (!sideColors.contains(right)) {
                                    sideColors.add(right);
                                }
                                if (!sideColors.contains(bottom)) {
                                    sideColors.add(bottom);
                                }
                                while (sideColors.contains(colors[colorIndex])) {
                                    if (colorIndex >= colors.length - 1) {
                                        colorIndex = 0;
                                    } else {
                                        colorIndex++;
                                    }
                                }
                                if (!titleArray.contains(addScheduleDialogBinding.titleTxt.getText().toString())) {
                                    titleArray.add(addScheduleDialogBinding.titleTxt.getText().toString());
                                }
                            }
                            for (String x : selectedCoordinate) {
                                int row = getTextViewCoordinate(x)[0];
                                int col = getTextViewCoordinate(x)[1];
                                ScheduleCellData data = new ScheduleCellData(row,
                                        col,
                                        colors[colorIndex],
                                        colorIndex,
                                        addScheduleDialogBinding.titleTxt.getText().toString(),
                                        "time",
                                        addScheduleDialogBinding.locationTxt.getText().toString(),
                                        addScheduleDialogBinding.professorNameTxt.getText().toString());
                                scheduleCellDatas[row][col] = data;
                                cellStatus[row][col] = "using";
                            }
                            if (colorIndex == colors.length) {
                                colorIndex = 0;
                            } else {
                                colorIndex++;
                            }
                        } else {
                            for (String k : selectedCoordinate) {
                                int row = getTextViewCoordinate(k)[0];
                                int col = getTextViewCoordinate(k)[1];
                                cellStatus[row][col] = "none";
                            }
                            Toast.makeText(getActivity(), "같은 수업이 있습니다", Toast.LENGTH_SHORT).show();
                        }
                        changeCellStatus();
                        mergeCell();
                    }
                }).create();
        dialog.show();
    }

    private void refreshCellLayout() {
        for (String coordinate : cells.keySet()) {
            TextView textview = cells.get(coordinate);
            int i = getTextViewCoordinate(coordinate)[0];
            int j = getTextViewCoordinate(coordinate)[1];
            GridLayout.Spec row = GridLayout.spec(i);
            GridLayout.Spec column = GridLayout.spec(j);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(row, column);
            params.width = (int) calculatedWidth();
            params.height = (int) calculatedHeight();
            textview.setLayoutParams(params);
        }

        ArrayList<TextView> setParams = new ArrayList<>();
        setParams.add(mBinding.monTv);
        setParams.add(mBinding.tueTv);
        setParams.add(mBinding.wedTv);
        setParams.add(mBinding.thuTv);
        setParams.add(mBinding.friTv);
        for (TextView textView : setParams) {
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = (int) calculatedWidth();
            textView.setLayoutParams(params);
        }
    }

    private void mergeCell() {
        ArrayList<String> removeCell = new ArrayList<>();
        refreshCellLayout();
        for (String coordinate : cells.keySet()) {
            TextView textview = cells.get(coordinate);
            int i = getTextViewCoordinate(coordinate)[0];
            int j = getTextViewCoordinate(coordinate)[1];
            Log.d("mergeing", coordinate);
            int count = 1;
            for (int k = 0; k < cellrow; k++) {
                if (i + count < cellStatus.length
                        && scheduleCellDatas[i][j] != null
                        && scheduleCellDatas[i + count][j] != null
                        && scheduleCellDatas[i][j].getTitle().equals(scheduleCellDatas[i + count][j].getTitle())) {
                    removeCell.add(i + count + "," + j);
                    count++;
                } else {
                    break;
                }
            }
            if (!removeCell.contains(coordinate)) {
                GridLayout.Spec row = GridLayout.spec(i, count);
                GridLayout.Spec column = GridLayout.spec(j, 1);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(row, column);
                params.width = (int) calculatedWidth();
                params.height = (int) calculatedHeight() * count;
                textview.setLayoutParams(params);
            }
        }
        for (String coordinate : removeCell) {
            TextView textView = cells.get(coordinate);
            mBinding.cellGridLayout.removeView(textView);
            removedTextView.put(textView, coordinate);
        }
    }

    private void addView() {
        Log.d("removedTextView", removedTextView.values().toString());
        for (String coordinate : removedTextView.values()) {
            int i = getTextViewCoordinate(coordinate)[0];
            int j = getTextViewCoordinate(coordinate)[1];
            TextView textView = cells.get(coordinate);
            GridLayout.Spec row = GridLayout.spec(i);
            GridLayout.Spec column = GridLayout.spec(j);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(row, column);
            params.width = (int) calculatedWidth();
            params.height = (int) calculatedHeight();
            try {
                Log.d("removedTextViewCount", textView.toString());
                mBinding.cellGridLayout.addView(textView);
            } catch (Exception e) {

            }
        }
    }

    private double calculatedWidth() {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        double width = metrics.widthPixels;
        return (width - DpToPx(65)) / 5;
    }

    private double calculatedHeight() {
//        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
//        double height = metrics.heightPixels;
        return DpToPx(80);
    }

    private int DpToPx(int dp) {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        double density = metrics.density;
        double px = dp * density;
        return (int) px;
    }

    private int PxToDp(int px) {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        double desity = metrics.density;
        double dp = px / desity;
        return (int) dp;
    }

    private void changeCellStatus() {
        for (String coordinate : cells.keySet()) {
            int i = getTextViewCoordinate(coordinate)[0];
            int j = getTextViewCoordinate(coordinate)[1];
            TextView textView = cells.get(coordinate);
            if (cellStatus[i][j].equals("none")) {
                changeCellBackgroundColor(textView, Color.parseColor("#FFFFFF"));
                textView.setText("");
            } else if (cellStatus[i][j].equals("using")) {
                ScheduleCellData data = scheduleCellDatas[i][j];
                Log.d("scheduleCellDatas", Arrays.deepToString(scheduleCellDatas));
                Log.d("scheduleCellData", Arrays.deepToString(cellStatus));
                changeCellBackgroundColor(textView, data.getColor());
                textView.setText(changeCellTextStyle(data.getTitle(), data.getLocation()));

            } else if (cellStatus[i][j].equals("deleting")) {

            }
        }
    }

    private void setCheckedCelltoDefault() {
        Log.d("setDefault", "before" + Arrays.deepToString(cellStatus));
        for (int i = 0; i < cellStatus.length; i++) {
            for (int j = 0; j < cellStatus[i].length; j++) {
                if (cellStatus[i][j] == null | cellStatus[i][j].equals("selected")) {
                    cellStatus[i][j] = "none";
                }
            }
        }
        Log.d("setDefault", "After" + Arrays.deepToString(cellStatus));
        changeCellStatus();
    }

    private SpannableStringBuilder changeCellTextStyle(String title, String location) {
        String text = title + "\n" + location;
        int start = title.length();
        int end = text.length();
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#5D5D5D"));
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(26);
        sb.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(sizeSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    private SpannableStringBuilder changeTimeTextStyle(String text) {
        String[] splitedText = text.split("\n");
        int start = splitedText[0].length();
        int end = text.length();
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(30);
        sb.setSpan(sizeSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    private void deleteSchedule() {
        refreshCellLayout();
        ArrayList<String> selectedCoordinate = new ArrayList<>();
        ArrayList<int[]> delCell = new ArrayList<>();
        ArrayList<String> delCellTitle = new ArrayList<>();
        for (int i = 0; i < cellStatus.length; i++) {
            for (int j = 0; j < cellStatus[i].length; j++) {
                if (cellStatus[i][j].equals("deleting")) {
                    delCell.add(new int[]{i, j});
                    selectedCoordinate.add(i + "," + j);
                    delCellTitle.add(scheduleCellDatas[i][j].getTitle());
                }
            }
        }
        for (String coordinate : selectedCoordinate) {
            int i = getTextViewCoordinate(coordinate)[0];
            int j = getTextViewCoordinate(coordinate)[1];
            int count = 1;
            if (titleArray.contains(scheduleCellDatas[i][j].getTitle())) {
                titleArray.remove(scheduleCellDatas[i][j].getTitle());
            }
            for (int k = 0; k < cellrow; k++) {
                if (i + count < cellStatus.length
                        && scheduleCellDatas[i][j] != null
                        && scheduleCellDatas[i + count][j] != null
                        && scheduleCellDatas[i][j].getTitle().equals(scheduleCellDatas[i + count][j].getTitle())) {
                    delCell.add(new int[]{i + count, j});
                }
                count++;
            }
        }
        for (int[] delCoordinate : delCell) {
            cellStatus[delCoordinate[0]][delCoordinate[1]] = "none";
            scheduleCellDatas[delCoordinate[0]][delCoordinate[1]] = null;
        }
    }

    private void uploadToFireStore() {
        Map<String, ScheduleCellData> cellDatas = new HashMap<>();
        for (int i = 0; i < cellStatus.length; i++) {
            for (int j = 0; j < cellStatus[i].length; j++) {
                if (cellStatus[i][j].equals("using")) {
                    cellDatas.put(i + "-" + j, scheduleCellDatas[i][j]);
                }
            }
        }

        mStore.collection("Account")
                .document(mAuth.getUid())
                .collection("schedule")
                .document(currentGrade)
                .set(cellDatas).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(), "저장되었습니다", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void downloadFromFireStore() {
        initCellStatus();
        mStore.collection("Account").document(mAuth.getUid())
                .collection("schedule").document(currentGrade)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                Map<String, Object> data = documentSnapshot.getData();
                                for (String coordinate : data.keySet()) {
                                    Map<String, Object> oneData = (Map<String, Object>) data.get(coordinate);
                                    Log.d("database", String.valueOf(oneData));
                                    if (!titleArray.contains((String.valueOf(oneData.get("title"))))) {
                                        titleArray.add(oneData.get("title").toString());
                                    }
                                    ScheduleCellData scheduleCellData = new ScheduleCellData(
                                            Integer.parseInt(oneData.get("row").toString()),
                                            Integer.parseInt(oneData.get("col").toString()),
                                            Integer.parseInt(oneData.get("color").toString()),
                                            Integer.parseInt(oneData.get("colorIndex").toString()),
                                            oneData.get("title").toString(),
                                            oneData.get("time").toString(),
                                            oneData.get("location").toString(),
                                            oneData.get("professorName").toString());
                                    if (Integer.parseInt(oneData.get("colorIndex").toString()) > colorIndex) {
                                        colorIndex = Integer.parseInt(oneData.get("colorIndex").toString());
                                    }
                                    String[] splitedCoordinate = coordinate.split("-");
                                    Integer[] tempCoordinate = Stream.of(splitedCoordinate).mapToInt(Integer::parseInt).boxed().toArray(Integer[]::new);
                                    scheduleCellDatas[tempCoordinate[0]][tempCoordinate[1]] = scheduleCellData;
                                    cellStatus[tempCoordinate[0]][tempCoordinate[1]] = "using";
                                    changeCellStatus();
                                    mergeCell();
                                }
                            } else {
                                initCellStatus();
                            }
                        }
                    }
                });
    }

    private void setClassCellStyle() {
        mBinding.classTxt1.setText(changeTimeTextStyle(mBinding.classTxt1.getText().toString()));
        mBinding.classTxt2.setText(changeTimeTextStyle(mBinding.classTxt2.getText().toString()));
        mBinding.classTxt3.setText(changeTimeTextStyle(mBinding.classTxt3.getText().toString()));
        mBinding.classTxt4.setText(changeTimeTextStyle(mBinding.classTxt4.getText().toString()));
        mBinding.classTxt5.setText(changeTimeTextStyle(mBinding.classTxt5.getText().toString()));
        mBinding.classTxt6.setText(changeTimeTextStyle(mBinding.classTxt6.getText().toString()));
        mBinding.classTxt7.setText(changeTimeTextStyle(mBinding.classTxt7.getText().toString()));
        mBinding.classTxt8.setText(changeTimeTextStyle(mBinding.classTxt8.getText().toString()));
        mBinding.classTxt9.setText(changeTimeTextStyle(mBinding.classTxt9.getText().toString()));
    }

    private void baseSetting() {
        cells.put("0,0", mBinding.timeRow0Col0);
        cells.put("0,1", mBinding.timeRow0Col1);
        cells.put("0,2", mBinding.timeRow0Col2);
        cells.put("0,3", mBinding.timeRow0Col3);
        cells.put("0,4", mBinding.timeRow0Col4);

        cells.put("1,0", mBinding.timeRow1Col0);
        cells.put("1,1", mBinding.timeRow1Col1);
        cells.put("1,2", mBinding.timeRow1Col2);
        cells.put("1,3", mBinding.timeRow1Col3);
        cells.put("1,4", mBinding.timeRow1Col4);

        cells.put("2,0", mBinding.timeRow2Col0);
        cells.put("2,1", mBinding.timeRow2Col1);
        cells.put("2,2", mBinding.timeRow2Col2);
        cells.put("2,3", mBinding.timeRow2Col3);
        cells.put("2,4", mBinding.timeRow2Col4);

        cells.put("3,0", mBinding.timeRow3Col0);
        cells.put("3,1", mBinding.timeRow3Col1);
        cells.put("3,2", mBinding.timeRow3Col2);
        cells.put("3,3", mBinding.timeRow3Col3);
        cells.put("3,4", mBinding.timeRow3Col4);

        cells.put("4,0", mBinding.timeRow4Col0);
        cells.put("4,1", mBinding.timeRow4Col1);
        cells.put("4,2", mBinding.timeRow4Col2);
        cells.put("4,3", mBinding.timeRow4Col3);
        cells.put("4,4", mBinding.timeRow4Col4);

        cells.put("5,0", mBinding.timeRow5Col0);
        cells.put("5,1", mBinding.timeRow5Col1);
        cells.put("5,2", mBinding.timeRow5Col2);
        cells.put("5,3", mBinding.timeRow5Col3);
        cells.put("5,4", mBinding.timeRow5Col4);

        cells.put("6,0", mBinding.timeRow6Col0);
        cells.put("6,1", mBinding.timeRow6Col1);
        cells.put("6,2", mBinding.timeRow6Col2);
        cells.put("6,3", mBinding.timeRow6Col3);
        cells.put("6,4", mBinding.timeRow6Col4);

        cells.put("7,0", mBinding.timeRow7Col0);
        cells.put("7,1", mBinding.timeRow7Col1);
        cells.put("7,2", mBinding.timeRow7Col2);
        cells.put("7,3", mBinding.timeRow7Col3);
        cells.put("7,4", mBinding.timeRow7Col4);

        cells.put("8,0", mBinding.timeRow8Col0);
        cells.put("8,1", mBinding.timeRow8Col1);
        cells.put("8,2", mBinding.timeRow8Col2);
        cells.put("8,3", mBinding.timeRow8Col3);
        cells.put("8,4", mBinding.timeRow8Col4);

    }
}