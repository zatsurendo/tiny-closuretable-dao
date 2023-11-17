package com.roughandcheap.tinyclosuretabledao.jpa.capability;

import java.time.LocalDateTime;

public interface TimeSampedPersistable {
    
    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);
    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);
}
