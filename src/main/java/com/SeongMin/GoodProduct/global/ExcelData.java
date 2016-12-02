package com.SeongMin.GoodProduct.global;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;

/**
 * Created by SeongMin on 2015-01-22.
 */
public class ExcelData extends Application {
    private int result = -1;
    private db dbAdapter;

    public static void setDefaultFont(Context ctx,
                                      String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(ctx.getAssets(),
                fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName,
                                      final Typeface newTypeface) {
        try {
            final Field StaticField = Typeface.class
                    .getDeclaredField(staticTypefaceFieldName);
            StaticField.setAccessible(true);
            StaticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public int getResult() {
        return result;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("mytag", "DatabaseTest :: onCreate()");

        String resultString = "default";
        this.dbAdapter = new db(this);

        dbAdapter.open();
        //dbAdapter.
        if (dbAdapter.isEmpty()) {
            // DB 파일이 없다면 엑셀로부터 생성
            Log.i("mytag", "비어쓰");

            copyExcelDataToDatabase();
        }
        dbAdapter.close();

        String FontName = "NanumBarunGothicUltraLight.ttf";

        setDefaultFont(this, "DEFAULT", FontName);
        setDefaultFont(this, "SANS_SERIF", FontName);
        setDefaultFont(this, "SERIF", FontName);

        setDefaultFont(this, "DEFAULT_BOLD", FontName);
        setDefaultFont(this, "MONOSPACE", FontName);
    }

    /*
     *                                  Copy
     *        Excel  -------------------------------------------> SQLite
     *
     */
    private void copyExcelDataToDatabase() {
        Log.w("ExcelToDatabase", "copyExcelDataToDatabase()");

        Workbook workbook = null;
        Sheet sheet = null;


        try {
            InputStream is = getBaseContext().getResources().getAssets().open("grcompany150228.xls");
            workbook = Workbook.getWorkbook(is);

            if (workbook != null) {
                sheet = workbook.getSheet(1);

                if (sheet != null) {

                    int nMaxColumn = sheet.getColumns();
                    int nRowStartIndex = 4;
                    int nRowEndIndex = sheet.getColumn(nMaxColumn - 1).length - 1;
                    boolean duplicationFlag;
                    ArrayList<String> tempCompany = new ArrayList<String>();
                    dbAdapter.open();
                    Log.i("ERROR", "Database Opend");

                    /* 컬럼을 하나하나 읽는데
                    * 같은 회사명이 나왔을 때
                    * 그 회사명을(FK) 로 타고들어가서 표준테이블에 새 표준으로 추가.
                    * 그리고 표준으로 검색할 때는......어차피 종류까지 포함한 쿼리를 날릴 수 없으니
                    * 종류 속성이 없는 표준명으로 Company 테이블에 필드를 하나 두면 됨.
                    */
                    for (int nRow = nRowStartIndex; nRow <= nRowEndIndex; nRow++) {
                        duplicationFlag = false;

                        String areacode = sheet.getCell(1, nRow).getContents();
                        String standardcode = sheet.getCell(2, nRow).getContents();
                        String standardname = sheet.getCell(3, nRow).getContents();
                        String type = sheet.getCell(4, nRow).getContents();
                        String name = sheet.getCell(10, nRow).getContents();
                        String representative = sheet.getCell(11, nRow).getContents();
                        String enddate = sheet.getCell(9, nRow).getContents();
                        String address = sheet.getCell(12, nRow).getContents();
                        Log.i("ERROR", "each for 전");


                        // tempCompany 리스트를 돌면서 중복업체인지 확인
                        Log.i("Error", "name: " + name);

                        for (int i = 0; i < tempCompany.size(); i++) {
                            String temp = tempCompany.get(i);
                            Log.i("Error", "temp : " + temp);
                            if (temp.equals(name)) {
                                dbAdapter.createGRlistdata(name, standardname, standardcode, areacode, type, enddate);//여기서 grlist 생성
                                dbAdapter.updateCompanyStdcount(name);
                                duplicationFlag = true;
                                Log.i("ERROR2", i + " - DUPLICATED " + name + " with " + temp);

                                break;
                            }
                        }

                        tempCompany.add(name); // 중복업체가 아닐경우 검사셋에 등록

                        if (!duplicationFlag) { // 중복업체가 아닐 경우
                            dbAdapter.createCompanyData(name, representative, address);
                            dbAdapter.createGRlistdata(name, standardname, standardcode, areacode, type, enddate);
                            Log.i("ERROR2", "새로운 회사이므로 등록");
                        }
                    }
                    dbAdapter.close();
                    Log.i("ERROR", "Database Closed");

                } else {
                    System.out.println("Sheet is null!!");
                }

            } else {
                System.out.println("WorkBook is null!!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
    }

}