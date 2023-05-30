package org.example.services.names;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RowName implements Comparable<RowName>{
    String name;
    int rowByte;

    @Override
    public int compareTo(RowName o) {
        return name.compareTo(o.name);
    }
}
