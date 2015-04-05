package com.ragego.engine;

/**
 * Describe a violation of Go rules
 */
public class GoRuleViolation extends Exception {

    public GoRuleViolation(Type type, String message) {
        super();
        switch (type){

            case KO:

                break;
        }
    }

    enum Type{
        KO, SUCIDE,
    }

}
