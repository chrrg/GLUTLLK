package cn.edu.glut.llk.zhu.suanfa;

import android.graphics.Bitmap;

interface LinkInterface<T> {

    public boolean isEmpty();

    public void setEmpty();

    public void setNonEmpty();

    public T getContent();

    public void setContent(Bitmap content);
}
