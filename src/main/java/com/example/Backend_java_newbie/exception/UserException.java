package com.example.Backend_java_newbie.exception;

public class UserException extends BaseException {
    public UserException(String code) {
        super("user."+ code);
    }
    public static UserException notFound(){
        return new UserException("not.found");
    }

    public static UserException requestNull(){
        return new UserException("register.request.null");
    }

    public static UserException emailNull(){
        return new UserException("register.email.null");
    }

    public static UserException createEmailNull() {
        return new UserException("create.email.null");
    }

    public static UserException createEmailDuplicated() {
        return new UserException("create.email.duplicated");
    }

    public static UserException createPasswordNull() {
        return new UserException("create.password.null");
    }

    public static UserException createFirstNameNull() {
        return new UserException("create.firstname.null");
    }

    public static UserException createLastNameNull() {
        return new UserException("create.lastname.null");
    }
    public static UserException createPhoneDuplicated() {
        return new UserException("create.phone.duplicated");
    }

    public static UserException createPhoneNull() {
        return new UserException("create.phone.null");
    }

    //LOGIN
    public static UserException loginEmailNotFound(){
        return new UserException("login.fail");
    }

    public static UserException loginFailPasswordIncorrect(){
        return new UserException("login.fail");
    }

    public static UserException unauthorized() {
        return new UserException("unauthorized");
    }

    //PASSWORD
    public static UserException setPasswordTokenNull() {
        return new UserException("set.password.token.null");
    }

    public static UserException setPasswordTokenInvalid() {
        return new UserException("set.password.token.invalid");
    }

    public static UserException setPasswordTokenExpired() {
        return new UserException("set.password.token.expired");
    }
}
