package com.roughandcheap.tinyclosuretabledao.commons.entity;

import java.io.Serializable;

/**
 * 
 * @author zaturendo
 * @since 2024-01-07
 */
public interface BasicEntity extends Serializable {
    
    /**
	 * @return the primary key of this tree node.
	 */
    Serializable getId();
}
