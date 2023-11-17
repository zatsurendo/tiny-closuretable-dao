package com.roughandcheap.tinyclosuretabledao.jpatree;

import com.roughandcheap.tinyclosuretabledao.jpatree.closuretable.ClosureTableTreeNode;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class ClosureTableTreeNodeInfo {
    
    private ClosureTableTreeNode descendant;
    private String treePaths;
    private long depth;
    public ClosureTableTreeNodeInfo(ClosureTableTreeNode descendant, String treePaths, long depth) {
        this.descendant = descendant;
        this.treePaths = treePaths;
        this.depth = depth;
    }
}
