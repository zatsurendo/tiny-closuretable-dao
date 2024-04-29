package com.roughandcheap.tinyclosuretabledao.commons.entity;

import java.io.Serializable;

public interface BasicEntity extends Serializable {
    
    /**
	 * @return the primary key of this tree node.
	 */
    Serializable getId();
}
