package com.driverskr.lib.net.callback;

import com.driverskr.lib.net.ParameterizedTypeImpl;
import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.driverskr.lib.bean.BaseBean;

public abstract class HttpCallback<T> implements CallBack<T> {

    @Override
    public void onNext(String responseBody) {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            Type ty = new ParameterizedTypeImpl(BaseBean.class, new Type[]{types[0]});
            BaseBean<T> data = new Gson().fromJson(responseBody, ty);
            onSuccess(data.getResult(), "" + data.getCode(), data.getMsg());
        } else {
            throw new ClassCastException();
        }
    }
}