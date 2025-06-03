package org.rubilnik.auth_service.record_classes;

public class Records{
    public static record UserValidationInfo(String id, String email, String password) {}
    public static record UserTokenInfo(String id, String email, String password, String name, String token){}
    public static record AuthReqData(String username, String password){}
}
