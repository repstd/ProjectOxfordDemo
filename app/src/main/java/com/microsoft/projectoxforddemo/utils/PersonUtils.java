package com.microsoft.projectoxforddemo.utils;

/**
 * Created by yulw on 7/4/2015.
 */
public class PersonUtils {
    static Person m_currentPerson = new Person();

    public static String getPersonName() {
        return m_currentPerson.getName();
    }

    public static void setPerson(String name, String group) {
        m_currentPerson = new Person(name, group);
    }

    static class Person {
        private String m_name = "Test";
        private String m_group = "Microsoft";

        Person(String name, String group) {
            m_name = name;
            m_group = group;
        }

        Person() {
        }

        String getName() {
            return m_name;
        }

        String getGroup() {
            return m_group;
        }
    }
}
