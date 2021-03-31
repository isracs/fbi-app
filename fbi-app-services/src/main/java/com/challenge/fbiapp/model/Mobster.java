package com.challenge.fbiapp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "mobsters")
@JsonInclude(Include.NON_EMPTY)
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Mobster {

    private String name;
    private String surName;

    @TextIndexed
    private String nickname;

    private String directBoss;

    private List<String> formerBosses;

    private boolean inPrison;

    private String since;

    @Override
    public String toString() {
        return name + ", The " + nickname + " " + surName;
    }

}
