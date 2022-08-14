package com.universityadministration.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "dataset")
@Setter
@Getter
public class dataset {

    @Id
    @Column(name = "dataset_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int voltage;
    private int ampere;
    private String catchTime;
    private int userId;

}
