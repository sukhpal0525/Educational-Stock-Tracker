package com.aston.stockapp.domain.transaction;

import com.aston.stockapp.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime dateTime;
    private String stockTicker;
    private int quantity;
    private BigDecimal purchasePrice;
    private BigDecimal totalCost;
    private String transactionType;
}