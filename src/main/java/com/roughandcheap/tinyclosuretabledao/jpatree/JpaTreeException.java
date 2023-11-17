package com.roughandcheap.tinyclosuretabledao.jpatree;

public class JpaTreeException extends RuntimeException {
    
    public JpaTreeException() {}
    public JpaTreeException(String mesg) {
        super(mesg);
    }
    public JpaTreeException(String mesg, Throwable cause) {
        super(mesg, cause);
    }
    public JpaTreeException(Throwable cause) {
        super(cause);
    }
}
