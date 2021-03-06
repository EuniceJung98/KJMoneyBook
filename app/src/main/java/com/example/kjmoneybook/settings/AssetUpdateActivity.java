package com.example.kjmoneybook.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kjmoneybook.DatabaseHelper;
import com.example.kjmoneybook.R;

import java.util.ArrayList;

public class AssetUpdateActivity extends Activity {
    RecyclerView assetRecyclerView;
    AssetUpdateAdapter assetUpdateAdapter;
    DatabaseHelper dbHelper;
    SQLiteDatabase database;
    Cursor cursor;
    String updateStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_asset_update);

        assetRecyclerView = findViewById(R.id.assetItemRecyclerView);
        assetUpdateAdapter = new AssetUpdateAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        assetRecyclerView.setLayoutManager(layoutManager);
        assetRecyclerView.setAdapter(assetUpdateAdapter);
        dbHelper = new DatabaseHelper(getApplicationContext());
        database = dbHelper.getWritableDatabase();

        findViewById(R.id.addAssetButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    regDBAsset();
                }
            }
        });

        setAssetName();
    }//onCreate끝부분

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void regDBAsset() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog_Alert);
        final EditText addAssetEditText = new EditText(AssetUpdateActivity.this);
        addAssetEditText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        addAssetEditText.setBackgroundColor(Color.parseColor("#FFF1F1"));
        addAssetEditText.setTextColor(Color.BLACK);
        addAssetEditText.setTextCursorDrawable(R.drawable.dialog_cursor_color);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(10);
        addAssetEditText.setFilters(FilterArray);
        addAssetEditText.addTextChangedListener(new TextWatcher(){
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (addAssetEditText.getText().toString().length()>9) {
                    Toast.makeText(getApplicationContext(),"10자이상 입력할수 없습니다",Toast.LENGTH_SHORT).show();
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void afterTextChanged(Editable s){}
        });
        builder.setView(addAssetEditText);
        builder.setTitle("자산 추가");

        builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {}
        });

        builder.setNegativeButton("돌아감", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog= builder.create();
        // 창 띄우기
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Boolean wantToCloseDialog = false;
                String inputRegAsset= addAssetEditText.getText().toString();
                if (inputRegAsset.equals("") || inputRegAsset.trim().equals("")) {
                    addAssetEditText.post(new Runnable() {
                        @Override
                        public void run() {
                            addAssetEditText.setFocusableInTouchMode(true);
                            addAssetEditText.requestFocus();
                        }
                    });
                    Toast.makeText(AssetUpdateActivity.this, "추가할 이름을 입력하세요", Toast.LENGTH_SHORT).show();
                }else {
                    String assetInsertsql="insert into asset(asset_name) values('"+inputRegAsset.trim()+"')";
                    try {
                        database.execSQL(assetInsertsql);
                        Toast.makeText(AssetUpdateActivity.this, "자산등록완료", Toast.LENGTH_SHORT).show();
                        setAssetName();
                        wantToCloseDialog = true;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                if(wantToCloseDialog)
                    dialog.dismiss();

            }
        });
    }

    private void setAssetName() {
        assetUpdateAdapter.clear();
        String assetSelecSql="select asset_id,asset_name from asset";
        try {
            cursor = database.rawQuery(assetSelecSql,null);
            while(cursor.moveToNext()){
                int assetId= cursor.getInt(0);
                String assetitem =cursor.getString(1);
                assetUpdateAdapter.addItem(new UpdateSetting(assetId,assetitem));
            }
            assetUpdateAdapter.notifyDataSetChanged();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            cursor.close();
        }
    }


    ///////////////////////////////////////////////////

    public class AssetUpdateAdapter extends RecyclerView.Adapter<AssetUpdateAdapter.ViewHolder> {
        ArrayList<UpdateSetting> items = new ArrayList<>();

        public AssetUpdateAdapter() { }

        @NonNull
        @Override
        public AssetUpdateActivity.AssetUpdateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.cate_andasset_item, parent, false);
            return new AssetUpdateActivity.AssetUpdateAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull AssetUpdateActivity.AssetUpdateAdapter.ViewHolder holder, int position) {
            UpdateSetting item = items.get(position);
            holder.setItem(item);
        }


        @Override
        public int getItemCount() {
            return items.size();
        }

        public void addItem(UpdateSetting item) {
            items.add(item);
        }

        public UpdateSetting getItem(int position) {
            return items.get(position);
        }

        public void setItem(int position, UpdateSetting item) {
            items.set(position, item);
        }

        public void clear() {
            items.clear();
        }

        public ArrayList<UpdateSetting> getList() {
            return items;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView itembutton;

            public ViewHolder(@NonNull final View itemView) {
                super(itemView);
                itembutton = itemView.findViewById(R.id.settingItemsButton);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            updateAsset();
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    private void updateAsset() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext(),R.style.Theme_AppCompat_Light_Dialog_Alert);
                        final EditText updateAssetEditText = new EditText(itemView.getContext());
                        updateAssetEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                        updateAssetEditText.setBackgroundColor(Color.parseColor("#FFF1F1"));
                        updateAssetEditText.setTextColor(Color.BLACK);
                        updateAssetEditText.setTextCursorDrawable(R.drawable.dialog_cursor_color);
                        InputFilter[] FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(10);
                        updateAssetEditText.setFilters(FilterArray);
                        updateAssetEditText.addTextChangedListener(new TextWatcher(){
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if (updateAssetEditText.getText().toString().length()>9) {
                                    Toast.makeText(itemView.getContext(),"10자이상 입력할수 없습니다",Toast.LENGTH_SHORT).show();
                                }
                            }
                            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                            public void afterTextChanged(Editable s){}
                        });
                        updateAssetEditText.setText(items.get(getAdapterPosition()).getAssetName());
                        builder.setView(updateAssetEditText);
                        builder.setTitle("자산 수정");
                        builder.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {}
                        });

                        builder.setNeutralButton("돌아감", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        if(items.get(getAdapterPosition()).getId()>1){//자산 하나는 반드시 있어야 함, 수정은 가능
                            builder.setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String assetDeleteSql="delete from asset where asset_id="+items.get(getAdapterPosition()).getId();
                                    try {
                                        database.execSQL(assetDeleteSql);
                                        Toast.makeText(itemView.getContext(), "자산 삭제완료", Toast.LENGTH_SHORT).show();
                                        setAssetName();
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        final AlertDialog dialog= builder.create();
                        dialog.show();


                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                Boolean wantToCloseDialog = false;
                                String inputRegCategory= updateAssetEditText.getText().toString();
                                if (inputRegCategory.equals("") || inputRegCategory.trim().equals("")) {
                                    updateAssetEditText.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            updateAssetEditText.setFocusableInTouchMode(true);
                                            updateAssetEditText.requestFocus();
                                        }
                                    });
                                    Toast.makeText(itemView.getContext(), "수정할 이름을 입력하세요", Toast.LENGTH_SHORT).show();
                                }else {
                                    updateStr= updateAssetEditText.getText().toString().trim();
                                    wantToCloseDialog = true;
                                    updateTypeCheck();
                                }

                                if(wantToCloseDialog)
                                    dialog.dismiss();




                            }
                        });
                    }
                });


            }

            public void setItem(UpdateSetting item) {
                itembutton.setText(item.getAssetName());
            }

            public void updateTypeCheck() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext(),R.style.Theme_AppCompat_Light_Dialog_Alert);
                    builder.setTitle("수정확인");
                    builder.setPositiveButton("이후 입력부터 적용", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String assetUpdatesql="update asset set asset_name='"+
                                    updateStr+"' where asset_id="+items.get(getAdapterPosition()).getId();
                            try {
                                database.execSQL(assetUpdatesql);
                                Toast.makeText(itemView.getContext(), "자산수정완료", Toast.LENGTH_SHORT).show();
                                setAssetName();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    builder.setNegativeButton("이전 입력에도 적용", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String updateExpenseOldReg = "update expense set asset_name='"+
                                    updateStr+"' where asset_name='"+items.get(getAdapterPosition()).getAssetName()+"'";
                            String updateIncomeOldReg = "update income set asset_name='"+
                                    updateStr+"' where asset_name='"+items.get(getAdapterPosition()).getAssetName()+"'";
                            String assetUpdatesql="update asset set asset_name='"+
                                    updateStr+"' where asset_id="+items.get(getAdapterPosition()).getId();

                                database.beginTransaction();
                                try {
                                    database.execSQL(assetUpdatesql);
                                    database.execSQL(updateExpenseOldReg);
                                    database.execSQL(updateIncomeOldReg);
                                    database.setTransactionSuccessful();
                                    Toast.makeText(itemView.getContext(), "자산수정완료", Toast.LENGTH_SHORT).show();
                                    setAssetName();
                                }finally {
                                    database.endTransaction();
                                }

                        }
                    });
                    builder.show();
            }
        }
    }




}