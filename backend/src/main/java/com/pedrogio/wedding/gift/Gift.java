package com.pedrogio.wedding.gift;

import com.pedrogio.wedding.infra.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gifts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gift extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "s3_key", length = 500)
    private String s3Key;

    @OneToMany(mappedBy = "gift")
    @Builder.Default
    private List<GiftPurchase> purchases = new ArrayList<>();
}
