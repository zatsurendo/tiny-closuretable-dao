package com.roughandcheap.tinyclosuretabledao.jpatree.closuretable;

import com.roughandcheap.tinyclosuretabledao.jpatree.TreeNode;

/**
 * Every domain object (POJO) that wants tree structure
 * by a ClosureTableTreeDao needs to implement this interface.
 * 
 * @author Fritz Ritzberger, 14.10.2012
 */
public interface ClosureTableTreeNode extends TreeNode {
    
    /** For copy and unique constraint checking this is required. */
    @Override
    ClosureTableTreeNode clone();
}
