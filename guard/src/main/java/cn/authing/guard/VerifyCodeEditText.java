package cn.authing.guard;

import static cn.authing.guard.util.Const.NS_ANDROID;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import cn.authing.guard.activity.AuthActivity;
import cn.authing.guard.analyze.Analyzer;
import cn.authing.guard.data.Config;
import cn.authing.guard.internal.EditTextLayout;
import cn.authing.guard.util.Util;

public class VerifyCodeEditText extends EditTextLayout implements TextWatcher {

    private static final int ENormal = 0;
    private static final int EFrame = 1;
    private static final int EUnderline = 2;

    private int maxLength = 6;
    private int codeMode;
    private final List<EditText> editTextList = new ArrayList<>();
    private boolean autoLogin;

    public VerifyCodeEditText(@NonNull Context context) {
        this(context, null);
    }

    public VerifyCodeEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerifyCodeEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Analyzer.report("VerifyCodeEditText");
        if (attrs == null || attrs.getAttributeValue(NS_ANDROID, "hint") == null) {
            getEditText().setHint(context.getString(R.string.authing_verify_code_edit_text_hint));
        }
        Authing.getPublicConfig(config -> init(config, context, attrs));
    }

    private void init(Config config, Context context, @Nullable AttributeSet attrs) {
        if (config != null) {
            maxLength = config.getVerifyCodeLength();
        }

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VerifyCodeEditText);
        codeMode = array.getInt(R.styleable.VerifyCodeEditText_codeMode, 0);
        maxLength = array.getInt(R.styleable.VerifyCodeEditText_digit, maxLength);
        boolean hyphen = array.getBoolean(R.styleable.VerifyCodeEditText_hyphen, false);
        autoLogin = array.getBoolean(R.styleable.VerifyCodeEditText_autoLogin, true);


        if (codeMode == ENormal) {
            CharSequence s = getEditText().getHint();
            if (s == null) {
                editText.setHint(R.string.authing_verify_code_edit_text_hint);
            }
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
            editText.addTextChangedListener(this);
        } else {
            root.removeAllViews();

            int bw = (int)array.getDimension(R.styleable.VerifyCodeEditText_boxWidth, 108);
            int bh = (int)array.getDimension(R.styleable.VerifyCodeEditText_boxHeight, 128);
            int space = (int)array.getDimension(R.styleable.VerifyCodeEditText_boxSpacing, 36);
            int p = space / 2;
            for (int i = 0;i < maxLength;++i) {
                LayoutParams lp = new LayoutParams(bw, bh);
                int left = p;
                int right = p;
                if (i == 0) {
                    left = 0;
                }
                if (i == maxLength - 1) {
                    right = 0;
                }
                lp.setMargins(left, 0, right, 0);

                EditText et = new EditText(context);
                editTextList.add(et);
                root.addView(et);

                if (hyphen && i == maxLength / 2 - 1) {
                    addHyphenView(bw);
                }

                et.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                et.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                et.setText(" "); // when text is empty & cursor is hidden, menu will not show on long click
                et.setCursorVisible(false);
                et.setSelectAllOnFocus(true);
                et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
                et.setLayoutParams(lp);
                et.setHighlightColor(0);
                et.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        if (item.getItemId() == android.R.id.paste) {
                            ClipboardManager clipboard = context.getSystemService(ClipboardManager.class);
                            ClipData clip = clipboard.getPrimaryClip();
                            if (clip != null) {
                                for (int i = 0; i < clip.getItemCount(); i++) {
                                    final CharSequence paste;
                                    // Get an item as text and remove all spans by toString().
                                    final CharSequence text = clip.getItemAt(i).coerceToText(getContext());
                                    paste = (text instanceof Spanned) ? text.toString() : text;
                                    if (paste != null && paste.length() == maxLength) {
                                        for (int j = 0; j < maxLength; ++j) {
                                            EditText e = editTextList.get(j);
                                            e.setText(paste.subSequence(j, j + 1));
                                        }
                                    }
                                }
                            }
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {

                    }
                });
                et.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s != null && s.toString().trim().length() != 0) {
                            if (!editTextList.get(maxLength - 1).hasFocus()) {
                                for (int i = 0;i < maxLength - 1;++i) {
                                    EditText cur = editTextList.get(i);
                                    if (cur.hasFocus()) {
                                        EditText next = editTextList.get(i + 1);
                                        next.requestFocus();
                                        if (next.getText().length() == 1) {
                                            next.setSelection(0, 1);
                                        }
                                        break;
                                    }
                                }
                            }

                            boolean ready = true;
                            for (EditText et : editTextList) {
                                if (TextUtils.isEmpty(et.getText().toString().trim())) {
                                    ready = false;
                                    break;
                                }
                            }
                            if (ready) {
                                codeEntered();
                            }
                        }
                    }
                });
                et.setOnKeyListener((v, keyCode, event) -> {
                    if(event.getAction() == KeyEvent.ACTION_UP  && keyCode == KeyEvent.KEYCODE_DEL) {
                        if (!editTextList.get(0).hasFocus()) {
                            for (int i1 = 1; i1 < maxLength; ++i1) {
                                EditText cur = editTextList.get(i1);
                                if (cur.hasFocus()) {
                                    cur.setText(" ");
                                    EditText next = editTextList.get(i1 - 1);
                                    if (next.getText().length() == 1) {
                                        next.setSelection(0, 1);
                                    }
                                    next.requestFocus();
                                    break;
                                }
                            }
                        } else {
                            editTextList.get(0).setText(" ");
                            editTextList.get(0).setSelection(0, 1);
                        }
                    }
                    return false;
                });

                et.setOnClickListener(view -> {
                    String value = ((EditText)view).getText().toString();
                    if (!TextUtils.isEmpty(value) && value.length() == 1){
                        ((EditText)view).setSelection(0, 1);
                    }
                });

                if (codeMode == EFrame) {
                    et.setBackgroundResource(R.drawable.authing_verify_code_background);
                } else if (codeMode == EUnderline) {
                    et.setBackgroundResource(R.drawable.authing_verify_code_background_underline);
                }
            }

            if (editTextList.size() > 0) {
                post(()->{
                    editTextList.get(0).requestFocus();
                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
                });
            }
        }

        array.recycle();
    }

    private void addHyphenView(int bw) {
        View hyphenView = new View(getContext());
        hyphenView.setBackgroundColor(0xFFDDDDDD);
        int h = (int)Util.dp2px(getContext(), 2);
        LayoutParams lp = new LayoutParams(bw/3, h);
        hyphenView.setLayoutParams(lp);
        root.addView(hyphenView);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        super.onTextChanged(s, start, before, count);
    }

    @Override
    public void afterTextChanged(Editable s) {
        // code mode is ENormal
        if (s.length() == maxLength && autoLogin) {
            codeEntered();
        }
    }

    private void codeEntered() {
        LoginButton button = (LoginButton) Util.findViewByClass(this, LoginButton.class);
        if (button != null) {
            button.login();
        }

        if (getContext() instanceof AuthActivity) {
            AuthActivity activity = (AuthActivity) getContext();
            activity.fire(AuthActivity.EVENT_VERIFY_CODE_ENTERED, "");
        }
    }

    public Editable getText() {
        if (codeMode == ENormal)
            return editText.getText();
        else {
            Editable res = new SpannableStringBuilder();
            for (EditText et : editTextList) {
                res.append(et.getText().toString().trim());
            }
            return res;
        }
    }

    public int getCodeMode() {
        return codeMode;
    }

    public int getMaxLength() {
        return maxLength;
    }
}
