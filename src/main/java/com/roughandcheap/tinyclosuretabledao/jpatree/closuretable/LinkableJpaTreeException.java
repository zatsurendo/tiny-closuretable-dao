package com.roughandcheap.tinyclosuretabledao.jpatree.closuretable;

import com.roughandcheap.tinyclosuretabledao.jpatree.JpaTreeException;

public class LinkableJpaTreeException extends JpaTreeException {

    public LinkableJpaTreeException() {}
    public LinkableJpaTreeException(String mesg) {
        super(mesg);
    }
    public LinkableJpaTreeException(String mesg, Throwable cause) {
        super(mesg, cause);
    }
    public LinkableJpaTreeException(Throwable cause) {
        super(cause);
    }
}
