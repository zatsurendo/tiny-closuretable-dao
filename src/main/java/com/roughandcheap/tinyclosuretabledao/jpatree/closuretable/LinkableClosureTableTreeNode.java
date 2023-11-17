package com.roughandcheap.tinyclosuretabledao.jpatree.closuretable;

import com.roughandcheap.tinyclosuretabledao.jpatree.LinkableTreeNode;

public interface LinkableClosureTableTreeNode<N extends LinkableClosureTableTreeNode<N>> extends ClosureTableTreeNode, LinkableTreeNode {
    
    N getLinkTo();
    void setLinkTo(N linkTo);
    /**  */
    @Override
    N clone();
}
