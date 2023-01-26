package com.example.soswatch;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.soswatch.databinding.ActivityMainBinding;

public class MainActivity extends Activity {

    private TextView mTextView;
    private EditText eText;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mTextView = binding.text;

      eText=  binding.editName;
      //  eText.setImeOptions(EditorInfo.IME_ACTION_DONE);

        eText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
          @Override
          public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
              if (i == EditorInfo.IME_ACTION_DONE){
               //   Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                 // startActivity(intent);
                  binding.editGmail.requestFocus();
                  return  true;
              }
              return false;
          }
      });
        binding.editGmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE){

                    binding.editPhone.requestFocus();
                    return  true;
                }
                return false;
            }
        });

        binding.editPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE){

                    binding.editAge.requestFocus();
                    return  true;
                }
                return false;
            }
        });
        binding.editAge.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE){

                       Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                     startActivity(intent);
                    return  true;
                }
                return false;
            }
        });

    }
}