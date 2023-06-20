package x590.yava.example.annotation;

import java.lang.annotation.*;

@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.CLASS)
@Repeatable(PackageAnnotationsExample.class)
public @interface PackageAnnotationExample {
}
