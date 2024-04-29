package com.roughandcheap.tinyclosuretabledao.commons.entity;

import com.roughandcheap.tinyclosuretabledao.jpatree.closuretable.TreePathId;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class LongPathId implements TreePathId<Long>{
    @EqualsAndHashCode.Include
    private Long ancestor;
    @EqualsAndHashCode.Include
    private Long descendant;
}
