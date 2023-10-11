package com.aston.stockapp.action.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Actions")
public class Action {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    private Date timestamp;
}