package br.com.eletronicapy.keras_androi_aplication;
import static org.apache.commons.lang3.StringUtils.isNumeric;

import androidx.appcompat.app.AppCompatActivity;
import org.apache.commons.lang3.StringUtils;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView textView;
    Button button;
    Interpreter interpreter;
    String entrada;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Predição da Função y = 5x-2");
        editText = findViewById(R.id.inputX);
        button = findViewById(R.id.predictbtn);
        textView = findViewById(R.id.outputY);

        try {
            interpreter = new Interpreter(loadModelFile(),null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entrada = editText.getText().toString();
                if (isNumeric(entrada.replace('.','1'))){
                    float f =  doInference(entrada);
                    textView.setText("Resultado: "+f);
                }else{
                    textView.setText("Entrada Invalidada");
                }

            }
        });
    }

    private MappedByteBuffer loadModelFile() throws IOException
    {
        AssetFileDescriptor assetFileDescriptor = this.getAssets().openFd("linear.tflite");
        FileInputStream fileInputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();

        long startOffset = assetFileDescriptor.getStartOffset();
        long len = assetFileDescriptor.getLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,len);
    }

    public float doInference(String val)
    {
        float[] input = new float[1];
        input[0] = Float.parseFloat(val);
        float[][] output = new float[1][1];

        interpreter.run(input,output);
        return  output[0][0];
    }
}
