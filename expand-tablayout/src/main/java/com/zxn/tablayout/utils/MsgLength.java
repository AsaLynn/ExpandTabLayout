package com.zxn.tablayout.utils;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by zxn on 2020/2/1.
 */

@IntDef({IUnreadMsg.TEN_LENGTH, IUnreadMsg.HUNDRED_LENGTH, IUnreadMsg.THOUSAND_LENGTH, IUnreadMsg.TEN_THOUSAND_LENGTH})
@Retention(RetentionPolicy.SOURCE)
public @interface MsgLength {
}
