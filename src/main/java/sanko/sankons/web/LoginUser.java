package sanko.sankons.web;

import java.lang.annotation.*; //ElementType, Retention, RetentionPolicy, Target

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser {

}
