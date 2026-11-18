package domain.validators;

@FunctionalInterface
public interface Validator<T>{
    void validate(T entity);
    default Validator<T> andThen(Validator<T> other){
        return (T entity)->{
            this.validate(entity);
            other.validate(entity);
        };
    }
}