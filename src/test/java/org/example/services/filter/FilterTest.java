package org.example.services.filter;

import org.apache.commons.jexl3.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class FilterTest {


    @Test
    public void checkValidQuery() {
        String q = "(column[1]>10 & (column[10]='GKA')) & column[3] = 15";
        FilterImpl filter = new FilterImpl();
        assertTrue(filter.isValidExpression(q));
    }

    @Test
    public void checkInValidQuery() {
        String q = "dada (column[1]>10 & (column[5]='GKA')) & column[3] = 15 adadadadaw";
        FilterImpl filter = new FilterImpl();
        assertFalse(filter.isValidExpression(q));
    }

    @Test
    public void filter() {
        String row = "1,\"Goroka Airport\",\"Goroka\",\"Papua New Guinea\",\"GKA\",\"AYGA\",-6.081689834590001,145.391998291,5282,10,\"U\",\"Pacific/Port_Moresby\",\"airport\",\"OurAirports\"\n";
        String expression = "(column[3] = 'Goroka' || column[2] = 'Paris') & column[4] <> 'Rome' & column[7] < -5.6913";
        Filter filter = new FilterImpl();
        assertTrue(filter.filterByExpression(row, expression));
    }


    @Test
    public void jexl() {
        String q = "(column1 == 'gpa' || column2 == 7) && column3 < 19";
        var data = "gpa, 5, 17, 12QQ".split(", ");
        JexlContext context = new MapContext();
        JexlEngine engine = new JexlBuilder().create();
        JexlExpression expression = engine.createExpression(q);
        for (int i = 0; i < data.length; ++i) {
           try {
               int val = Integer.parseInt(data[i]);
               context.set("column"+(i+1), val);
           } catch (Exception e) {
               context.set("column"+(i+1), data[i]);
           }

       }
        Object res = expression.evaluate(context);
        boolean result = (boolean) res;
        System.out.println(result);
    }
}