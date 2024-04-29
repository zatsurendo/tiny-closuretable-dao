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
    private String pathSeq;
    private long depth;
    private int orderIndex;
    public ClosureTableTreeNodeInfo(ClosureTableTreeNode descendant, String treePaths, long depth) {
        this.descendant = descendant;
        this.treePaths = treePaths;
        this.depth = depth;
    }
    public ClosureTableTreeNodeInfo(ClosureTableTreeNode descendant, String treePaths, long depth, int orderIndex) {
        this(descendant, treePaths, depth);
        this.orderIndex = orderIndex;
    }
    public ClosureTableTreeNodeInfo(ClosureTableTreeNode descendant, String treePaths, String pathSeq, long depth, int orderIndex) {
        this(descendant, treePaths, depth, orderIndex);
        this.pathSeq = pathSeq;
    }
}
