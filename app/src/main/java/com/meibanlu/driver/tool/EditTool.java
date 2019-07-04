package com.meibanlu.driver.tool;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * EditText是否全部为空
 * Created by lhq on 2017-12-26.
 */

public class EditTool {
    public static void setTxtChange(final TxtChangeListener txtChangeListener, final EditText... editTexts) {
        for (EditText edit : editTexts) {
            edit.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // 输入的内容变化的监听
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // 输入前的监听
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // 输入后的监听
                    boolean isEmpty = false;
                    for (EditText edit : editTexts) {
                        if (TextUtils.isEmpty(edit.getText().toString())) {
                            isEmpty = true;
                            break;
                        }
                    }
                    txtChangeListener.onTxtChange(isEmpty);
                }
            });
        }
    }

}
