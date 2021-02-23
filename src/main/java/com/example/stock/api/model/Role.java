package com.example.stock.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@ToString
@Document
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    private long id;
    @Indexed
    private String name;
}
