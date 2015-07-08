package com.microsoft.projectoxforddemo.utils;

/**
 * Created by yulw on 7/5/2015.
 */
interface Subject {
    void alert();

    void attach(Observer ob);

    void detach(Observer ob);
}
