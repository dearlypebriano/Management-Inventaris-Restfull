package com.management.ManagementInventaris.location.province;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name  = "provinces")
public class Province implements Serializable {

    @Id
    @Column(unique = true, nullable = false, length = 2)
    private String id;

    private String name;

    public Province(String name) {
        this.name = name;
    }
}