package com.roughandcheap.tinyclosuretabledao.jpatree.closuretable;

import java.io.Serializable;

public interface TreePathId<T> extends Serializable {
    
    T getAncestor();
    void setAncestor(T t);
    T getDescendant();
    void setDescendant(T t);
}
