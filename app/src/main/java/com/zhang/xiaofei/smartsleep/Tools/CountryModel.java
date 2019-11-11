package com.zhang.xiaofei.smartsleep.Tools;

import android.graphics.drawable.Drawable;

public class CountryModel
{
    // 国家名称
    public String countryName;

    // 国家代码
    public String countryNumber;

    public String simpleCountryNumber;

    // 国家名称缩写
    public String countrySortKey;

    // 国家图标
    public Drawable contactPhoto;

    public CountryModel(String countryName, String countryNumber, String countrySortKey) {
        super();
        this.countryName = countryName;
        this.countryNumber = countryNumber;
        this.countrySortKey = countrySortKey;
        if (countryNumber != null)
        {
            this.simpleCountryNumber = countryNumber.replaceAll("\\-|\\s", "");
        }
    }

}
