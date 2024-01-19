package org.jingtao8a.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class Hello implements Serializable {
    private String message;
    private String destription;
}
