package com.roughandcheap.tinyclosuretabledao.jpatree;

public interface LinkableTreeNode {
    
    Type getType();
    void setType(Type type);
    boolean isDeadLink();

    public static enum Type {
        LINK,
        NORMAL
    }
}
