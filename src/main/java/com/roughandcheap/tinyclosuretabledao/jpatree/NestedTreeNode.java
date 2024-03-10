package com.roughandcheap.tinyclosuretabledao.jpatree;

import java.util.ArrayList;
import java.util.List;

import com.roughandcheap.tinyclosuretabledao.jpatree.closuretable.ClosureTableTreeNode;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Zatsurendo
 * @since 2024-03-09
 */
@Setter
@Getter
@ToString
public class NestedTreeNode <N extends TreeNode> {
    
    private N node;
    private List<NestedTreeNode<N>> children;
    public void add(N node) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(new NestedTreeNode<>(node));
    }
    public NestedTreeNode(N node) {
        this.node = node;
        this.children = new ArrayList<>();
    }
}
