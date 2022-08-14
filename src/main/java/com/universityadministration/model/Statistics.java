package com.universityadministration.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "statistics")
@Setter
@Getter
public class Statistics {

    @Id
    @Column(name = "statistics_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int voltage;
    private int ampere;
    private String catchTime;

    public Statistics() {
    }

    public Statistics(int voltage, int ampere, String catchTime, User user) {
        this.voltage = voltage;
        this.ampere = ampere;
        this.catchTime = catchTime;
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

}
