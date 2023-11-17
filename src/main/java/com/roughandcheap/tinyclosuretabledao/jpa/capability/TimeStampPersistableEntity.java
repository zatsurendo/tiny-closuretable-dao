package com.roughandcheap.tinyclosuretabledao.jpa.capability;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

/**
 * 永続化データの作成、更新時に
 * {@link https://www.baeldung.com/jpa-java-time}
 */
@Getter
@Setter
@MappedSuperclass
public class TimeStampPersistableEntity {
    
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    protected LocalDateTime updatedAt;
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    protected LocalDateTime createdAt;
    @PrePersist
    private void onInsert() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
