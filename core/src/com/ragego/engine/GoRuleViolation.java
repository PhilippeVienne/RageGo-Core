package com.ragego.engine;

/**
 * Describe a violation of Go rules
 */
public class GoRuleViolation extends Exception {

    private String message = "Go Rule Violating";
    private Type type;

    public GoRuleViolation(Type type, String message) {
        super();
        this.message = message;
        this.type = type;
        switch (type){

            case KO:

                break;
        }
    }

    @Override
    public String getMessage() {
        return "[GO RULE Type=" + type + "] " + message;
    }

    enum Type{
        KO, SUICIDE,
    }

}
