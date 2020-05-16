package com.example.linear_tflite;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Interpreter.Options;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    private Object Intrinsics;

    private MappedByteBuffer loadModelFile(Activity activity, String MODEL_FILE) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
    String modelFile="model.tflite";

    private Interpreter tflite;
    private Options tfliteoptions = new Options();
    private MappedByteBuffer tflitemodel;
    private TextView  output;
    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input=findViewById(R.id.Input_text);
        output=findViewById(R.id.output);

        try {
            this.tflitemodel = this.loadModelFile(MainActivity.this,modelFile);
            this.tfliteoptions.setNumThreads(1);
            tflite = new Interpreter(tflitemodel, tfliteoptions);


        } catch (IOException e) {
            e.printStackTrace();
        }

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                MainActivity.this.doInference();

            }
        });
    }

    private final void doInference() {

        float number = ParseDouble(input.getText().toString());



        float[] inal = new float[]{number};
        ByteBuffer out = ByteBuffer.allocateDirect(4);

        out.order(ByteOrder.nativeOrder());
        //float out=0;
        tflite.run(inal, out);
        out.rewind();

        float prediction = out.getFloat();
        output.setText("Result: "+ String.valueOf(prediction));




    }
    float ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Float.parseFloat(strNumber);
            } catch(Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        }
        else return 0;
    }
}
