package com.perfectorium.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "suppliers")
public class Supplier {
    @Id
    @Column(name = "sid")
    private long sysId;
    @Column
    private String id;
    @Column
    private String name;
    @Column
    private String address;
    @Column
    private String phone;
    @Column
    private String phone2;
    @Column
    private String email;
}
