package com.roughandcheap.tinyclosuretabledao.jpatree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import lombok.Getter;
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
public class NestedTreeNode <N extends Serializable> {
    
    private N node;
    private List<NestedTreeNode<N>> children;
    public NestedTreeNode(N node) {
        this.node = node;
        this.children = new ArrayList<>();
    }
    public void add(N node) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(new NestedTreeNode<>(node));
    }
    public void add(NestedTreeNode<N> node) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(node);
    }
    public boolean hasChildren() {
        if (CollectionUtils.isEmpty(children)) {
            return true;
        }
        return false;
    }
}
