package com.example.soswatch;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.soswatch.databinding.ActivityMain2Binding;

public class MainActivity2 extends Activity {

    private TextView mTextView;
    private ActivityMain2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mTextView = binding.text;
    }
}