package eu.ajr.moviechallenge.util;

import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;

@Service
public class ValidationUtil {

    /**
     * Check if given string exists as a field in a given class
     * @param modelClass Class to search for the field
     * @param testField String Search criteria
     */
    public Boolean validateModelField(Class modelClass, String testField) {

        Field[] fieldlist = modelClass.getDeclaredFields();

        if(Arrays.stream(fieldlist).anyMatch(aField -> aField.getName().equals(testField))) {
            return true;
        }

        return false;
    }
}
