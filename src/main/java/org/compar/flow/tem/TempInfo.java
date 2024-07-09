package org.compar.flow.tem;

import java.util.Random;

public class TempInfo {
    public static final Random random = new Random();

    private final String town;
    private final int temp;


    public TempInfo(String town, int temp) {
        this.town = town;
        this.temp = temp;
    }

    public static TempInfo fetch(String town){
        //10次随机失败一次
        if (random.nextInt(10) == 0)
            throw new RuntimeException("Error!");
        return new TempInfo(town,random.nextInt(100));
    }

    @Override
    public String toString() {
        return "TempInfo{" +
                "town='" + town + '\'' +
                ", temp=" + temp +
                '}';
    }

    public int getTemp() {
        return temp;
    }

    public String getTown() {
        return town;
    }


}
