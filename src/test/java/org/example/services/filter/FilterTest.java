package org.example.services.filter;

import org.apache.commons.jexl3.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FilterTest {

    static Stream<String> validQueries() {
        return Stream.of(
                "(column[1]>10 & (column[10]='GKA')) & column[3] = 15",
                "column[13] <> -13.413 || column[1] > 45 & (column[5] < 4 & column[12] = 6)",
                "column[11] = 'UwU'",
                "column[3144] <>    \"pomp\" & column[3] <4 || column[1]='qqq'",
                "(((column[314]=-3.14)))",
                "\n",
                ""
        );
    }

    static Stream<String> invalidQueries() {
        return Stream.of(
                "(colmn[1]>10 & (column[10]='GKA')) & column[3] = 15",
                "adad  column[13] <> -13.413 || column[1] > 45 & (column[5] < 4 & column[12] = 6)",
                "column[13] <> -13.413 | column[1] > 45 & (column[5] < 4 & column[12] = 6)",
                "column[13] <> -13.413 || column[1] > 45 & (column[5] < 4 && column[12] = 6)",
                "column[13] <  > -13.413 || column[1] > 45 & (column[5] < 4 & column[12] = 6)",
                "column[11] = 'UwU' kadakmdasl",
                "column[3144] <> ()    \"pomp\" & column[3] <4 || column[1]='qqq'",
                "(((column[314] != -3.14)))"
        );
    }


    @ParameterizedTest
    @MethodSource("validQueries")
    public void checkValidQuery(String q) throws Exception {
        new FilterImpl(q);
    }

    @ParameterizedTest
    @MethodSource("invalidQueries")
    public void checkInValidQuery(String q) {
        try {
            new FilterImpl(q);
        } catch (Exception e) {
            assertTrue(true);
            return;
        }
        fail();
    }

    @Test
    public void acceptableFilter() throws Exception {
        String row = "1,\"Goroka Airport\",\"Goroka\",\"Papua New Guinea\",\"GKA\",\"AYGA\",-6.081689834590001,145.391998291,5282,10,\"U\",\"Pacific/Port_Moresby\",\"airport\",\"OurAirports\"\n";
        String expression = "(column[3] = 'Goroka' || column[2] = 'Paris') & column[4] <> 'Rome' & column[7] < -5.6913";
        Filter filter = new FilterImpl(expression);
        assertTrue(filter.filter(row));
    }

    @Test
    public void notAcceptableFilter() throws Exception {
        String row = "1,\"Goroka Airport\",\"Goroka\",\"Papua New Guinea\",\"GKA\",\"AYGA\",-6.081689834590001,145.391998291,5282,10,\"U\",\"Pacific/Port_Moresby\",\"airport\",\"OurAirports\"\n";
        String expression = "(column[3] = 'Goroka' || column[2] = 'Paris') & column[4] <> 'Rome' & column[7] < -8.6913";
        Filter filter = new FilterImpl(expression);
        assertFalse(filter.filter(row));
    }

}