package com.test.ecomm.modules.address.entity;

import com.test.ecomm.common.entity.BaseEntity;
import com.test.ecomm.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Builder.Default
    @Column(nullable = false, length = 50)
    private String country = "Azərbaycan";

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 200)
    private String street;

    @Column(length = 20)
    private String zipCode;

    @Column(nullable = false, length = 20)
    private String phone;

    @Builder.Default
    private boolean isDefault = false;
}
