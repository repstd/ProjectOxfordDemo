package com.microsoft.projectoxforddemo.utils;

/**
 * Created by yulw on 7/5/2015.
 */
interface Subject {
    public abstract void alert();

    public abstract void attach(Observer ob);

    public abstract void detach(Observer ob);
}
