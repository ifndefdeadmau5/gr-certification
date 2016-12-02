package com.SeongMin.GoodProduct.fragment;

/**
 * Created by SeongMin on 2015-01-28.
 */
public class GRListData {

    public String areacode;
    public String stdCode;
    public String stdName;
    public String orddate;
    public String udtdate;
    public String attachfile;
    public String attachfileUrl;
    public int CompanyCount;

    public GRListData() {

    }

    public GRListData(String areacode, String stdCode, String stdName, String orddate, String udtdate, String attachfile, String attachfileUrl, int companyCount) {
        this.areacode = areacode;
        this.stdCode = stdCode;
        this.stdName = stdName;
        this.orddate = orddate;
        this.udtdate = udtdate;
        this.attachfile = attachfile;
        this.attachfileUrl = attachfileUrl;
        CompanyCount = companyCount;
    }

    /*    public static final Comparator<GRListData> ALPHA_COMPARATOR = new Comparator<GRListData>() {
        private final Collator sCollator = Collator.getInstance();                 
                     
        Override
                
        public int compare(GRListData mListDate_1, GRListData mListDate_2) {
                        
            return sCollator.compare(mListDate_1.stdCode, mListDate_2.stdName);
                   
            }
    };*/
}